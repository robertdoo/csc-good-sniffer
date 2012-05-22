using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    public class KeywordQueueChangeEventArgs : EventArgs
    {
        public Dictionary<String, int> KeywordQueue { get; set; }
    }
}
