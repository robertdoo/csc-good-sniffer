using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler
{
    class GoodsBufferPool
    {
        private List<Goods> PoolData { get; set; }

        public GoodsBufferPool()
        {
            this.PoolData = new List<Goods>();
        }

        public void Clear()
        {
            this.PoolData.Clear();
        }

        public void Put(Goods goods)
        {
            this.PoolData.Add(goods);
        }

        public IEnumerable<Goods> GetAll()
        {
            return PoolData;
        }
    }
}
