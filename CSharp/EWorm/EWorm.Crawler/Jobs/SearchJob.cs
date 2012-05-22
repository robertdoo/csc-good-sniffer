using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class SearchJob : Job
    {
        public String Keyword { get; set; }
        public int LimitSize { get; set; }

        public SearchJob(Job creator, Crawler context, string keyword, int limitSize)
            : base(creator, context)
        {
            this.Keyword = keyword;
            this.LimitSize = limitSize;
            this.Priority = creator.Priority;
        }

        public override void Work()
        {
            var fetchers = GoodsFetcherManager.Instance.GetAllFetcher();
            int sizePerFetcher = LimitSize / fetchers.Count();
            foreach (var fetcher in fetchers)
            {
                IEnumerable<Uri> uriList = fetcher.GetGoodsUriByKeyowrd(this.Keyword, sizePerFetcher);
                foreach (var uri in uriList)
                {
                    FetchJob job = new FetchJob(this, Context, fetcher, uri);
                    this.Context.JobQueue.Enqueue(job);
                }
            }
            KeywordSelectJob keywordSelectJob = new KeywordSelectJob(this, Context);
            FilterJob filterJob = new FilterJob(this, Context);
            this.Context.JobQueue.Enqueue(keywordSelectJob);

        }
    }
}
