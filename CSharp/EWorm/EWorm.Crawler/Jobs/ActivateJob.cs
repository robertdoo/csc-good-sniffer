using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace EWorm.Crawler.Jobs
{
    class ActivateJob : Job
    {
        private Thread WorkingThread { get; set; }
        public ActivateJob(Crawler crawler)
            : base(null, crawler)
        {
            this.Priority = 10;
        }


        public override void Work()
        {
            if (this.WorkingThread == null)
            {
                this.WorkingThread = new Thread(new ThreadStart(this.GetJobAndWork));
            }
        }

        public void AddSearchKeyword(string keyword)
        {
            //TODO 这里的参数应该可配置
            SearchJob job = new SearchJob(this, this.Context, keyword, 50);
            job.Priority = 9;
            this.Context.JobQueue.Enqueue(job);
            if (this.WorkingThread.ThreadState == ThreadState.Suspended)
            {
                this.WorkingThread.Interrupt();
            }
        }

        private void GetJobAndWork()
        {
            while (true)
            {
                while (this.Context.JobQueue.HasJob)
                {
                    Job job = this.Context.JobQueue.Dequeue();
                    job.Work();
                }
                try
                {
                    Thread.Sleep(System.Threading.Timeout.Infinite);
                }
                catch (ThreadInterruptedException)
                {
                    continue;
                }
            }
        }
    }
}
