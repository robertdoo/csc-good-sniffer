using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    class JobQueue
    {
        private List<Job> PendingJobs { get; set; }
        private int QueueLimit { get; set; }
        private object sync = new object();

        public JobQueue(int capacity)
        {
            this.QueueLimit = capacity;
            this.PendingJobs = new List<Job>();
        }

        public JobQueue() : this(1000) { }

        public void Clear()
        {
            lock (sync)
            {
                PendingJobs.Clear();
            }
        }

        public void Enqueue(Job job)
        {
            lock (sync)
            {
                int insertPos = FindInsertPointForJob(job);
                PendingJobs.Insert(insertPos, job);
                if (PendingJobs.Count > QueueLimit)
                {
                    PendingJobs.Remove(PendingJobs.Last());
                }
            }
        }

        public Job Dequeue()
        {
            lock (sync)
            {
                if (PendingJobs.Count == 0)
                    return null;
                Job head = PendingJobs[0];
                PendingJobs.Remove(head);
                return head;
            }
        }

        private int FindInsertPointForJob(Job job)
        {
            //TODO: 可以改成二分的
            int insertPos = PendingJobs.Count;
            while (insertPos > 0 && PendingJobs[insertPos - 1].Priority < job.Priority)
                insertPos--;
            return insertPos;
        }

        public bool HasJob { get { return this.PendingJobs.Count > 0; } }

        internal IEnumerable<Job> GetAll()
        {
            return this.PendingJobs;
        }
    }
}
