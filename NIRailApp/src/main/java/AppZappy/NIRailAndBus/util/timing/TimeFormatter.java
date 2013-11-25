package AppZappy.NIRailAndBus.util.timing;

import java.util.Calendar;

import AppZappy.NIRailAndBus.data.enums.DayOfWeek;
import AppZappy.NIRailAndBus.userdata.Settings;

/**
 * Class to aid in formating the times
 * @author Kurru
 *
 */
public class TimeFormatter
{
	/**
	 * Get the time in the format HHMM
	 * @param time
	 * @return
	 */
	public static String basicStringFromTime(short time)
	{
		final int hour = time/60;
		final int minute = time-hour*60;
		
		if (hour <10)
		{
			if (minute < 10)
			{
				return "0"+hour+"0"+minute;
			}
			else
			{
				return "0"+hour+""+minute;
			}
		}
		else
		{
			if (minute < 10)
			{
				return hour+"0"+minute;
			}
			else
			{
				return ""+hour+""+minute;
			}
		}
	}
	
	/**
	 * Get the time according to the users settings
	 * Either 24HH:MM or 12HH:MM AM/PM 
	 * @param time The time short
	 * @return 24HH:MM or 12HH:MM AM/PM 
	 */
	public static String formattedStringFromTime(short time)
	{
		int hour = time/60;
		int minute = time-hour*60;
		
		if (hour >= 24)
			hour -= 24;
		
		boolean am_pm = Settings.isAMPMTime(); 
		boolean pm = false;
		
		if (hour >= 12)
			pm = true;
		
		if (am_pm)
		{
			if (hour > 12)
			{
				hour -= 12;
			}
		}
		
		String out = "";
		if (hour < 10 && !am_pm)
		{
			out += "0" + hour;
			out += ":";
			if (minute < 10)
			{
				out += "0"+minute;
			}
			else
			{
				out += minute;
			}
		}
		else
		{
			out += hour;
			out += ":";
			if (minute < 10)
			{
				out += "0"+minute;
			}
			else
			{
				out += minute;
			}
		}
		
		if (am_pm)
		{
			if (pm)
				out += "pm";
			else
				out += "am";
		}
		return out;
	}
	
	/**
	 * Get the time span in hrs/mins
	 * @param time The time short
	 * @return 00hr00min
	 */
	public static String formattedStringFromLength(short time)
	{
		final int hour = time/60;
		final int minute = time-hour*60;
		
		String out = "";

		if (hour > 0)
			out = hour + "h";
		if (minute < 10 && hour > 0)
		{
			out += "0" + minute + "m";
		}
		else
		{
			out += minute + "m";
		}
		
		return out;
	}
	
	/**
	 * Convert "HHMM" string into the number of minutes since midnight
	 * @param time Time to convert "2013"
	 * @return {hours}*60+{minutes}
	 */
	public static short timeFromString(String time)
	{
		short shortTime = Short.parseShort(time);
		int minute = shortTime %100;
		int hour = shortTime /100;
		return (short) (hour*60+minute); // store the time in memory as the number of minutes from midnight
	}
	
	/**
	 * Convert a HHMM number into the number of minutes since midnight
	 * @param time Time to convert. Number "2013" means H20M13
	 * @return {hours}*60+{minutes}
	 */
	public static short timeFromPlainTime(int time)
	{
		int minute = time % 100;
		int hour = time / 100;
		return (short)(hour * 60 + minute);
	}
	
	
	/**
	 * Get time value from hours and minutes
	 * @param hour The hour value
	 * @param minutes The minute value
	 * @return {hours}*60+{minutes}
	 */
	public static short getTimeFromHourMinutes(int hour, int minutes)
	{
		return (short)(hour*60+minutes);
	}
	
	/**
	 * Returns a unique number for each day. ~1 unit between each day. but >1 at new years year change
	 * @return {year} * 366 + dayofyear
	 */
	public static int getTodaysDayNumber()
	{
		Calendar now = Calendar.getInstance();
		int dayofyear = now.get(Calendar.DAY_OF_YEAR);
		int year = now.get(Calendar.YEAR);
		return dayofyear + year * 366;
	}
	
	
	/**
	 * Number is in format YYYYMMDD
	 * @return YYYYMMDD
	 */
	public static int getTodaysDateNumber()
	{
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_MONTH);
		int month = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);
		
		int value = year*10000 + month*100 + day;
		return value;
	}
	
	/**
	 * Format the date number YYYYMMDD to DD/MM/YYYY
	 * @param dateNumber The date number YYYYMMDD
	 * @return DD/MM/YYYY
	 */
	public static String getTodayDate_Formated(int dateNumber)
	{
		int day = dateNumber % 100;
		dateNumber /= 100;
		int month = dateNumber % 100;
		dateNumber /= 100;
		int year = dateNumber;
		
		String day_s = String.format("%1$2s", day).replace(' ', '0');
		String month_s = String.format("%1$2s", month).replace(' ', '0');
		String year_s = String.format("%1$4s", year).replace(' ', '0');
		return day_s + "/" + month_s + "/" + year_s;
		//return String.format("%2.0f/%2.0f/%4.0f", day, month, year);
	}
	
	
	/**
	 * Is this date valid according to the "start date" and the "end date"
	 * @param today Today's date
	 * @param start_date The starting valid date
	 * @param end_date The ending valid date
	 * @return True if valid
	 */
	public static boolean isValidDate(DayOfWeek todayDay, int today_number, DayOfWeek targetDay, int start_date, int end_date)
	{
		// TODO move this to the route/service class
		int difference = (targetDay.ordinal() - todayDay.ordinal() + 7) % 7;
		today_number += difference;
		
		if (!Settings.isUsingExpiryDates())
		{
			return true;
		}
		if (start_date == 0 || start_date <= today_number)
		{
			if (end_date == 0 || today_number <= end_date)
			{
				return true;
			}
		}
		return false;
	}
	
	private TimeFormatter(){}

}
