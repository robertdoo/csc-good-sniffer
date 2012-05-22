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

        public Crawler()
        {
            this.JobQueue = new JobQueue();
            this.KeywordQueue = new KeywordQueue();
            this.GoodsBufferPool = new GoodsBufferPool();
            this.GoodsStorage = new GoodStorage();
        }
    }
}
