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
        event GoodsFetchedEvent OnGoodsFetched;
    }
}
