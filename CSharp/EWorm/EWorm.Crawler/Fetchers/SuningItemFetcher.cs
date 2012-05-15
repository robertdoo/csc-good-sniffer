using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;
using System.Threading;

namespace EWorm.Crawler
{
   [GoodsFetcher(guid: "546A637C-2310-460E-72CB-2288113C94C4", name: "Suning", url: "http://www.suning.com")]   
   public class SuningItemFetcher:IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://product.dangdang.com/product.aspx\?product_id=\d+)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<h1>(?<Title>.+?)</h1>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"id=\042salePriceTag\042\s*?>\￥(?<Price>\d+\.\d{2})", RegexOptions.Compiled);

        #endregion

        /// <summary>
        /// 生成苏宁易购网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchSuningUrl(string keyword, int pageIndex)
        {
            string url;
            // 苏宁易购搜索结果搜索结果分页pageNo相差1，首页无pageNo
            if (pageIndex == 0)
            {
                url = String.Format("http://searchb.dangdang.com/?key={0}", keyword);
            }
            else
            {
                url = String.Format("http://searchb.dangdang.com/?key={0}&page_index={1}", keyword, pageIndex + 1);
            }
            return url;
        }


        /// <summary>
        /// 抓取搜索结果中显示的商品
        /// </summary>
        /// <param name="keyword">要搜索的商品的关键字</param>
        /// <param name="pageToFetch">表明要抓取多少页的商品</param>
        /// <returns></returns>
        public void FetchByKeyword(string keyword, int limit)
        {
            Thread fetchThread = new Thread(new ThreadStart(delegate
            {
                // 记录已经抓过的Url（去重复）
                var fetched = new HashSet<string>();

                int page = 0;
                while (fetched.Count < limit)
                {
                    string searchUrl = BuildSearchSuningUrl(keyword, page++);
                    string searchResult = Http.Get(searchUrl);

                    // 匹配出商品的Url
                    var itemMatches = ItemUrlPattern.Matches(searchResult);
                    if (itemMatches.Count == 0)
                        return;
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
            }));
            fetchThread.Start();
        }

        public event GoodsFetchedEvent OnGoodsFetched;
        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        private Goods FetchGoods(string itemUrl)
        {
         
            string itemResult = Http.Get(itemUrl);

            Match titleMatch, priceMatch;//  creditMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(itemResult);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
                Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
                SellerCredit = -1,
                SellingUrl = itemUrl,
                UpdateTime = DateTime.Now,
            };
            return goods;
        }
    }
}
