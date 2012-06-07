using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler.Jobs
{
    class FilterJob : Job
    {
        private double KeepPercent { get; set; }
        private bool Clear { get; set; }

        public FilterJob(Job creator, double keepPercent, bool clear)
            : base(creator)
        {
            this.KeepPercent = keepPercent;
            this.Clear = clear;
        }

        public override void Work()
        {
            Debug.WriteLine(String.Format("Filterling({0})", this.Priority));
            var allGoods = this.Context.GoodsBufferPool.GetAll();
            var creditTotal = allGoods.Sum(x => x.SellerCredit);
            var sellTotal = allGoods.Sum(x => x.SellAmount);
            var goodsWithRValue = allGoods.Select(x => new { Goods = x, RValue = CalculateRValue(x, creditTotal, sellTotal) });
            var ordered = goodsWithRValue.OrderByDescending(x => x.RValue);
            var takened = ordered.Take((int)(allGoods.Count() * KeepPercent)).Select(x => x.Goods);
            this.Context.GoodsStorage.SaveGoods(takened);
            if (this.Clear)
            {
                this.Context.GoodsBufferPool.Clear();
            }
        }

        private double CalculateRValue(Goods goods, double creditTotal, double sellTotal)
        {
            if (goods.SellerCredit > 0 && goods.SellAmount > 0)
            {
                return 50 * goods.SellerCredit / creditTotal + 50 * goods.SellAmount / sellTotal;
            }
            else if (goods.SellerCredit > 0)
            {
                return 100 * goods.SellerCredit / creditTotal;
            }
            else if (goods.SellAmount > 0)
            {
                return 100 * goods.SellAmount / sellTotal;
            }
            return 0;
        }

        public override string ToString()
        {
            return String.Format("Filter Goods({0})", this.Priority);
        }
    }
}
