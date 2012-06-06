using System;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using System.ComponentModel.Composition.Hosting;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public class GoodsFetcherManager
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
            try
            {
                catalog = new DirectoryCatalog(dir + "/bin");
                container = new CompositionContainer(catalog);
                container.ComposeParts(this);
            }
            catch (Exception) { }
            FetcherCollection = FetcherCollection.Where(x => x.Metadata.Disabled == false).ToList();
        }

        internal IEnumerable<IGoodsFetcher> GetAllFetcher()
        {
            return FetcherCollection.Select(x => x.Value);
        }

        internal IGoodsFetcher GetFetcher(string name)
        {
            var fetcher = FetcherCollection.SingleOrDefault(x => x.Metadata.Name == name);
            if (fetcher == null)
                return null;
            return fetcher.Value;
        }

        internal IGoodsFetcherMetadata GetMetadata(IGoodsFetcher fetcher)
        {
            var choosen = FetcherCollection.SingleOrDefault(x => x.Value == fetcher);
            if (choosen == null)
            {
                return null;
            }
            return choosen.Metadata;
        }

        public IEnumerable<IGoodsFetcherMetadata> GetGoodsGetherMetadatas()
        {
            return FetcherCollection.Select(x => x.Metadata);
        }

    }

}
