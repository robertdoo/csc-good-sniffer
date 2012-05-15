using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public interface IGoodsFetcherMetadata
    {
        string Guid { get; }
        string Name { get; }
        string Url { get; }
        bool Disabled { get; }
    }
}
