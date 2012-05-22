using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class KeywordSelectJob : Job
    {
        public KeywordSelectJob(Job creator, Crawler context)
            : base(creator, context)
        {
            this.Priority = creator.Priority - 1;
        }

        public override void Work()
        {
            var keyword = this.Context.KeywordQueue.Dequeue();
            SearchJob job = new SearchJob(this, this.Context, keyword, 50);
            this.Context.JobQueue.Enqueue(job);
        }
    }
}
