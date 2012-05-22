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
    [GoodsFetcher(guid: "8CD62BE2-640F-A964-09E1-E96C19EDF8BE", name: "Dangdang", url: "http://www.dangdang.com", disabled: false)]
    public class DangdangItemFetcher : IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://product.dangdang.com/product.aspx\?product_id=\d+)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<h1>(?<Title>.+?)<[/,s,f]", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"<span class=\042num.*?\042>\￥(?<Price>.+?)</span>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品卖家的信誉度
        /// </summary>
        private static readonly Regex CreditPattern = new Regex(@"<span><img src=\'images/(?<Level1>.+?).gif\' /><img src=\'images/(?<Level2>.+?).gif\' /><img src=\'images/(?<Level3>.+?).gif\' /><img src=\'images/(?<Level4>.+?).gif\' /><img src=\'images/(?<Level5>.+?).gif\' /></span>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<img src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        // private static readonly Regex PropertyListPattern = new Regex(@"<ul\sclass=\042attributes-list\042\s?>\s+?(?<PropertyList>.+?)</ul>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        //private static readonly Regex PropertyPattern = new Regex(@"<li.+?>(?<Name>.+?):&nbsp;(?<Value>.+?)\s*?</li>", RegexOptions.Compiled);
        #endregion


        /// <summary>
        /// 生成太平洋网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchDangdangUrl(string keyword, int pageIndex)
        {
            string url;
            // 太平洋搜索结果搜索结果分页pageNo相差1，首页无pageNo
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
        public IEnumerable<Uri> GetGoodsUriByKeyowrd(string keyword, int limit)
        {       
                // 记录已经抓过的Url（去重复）
                var fetched = new HashSet<string>();

                int page = 0;
                while (fetched.Count < limit)
                {
                    string searchUrl = BuildSearchDangdangUrl(keyword, page++);
                    string searchResult = Http.Get(searchUrl);

                    // 匹配出商品的Url
                    var itemMatches = ItemUrlPattern.Matches(searchResult);
                    if (itemMatches.Count == 0)
                        return new List<Uri>();
                    foreach (var itemMatch in itemMatches.OfType<Match>())
                    {
                        string itemUrl = itemMatch.Groups["Url"].Value;
                        if (!fetched.Contains(itemUrl))
                        {
                            
                            fetched.Add(itemUrl);
                        }
                    }
                }
         return fetched.Select(x => new Uri(x));
        
            
        }

        public event GoodsFetchedEvent OnGoodsFetched;
        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        private Goods FetchGoods(Uri goodsUri)
        {
            string itemResult = Http.Get(goodsUri.ToString());

            Match titleMatch, priceMatch, creditMatch, imageMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(itemResult);
            creditMatch = CreditPattern.Match(itemResult);
            imageMatch = ImagePattern.Match(itemResult);

            string imageurl = imageMatch.Groups["ImageUrl"].Value;

            string downloadedImage = Http.DownloadImage(imageurl);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
                Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
                SellerCredit = CalculateDangdangCredit(creditMatch.Groups["Level1"].Value, creditMatch.Groups["Level2"].Value, creditMatch.Groups["Level3"].Value, creditMatch.Groups["Level4"].Value, creditMatch.Groups["Level5"].Value),
                SellingUrl = goodsUri.ToString(),
                UpdateTime = DateTime.Now,
                ImagePath = downloadedImage,
            };
            return goods;
        }

        public int CalculateDangdangCredit(string level1, string level2, string level3, string level4, string level5)
        {
            if (String.IsNullOrWhiteSpace(level1) || String.IsNullOrWhiteSpace(level2) || String.IsNullOrWhiteSpace(level3) || String.IsNullOrWhiteSpace(level4) || String.IsNullOrWhiteSpace(level5))
            {
                return 0;
            }
            int credit = 0;
           // Console.Write(level1);
            switch (level1)
            {
                case "star_red":
                    credit += 10;
                    break;
                case "star_red2":
                    credit += 5;
                    break;
                case "star_gray":
                    credit += 0;
                    break;
            }
            switch (level2)
            {
                case "star_red":
                    credit += 10;
                    break;
                case "star_red2":
                    credit += 5;
                    break;
                case "star_gray":
                    credit += 0;
                    break;
            }
            switch (level3)
            {
                case "star_red":
                    credit += 10;
                    break;
                case "star_red2":
                    credit += 5;
                    break;
                case "star_gray":
                    credit += 0;
                    break;
            }
            switch (level4)
            {
                case "star_red":
                    credit += 10;
                    break;
                case "star_red2":
                    credit += 5;
                    break;
                case "star_gray":
                    credit += 0;
                    break;
            }
            switch (level5)
            {
                case "star_red":
                    credit += 10;
                    break;
                case "star_red2":
                    credit += 5;
                    break;
                case "star_gray":
                    credit += 0;
                    break;
            }

            return credit;

        }
    }
}
