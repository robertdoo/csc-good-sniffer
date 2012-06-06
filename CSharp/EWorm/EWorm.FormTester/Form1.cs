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

        private volatile bool InvokingKeywordChanged = false;
        void Crawler_OnKeywordQueueChanged(object sender, Crawler.KeywordQueueChangeEventArgs e)
        {
            if (txtKeywordQueue.InvokeRequired)
            {
                if (!InvokingKeywordChanged)
                {
                    this.Invoke(new EventHandler<Crawler.KeywordQueueChangeEventArgs>(this.Crawler_OnKeywordQueueChanged), new object[] { sender, e });
                }
            }
            else
            {
                InvokingKeywordChanged = true;
                string text = "";
                foreach (var keyword in e.KeywordQueue.OrderByDescending(x => x.Value))
                {
                    text += String.Format("{0} ({1})", keyword.Key, keyword.Value) + Environment.NewLine;
                }
                txtKeywordQueue.Text = text;
                InvokingKeywordChanged = false;
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
                string text = "[Current] " + e.CurrentJob + Environment.NewLine;
                foreach (var job in e.JobQueue)
                {
                    text += job + Environment.NewLine;
                }
                txtJobQueue.Text = text;
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
