using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public class JobQueueChangeEventArgs : EventArgs
    {
        public string CurrentJob { get; set; }
        public IEnumerable<String> JobQueue { get; set; }
    }
}
