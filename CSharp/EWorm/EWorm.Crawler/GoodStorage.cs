using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler
{
    class GoodStorage
    {
        public void SaveGoods(IEnumerable<Goods> goodsCollection)
        {
            Storage.Storage sto = new Storage.Storage(System.Configuration.ConfigurationManager.ConnectionStrings["eworm"].ConnectionString);
            foreach (var goods in goodsCollection)
            {
                var stoGoods = sto.GetGoodsBySellingUrl(goods.SellingUrl);
                if (stoGoods != null)
                {
                    goods.Id = stoGoods.Id;
                }
                sto.SaveGoods(goods);
            }
        }
    }
}
