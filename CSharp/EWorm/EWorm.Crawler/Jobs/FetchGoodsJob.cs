using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class FetchGoodsJob
    {
        /// <summary>
        /// 当前抓取的深度
        /// </summary>
        public int Depth { get; set; }

        /// <summary>
        /// 
        /// </summary>
        public string Url { get; set; }
    }
}
