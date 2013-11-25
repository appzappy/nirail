using System;
namespace TimetableDownloader
{
    interface ITimetableExtractor
    {
        Service ExtractTimetable(string rawData);
    }
}
