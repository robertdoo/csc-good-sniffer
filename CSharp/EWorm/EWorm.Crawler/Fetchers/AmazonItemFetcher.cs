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
    //[GoodsFetcher(guid: "44351351-EDB7-479A-88D7-AC2ECC5232AC", name: "Amazon", url: "http://www.amazon.com")]
   
    
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
        private static readonly Regex PricePattern = new Regex(@"<b class=\042priceLarge\042>￥ (?<Price>.+?)</b>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<div id=\042rwImages_hidden\042 style=\042display:none;\042>\n<img src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);
        
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
                    string searchUrl = BuildSearchAmazonUrl(keyword, page++);
                    string searchResult = Http.Get(searchUrl);

                    // 匹配出商品的Url
                    var itemMatches = ItemUrlPattern.Matches(searchResult);
                    if (itemMatches.Count == 0)
                        return;
                    foreach (var itemMatch in itemMatches.OfType<Match>())
                    {
                        try
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
                        catch(WebException e)
                        {
                            continue;
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

            Match titleMatch, priceMatch,imageMatch;
            titleMatch = TitlePattern.Match(itemResult);
            priceMatch = PricePattern.Match(itemResult);
            imageMatch = ImagePattern.Match(itemResult);
            string modifyPrice = priceMatch.Groups["Price"].Value;
            modifyPrice.Replace(",","");

            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            //Console.Write(imageurl);
            string downloadedImage = Http.DownloadImage(imageurl);

            Goods goods = new Goods()
            {
                Title = titleMatch.Groups["Title"].Value,
              //  Price = Convert.ToDouble(modifyPrice),
                SellerCredit = -1,
                SellingUrl = itemUrl,
                UpdateTime = DateTime.Now,
                ImagePath = downloadedImage,
            };

           
            return goods;
        }

    }
}
