using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler.Jobs
{
    class KeywordSelectJob : Job
    {
        public KeywordSelectJob(Job creator)
            : base(creator)
        {
            this.Priority = creator.Priority - 1;
        }
    }
}
