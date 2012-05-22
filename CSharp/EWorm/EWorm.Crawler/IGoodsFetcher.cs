using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;

namespace EWorm.Crawler
{
    public interface IGoodsFetcher
    {
        IEnumerable<Uri> GetGoodsUriByKeyowrd(string keyword, int count);
        Goods FetchGoods(Uri goodsUri);
    }
}
