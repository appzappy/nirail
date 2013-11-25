using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Xml.Linq;

namespace TimetableDownloader
{
    public partial class LocationKMLFinder : Form
    {
        FileInfo location_xml = null;
        FileInfo kml_file = null;

        Network network = null;
        Dictionary<string, List<string>> values = null;

        int index = 0;

        public LocationKMLFinder()
        {
            InitializeComponent();
        }

        private void start_button_Click(object sender, EventArgs e)
        {
            index = 0;
            OpenFileDialog open_location = new OpenFileDialog();
            open_location.AutoUpgradeEnabled = true;
            open_location.Title = "Select Location.xml";
            open_location.Filter = "xml files (*.xml)|*.xml|All files (*.*)|*.*";
            open_location.FileName = "MetroNetworkData.xml";

            if (open_location.ShowDialog() == DialogResult.OK)
            {
                location_xml = new FileInfo(open_location.FileName);
            }
            else
            {
                return;
            }


            OpenFileDialog open_kml = new OpenFileDialog();
            open_kml.AutoUpgradeEnabled = true;
            open_kml.Title = "Select KML File";
            open_kml.Filter = "kml files (*.kml)|*.kml|All files (*.*)|*.*";
            open_kml.FileName = "stops.kml";

            if (open_kml.ShowDialog() == DialogResult.OK)
            {
                kml_file = new FileInfo(open_kml.FileName);
            }
            else
            {
                return;
            }

            Console.WriteLine("Loading network");

            System.Xml.Linq.XElement networkXML = System.Xml.Linq.XElement.Load(location_xml.FullName);
            network = new Network(networkXML, "metro");
            Console.WriteLine("Complete");

            Console.WriteLine("Loading KML");
            System.Xml.Linq.XElement kmlXML = System.Xml.Linq.XElement.Load(kml_file.FullName);
            Console.WriteLine("Complete");

            Console.WriteLine("Parsing KML");
            values = new Dictionary<string, List<string>>();
            XElement node = (XElement) kmlXML.FirstNode;
            foreach (XElement n in node.Elements("{http://www.opengis.net/kml/2.2}Placemark"))
            {
                String name = n.Element("{http://www.opengis.net/kml/2.2}name").Value;
                String coords = n.Element("{http://www.opengis.net/kml/2.2}Point").Element("{http://www.opengis.net/kml/2.2}coordinates").Value;

                if (!values.ContainsKey(name))
                {
                    values.Add(name, new List<string>());
                }
                values[name].Add(coords);
            }
            Console.WriteLine("Complete");
            Console.WriteLine("{0} locations loaded", values.Keys.Count);
            display_item();            

        }

        private void display_item()
        {
            if (index >= network.Locations.Count)
            {
                return;
            }

            possible_checkedListBox.SelectedIndices.Clear();
            possible_checkedListBox.Items.Clear();

            string name = network.Locations[index].RealName;
            location_textBox.Text = name;


            HashSet<string> possible = new HashSet<string>();
            foreach (string part in name.Split(new char[] { ' ', ',' }))
            {
                if (network.Locations[index].Longtitude != "0" && network.Locations[index].Latitude != "0")
                {
                    continue;
                }
                if (part.Trim() == String.Empty)
                    continue;
                if (part == "Road")
                    continue;
                if (part == "Drive")
                    continue;
                if (part == "Rd")
                    continue;
                if (part == "Ave")
                    continue;

                if (part == "Center")
                    continue;
                Console.WriteLine("name : {0} part : {1}", name, part);
                List<string> keys = new List<string>(values.Keys);
                foreach (var key in keys)
                {
                    if (key.Contains(part))
                    {
                        possible.Add(key);
                    }
                }
            }

            
            foreach (string item in possible)
            {
                possible_checkedListBox.Items.Add(item);
            }

            index++;

        }

        private void save_button_Click(object sender, EventArgs e)
        {
            SaveFileDialog saveFileDialog1 = new SaveFileDialog();
            saveFileDialog1.FileName = "LocationData.xml";
            saveFileDialog1.Filter = "XML file (*.xml)|*.xml|All Files (*.*)|*.*";
            saveFileDialog1.FilterIndex = 1;

            if (saveFileDialog1.ShowDialog() == DialogResult.OK)
            {
                if (File.Exists(saveFileDialog1.FileName))
                {
                    File.Delete(saveFileDialog1.FileName);
                }
                network.ToXML().Save(saveFileDialog1.FileName);
                Console.WriteLine("Saved");
            }
            else
            {
                Console.WriteLine("Canceled");
            }
            
        }

        private void select_all_button_Click(object sender, EventArgs e)
        {
            possible_checkedListBox.SelectedIndices.Clear();
            for (int i=0;i<possible_checkedListBox.Items.Count;i++)
            {
                possible_checkedListBox.SelectedIndices.Add(i);
            }
        }

        private void deselect_all_button_Click(object sender, EventArgs e)
        {
            possible_checkedListBox.SelectedIndices.Clear();
        }

        private void next_button_Click(object sender, EventArgs e)
        {
            int items = 0;

            double lat = 0;
            double lon = 0;

            foreach (int selected_index in possible_checkedListBox.SelectedIndices)
            {
                string name = (string) possible_checkedListBox.Items[selected_index];
                List<string> data = values[name];

                foreach (string d in data)
                {

                    string[] parts = d.Split(new char[] { ',' });
                    double lat1 = double.Parse(parts[1].Trim());
                    double lon1 = double.Parse(parts[0].Trim());

                    lat += lat1;
                    lon += lon1;
                    items++;

                }
            }

            lat /= items;
            lon /= items;

            network.Locations[index - 1].Latitude = lat.ToString();
            network.Locations[index - 1].Longtitude = lon.ToString();
            display_item();
        }
    }
}
