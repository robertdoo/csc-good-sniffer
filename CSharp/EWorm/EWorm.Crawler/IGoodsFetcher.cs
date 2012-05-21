using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler
{
    public interface IGoodsFetcher
    {
        void FetchByKeyword(string keyword, int limit);
        string Search(string keyword, int page);
        IEnumerable<Uri> FindGoodsUri(string content);
        event GoodsFetchedEvent OnGoodsFetched;
    }
}
