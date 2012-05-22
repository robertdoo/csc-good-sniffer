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
            List<FetchJob> fetchJobList = new List<FetchJob>();
            Random rand = new Random();
            foreach (var fetcher in fetchers)
            {
                IEnumerable<Uri> uriList = fetcher.GetGoodsUriByKeyowrd(this.Keyword, sizePerFetcher);
                foreach (var uri in uriList)
                {
                    FetchJob job = new FetchJob(this, Context, fetcher, uri);
                    fetchJobList.Insert(rand.Next(fetchJobList.Count),job);
                }
            }
            KeywordSelectJob keywordSelectJob = new KeywordSelectJob(this, Context);
            FilterJob filterJob = new FilterJob(this, Context);

            foreach (var fetchJob in fetchJobList)
            {
                this.Context.JobQueue.Enqueue(fetchJob);
            }
            this.Context.JobQueue.Enqueue(keywordSelectJob);
            this.Context.JobQueue.Enqueue(filterJob);
        }

        public override string ToString()
        {
            return String.Format("Search({0})", this.Priority);
        }
    }
}
