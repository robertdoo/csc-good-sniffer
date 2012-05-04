using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;


namespace EWorm.Crawler.Fetchers
{
    public delegate void BeforeFetchAmazonItemEvent(string itemUrl);
    public delegate void FetchAmazonItemCompletedEvent(Goods goods);

    [GoodsFetcher(guid: "44351351-EDB7-479A-88D7-AC2ECC5232AC", name: "Amazon", url: "http://www.amazon.com")]
    public class AmazonItemFetcher : IGoodsFetcher
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


        public event GoodsFetchedEvent OnGoodsFetched;

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
        /// <returns></returns>
        public void FetchByKeyword(string keyword, int limit)
        {
            // 记录已经抓过的Url（去重复）
            var fetched = new HashSet<string>();

            int page = 0;
            while (fetched.Count < limit)
            {
                string searchUrl = BuildSearchAmazonUrl(keyword, page++);
                string searchResult = Http.Get(searchUrl);

                // 匹配出商品的Url
                var itemMatches = ItemUrlPattern.Matches(searchResult);
                if (itemMatches.Count == 0)
                {
                    return;
                }
                foreach (var itemMatch in itemMatches.OfType<Match>())
                {
                    string itemUrl = itemMatch.Groups["Url"].Value;
                    if (!fetched.Contains(itemUrl))
                    {
                        Goods goods = FetchGoods(itemUrl);
                        if (OnGoodsFetched != null)
                        {
                            OnGoodsFetched.BeginInvoke(this, goods, null, null);
                        }
                        fetched.Add(itemUrl);
                    }
                }
            }
        }

        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        private Goods FetchGoods(string itemUrl)
        {
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
            return goods;
        }

    }
}
