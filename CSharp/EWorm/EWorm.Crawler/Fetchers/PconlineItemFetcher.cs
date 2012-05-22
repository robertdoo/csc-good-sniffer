using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;
using System.Web;
using System.Threading;

namespace EWorm.Crawler.Fetcher
{
    [GoodsFetcher(guid: "6D9C16BD-02C1-4E55-A7DE-7365E21A228F", name: "Pconline", url: "http://www.pconline.com", disabled: false)]
    public class PconlineItemFetcher : IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://product.pconline.com.cn/\D+/[^s][^e]\D+/\d+\.html)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<div class=\042hd clearfix\042>\s*?<h1>(?<Title>.+?)</h1>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格的url
        /// </summary>
        private static readonly Regex PriceUrlPattern = new Regex(@"<em id=\042curPrice\042></em>\s+?<a href=\042(?<PriceUrl>.+?)\042\s+?target=\042_blank\042>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"<span>￥<i class=\042price\042>(?<Price>\d+?)</i>	</span>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"img id=\042idImage\042 width=.+? height=.+? src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        private static readonly Regex PropertyListPattern = new Regex(@"<ul\sclass=\042param\sclearfix\042>\s(?<PropertyList>(.|\s)+?)</ul>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        private static readonly Regex PropertyPattern = new Regex(@"<li title=.+?><i class=\042tit\042>(?<Name>.+?)</i><a.+?target=\042_blank\042>(?<Value>.+?)</a>(,|\s)", RegexOptions.Compiled);
        #endregion

        /// <summary>
        /// 生成太平洋网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchPconlineUrl(string keyword, int pageIndex)
        {
            string url;
            // 太平洋搜索结果搜索结果分页pageNo相差1，首页无pageNo
            if (pageIndex == 0)
            {
                url = String.Format("http://ks.pconline.com.cn/product.jsp?q={0}", HttpUtility.UrlEncodeUnicode(keyword));
            }
            else
            {
                url = String.Format("http://ks.pconline.com.cn/product.jsp?q={0}&pageNo={1}", HttpUtility.UrlEncodeUnicode(keyword), pageIndex + 1);
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
                    string searchUrl = BuildSearchPconlineUrl(keyword, page++);
                    string searchResult = Http.Get(searchUrl);

                    // 匹配出商品的Url
                    var itemMatches = ItemUrlPattern.Matches(searchResult);
                    if (itemMatches.Count == 0)
                        return new List<Uri>(); ;
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

      
        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        private Goods FetchGoods(Uri goodsUri)
        {
            string itemResult = Http.Get(goodsUri.ToString());

            Match titleMatch, priceMatch, imageMatch, priceUrlMatch ;
            priceUrlMatch = PriceUrlPattern.Match(itemResult);
            titleMatch = TitlePattern.Match(itemResult);
           
            imageMatch = ImagePattern.Match(itemResult);
            string priceurl = priceUrlMatch.Groups["PriceUrl"].Value;
            //Console.Write(priceurl);
            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            string downloadedImage = Http.DownloadImage(imageurl);
            Goods goods = new Goods();
            if (priceurl == "")//如果该商品未上市或者已经过期标价均记为0
            {
                goods = new Goods()
                {
                    Title = titleMatch.Groups["Title"].Value,
                    Price = 0,
                    SellerCredit = -1,
                    SellingUrl = goodsUri.ToString(),
                    UpdateTime = DateTime.Now,
                    ImagePath = downloadedImage,
                };
               
            }
            else
            {
                string priceResult = Http.Get(priceurl);
                priceMatch = PricePattern.Match(priceResult);
                //Console.Write(priceMatch.Groups["Price"].Value);

                goods = new Goods()
                {
                    Title = titleMatch.Groups["Title"].Value,
                    Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
                    SellerCredit = -1,
                    SellingUrl = goodsUri.ToString(),
                    UpdateTime = DateTime.Now,
                    ImagePath = downloadedImage,
                };

            }
            Match propertyListMatch = PropertyListPattern.Match(itemResult);
            if (propertyListMatch.Success)
            {
                string propertyResult = propertyListMatch.Groups["PropertyList"].Value;
                //Console.Write(propertyResult);
                var propertyMatches = PropertyPattern.Matches(propertyResult);
                var properties = new List<Property>();
                foreach (Match propertyMatch in propertyMatches)
                {
                    Property property = new StringProperty()
                    {
                        Name = propertyMatch.Groups["Name"].Value,
                        Value = propertyMatch.Groups["Value"].Value
                    };
                    properties.Add(property);
                }
                goods.Properties = properties;
            }

            return goods;
        }

    }
}
