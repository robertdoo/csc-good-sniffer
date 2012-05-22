using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;
using System.IO;
using System.Drawing;
using System.Threading;
using System.Net;

namespace EWorm.Crawler.Fetchers
{

    [GoodsFetcher(guid: "44351351-EDB7-479A-88D7-AC2ECC5232AC", name: "Amazon", url: "http://www.amazon.com", disabled: false)]
    public class AmazonItemFetcher : IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"class=\042title\042 href=\042(?<Url>http://www.amazon.cn/.+?)\042 target=\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private readonly Regex TitlePattern = new Regex(@"<span id=\042btAsinTitle\042>(?<Title>.+?)</span>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"￥\s*?(?<Price>.+?)</b>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<div id=\042rwImages_hidden\042 style=\042display:none;\042>\n<img src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        private static readonly Regex PropertyListPattern = new Regex(@"<div class=\042content\042>\s*?<ul>(?<PropertyList>(.|\s)+?)<b>.+?</b>\s*?<script type=.+?>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        private static readonly Regex PropertyPattern = new Regex(@"<li><b>\s*?(?<Name>.+?):\s*?</b>\s*?(?<Value>.+?)\s*?</li>", RegexOptions.Compiled);
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
                url = String.Format("http://www.amazon.cn/s/field-keywords={0}", keyword);
            }
            else
            {
                url = String.Format("http://www.amazon.cn/s/field-keywords={0}&page={1}", keyword, pageIndex + 1);
            }
            return url;
        }

        public IEnumerable<Uri> GetGoodsUriByKeyowrd(string keyword, int limit)
        {
            // 记录已经抓过的Url（去重复）
            var fetched = new HashSet<string>();

            int page = 0;
            while (fetched.Count < limit)
            {
                string searchUrl = BuildSearchAmazonUrl(keyword, page++);
                string searchResult = Http.Get(searchUrl, Encoding.Unicode);

                // 匹配出商品的Url
                var itemMatches = ItemUrlPattern.Matches(searchResult);
                if (itemMatches.Count == 0)
                    return new List<Uri>();
                foreach (var itemMatch in itemMatches.OfType<Match>())
                {
                    try
                    {
                        string itemUrl = itemMatch.Groups["Url"].Value;
                        if (!fetched.Contains(itemUrl))
                        {
                            fetched.Add(itemUrl);
                        }
                    }
                    catch (WebException e)
                    {
                        Console.Write(e.Message);
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
        public Goods FetchGoods(Uri goodsUri)
        {
            string itemResult = Http.Get(goodsUri.ToString());

            Match titleMatch, priceMatch, imageMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(itemResult);
            imageMatch = ImagePattern.Match(itemResult);
            string modifyPrice = priceMatch.Groups["Price"].Value;

            if (modifyPrice == "")
            {
                modifyPrice = "0";
            }
            Console.Write(modifyPrice);
            modifyPrice.Replace(",", "");


            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            //Console.Write(imageurl);
            string downloadedImage = Http.DownloadImage(imageurl);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,

                Price = Convert.ToDouble(modifyPrice),

                //  Price = Convert.ToDouble(modifyPrice),

                SellerCredit = -1,
                SellingUrl = goodsUri.ToString(),
                UpdateTime = DateTime.Now,
                ImagePath = downloadedImage,
            };
            Match propertyListMatch = PropertyListPattern.Match(itemResult);
            if (propertyListMatch.Success)
            {
                string propertyResult = propertyListMatch.Groups["PropertyList"].Value;
                // Console.Write(propertyResult);
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
