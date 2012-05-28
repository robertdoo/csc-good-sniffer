using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public class Crawler
    {
        internal JobQueue JobQueue { get; private set; }
        internal KeywordQueue KeywordQueue { get; private set; }
        internal GoodsBufferPool GoodsBufferPool { get; private set; }
        internal GoodStorage GoodsStorage { get; private set; }

        internal Crawler()
        {
            this.JobQueue = new JobQueue();
            this.KeywordQueue = new KeywordQueue();
            this.GoodsBufferPool = new GoodsBufferPool();
            this.GoodsStorage = new GoodStorage();
        }

        internal static void NotifyJobQueueChange(IEnumerable<Job> jobQueue)
        {
            if (OnQueueChanged != null)
            {
                JobQueueChangeEventArgs args = new JobQueueChangeEventArgs()
                {
                    JobQueue = jobQueue.Select(x => x.ToString()).ToList()
                };
                OnQueueChanged.BeginInvoke(ActivateJob.Context, args, null, null);
            }
        }

        internal static void NotifyKeywordQueueChange(Dictionary<string, int> keywordQueue)
        {
            if (OnKeywordQueueChanged != null)
            {
                KeywordQueueChangeEventArgs args = new KeywordQueueChangeEventArgs()
                {
                    KeywordQueue = keywordQueue.ToDictionary(x => x.Key, x => x.Value)
                };
                OnKeywordQueueChanged.BeginInvoke(ActivateJob.Context, args, null, null);
            }
        }

        private static Jobs.ActivateJob ActivateJob { get; set; }

        public static void Start()
        {
            if (ActivateJob == null)
            {
                ActivateJob = new Jobs.ActivateJob(new Crawler());
                ActivateJob.Work();
            }
        }

        public static void AddKeyword(string keyword)
        {
            ActivateJob.AddSearchKeyword(keyword);
        }

        public static event EventHandler<JobQueueChangeEventArgs> OnQueueChanged;
        public static event EventHandler<KeywordQueueChangeEventArgs> OnKeywordQueueChanged;
    }
}
