using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Crawler;
using EWorm.Model;

namespace EWorm.ConsoleTester
{
    class Program
    {
        static void Main(string[] args)
        {
            TaobaoItemFetcher fetcher = new TaobaoItemFetcher();
            PconlineItemFetcher fetcher2 = new PconlineItemFetcher();
            JingdongItemFetcher fetcher3 = new JingdongItemFetcher();
            DangdangItemFetcher fetcher4 = new DangdangItemFetcher();
            fetcher.FetchItemComplete += new FetchItemCompletedEvent(fetcher_FetchItemComplete);
            fetcher2.FetchItemComplete += new FetchPconItemCompletedEvent(fetcher_FetchPconItemComplete);
            fetcher3.FetchItemComplete += new FetchJingdongItemCompletedEvent(fetcher_FetchJingdongItemComplete);
            fetcher4.FetchItemComplete += new FetchDangdangItemCompletedEvent(fetcher_FetchDangdangItemComplete);
            while (true)
            {
                Console.Write("请选择网站0.淘宝1.太平洋2.京东商城3.当当网：");
                int word =Convert.ToInt32( Console.ReadLine());
                if(word==0)
                {
                Console.Write("淘宝网输入关键字：");
                String keyword = Console.ReadLine();
                var result = fetcher.FetchByKeyword(keyword, 10);
                Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
                }
                if (word == 1)
                {
                    Console.Write("太平洋输入关键字：");
                    String keyword = Console.ReadLine();
                    var result = fetcher2.FetchByKeyword(keyword, 10);
                    Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
                }
                if (word == 2)
                {
                    Console.Write("京东商城输入关键字：");
                    String keyword = Console.ReadLine();
                    var result = fetcher3.FetchByKeyword(keyword, 10);
                    Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
                }
                if (word == 3)
                {
                    Console.Write("当当网输入关键字：");
                    String keyword = Console.ReadLine();
                    var result = fetcher4.FetchByKeyword(keyword, 10);
                    Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
                }
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
        static void fetcher_FetchPconItemComplete(Model.Goods goods)
        {
            Console.WriteLine("Title: " + goods.Title);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Price: " + goods.Price2);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
        static void fetcher_FetchJingdongItemComplete(Model.Goods goods)
        {
            Console.WriteLine("Title: " + goods.Title);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
        static void fetcher_FetchDangdangItemComplete(Model.Goods goods)
        {
            Console.WriteLine("Title: " + goods.Title);
            Console.WriteLine("Price: " + goods.Price);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
    }
}
