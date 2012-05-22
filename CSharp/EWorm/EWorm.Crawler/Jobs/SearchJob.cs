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

        public SearchJob(int limitSize)
        {
            this.LimitSize = limitSize;
        }

        public override void Work()
        {
            var fetchers = GoodsFetcherManager.Instance.GetAllFetcher();
        }
    }
}
