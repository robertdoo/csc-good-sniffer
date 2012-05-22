using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class FilterJob : Job
    {
        public FilterJob(Job creator)
            :base(creator)
        {
            this.Priority = this.Creator.Priority - 1;
        }
    }
}
