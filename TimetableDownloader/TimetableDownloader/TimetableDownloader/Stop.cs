using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    public class Stop : IToXML, IID
    {
        public int ID { get; set; }
        public bool PickUp { get; private set; }
        public bool DropOff { get; private set; }
        public bool Normal { get { return PickUp && DropOff; } }
        public string Name { get; private set; }
        public string Time { get; private set; }
        public int TimeValue
        {
            get
            {
                return int.Parse(Time);
            }
        }
        public void Add24Hours()
        {
            Time = (TimeValue + 24 * 100).ToString();
        }

        public Stop(string name, string time, bool pickUp, bool dropOff)
        {
            this.Name = name;
            this.Time = time;
            this.PickUp = pickUp;
            this.DropOff = dropOff;
        }


        public System.Xml.Linq.XElement ToXML()
        {
            System.Xml.Linq.XElement ele = new System.Xml.Linq.XElement("Stop");
            ele.Add(new System.Xml.Linq.XAttribute("Name", this.Name));
            ele.Add(new System.Xml.Linq.XAttribute("Time", this.Time));
            ele.Add(new System.Xml.Linq.XAttribute("PickUp", this.PickUp));
            ele.Add(new System.Xml.Linq.XAttribute("DropOff", this.DropOff));
            ele.Add(new System.Xml.Linq.XAttribute("Normal", this.Normal));
            return ele;
        }
    }
}
