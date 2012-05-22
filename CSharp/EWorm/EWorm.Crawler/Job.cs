using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public abstract class Job
    {
        /// <summary>
        /// 爬虫的实例
        /// </summary>
        public Crawler Context { get; set; }

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

        /// <summary>
        /// 具体的Job有具体的实现
        /// </summary>
        /// <param name="context"></param>
        public abstract void Work();

        public Job(Job creator)
        {
            this.Creator = creator;
        }
    }
}
