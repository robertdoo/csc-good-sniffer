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
    public delegate void BeforeFetchSuningItemEvent(string itemUrl);
    public delegate void FetchSuningItemCompletedEvent(Goods goods);
    public class SuningItemFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"<li id=.+?>\s*?.+?(?<Url>prd_10052_10051_-7_\d+_\.html)", RegexOptions.Compiled);
        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        //private static readonly Regex TitlePattern = new Regex(@"<h1>(?<Title>.+?)</h1>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
      //  private static readonly Regex PricePattern = new Regex(@"id=\042salePriceTag\042\s*?>\￥(?<Price>\d+\.\d{2})", RegexOptions.Compiled);

        #endregion

        #region 事件
        public event BeforeFetchSuningItemEvent BeforeFetchItem;
        public event FetchSuningItemCompletedEvent FetchItemComplete;
        #endregion

        /// <summary>
        /// 生成太平洋网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchSuningUrl(string keyword, int pageIndex)
        {
            string url;
            // 苏宁搜索结果搜索结果分页currentPage相差1，首页无currentPage
            if (pageIndex == 0)
            {
                url = String.Format("http://www.suning.com/emall/Search?storeId=10052&catalogId=10051&searchKeywords={0}", keyword);
            }
            else
            {
                url = String.Format("http://www.suning.com/emall/Search?storeId=10052&catalogId=10051&searchKeywords={0}&isList=0&currentPage={1}", keyword, pageIndex);
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
                string searchUrl = BuildSearchSuningUrl(keyword, pageIndex);
                
                string searchResult = Http.Get(searchUrl);
                // 匹配出商品的Url
                var itemMatches = ItemUrlPattern.Matches(searchResult);
                
                
                
                foreach (var itemMatch in itemMatches.OfType<Match>())
                {
                    string itemUrl = "http://www.suning.com/emall/";
                    itemUrl =itemUrl + itemMatch.Groups["Url"].Value;
                    
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

           // Match titleMatch, priceMatch;  creditMatch;
         //   titleMatch = TitlePattern.Match(itemResult);
         //   priceMatch = PricePattern.Match(itemResult);

            Goods goods = new Goods()
            {
             //   Title = titleMatch.Groups["Title"].Value,
              //  Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
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
