using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;

namespace EWorm.Crawler
{
    public delegate void BeforeFetchJingdongItemEvent(string itemUrl);
    public delegate void FetchJingdongItemCompletedEvent(Goods goods);
    public class JingdongItemFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://www.360buy.com/product/\d+.html)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<h1>(?<Title>.+?)<font", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
       // private static readonly Regex PricePattern = new Regex(@"class=\042price-b\042\s*?>(?<Price>\d+)", RegexOptions.Compiled);

        #endregion

        #region 事件
        public event BeforeFetchJingdongItemEvent BeforeFetchItem;
        public event FetchJingdongItemCompletedEvent FetchItemComplete;
        #endregion

        /// <summary>
        /// 生成太平洋网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchJingdongUrl(string keyword, int pageIndex)
        {
            string url;
            // 太平洋搜索结果搜索结果分页pageNo相差1，首页无pageNo
            if (pageIndex == 0)
            {
                url = String.Format("http://search.360buy.com/Search?keyword={0}", keyword);
            }
            else
            {
                url = String.Format("http://search.360buy.com/search?keyword={0}&page={1}", keyword, pageIndex + 1);
            }
            return url;
        }
        /// <summary>
        /// 抓取搜索结果中显示的商品
        /// </summary>
        /// <param name="keyword">要搜索的商品的关键字</param>
        /// <param name="pageToFetch">表明要抓取多少页的商品</param>
        /// <returns></returns>
        public IEnumerable<Goods> FetchByKeyword(string keyword, int pageToFetch = 1)
        {
            // 记录已经抓过的Url（去重复）
            var fetched = new HashSet<string>();
            var goodsList = new List<Goods>();

            for (int pageIndex = 0; pageIndex < pageToFetch; pageIndex++)
            {
                string searchUrl = BuildSearchJingdongUrl(keyword, pageIndex);
                string searchResult = Http.Get(searchUrl);

                // 匹配出商品的Url
                var itemMatches = ItemUrlPattern.Matches(searchResult);
                foreach (var itemMatch in itemMatches.OfType<Match>())
                {
                    string itemUrl = itemMatch.Groups["Url"].Value;
                    if (!fetched.Contains(itemUrl))
                    {
                        Goods goods = FetchGoods(itemUrl);
                        goodsList.Add(goods);
                        fetched.Add(itemUrl);
                    }
                }
            }
            return goodsList;
        }
        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        private Goods FetchGoods(string itemUrl)
        {
            if (this.BeforeFetchItem != null)
            {
                this.BeforeFetchItem.Invoke(itemUrl);
            }

            string itemResult = Http.Get(itemUrl);

            Match titleMatch; //priceMatch;//  creditMatch;
            titleMatch = TitlePattern.Match(itemResult);
          //  priceMatch = PricePattern.Match(itemResult);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
              //  Price2 = priceMatch.Groups["Price"].Value,
                SellingUrl = itemUrl,
                UpdateTime = DateTime.Now,
            };

            if (this.FetchItemComplete != null)
            {
                this.FetchItemComplete.Invoke(goods);
            }
            return goods;
        }
    }
}
