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
    public partial class Form1 : Form
    {
        private ITransportInterface data = null;

        public Form1()
        {
            InitializeComponent();

            setTransportType(TransportType.Train);
        }


        private void train_radioButton_CheckedChanged(object sender, EventArgs e)
        {
            if (train_radioButton.Checked)
            {
                setTransportType(TransportType.Train);
            }
        }

        private void bus_radioButton_CheckedChanged(object sender, EventArgs e)
        {
            if (bus_radioButton.Checked)
            {
                setTransportType(TransportType.Metro);
            }
        }

        private void setTransportType(TransportType type)
        {
            TransportFactory factory = new TransportFactory();
            data = factory.TransportSource(type);


            int length = 80;

            sql_locationtextBox.Text = ShortenString(data.DefaultDBLocation.FullName, length);
            network_textBox.Text = ShortenString(data.DefaultNetworkLocation.FullName, length);
        }

        private string ShortenString(string input, int maxLength)
        {
            int startIndex = input.Length - maxLength;
            if (startIndex < 0)
                return input;
            return "..." + input.Substring(startIndex);

        }

        private void auto_load_button_Click(object sender, EventArgs e)
        {
            auto_load_button.Enabled = false;

            Console.WriteLine("###################################");
            Console.WriteLine("Loading and saving timetable data to SQLiteDB");

            // clear old data
            ObjectsToSQL.ClearTimetable(data.DefaultDBLocation);
            ObjectsToSQL.ClearNetwork(data.DefaultDBLocation);
            Console.WriteLine("DB emptied");

            // load in network data
            System.Xml.Linq.XElement networkXML = System.Xml.Linq.XElement.Load(data.DefaultNetworkLocation.FullName);
            Network network = new Network(networkXML, data.TimetableName);
            // save network data
            ObjectsToSQL.LoadNetwork(data.DefaultDBLocation, network);
            Console.WriteLine("Network loaded");

            // load in timetable data
            Console.WriteLine("Downloading timetable. {0} pages", data.TimetableUrls.Count);
            ITimetableSource source = new TimetableSource();
            Timetable timetable = source.BuildTimetable(data);
            ObjectsToSQL.LoadTimetable(data.DefaultDBLocation, timetable);
            Console.WriteLine("Timetable loaded");

            // load network dependants
            ObjectsToSQL.LoadNetworkDependants(data.DefaultDBLocation, network, data);
            Console.WriteLine("Dependants loaded");

            ZipWrapper zip = new ZipWrapper();
            zip.ZipFile(data.DefaultDBLocation, data.DefaultZipLocation);
            Console.WriteLine("DB Zipped");

            auto_load_button.Enabled = true;
        }

        private void network_template_button_Click(object sender, EventArgs e)
        {
            Console.WriteLine("###################################");
            Console.WriteLine("Generating network template xml");

            // download timetable
            Console.WriteLine("Downloading timetable pages: {0} pages", data.TimetableUrls.Count);
            ITimetableSource source = new TimetableSource();
            Timetable timetable = source.BuildTimetable(data);
            Console.WriteLine("Timetable loaded");

            // extract unique station locations
            string[] stops = timetable.AllStops;

            Network network = new Network(data.Transport_Type.ToString());
            foreach (String s in stops)
            {
                network.AddLocation(new Location(s, new string[] { s }, "0", "0", data.Transport_Type));
            }
            Console.WriteLine("{0} unique stops", stops.Length);

            // generate XML
            XElement xml = network.ToXML();
            Console.WriteLine("XML Generated");

            // save
            SaveFileDialog saveFileDialog1 = new SaveFileDialog();
            saveFileDialog1.FileName = data.DefaultNetworkLocation.Name;
            saveFileDialog1.Filter = "XML file (*.xml)|*.xml|All Files (*.*)|*.*";
            saveFileDialog1.FilterIndex = 1;

            if (saveFileDialog1.ShowDialog() == DialogResult.OK)
            {
                if (File.Exists(saveFileDialog1.FileName))
                {
                    File.Delete(saveFileDialog1.FileName);
                }
                xml.Save(saveFileDialog1.FileName);
                Console.WriteLine("Saved");
            }
            else
            {
                Console.WriteLine("Canceled");
            }

        }

        private void linking_button_Click(object sender, EventArgs e)
        {
            Console.WriteLine("###################################");
            Console.WriteLine("Generating Linking Locations C# Code");

            // download timetable
            Console.WriteLine("Downloading timetable pages: {0} pages", data.TimetableUrls.Count);
            ITimetableSource source = new TimetableSource();
            Timetable timetable = source.BuildTimetable(data);
            Console.WriteLine("Timetable loaded");

            // extract unique station links
            List<Pair<string, string>> linkingLocas = timetable.LinkingLocations;


            Console.WriteLine("{0} unique links", linkingLocas.Count);

            // generate XML

            StringBuilder builder = new StringBuilder();
            //l.Add(getTL("Poyntzpass", "Newry", items));
            foreach (Pair<string, string> pair_item in linkingLocas)
            {
                builder.Append("l.Add(getTL(");

                builder.Append("\"");
                builder.Append(pair_item.value1);
                builder.Append("\"");

                builder.Append(", ");

                builder.Append("\"");
                builder.Append(pair_item.value2);
                builder.Append("\"");

                builder.Append(", items));");
                builder.AppendLine();
            }


            // save
            SaveFileDialog saveFileDialog1 = new SaveFileDialog();
            saveFileDialog1.FileName = "generated_code.txt";
            saveFileDialog1.Filter = "txt file (*.txt)|*.txt|All Files (*.*)|*.*";
            saveFileDialog1.FilterIndex = 1;

            if (saveFileDialog1.ShowDialog() == DialogResult.OK)
            {
                if (File.Exists(saveFileDialog1.FileName))
                {
                    File.Delete(saveFileDialog1.FileName);
                }
                using (StreamWriter outfile = new StreamWriter(saveFileDialog1.FileName))
                {
                    outfile.Write(builder.ToString());
                }
                Console.WriteLine("Saved");
            }
            else
            {
                Console.WriteLine("Canceled");
            }
        }

        private void location_helper_button_Click(object sender, EventArgs e)
        {
            new LocationKMLFinder().Show();
        }

        
    }
}

