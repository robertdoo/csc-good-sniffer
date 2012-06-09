using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using System.Threading;

namespace EWorm.Crawler
{
    [GoodsFetcher(guid: "546A637C-2310-460E-72CB-2288113C94C4", name: "Suning", url: "http://www.suning.com", disabled: false)]
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
        private static readonly Regex TitlePattern = new Regex(@"<h2>(?<Title>.+?)&nbsp;", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"\042promotionPrice\042:\042(?<Price>\d+\.\d{2})", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<img src=\042(?<ImageUrl>.+?.jpg)\042 width=", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        private static readonly Regex PropertyListPattern = new Regex(@"cellpadding=\042\d\042 cellspacing=\042\d\042 border=\042\d\042>\s(?<PropertyList>(.|\s)+?)</table>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        private static readonly Regex PropertyPattern = new Regex(@"class=\042tt\042>(?<Name>.+?)\s*?</td>\s*?<td.*?>(?<Value>.+?)</td>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配页面内价格url上的一个ProdutId
        /// </summary>
        private static readonly Regex ProductIdPattern = new Regex(@"http://www.suning.com/emall/prd_10052_10051_-7_(?<ProductId>\d+?)_.html", RegexOptions.Compiled);
        #endregion

        /// <summary>
        /// 生成苏宁易购网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchSuningUrl(string keyword, int limit)
        {
            string url = String.Format("http://www.suning.com/ssp-sp/searchMix?keyword={1}&start=0&row={0}&orgId=0000A,10010B,50160F,50160Z,501611Z,501112Z,501313Z,500814Z,501331Z,501661Z,501662Z,501663Z,501664Z", limit, keyword);
            return url;
        }

        private string BuildShopItemUrl(string catentry_Id)
        {
            string url =String.Format( "http://www.suning.com/emall/prd_10052_10051_-7_{0}_.html",catentry_Id);
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
                // 记录已经抓过的Id（去重复）
                var fetched = new HashSet<string>();

                string searchUrl = BuildSearchSuningUrl(keyword, limit);
                
                string searchResult = Http.Get(searchUrl);

                // 匹配出商品的Url
                var idMatches = CatentryIdPattern.Matches(searchResult);
                
                if (idMatches.Count == 0)
                {
                    return new List<Uri>();
                }
                foreach (var itemMatch in idMatches.OfType<Match>())
                {
                    string catentry_Id = itemMatch.Groups["CatentryId"].Value;
                    string newUrl = BuildShopItemUrl(catentry_Id); 
                    if (!fetched.Contains(newUrl))
                    {
                        fetched.Add(newUrl);
                    }
                }
                return fetched.Select(x => new Uri(x));
        }

        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl">商品的Url</param>
        /// <returns></returns>
        public Goods FetchGoods(Uri goodsUri, string catentry_Id)
        {
            string priceUrl = String.Format("http://www.suning.com/emall/SNProductStatusView?storeId=10052&catalogId=10051&productId={0}&langId=-7&cityId=9173&_=1337310463016", catentry_Id);
            string itemResult = Http.Get(goodsUri.ToString());
            string priceResult = Http.Get(priceUrl);
            Match titleMatch, priceMatch,imageMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(priceResult);
            imageMatch = ImagePattern.Match(itemResult);
            //根据图片url下载图片
            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            string downloadedImage = Http.DownloadImage(imageurl);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
                Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
                SellerCredit = -1,
                SellingUrl = goodsUri.ToString(),
                ImagePath = downloadedImage,
                UpdateTime = DateTime.Now,
            };

            Match propertyListMatch = PropertyListPattern.Match(itemResult);
           // Console.Write(propertyListMatch.Groups["PropertyList"].Value);
            if (propertyListMatch.Success)
            {
                string propertyResult = propertyListMatch.Groups["PropertyList"].Value;
                var propertyMatches = PropertyPattern.Matches(propertyResult);
                var properties = new List<Property>();
                foreach (Match propertyMatch in propertyMatches)
                {
                   // Console.Write(propertyMatch.Groups["Name"].Value);
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

        #region IGoodsFetcher 成员

        public Goods FetchGoods(Uri goodsUri)
        {
            var proIdMatch = ProductIdPattern.Match(goodsUri.ToString());
            string productId = proIdMatch.Groups["ProductId"].Value;
            string priceUrl = String.Format("http://www.suning.com/emall/SNProductStatusView?storeId=10052&catalogId=10051&productId={0}&langId=-7&cityId=9173&_=1337310463016", productId);
            string itemResult = Http.Get(goodsUri.ToString(), Encoding.UTF8);
            string priceResult = Http.Get(priceUrl);
            Match titleMatch, priceMatch, imageMatch;
            titleMatch = TitlePattern.Match(itemResult);
            imageMatch = ImagePattern.Match(itemResult);
            priceMatch = PricePattern.Match(priceResult);
            //根据图片url下载图片
            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            string downloadedImage = Http.DownloadImage(imageurl);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
                Price = Convert.ToDouble(priceMatch.Groups["Price"].Value),
                SellerCredit = -1,
                SellingUrl = goodsUri.ToString(),
                ImagePath = downloadedImage,
                UpdateTime = DateTime.Now,
            };

            Match propertyListMatch = PropertyListPattern.Match(itemResult);
            // Console.Write(propertyListMatch.Groups["PropertyList"].Value);
            if (propertyListMatch.Success)
            {
                string propertyResult = propertyListMatch.Groups["PropertyList"].Value;
                var propertyMatches = PropertyPattern.Matches(propertyResult);
                var properties = new List<Property>();
                foreach (Match propertyMatch in propertyMatches)
                {
                    // Console.Write(propertyMatch.Groups["Name"].Value);
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

        #endregion
    }
}
