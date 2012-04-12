using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace EWorm.Crawler
{
    public class Http
    {
        /// <summary>
        /// 执行HTTP GET获得网页结果
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public static String Get(String url)
        {
            WebClient client = new WebClient();
            Uri uri = new Uri(url);
            return client.DownloadString(uri);
        }
    }
}
