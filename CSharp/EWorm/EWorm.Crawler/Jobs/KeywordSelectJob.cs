using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class KeywordSelectJob : Job
    {
        public KeywordSelectJob(Job creator)
            : base(creator)
        {
            this.Priority--;
        }

        public override void Work()
        {
            var keyword = this.Context.KeywordQueue.Dequeue();
            Debug.WriteLine(String.Format("Selected Keyword({0}) {1}", this.Priority, keyword));
            SearchJob job = new SearchJob(this, keyword, 200);
            this.Context.JobQueue.Enqueue(job);
        }

        public override string ToString()
        {
            return String.Format("KeywordSelect({0})", this.Priority);
        }
    }
}
