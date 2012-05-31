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
        public ActivateJob(Crawler crawler) : base(null)
        {
            this.Context = crawler;
            this.Priority = 10;
        }


        public override void Work()
        {
            if (this.WorkingThread == null)
            {
                this.WorkingThread = new Thread(new ThreadStart(this.GetJobAndWork));
                this.WorkingThread.Start();
            }
        }

        public void AddSearchKeyword(string keyword)
        {
            //TODO 这里的参数应该可配置
            this.Context.KeywordQueue.Enqueue(keyword, 100);
            SearchJob searching = this.Context.JobQueue.CurrentJob as SearchJob;
            if (searching != null && searching.Keyword == keyword)
                return;
            KeywordSelectJob job = new KeywordSelectJob(this);
            this.Context.JobQueue.Enqueue(job);
            if (this.WorkingThread.ThreadState == ThreadState.WaitSleepJoin)
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
                    Crawler.NotifyJobQueueChange(this.Context.JobQueue.GetAll());
                    try
                    {
                        job.Work();
                    }
                    catch (Exception ex)
                    {
                        this.Context.JobQueue.Enqueue(job);
                    }
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

        public override string ToString()
        {
            return "ActivateJob";
        }
    }
}
