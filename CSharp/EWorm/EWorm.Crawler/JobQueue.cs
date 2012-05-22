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

        public JobQueue(int capacity)
        {
            this.QueueLimit = capacity;
        }

        public JobQueue() : this(1000) { }

        public void Enqueue(Job job)
        {
            int insertPos = FindInsertPointForJob(job);
            PendingJobs.Insert(insertPos, job);
            if (PendingJobs.Count > QueueLimit)
            {
                PendingJobs.Remove(PendingJobs.Last());
            }
        }

        public Job Dequeue()
        {
            if (PendingJobs.Count == 0)
                return null;
            Job head = PendingJobs[0];
            PendingJobs.Remove(head);
            return head;
        }

        private int FindInsertPointForJob(Job job)
        {
            //TODO: 可以改成二分的
            int insertPos = PendingJobs.Count;
            while (insertPos >= 0 && PendingJobs[insertPos].Priority < job.Priority)
                insertPos--;
            return insertPos;
        }
    }
}
