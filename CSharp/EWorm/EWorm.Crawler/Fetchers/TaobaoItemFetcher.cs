using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;
using System.Threading;

namespace EWorm.Crawler.Fetchers
{
    [GoodsFetcher(guid: "525E1313-1E04-47C2-A05A-D93079865079", name: "Taobao", url: "http://www.taobao.com", disabled: true)]
    public class TaobaoItemFetcher : IGoodsFetcher
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

        /// <summary>
        /// 匹配商品页面上商品的图片url
        /// </summary>
        private static readonly Regex ImagePattern = new Regex(@"<img id=\042J_ImgBooth\042 src=\042(?<ImageUrl>.+?.jpg)\042", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品的属性列表
        /// </summary>
        private static readonly Regex PropertyListPattern = new Regex(@"<ul\sclass=\042attributes-list\042\s?>\s+?(?<PropertyList>.+?)</ul>", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品属性
        /// </summary>
        private static readonly Regex PropertyPattern = new Regex(@"<li.+?>(?<Name>.+?):&nbsp;(?<Value>.+?)\s*?</li>", RegexOptions.Compiled);
        #endregion


        /// <summary>
        /// 淘宝搜索结果分页大小： 40
        /// </summary>
        public const int SearchPageSize = 40;

        /// <summary>
        /// 生成淘宝搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <param name="pageIndex">表明搜索的页码，从0开始</param>
        /// <returns></returns>
        public string BuildSearchTaobaoUrl(string keyword, int pageIndex)
        {
            string url = String.Format("http://s.taobao.com/search?q={0}&s={1}", keyword, pageIndex * SearchPageSize);
            return url;
        }

        public void FetchByKeyword(string keyword, int limit)
        {
            Thread fetchThread = new Thread(new ThreadStart(delegate
            {
                // 记录已经抓过的Url（去重复）
                var fetched = new HashSet<string>();

                int page = 0;
                while (fetched.Count < limit)
                {
                    string searchUrl = BuildSearchTaobaoUrl(keyword, page++);
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
                SellerCredit = CalculateTaobaoCredit(creditMatch.Groups["Level1"].Value, creditMatch.Groups["Level2"].Value),
                SellingUrl = itemUrl,
                UpdateTime = DateTime.Now,
                ImagePath = downloadedImage
            };

            Match propertyListMatch = PropertyListPattern.Match(itemResult);
            if (propertyListMatch.Success)
            {
                string propertyResult = propertyListMatch.Groups["PropertyList"].Value;
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

        /// <summary>
        /// 计算淘宝卖家的信誉度
        /// </summary>
        /// <param name="level1">信誉等级一：图标类型（红心red、蓝钻blue、蓝冠cap、金冠crown）</param>
        /// <param name="level2">信誉等级二：图标数量（1-5）</param>
        /// <returns></returns>
        private int CalculateTaobaoCredit(string level1, string level2)
        {
            if (String.IsNullOrWhiteSpace(level1) || String.IsNullOrWhiteSpace(level2))
            {
                return 0;
            }
            int credit = 0;
            switch (level1)
            {
                case "red":
                    credit = 10;
                    break;
                case "blue":
                    credit = 20;
                    break;
                case "cap":
                    credit = 30;
                    break;
                case "crown":
                    credit = 40;
                    break;
            }
            credit += Convert.ToInt32(level2);
            return credit;
        }

    }
}
