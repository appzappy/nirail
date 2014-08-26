package appzappy.nirail.util.timing;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Format a time stamp
 */
public class TimestampFormatter
{
	private static TimestampFormatter _singleton = null;
	
	private static Object _lock = new Object();
	
	/**
	 * Get the singleton instance
	 * @return Singleton instance
	 */
	public static TimestampFormatter getInstance()
	{
		if (_singleton == null)
		{
			synchronized (_lock)
			{
				if (_singleton == null)
					_singleton = new TimestampFormatter();
			}
		}
		return _singleton;
	}
	private TimestampFormatter()	{}
	
	/**
	 * Get the current time formatted in a file safe format
	 * @return Time in "yyyy-MM-dd--HH-mm-ss" format
	 * @see http://www.java-tips.org/java-se-tips/java.util/how-to-get-current-date-time.html
	 */
	public String getTimestamp()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	
	@Override
	public String toString()
	{
		return "TimestampFormatter";
	}
}
