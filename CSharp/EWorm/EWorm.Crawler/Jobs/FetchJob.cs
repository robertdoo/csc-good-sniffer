using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class FetchJob : Job
    {
        public FetchJob(Job creator, IGoodsFetcher fetcher, Uri uri) : base(creator) 
        {
            this.Fetcher = fetcher;
            this.Uri = uri;
            this.Priority = creator.Priority;
        }

        /// <summary>
        /// 当前抓取的深度
        /// </summary>
        public int Depth { get; set; }

        /// <summary>
        /// 需要抓取的商品的URL
        /// </summary>
        public Uri Uri { get; set; }

        /// <summary>
        /// 
        /// </summary>
        public IGoodsFetcher Fetcher { get; set; }
    }
}
