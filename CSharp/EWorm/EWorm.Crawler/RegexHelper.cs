using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace EWorm.Crawler
{
    public static class RegexHelper
    {
        public static String RemoveHtmlTag(this String html)
        {
            Regex regex = new Regex(@"<(?<TagName>\w+?).*?>(?<Content>.*?)</\k<TagName>>");
            while (regex.Match(html).Success)
            {
                html = regex.Replace(html, "${Content}");
            }
            return html;
        }
    }
}
