using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace EWorm.Crawler
{
    public static class RegexHelper
    {
        public static String RemoveHtmlTag()
        {
            Regex regex = new Regex(@"<(?<TagName>\w+?).*?>(?<Content>.*?)</\1>");
        }
    }
}
