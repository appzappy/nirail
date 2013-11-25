using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

using System.Xml.Serialization;
using System.IO;

namespace TimetableDownloader
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            //LocationData.Network temp = new TimetableDownloader.LocationData.Network();
            //temp.AddLocation(new TimetableDownloader.LocationData.Location("Real Name", new string[] {"alias1", "alias2"}, 5000000, 5000000));
            //temp.ToXML().Save("LocationData.xml");
            //return;
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1());
        }

        static void SaveAsXMLFormat(object objGraph, string filename)
        {
            XmlSerializer xmlFormat = new XmlSerializer(
                typeof(Timetable),
                new Type[] { 
                    typeof(Route), 
                    typeof(Service),
                    typeof(Stop) 
                });
            using (Stream fStream = new FileStream(filename, FileMode.Create, FileAccess.Write, FileShare.None))
            {
                xmlFormat.Serialize(fStream, objGraph);
            }
            Console.WriteLine("Saved in XML format");
        }
    }
}
