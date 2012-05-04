using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler
{
    public delegate void GoodsFetchedEvent(IGoodsFetcher fetcher, Goods goods);
}
