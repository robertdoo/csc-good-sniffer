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
            foreach (var fetcher in GoodsFetcherManager.Instance.GetAllFetcher())
            {
                Console.WriteLine("加载商家：" + GoodsFetcherManager.Instance.GetMetadata(fetcher).Name);
                fetcher.OnGoodsFetched += OnGoodsFetched;
            }
            Console.WriteLine();
            Console.WriteLine("请输入关键字：");
            string keyword = Console.ReadLine();
            GoodsFetcherManager.Instance.FetchByKeywordInAllShops(keyword, 100);
            Console.Read();
        }

        private static void OnGoodsFetched(IGoodsFetcher fetcher, Goods goods)
        {
            Console.WriteLine(String.Format("{0} 返回的结果:", GoodsFetcherManager.Instance.GetMetadata(fetcher).Name));
            Console.WriteLine("Title: " + goods.Title);
            Console.WriteLine("Url: " + goods.SellingUrl);
            Console.WriteLine("Price: " + goods.Price);
            Console.WriteLine("Credit: " + goods.SellerCredit);
            Console.WriteLine("Update: " + goods.UpdateTime);
            Console.WriteLine("Image: " + goods.ImagePath);
            if (goods.Properties != null)
            {
                foreach (var property in goods.Properties.OfType<StringProperty>())
                {
                    Console.WriteLine(String.Format("{0}: {1}", property.Name, property.Value));
                }
            }
            Console.WriteLine();
        }
    }
}
