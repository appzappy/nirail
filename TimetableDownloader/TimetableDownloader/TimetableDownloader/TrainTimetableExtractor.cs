using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using HtmlAgilityPack;

namespace TimetableDownloader
{
    class TrainTimetableExtractor : ITimetableExtractor
    {
        public Service ExtractTimetable(string rawData)
        {
            HtmlDocument doc = new HtmlDocument();
            doc.LoadHtml(rawData);


            HtmlNode serviceNameNode = doc.DocumentNode.SelectSingleNode("//*[@id='top']/h2").FirstChild;
            string serviceName = serviceNameNode.InnerText;

            HtmlNode routeSummaryNode = doc.DocumentNode.SelectSingleNode("//*[@id='rt-timetable']/div[1]/h4/p").FirstChild;
            string summary = routeSummaryNode.InnerText;

            string direction = "Inbound";
            HtmlNode directionNode = doc.DocumentNode.SelectSingleNode("//*[@id='rt-timetable']/div[1]/span/a");
            if (directionNode != null)
            {
                HtmlAttribute attri = directionNode.Attributes["href"];
                if (attri != null)
                {
                    string directionRaw = attri.Value;
                    direction = directionRaw.Contains("Inbound") ? "Inbound" : "Outbound";
                }
            }


            Service service = new Service(serviceName, summary, direction);
            service.Type = TransportType.Train;


            HtmlNodeCollection routeNodes = doc.DocumentNode.SelectNodes("//table[@summary='layout table']");


            // Find the starting and end dates for this service
            foreach (HtmlNode table in routeNodes)
            {
                if (table.InnerHtml.Contains("Regular operator:"))
                {
                    HtmlNodeCollection rowCollection = table.SelectNodes(".//tr");
                    foreach (HtmlNode singleRow in rowCollection)
                    {
                        if (singleRow.InnerHtml.Contains("Table valid from:"))
                        {
                            // start date
                            HtmlNode textData = singleRow.SelectSingleNode(".//td/div");
                            service.StartDate = DateTimeFromString(textData.InnerText.Trim());
                        }
                        if (singleRow.InnerHtml.Contains("Table valid until:"))
                        {
                            // end date
                            HtmlNode textData = singleRow.SelectSingleNode(".//td/div");
                            service.EndDate = DateTimeFromString(textData.InnerText.Trim());
                        }
                    }
                    break;
                }
            }
            
            
            // check each of the tables in the page for the timetable data
            foreach (HtmlNode table in routeNodes) // for each table in the page
            {
                HtmlNodeCollection rows = table.SelectNodes("tr");

                int ServiceRow = FindRow(rows, "Service:");
                int DaysOfOperationRow = FindRow(rows, "Days of operation:");
                int CallingPointsRow = FindRow(rows, "Calling points:");
                int StartDateRow = FindRow(rows, "Start date:");
                int EndDateRow = FindRow(rows, "End date:");

                if (ServiceRow == -1 || DaysOfOperationRow == -1 || CallingPointsRow == -1)
                {
                    continue; // invalid table
                }

                HtmlNodeCollection serviceCells = rows[ServiceRow].SelectNodes("td");

                int numberRoutes = serviceCells.Count;
                Route[] routes = new Route[numberRoutes];


                // initialize the route classes
                for (int i = 0; i < numberRoutes; i++)
                {
                    string serviceNameInTable = Cleaner(serviceCells[i].InnerText);

                    Route r = new Route(serviceNameInTable);
                    routes[i] = r;
                }

                // set start date
                if (StartDateRow >= 0)
                {
                    HtmlNodeCollection startDateCells = rows[StartDateRow].SelectNodes("td");
                    for (int i = 0; i < numberRoutes; i++)
                    {
                        string startDate = Cleaner(startDateCells[i].InnerText);

                        if (startDate == "...") { } // no value
                        else
                        {
                            routes[i].StartDate = DateTimeFromString(startDate);
                        }
                    }
                }

                // set end date
                if (EndDateRow >= 0)
                {
                    HtmlNodeCollection endDateCells = rows[EndDateRow].SelectNodes("td");
                    for (int i = 0; i < numberRoutes; i++)
                    {
                        string endDate = Cleaner(endDateCells[i].InnerText);

                        if (endDate == "...") { } // no value
                        else
                        {
                            routes[i].EndDate = DateTimeFromString(endDate);
                        }
                    }
                }

                // set days of operation
                HtmlNodeCollection daysOperationCells = rows[DaysOfOperationRow].SelectNodes("td");
                for (int i = 0; i < numberRoutes; i++)
                {
                    string operationTime = Cleaner(daysOperationCells[i].InnerText);

                    if (operationTime == "M-F") // monday to friday
                    {
                        routes[i].setRunning(DayOfWeek.Monday, true);
                        routes[i].setRunning(DayOfWeek.Tuesday, true);
                        routes[i].setRunning(DayOfWeek.Wednesday, true);
                        routes[i].setRunning(DayOfWeek.Thursday, true);
                        routes[i].setRunning(DayOfWeek.Friday, true);
                    }
                    else if (operationTime == "S") // saturday
                    {
                        routes[i].setRunning(DayOfWeek.Saturday, true);
                    }
                    else if (operationTime == "Su") // sunday
                    {
                        routes[i].setRunning(DayOfWeek.Sunday, true);
                    }
                }


                // add in the timetable data now
                for (int i = CallingPointsRow + 1; i < rows.Count; i++) // get the timetable data
                {
                    HtmlNode timetableRow = rows[i];
                    string stationName = timetableRow.SelectSingleNode("th").InnerText;

                    HtmlNodeCollection timetableData = timetableRow.SelectNodes("td");

                    for (int j = 0; j < numberRoutes; j++)
                    {
                        if (timetableData[j].Attributes["align"].Value == "middle") // the values aligned to middle are empty. ie no stop
                            continue;

                        string timeValue = Cleaner(timetableData[j].InnerText);

                        bool pickUpOnly = timeValue.EndsWith("U");
                        bool dropOffOnly = timeValue.EndsWith("D");

                        bool pickup = true;
                        bool dropoff = true;

                        if (pickUpOnly)
                            dropoff = false;
                        if (dropOffOnly)
                            pickup = false;

                        timeValue = timeValue.TrimEnd(new char[] { 'D', 'U' });
                        timeValue = Cleaner(timeValue);

                        routes[j].AddStop(stationName, timeValue, pickup, dropoff);
                    }
                }


                foreach (Route r in routes)
                {
                    service.AddRoute(r);
                }
            }

            return service;
        }


        string Cleaner(string msg)
        {
            return msg.Replace("&nbsp;", " ").Trim();
        }

        int FindRow(HtmlNodeCollection rows, string content)
        {
            for (int i = 0; i < rows.Count; i++)
            {
                if (rows[i].FirstChild.InnerText == content)
                    return i;
            }
            return -1;
        }

        DateTime DateTimeFromString(string date)
        {
            // from format dd/mm/yy
            if (date == "99/99/99")
                return DateTime.MaxValue;

            int day = int.Parse(date.Substring(0, 2));
            int month = int.Parse(date.Substring(3, 2));
            int year = int.Parse(date.Substring(6, 2)) + 2000;
            return new DateTime(year, month, day);
        }

    }
}
