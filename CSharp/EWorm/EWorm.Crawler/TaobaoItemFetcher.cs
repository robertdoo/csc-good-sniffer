using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using System.Text.RegularExpressions;
using Ivony.Html.Parser;
using Ivony.Html;

namespace EWorm.Crawler
{
    public delegate void BeforeFetchItemEvent(string itemUrl);
    public delegate void FetchItemCompletedEvent(Goods goods);
    public class TaobaoItemFetcher
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
        private static readonly Regex PricePattern = new Regex(@"<strong id=\042J_StrPrice\042 >(?<Price>\d+\.\d{2})", RegexOptions.Compiled);

        /// <summary>
        /// 匹配商品页面上商品卖家的信誉度
        /// </summary>
        private static readonly Regex CreditPattern = new Regex(@"http://pics.taobaocdn.com/newrank/s_(?<Level1>red|blue|cap|crown)_(?<Level2>[1-5])\.gif", RegexOptions.Compiled);
        #endregion

        #region 事件
        public event BeforeFetchItemEvent BeforeFetchItem;
        public event FetchItemCompletedEvent FetchItemComplete;
        #endregion

        /// <summary>
        /// 生成淘宝搜索的地址
        /// </summary>
        /// <param name="keyword">搜索的关键字</param>
        /// <returns></returns>
        public string BuildSearchTaobaoUrl(string keyword)
        {
            return String.Format("http://s.taobao.com/search?q=" + keyword);
        }

        /// <summary>
        /// 抓取搜索结果中显示的商品
        /// </summary>
        /// <param name="keyword"></param>
        /// <returns></returns>
        public IEnumerable<Goods> FetchByKeyword(string keyword)
        {
            HashSet<string> fetched = new HashSet<string>();
            var list = new List<Goods>();
            string searchUrl = BuildSearchTaobaoUrl(keyword);
            string searchResult = Http.Get(searchUrl);
            var matches = ItemUrlPattern.Matches(searchResult);
            foreach (var match in matches.OfType<Match>())
            {
                string itemUrl = match.Groups["Url"].Value;
                if (!fetched.Contains(itemUrl))
                {
                    Goods goods = FetchGoods(itemUrl);
                    list.Add(goods);
                    fetched.Add(itemUrl);
                }
            }
            return list;
        }

        /// <summary>
        /// 在指定的URL上提取商品数据
        /// </summary>
        /// <param name="itemUrl"></param>
        /// <returns></returns>
        private Goods FetchGoods(string itemUrl)
        {
            if (this.BeforeFetchItem != null)
            {
                this.BeforeFetchItem.Invoke(itemUrl);
            }
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
                SellerCredit = CalculateTaobaoCredit(creditMatch.Groups["Level1"].Value, creditMatch.Groups["Level2"].Value)
            };
            if (this.FetchItemComplete != null)
            {
                this.FetchItemComplete.Invoke(goods);
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
