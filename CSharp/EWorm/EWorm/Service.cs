using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm
{
    public class Service
    {
        public IEnumerable<Goods> ListGoods(string keyword, int start = 0, int limit = 50, string order = null, bool desc = false)
        {
            Storage.Storage sto = new Storage.Storage(System.Configuration.ConfigurationManager.ConnectionStrings["eworm"].ConnectionString);
            return sto.SearchGoods(keyword, start, limit, order, false);
        }

        public Goods GetGoods(int id)
        {
            return new Goods();
        }
    }
}
