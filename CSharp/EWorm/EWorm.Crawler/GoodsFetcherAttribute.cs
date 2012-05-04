using System;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    [MetadataAttribute]
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public class GoodsFetcherAttribute : ExportAttribute, IGoodsFetcherMetadata
    {
        public string Guid { get; private set; }
        public string Name { get; private set; }
        public string Url { get; private set; }

        public GoodsFetcherAttribute(string guid, string name, string url)
            : base(typeof(IGoodsFetcher))
        {
            this.Guid = guid;
            this.Name = name;
            this.Url = url;
        }
    }
}
