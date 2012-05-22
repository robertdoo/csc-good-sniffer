using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace EWorm.FormTester
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            Crawler.Crawler.OnQueueChanged += Crawler_OnQueueChanged;
            Crawler.Crawler.OnKeywordQueueChanged += Crawler_OnKeywordQueueChanged;
        }

        void Crawler_OnKeywordQueueChanged(object sender, Crawler.KeywordQueueChangeEventArgs e)
        {
            if (txtKeywordQueue.InvokeRequired)
            {
                this.Invoke(new EventHandler<Crawler.KeywordQueueChangeEventArgs>(this.Crawler_OnKeywordQueueChanged), new object[] { sender, e });
            }
            else
            {
                txtKeywordQueue.Clear();
                foreach (var keyword in e.KeywordQueue)
                {
                    txtKeywordQueue.Text += String.Format("{0} ({1})", keyword.Key, keyword.Value) + Environment.NewLine;
                }
            }
        }

        void Crawler_OnQueueChanged(object sender, Crawler.JobQueueChangeEventArgs e)
        {
            if (txtJobQueue.InvokeRequired)
            {
                this.Invoke(new EventHandler<Crawler.JobQueueChangeEventArgs>(this.Crawler_OnQueueChanged), new object[] { sender, e });
            }
            else
            {
                txtJobQueue.Clear();
                foreach (var job in e.JobQueue)
                {
                    txtJobQueue.Text += job + Environment.NewLine;
                }
            }
        }
        private void Form1_Load(object sender, EventArgs e)
        {
            Crawler.Crawler.Start();
        }

        private void btnAddKeyword_Click(object sender, EventArgs e)
        {
            Crawler.Crawler.AddKeyword(txtKeyword.Text);
        }
    }
}
