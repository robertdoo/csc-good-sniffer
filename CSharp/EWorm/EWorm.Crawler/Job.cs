using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public abstract class Job
    {
        /// <summary>
        /// 创建者
        /// </summary>
        public Job Creator { get; set; }

        /// <summary>
        /// 优先级
        /// </summary>
        public int Priority { get; set; }

        /// <summary>
        /// 当前工作状态
        /// </summary>
        public JobStatus Status { get; set; }
    }
}
