using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm
{
    public class Service
    {
        public IEnumerable<Goods> ListGoods(string keyword, string order, int start, int limit)
        {
            return new List<Goods>();
        }

        public Goods GetGoods(int id)
        {
            return new Goods();
        }
    }
}
