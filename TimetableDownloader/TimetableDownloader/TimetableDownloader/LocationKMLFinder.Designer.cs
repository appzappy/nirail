namespace TimetableDownloader
{
    partial class LocationKMLFinder
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.start_button = new System.Windows.Forms.Button();
            this.location_textBox = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.save_button = new System.Windows.Forms.Button();
            this.possible_checkedListBox = new System.Windows.Forms.CheckedListBox();
            this.next_button = new System.Windows.Forms.Button();
            this.select_all_button = new System.Windows.Forms.Button();
            this.deselect_all_button = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // start_button
            // 
            this.start_button.Location = new System.Drawing.Point(21, 24);
            this.start_button.Name = "start_button";
            this.start_button.Size = new System.Drawing.Size(75, 23);
            this.start_button.TabIndex = 0;
            this.start_button.Text = "Start";
            this.start_button.UseVisualStyleBackColor = true;
            this.start_button.Click += new System.EventHandler(this.start_button_Click);
            // 
            // location_textBox
            // 
            this.location_textBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.location_textBox.Location = new System.Drawing.Point(72, 81);
            this.location_textBox.Name = "location_textBox";
            this.location_textBox.ReadOnly = true;
            this.location_textBox.Size = new System.Drawing.Size(479, 20);
            this.location_textBox.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(18, 84);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(48, 13);
            this.label1.TabIndex = 2;
            this.label1.Text = "Location";
            // 
            // save_button
            // 
            this.save_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.save_button.Location = new System.Drawing.Point(476, 24);
            this.save_button.Name = "save_button";
            this.save_button.Size = new System.Drawing.Size(75, 23);
            this.save_button.TabIndex = 3;
            this.save_button.Text = "Save";
            this.save_button.UseVisualStyleBackColor = true;
            this.save_button.Click += new System.EventHandler(this.save_button_Click);
            // 
            // possible_checkedListBox
            // 
            this.possible_checkedListBox.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.possible_checkedListBox.FormattingEnabled = true;
            this.possible_checkedListBox.Location = new System.Drawing.Point(21, 124);
            this.possible_checkedListBox.Name = "possible_checkedListBox";
            this.possible_checkedListBox.Size = new System.Drawing.Size(530, 214);
            this.possible_checkedListBox.TabIndex = 4;
            // 
            // next_button
            // 
            this.next_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.next_button.Location = new System.Drawing.Point(476, 361);
            this.next_button.Name = "next_button";
            this.next_button.Size = new System.Drawing.Size(75, 23);
            this.next_button.TabIndex = 5;
            this.next_button.Text = "Next";
            this.next_button.UseVisualStyleBackColor = true;
            this.next_button.Click += new System.EventHandler(this.next_button_Click);
            // 
            // select_all_button
            // 
            this.select_all_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.select_all_button.Location = new System.Drawing.Point(21, 361);
            this.select_all_button.Name = "select_all_button";
            this.select_all_button.Size = new System.Drawing.Size(75, 23);
            this.select_all_button.TabIndex = 6;
            this.select_all_button.Text = "Select All";
            this.select_all_button.UseVisualStyleBackColor = true;
            this.select_all_button.Click += new System.EventHandler(this.select_all_button_Click);
            // 
            // deselect_all_button
            // 
            this.deselect_all_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.deselect_all_button.Location = new System.Drawing.Point(102, 361);
            this.deselect_all_button.Name = "deselect_all_button";
            this.deselect_all_button.Size = new System.Drawing.Size(75, 23);
            this.deselect_all_button.TabIndex = 7;
            this.deselect_all_button.Text = "Deselect All";
            this.deselect_all_button.UseVisualStyleBackColor = true;
            this.deselect_all_button.Click += new System.EventHandler(this.deselect_all_button_Click);
            // 
            // LocationKMLFinder
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(575, 416);
            this.Controls.Add(this.deselect_all_button);
            this.Controls.Add(this.select_all_button);
            this.Controls.Add(this.next_button);
            this.Controls.Add(this.possible_checkedListBox);
            this.Controls.Add(this.save_button);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.location_textBox);
            this.Controls.Add(this.start_button);
            this.Name = "LocationKMLFinder";
            this.Text = "LocationKMLFinder";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button start_button;
        private System.Windows.Forms.TextBox location_textBox;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button save_button;
        private System.Windows.Forms.CheckedListBox possible_checkedListBox;
        private System.Windows.Forms.Button next_button;
        private System.Windows.Forms.Button select_all_button;
        private System.Windows.Forms.Button deselect_all_button;
    }
}