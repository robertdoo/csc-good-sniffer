using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class FetchJob : Job
    {
        /// <summary>
        /// 当前抓取的深度
        /// </summary>
        public int Depth { get; set; }

        /// <summary>
        /// 需要抓取的商品的URL
        /// </summary>
        public string Url { get; set; }

    }
}
