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
            while (true)
            {
                Console.Write("输入关键字：");
                String keyword = Console.ReadLine();
                var result = fetcher.FetchByKeyword(keyword, 10);
                Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
            }
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
