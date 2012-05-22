namespace EWorm.FormTester
{
    partial class Form1
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.txtKeyword = new System.Windows.Forms.TextBox();
            this.btnAddKeyword = new System.Windows.Forms.Button();
            this.txtJobQueue = new System.Windows.Forms.TextBox();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.txtKeywordQueue = new System.Windows.Forms.TextBox();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            // 
            // txtKeyword
            // 
            this.txtKeyword.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtKeyword.Location = new System.Drawing.Point(12, 12);
            this.txtKeyword.Name = "txtKeyword";
            this.txtKeyword.Size = new System.Drawing.Size(821, 21);
            this.txtKeyword.TabIndex = 0;
            // 
            // btnAddKeyword
            // 
            this.btnAddKeyword.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnAddKeyword.Location = new System.Drawing.Point(854, 10);
            this.btnAddKeyword.Name = "btnAddKeyword";
            this.btnAddKeyword.Size = new System.Drawing.Size(102, 23);
            this.btnAddKeyword.TabIndex = 1;
            this.btnAddKeyword.Text = "添加关键字";
            this.btnAddKeyword.UseVisualStyleBackColor = true;
            this.btnAddKeyword.Click += new System.EventHandler(this.btnAddKeyword_Click);
            // 
            // txtJobQueue
            // 
            this.txtJobQueue.Dock = System.Windows.Forms.DockStyle.Fill;
            this.txtJobQueue.Location = new System.Drawing.Point(0, 0);
            this.txtJobQueue.Multiline = true;
            this.txtJobQueue.Name = "txtJobQueue";
            this.txtJobQueue.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtJobQueue.Size = new System.Drawing.Size(695, 660);
            this.txtJobQueue.TabIndex = 2;
            // 
            // splitContainer1
            // 
            this.splitContainer1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.splitContainer1.Location = new System.Drawing.Point(12, 39);
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.Controls.Add(this.txtJobQueue);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.txtKeywordQueue);
            this.splitContainer1.Size = new System.Drawing.Size(944, 660);
            this.splitContainer1.SplitterDistance = 695;
            this.splitContainer1.TabIndex = 3;
            // 
            // txtKeywordQueue
            // 
            this.txtKeywordQueue.Dock = System.Windows.Forms.DockStyle.Fill;
            this.txtKeywordQueue.Location = new System.Drawing.Point(0, 0);
            this.txtKeywordQueue.Multiline = true;
            this.txtKeywordQueue.Name = "txtKeywordQueue";
            this.txtKeywordQueue.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtKeywordQueue.Size = new System.Drawing.Size(245, 660);
            this.txtKeywordQueue.TabIndex = 3;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(966, 711);
            this.Controls.Add(this.splitContainer1);
            this.Controls.Add(this.btnAddKeyword);
            this.Controls.Add(this.txtKeyword);
            this.Name = "Form1";
            this.Text = "EWorm测试";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.Panel2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox txtKeyword;
        private System.Windows.Forms.Button btnAddKeyword;
        private System.Windows.Forms.TextBox txtJobQueue;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.TextBox txtKeywordQueue;
    }
}

