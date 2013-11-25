using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TimetableDownloader
{
    public class Pair <A,B>
    {
        public A value1 { get; private set; }
        public B value2 { get; private set; }
        public Pair(A a, B b)
        {
            value1 = a;
            value2 = b;
        }
    }
}
