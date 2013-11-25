using System;
namespace TimetableDownloader
{
    interface ITimetableSource
    {
        Timetable BuildTimetable(ITransportInterface data);
    }
}
