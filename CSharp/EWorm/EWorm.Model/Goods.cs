using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Model
{
    /// <summary>
    /// 表示一件商品
    /// </summary>
    public class Goods
    {
        /// <summary>
        /// 商品的标题
        /// </summary>
        public string Title { get; set; }

        /// <summary>
        /// 抓下商品信息的原始URL
        /// </summary>
        public string SellingUrl { get; set; }

        /// <summary>
        /// 更新日期
        /// </summary>
        public DateTime UpdateTime { get; set; }

        /// <summary>
        /// 抓取时商品的价格
        /// </summary>
        public double Price { get; set; }

        /// <summary>
        /// 卖家信用值
        /// </summary>
        public int SellerCredit { get; set; }

        public IEnumerable<Property> Properties { get; set; }
    }
}
