using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Crawler;

namespace EWorm.ConsoleTester
{
    class Program
    {
        static void Main(string[] args)
        {
            TaobaoItemFetcher fetcher = new TaobaoItemFetcher();
            fetcher.FetchItemComplete += new FetchItemCompletedEvent(fetcher_FetchItemComplete);
            var result = fetcher.FetchByKeyword("固态硬盘");
            Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
            Console.ReadKey();
        }

        static void fetcher_FetchItemComplete(Model.Goods goods)
        {
            Console.WriteLine("Title: " + goods.Title);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Price: " + goods.Price);
            Console.WriteLine("Credit: " + goods.SellerCredit);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
    }
}
