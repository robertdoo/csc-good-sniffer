using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public abstract class Job
    {
        public Job Creator { get; set; }
        public int Priority { get; set; }
    }
}
