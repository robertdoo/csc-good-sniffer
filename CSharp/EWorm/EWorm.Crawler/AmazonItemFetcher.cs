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
    public delegate void BeforeFetchAmazonItemEvent(string itemUrl);
    public delegate void FetchAmazonItemCompletedEvent(Goods goods);
    class AmazonItemFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://item.taobao.com/item.htm\?id=\d+)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<div class=\042tb-detail-hd\042>\s*?<h3>(?<Title>.+?)</h3>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"id=\042J_StrPrice\042\s*?>(?<Price>\d+\.\d{2})", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品卖家的信誉度
        /// </summary>
        private static readonly Regex CreditPattern = new Regex(@"http://pics.taobaocdn.com/newrank/s_(?<Level1>red|blue|cap|crown)_(?<Level2>[1-5])\.gif", RegexOptions.Compiled);
        #endregion

        #region 事件
        public event BeforeFetchAmazonItemEvent BeforeFetchItem;
        public event FetchAmazonItemCompletedEvent FetchItemComplete;
        #endregion

        /// <summary>
        /// 生成亚马逊搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码，从0开始</param>
        /// <returns></returns>
        public string BuildSearchAmazonUrl(string keyword, int pageIndex)
        {
            string url;
            // 太平洋搜索结果搜索结果分页pageNo相差1，首页无pageNo
            if (pageIndex == 0)
            {
                url = String.Format("http://ks.pconline.com.cn/product.jsp?q={0}", keyword);
            }
            else
            {
                url = String.Format("http://ks.pconline.com.cn/product.jsp?q={0}&pageNo={1}", keyword, pageIndex + 1);
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
                string searchUrl = BuildSearchAmazonUrl(keyword, pageIndex);
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

            Match titleMatch, priceMatch, creditMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(itemResult);
            creditMatch = CreditPattern.Match(itemResult);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
                Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
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
