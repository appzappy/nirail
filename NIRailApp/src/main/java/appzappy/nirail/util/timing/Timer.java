package appzappy.nirail.util.timing;

import java.util.HashMap;

import appzappy.nirail.util.L;

/**
 * Simple timer class
 * @author Kurru
 *
 */
public class Timer
{
	private static HashMap<Integer, Long> timers = new HashMap<Integer, Long>(); 
	private static int _count = 0;
	
	/**
	 * Start a timer
	 * @return The new timer's UID
	 */
	public static int start_timer()
	{
		_count++;
		long currentTime = System.currentTimeMillis();
		timers.put(_count, currentTime);
		return _count;
	}
	
	/**
	 * Get the time difference for specified timer
	 * @param timer_id The UID for a timer
	 * @return Time difference in millis
	 */
	public static long time_difference(int timer_id)
	{
		long startTime = timers.get(timer_id);
		long currentTime = System.currentTimeMillis();
		
		long diff = currentTime - startTime; 
		return diff;
	}
	
	/**
	 * Report a timer to Logcat
	 * @param text Timer text
	 * @param timer_id Timer UID
	 */
	public static void report_timer(String text, int timer_id)
	{
		long time = time_difference(timer_id);
		L.d(text + " : " + time + "ms");
	}
	
	/**
	 * Stop a timer
	 * @param timer_id Timer UID
	 * @return The length of the timer
	 */
	public static long stop_timer(int timer_id)
	{
		long diff = time_difference(timer_id);
		timers.remove(timer_id);
		return diff;
	}
	
	private Timer(){}
}
