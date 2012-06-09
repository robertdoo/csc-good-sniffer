using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class FetchJob : Job
    {
        public FetchJob(Job creator, IGoodsFetcher fetcher, Uri uri)
            : base(creator)
        {
            this.Fetcher = fetcher;
            this.Uri = uri;
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
        /// 抓取商品的Fetcher
        /// </summary>
        public IGoodsFetcher Fetcher { get; set; }

        public override void Work()
        {
            Debug.WriteLine(String.Format("Fetching({0}) {1}", this.Priority, this.Uri));
            var goods = Fetcher.FetchGoods(this.Uri);
            this.Context.GoodsBufferPool.Put(goods);
            this.Context.KeywordQueue.ExtractAndEnqueue(goods.Title, 1);
        }

        public override string ToString()
        {
            return String.Format("Fetch({0}) {1}", this.Priority, this.Uri);
        }
    }
}
