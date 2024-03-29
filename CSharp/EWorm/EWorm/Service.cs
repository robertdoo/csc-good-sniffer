﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Crawler;
using EWorm.Model;

namespace EWorm
{
    public class Service
    {
        public static void StartCrawler()
        {
            Crawler.Crawler.Start();
        }

        public IEnumerable<Goods> Search(string keyword, int start = 0, int limit = 50, string order = null, bool desc = false)
        {
            Crawler.Crawler.AddKeyword(keyword);
            Storage.Storage sto = new Storage.Storage(System.Configuration.ConfigurationManager.ConnectionStrings["eworm"].ConnectionString);
            return sto.SearchGoods(keyword, start, limit, order, desc);
        }

        public Goods GetGoods(int id)
        {
            Storage.Storage sto = new Storage.Storage(System.Configuration.ConfigurationManager.ConnectionStrings["eworm"].ConnectionString);
            return sto.GetGoodsById(id);
        }

        public IEnumerable<IGoodsFetcherMetadata> GetGoodsFetcherInfo()
        {
            return GoodsFetcherManager.Instance.GetGoodsGetherMetadatas();
        }

        public int SearchCount(string query)
        {
            Storage.Storage sto = new Storage.Storage(System.Configuration.ConfigurationManager.ConnectionStrings["eworm"].ConnectionString);
            return sto.SearchGoodsCount(query);
        }
    }
}