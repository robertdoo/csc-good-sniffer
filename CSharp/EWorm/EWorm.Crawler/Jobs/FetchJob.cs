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
            ExtractKeywords(goods.Title);
        }

        private void ExtractKeywords(string title)
        {
            IEnumerable<String> keywords = title.Split(new char[] { ' ' });
            keywords = keywords.Where(x => !String.IsNullOrEmpty(x));
            foreach (var keyword in keywords)
            {
                this.Context.KeywordQueue.Enqueue(keyword);
            }
        }

        public override string ToString()
        {
            return String.Format("Fetch({0}) {1}", this.Priority, this.Uri);
        }
    }
}
