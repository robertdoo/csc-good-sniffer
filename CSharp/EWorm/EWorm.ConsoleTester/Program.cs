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
            SuningItemFetcher fetcher5 = new SuningItemFetcher();
            AmazonItemFetcher fetcher6 = new AmazonItemFetcher();
            fetcher.FetchItemComplete += new FetchItemCompletedEvent(fetcher_FetchItemComplete);
            fetcher2.FetchItemComplete += new FetchPconItemCompletedEvent(fetcher_FetchPconItemComplete);
            fetcher3.FetchItemComplete += new FetchJingdongItemCompletedEvent(fetcher_FetchJingdongItemComplete);
            fetcher4.FetchItemComplete += new FetchDangdangItemCompletedEvent(fetcher_FetchDangdangItemComplete);
            fetcher5.FetchItemComplete += new FetchSuningItemCompletedEvent(fetcher_FetchSuningItemComplete);
            fetcher6.FetchItemComplete += new FetchAmazonItemCompletedEvent(fetcher_FetchAmazonItemComplete);
            while (true)
            {
                Console.Write("请选择网站0.淘宝1.太平洋2.京东商城3.当当网4.苏宁易购5.亚马逊：");
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
                if (word == 4)
                {
                    Console.Write("苏宁易购网输入关键字：");
                    String keyword = Console.ReadLine();
                    var result = fetcher5.FetchByKeyword(keyword, 10);
                    Console.WriteLine(String.Format("{0} result fetched.", result.Count()));
                
                }
                if (word == 5)
                {
                    Console.Write("亚马逊网输入关键字：");
                    String keyword = Console.ReadLine();
                    var result = fetcher6.FetchByKeyword(keyword, 10);
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
        //    Console.WriteLine("Price: " + goods.Price3);
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
            Console.WriteLine("Price: " + goods.Price3);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
        static void fetcher_FetchSuningItemComplete(Model.Goods goods)
        {
          //  Console.WriteLine("Title: " + goods.Title);
         //   Console.WriteLine("Price: " + goods.Price);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
        static void fetcher_FetchAmazonItemComplete(Model.Goods goods)
        {
            //  Console.WriteLine("Title: " + goods.Title);
            //   Console.WriteLine("Price: " + goods.Price);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine();
        }
    }
}
