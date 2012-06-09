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
            }
            Console.WriteLine();

            IGoodsFetcher testFetcher = null;
            while (true)
            {
                Console.WriteLine("请输入要测试的商家(输入End结束)：");
                string fetcherName = Console.ReadLine();
                if (fetcherName.ToUpper() == "END")
                    break;
                testFetcher = GoodsFetcherManager.Instance.GetFetcher(fetcherName);

                GetKeywordAndSearch(testFetcher, fetcherName);
                Console.WriteLine();
            }
        }

        private static void GetKeywordAndSearch(IGoodsFetcher testFetcher, string fetcherName)
        {
            if (testFetcher == null)
                return;
            Console.Write(String.Format("[{0}] 请输入要测试的关键字(输入End结束)：", fetcherName));
            string keyword;
            while ((keyword = Console.ReadLine()).ToUpper() != "END")
            {
                Console.Write("输入搜索结果数量上限：");
                string limit = Console.ReadLine();
                Console.Write("获得搜索列表中...");
                var list = testFetcher.GetGoodsUriByKeyowrd(keyword, Convert.ToInt32(limit));
                Console.WriteLine(list.Count() + "结果");
                foreach (var uri in list)
                {
                    Console.WriteLine();
                    Console.WriteLine("--------------------------------------------------------------------------------");
                    Console.WriteLine("抓取 " + uri + " ...");
                    Console.WriteLine("--------------------------------------------------------------------------------");
                    bool fetchAgain = true;
                    while (fetchAgain)
                    {
                        try
                        {
                            Goods goods = testFetcher.FetchGoods(uri);
                            OnGoodsFetched(testFetcher, goods);
                            fetchAgain = false;
                        }
                        catch (Exception ex)
                        {
                            Console.WriteLine("---------------发生异常！" + ex.Message + "---------------");
                            Console.WriteLine("敲回车继续，输入R敲回车重抓此商品");
                            string input = Console.ReadLine();
                            if (input == "R" || input == "r")
                            {
                                fetchAgain = true;
                            }
                        }
                    }
                }
                Console.WriteLine();
                Console.WriteLine(String.Format("[{0}] 请输入要测试的关键字(输入End结束)：", fetcherName));
            }
        }

        private static void OnGoodsFetched(IGoodsFetcher fetcher, Goods goods)
        {
            Console.WriteLine(String.Format("{0} 返回的结果:", GoodsFetcherManager.Instance.GetMetadata(fetcher).Name));
            Console.WriteLine("标题: " + goods.Title);
            Console.WriteLine("价格: " + goods.Price);
            Console.WriteLine("信誉: " + goods.SellerCredit);
            Console.WriteLine("销量: " + goods.SellAmount);
            Console.WriteLine("图片: " + goods.ImagePath);
            if (goods.Properties != null)
            {
                foreach (var property in goods.Properties.OfType<StringProperty>())
                {
                    Console.WriteLine(String.Format(" - {0}: {1}", property.Name, property.Value));
                }
            }
        }
    }
}
