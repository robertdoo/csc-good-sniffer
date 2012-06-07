using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Crawler
{
    class KeywordQueue
    {
        private Dictionary<String, int> KeywordData { get; set; }

        public KeywordQueue()
        {
            this.KeywordData = new Dictionary<string, int>();
        }

        public void Enqueue(string keyword)
        {
            this.Enqueue(keyword, 0);
        }

        public bool Enqueue(string keyword, int initFeq)
        {
            bool firstEnqueue = false;
            if (!KeywordData.ContainsKey(keyword))
            {
                KeywordData.Add(keyword, initFeq);
                firstEnqueue = true;
            }
            KeywordData[keyword]++;
            Crawler.NotifyKeywordQueueChange(KeywordData);
            return firstEnqueue;
        }

        public string Dequeue()
        {
            if (KeywordData.Count == 0)
            {
                return null;
            }
            var maxSequence = KeywordData.Max(x => x.Value);
            var keyword = KeywordData.First(x => x.Value == maxSequence).Key;
            KeywordData[keyword] = -KeywordData[keyword];
            Crawler.NotifyKeywordQueueChange(KeywordData);
            return keyword;
        }
    }
}
