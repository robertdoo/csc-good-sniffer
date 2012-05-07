using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Web;

namespace EWorm.Crawler
{
    public class Http
    {
        /// <summary>
        /// 执行HTTP GET获得网页结果
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public static String Get(String url, Encoding encoding)
        {
            
                WebClient client = new WebClient();
                client.Encoding = encoding;
                Uri uri = new Uri(url);
                return client.DownloadString(uri);
            
          
        }

        public static string DownloadImage(string url)
        {
            WebClient client = new WebClient();
            string ext = "jpg";
            string filename = String.Format("{0}.{1}", Guid.NewGuid(), ext);
            client.DownloadFile(url, filename);
            return filename;
        }

        public static string Get(string url)
        {
            return Get(url, Encoding.Default);
        }
    }
}
