using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

using System.Threading;

namespace TimetableDownloader
{
    class WebDownloader
    {

        public static int counter = 1;

        public Dictionary<string, string> downloadWebpagesConcurrently(List<string> urls)
        {
            counter = 1;
            List<SinglePage> pages = new List<SinglePage>();
            foreach (string url in urls)
            {
                SinglePage page = new SinglePage(url);
                pages.Add(page);

                Thread thread = new Thread(new ThreadStart(page.Download));
                thread.Start();
            }

            Dictionary<string, string> output = new Dictionary<string, string>();
            for (int i = 0; i < pages.Count; i++)
            {
                SinglePage p = pages[i];

                while (p.Content == null)
                {
                    Thread.Sleep(1000);
                }

                output.Add(p.URL, p.Content);
            }

            return output;
            
        }

        private class SinglePage
        {
            public String URL {get;private set;}
            public String Content { get;private set;}

            public SinglePage (string url)
            {
                URL = url;
            }

            public void Download()
            {
                DateTime startLoad = DateTime.Now;
                HttpWebRequest webRequest = (HttpWebRequest)HttpWebRequest.Create(URL);
                //WebRequestObject.UserAgent = ".NET Framework/3.5";
                webRequest.UserAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.44 Safari/534.7";
                //WebRequestObject.Referer = "http://www.aWebsite.com/";

                try
                {
                    WebResponse response = webRequest.GetResponse();
                    Stream responseStream = response.GetResponseStream();
                    StreamReader reader = new StreamReader(responseStream);

                    string content = reader.ReadToEnd();

                    reader.Close();
                    responseStream.Close();
                    response.Close();
                    this.Content=content;
                }
                catch (WebException e)
                {
                    string error = "========================";
                    error += "\n";
                    error += "Error Accessing page";
                    error += "\n";
                    error += "URL : " + URL;
                    error += "\n";
                    error += e.ToString();
                    error += "\n";

                    Console.WriteLine(error);
                    //Log.WriteError(error);

                    throw;
                }

                DateTime finishLoad = DateTime.Now;
                TimeSpan diff = finishLoad - startLoad;
                Console.WriteLine("-{2}-Downloaded Page in {0}s{1}ms", (int)diff.TotalSeconds, diff.Milliseconds, counter++);
            
            }
        }


    }
}
