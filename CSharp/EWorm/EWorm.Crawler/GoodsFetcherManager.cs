using System;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using System.ComponentModel.Composition.Hosting;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    class GoodsFetcherManager
    {
        private static GoodsFetcherManager _instance = new GoodsFetcherManager();
        public static GoodsFetcherManager Instance { get { return _instance; } }

        [ImportMany(typeof(IGoodsFetcher))]
        private IEnumerable<Lazy<IGoodsFetcher, IGoodsFetcherMetadata>> FetcherCollection = null;

        private GoodsFetcherManager()
        {
            string dir = AppDomain.CurrentDomain.BaseDirectory;
            DirectoryCatalog catalog = new DirectoryCatalog(dir);
            CompositionContainer container = new CompositionContainer(catalog);
            container.ComposeParts(this);
            FetcherCollection = FetcherCollection.Where(x => x.Metadata.Disabled == false).ToList();
        }

        public IEnumerable<IGoodsFetcher> GetAllFetcher()
        {
            return FetcherCollection.Select(x => x.Value);
        }

        public IGoodsFetcher GetFetcher(string name)
        {
            var fetcher = FetcherCollection.SingleOrDefault(x => x.Metadata.Name == name);
            if (fetcher == null)
                return null;
            return fetcher.Value;
        }

        public IGoodsFetcherMetadata GetMetadata(IGoodsFetcher fetcher)
        {
            var choosen = FetcherCollection.SingleOrDefault(x => x.Value == fetcher);
            if (choosen == null)
            {
                return null;
            }
            return choosen.Metadata;
        }

    }

}
