using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using System.Threading;

namespace EWorm.Crawler
{

    [GoodsFetcher(guid: "6EC22A43-9393-7106-C5D4-5C8FB886EA49", name: "Jingdong", url: "http://www.360buy.com", disabled: false)]

    public class JingdongItemFetcher : IGoodsFetcher
    {
        #region 正则表达式
        /// <summary>
        /// 匹配淘宝上一个商品的Url
        /// </summary>
        private static readonly Regex ItemUrlPattern = new Regex(@"(?<Url>http://www.360buy.com/product/\d+\.html)", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的标题
        /// </summary>
        private static readonly Regex TitlePattern = new Regex(@"<h1>(?<Title>.+?)<[/,f]", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的价格
        /// </summary>
        private static readonly Regex PricePattern = new Regex(@"￥\s*?(?<Price>.+?)</b>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品卖家的信誉度
        /// </summary>
        private static readonly Regex CreditPattern = new Regex(@"<div class=.+?><div class=\042star sa(?<Level>\d)\042></div>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<div id=\042spec-n1\042.+?><img onerror=.+?src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        private static readonly Regex PropertyListPattern = new Regex(@"<table.+?class=\042Ptable\042>.+?</table>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        private static readonly Regex PropertyPattern = new Regex(@"<tr><td class=\042tdTitle\042>(?<Name>.+?)</td><td>(?<Value>.+?)</td></tr>", RegexOptions.Compiled);
        #endregion



        /// <summary>
        /// 生成太平洋网站搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码</param>
        /// <returns></returns>
        public string BuildSearchJingdongUrl(string keyword, int pageIndex)
        {
            string url;
            // 太平洋搜索结果搜索结果分页pageNo相差1，首页无pageNo
            if (pageIndex == 0)
            {
                url = String.Format("http://search.360buy.com/Search?keyword={0}", keyword);
            }
            else
            {
                url = String.Format("http://search.360buy.com/search?keyword={0}&page={1}", keyword, pageIndex + 1);
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
            keyword = System.Web.HttpUtility.UrlEncode(keyword, Encoding.GetEncoding("GBK"));
            // 记录已经抓过的Url（去重复）
            var fetched = new HashSet<string>();

            int page = 0;
            while (fetched.Count < limit)
            {
                string searchUrl = BuildSearchJingdongUrl(keyword, page++);
                string searchResult = Http.Get(searchUrl);

                // 匹配出商品的Url
                var itemMatches = ItemUrlPattern.Matches(searchResult);

                var itemsNotFetched = itemMatches.OfType<Match>().Select(x => x.Groups["Url"].Value).Where(x => !fetched.Contains(x));
                if (itemsNotFetched.Count() == 0)
                {
                    break;
                }
                foreach (var item in itemsNotFetched)
                {
                    fetched.Add(item);
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

            Match titleMatch, imageMatch, creditMatch; //priceMatch;  
            titleMatch = TitlePattern.Match(itemResult);
            creditMatch = CreditPattern.Match(itemResult);
            //  priceMatch = PricePattern.Match(itemResult);
            imageMatch = ImagePattern.Match(itemResult);
            string imageurl = imageMatch.Groups["ImageUrl"].Value;
            string downloadedImage = Http.DownloadImage(imageurl);
            if (creditMatch.Groups["Level"].Value != "")
            {
                int a = Convert.ToInt32(creditMatch.Groups["Level"].Value);

                Goods goods = new Goods()
                {
                    Title = titleMatch.Groups["Title"].Value,
                    //Price =Convert.ToDouble( priceMatch.Groups["Price"].Value),
                    SellerCredit = CalculateJingdongCredit(a),
                    SellingUrl = goodsUri.ToString(),
                    UpdateTime = DateTime.Now,
                    ImagePath = downloadedImage,
                };
                Match propertyListMatch = PropertyListPattern.Match(itemResult);
                if (propertyListMatch.Success)
                {
                    string propertyResult = propertyListMatch.Value;
                    var propertyMatches = PropertyPattern.Matches(propertyResult);
                    var properties = new List<Property>();
                    foreach (Match propertyMatch in propertyMatches)
                    {
                        Property property = new StringProperty()
                        {
                            Name = propertyMatch.Groups["Name"].Value.RemoveHtmlTag(),
                            Value = propertyMatch.Groups["Value"].Value.RemoveHtmlTag()
                        };
                        properties.Add(property);
                    }
                    goods.Properties = properties;
                }

                return goods;
            }
            else {
                Goods goods = new Goods()
                {
                    Title = titleMatch.Groups["Title"].Value,
                    //Price =Convert.ToDouble( priceMatch.Groups["Price"].Value),
                    SellerCredit = 35,
                    SellingUrl = goodsUri.ToString(),
                    UpdateTime = DateTime.Now,
                    ImagePath = downloadedImage,
                };
                Match propertyListMatch = PropertyListPattern.Match(itemResult);
                if (propertyListMatch.Success)
                {
                    string propertyResult = propertyListMatch.Value;
                    var propertyMatches = PropertyPattern.Matches(propertyResult);
                    var properties = new List<Property>();
                    foreach (Match propertyMatch in propertyMatches)
                    {
                        Property property = new StringProperty()
                        {
                            Name = propertyMatch.Groups["Name"].Value.RemoveHtmlTag(),
                            Value = propertyMatch.Groups["Value"].Value.RemoveHtmlTag()
                        };
                        properties.Add(property);
                    }
                    goods.Properties = properties;
                }

                return goods;
            
            
            }
        }
        public int CalculateJingdongCredit(int level)
        {
            int credit = 0;
            switch (level)
            {
                case 0:
                    credit += 0;
                    break;
                case 1:
                    credit += 10;
                    break;
                case 2:
                    credit += 20;
                    break;
                case 3:
                    credit += 30;
                    break;
                case 4:
                    credit += 40;
                    break;
                case 5:
                    credit += 50;
                    break;

            }


            return credit;

        }


    }
}
