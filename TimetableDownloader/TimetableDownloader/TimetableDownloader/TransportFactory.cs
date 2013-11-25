using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    class TransportFactory
    {
        public ITransportInterface TransportSource(TransportType type)
        {
            switch (type)
            {
                case TransportType.Metro:
                    return new MetroSource();
                case TransportType.Train:
                    return new TrainSource();
                default:
                    throw new NotSupportedException("TransportType: " + type.ToString());
            }
        }
    }
}
