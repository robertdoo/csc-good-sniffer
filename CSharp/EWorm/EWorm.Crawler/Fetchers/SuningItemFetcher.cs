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
    public class SuningItemFetcher : IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配商品列表JSON上的一个CatentryId
        /// </summary>
        private static readonly Regex CatentryIdPattern = new Regex(@"\042catentry_Id\042:\042(?<CatentryId>\d+?)\042", RegexOptions.Compiled);

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
        public string BuildSearchSuningUrl(string keyword, int limit)
        {
            string url = String.Format("http://www.suning.com/ssp-sp/searchMix?keyword=ABC&start=0&row={0}&orgId=0000A,10010B,50160F,50160Z,501611Z,501112Z,501313Z,500814Z,501331Z,501661Z,501662Z,501663Z,501664Z", limit);
            return url;
        }

        private string BuildShopItemUrl(string catentry_Id)
        {
            string url = "http://www.suning.com/emall/prd_10052_10051_-7_{0}_.html";
            return String.Format(url, catentry_Id);
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
                // 记录已经抓过的Id（去重复）
                var fetched = new HashSet<string>();

                string searchUrl = BuildSearchSuningUrl(keyword, limit);
                string searchResult = Http.Get(searchUrl, Encoding.UTF8);

                // 匹配出商品的Url
                var idMatches = CatentryIdPattern.Matches(searchResult);
                if (idMatches.Count == 0)
                {
                    return;
                }
                foreach (var itemMatch in idMatches.OfType<Match>())
                {
                    string catentry_Id = itemMatch.Groups["CatentryId"].Value;
                    if (!fetched.Contains(catentry_Id))
                    {
                        Goods goods = FetchGoods(BuildShopItemUrl(catentry_Id));
                        if (OnGoodsFetched != null)
                        {
                            OnGoodsFetched.BeginInvoke(this, goods, null, null);
                        }
                        fetched.Add(catentry_Id);
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
