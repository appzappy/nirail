namespace TimetableDownloader
{
    partial class Form1
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
            this.sql_locationtextBox = new System.Windows.Forms.TextBox();
            this.train_radioButton = new System.Windows.Forms.RadioButton();
            this.bus_radioButton = new System.Windows.Forms.RadioButton();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.network_textBox = new System.Windows.Forms.TextBox();
            this.auto_load_button = new System.Windows.Forms.Button();
            this.network_template_button = new System.Windows.Forms.Button();
            this.linking_button = new System.Windows.Forms.Button();
            this.location_helper_button = new System.Windows.Forms.Button();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // sql_locationtextBox
            // 
            this.sql_locationtextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.sql_locationtextBox.Location = new System.Drawing.Point(12, 106);
            this.sql_locationtextBox.Name = "sql_locationtextBox";
            this.sql_locationtextBox.ReadOnly = true;
            this.sql_locationtextBox.Size = new System.Drawing.Size(483, 20);
            this.sql_locationtextBox.TabIndex = 12;
            // 
            // train_radioButton
            // 
            this.train_radioButton.AutoSize = true;
            this.train_radioButton.Checked = true;
            this.train_radioButton.Location = new System.Drawing.Point(6, 19);
            this.train_radioButton.Name = "train_radioButton";
            this.train_radioButton.Size = new System.Drawing.Size(49, 17);
            this.train_radioButton.TabIndex = 14;
            this.train_radioButton.TabStop = true;
            this.train_radioButton.Text = "Train";
            this.train_radioButton.UseVisualStyleBackColor = true;
            this.train_radioButton.CheckedChanged += new System.EventHandler(this.train_radioButton_CheckedChanged);
            // 
            // bus_radioButton
            // 
            this.bus_radioButton.AutoSize = true;
            this.bus_radioButton.Location = new System.Drawing.Point(124, 19);
            this.bus_radioButton.Name = "bus_radioButton";
            this.bus_radioButton.Size = new System.Drawing.Size(43, 17);
            this.bus_radioButton.TabIndex = 15;
            this.bus_radioButton.Text = "Bus";
            this.bus_radioButton.UseVisualStyleBackColor = true;
            this.bus_radioButton.CheckedChanged += new System.EventHandler(this.bus_radioButton_CheckedChanged);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.train_radioButton);
            this.groupBox1.Controls.Add(this.bus_radioButton);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(198, 55);
            this.groupBox1.TabIndex = 16;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Timetable Mode";
            // 
            // network_textBox
            // 
            this.network_textBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.network_textBox.Location = new System.Drawing.Point(12, 132);
            this.network_textBox.Name = "network_textBox";
            this.network_textBox.ReadOnly = true;
            this.network_textBox.Size = new System.Drawing.Size(483, 20);
            this.network_textBox.TabIndex = 19;
            // 
            // auto_load_button
            // 
            this.auto_load_button.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.auto_load_button.Location = new System.Drawing.Point(135, 159);
            this.auto_load_button.Name = "auto_load_button";
            this.auto_load_button.Size = new System.Drawing.Size(225, 29);
            this.auto_load_button.TabIndex = 21;
            this.auto_load_button.Text = "Auto Load All";
            this.auto_load_button.UseVisualStyleBackColor = true;
            this.auto_load_button.Click += new System.EventHandler(this.auto_load_button_Click);
            // 
            // network_template_button
            // 
            this.network_template_button.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.network_template_button.Location = new System.Drawing.Point(136, 193);
            this.network_template_button.Name = "network_template_button";
            this.network_template_button.Size = new System.Drawing.Size(225, 23);
            this.network_template_button.TabIndex = 22;
            this.network_template_button.Text = "Generate Network Template";
            this.network_template_button.UseVisualStyleBackColor = true;
            this.network_template_button.Click += new System.EventHandler(this.network_template_button_Click);
            // 
            // linking_button
            // 
            this.linking_button.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.linking_button.Location = new System.Drawing.Point(135, 222);
            this.linking_button.Name = "linking_button";
            this.linking_button.Size = new System.Drawing.Size(225, 23);
            this.linking_button.TabIndex = 23;
            this.linking_button.Text = "Generate Linking Locations";
            this.linking_button.UseVisualStyleBackColor = true;
            this.linking_button.Click += new System.EventHandler(this.linking_button_Click);
            // 
            // location_helper_button
            // 
            this.location_helper_button.Location = new System.Drawing.Point(379, 28);
            this.location_helper_button.Name = "location_helper_button";
            this.location_helper_button.Size = new System.Drawing.Size(116, 23);
            this.location_helper_button.TabIndex = 24;
            this.location_helper_button.Text = "Location Generator";
            this.location_helper_button.UseVisualStyleBackColor = true;
            this.location_helper_button.Click += new System.EventHandler(this.location_helper_button_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(507, 261);
            this.Controls.Add(this.location_helper_button);
            this.Controls.Add(this.linking_button);
            this.Controls.Add(this.network_template_button);
            this.Controls.Add(this.auto_load_button);
            this.Controls.Add(this.network_textBox);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.sql_locationtextBox);
            this.Name = "Form1";
            this.Text = "Timetable Downloader";
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox sql_locationtextBox;
        private System.Windows.Forms.RadioButton train_radioButton;
        private System.Windows.Forms.RadioButton bus_radioButton;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.TextBox network_textBox;
        private System.Windows.Forms.Button auto_load_button;
        private System.Windows.Forms.Button network_template_button;
        private System.Windows.Forms.Button linking_button;
        private System.Windows.Forms.Button location_helper_button;
    }
}

