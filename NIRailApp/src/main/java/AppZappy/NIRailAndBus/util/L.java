package AppZappy.NIRailAndBus.util;

import android.util.Log;

/**
 * Utility class for logging messages easily
 */
public class L
{
	/**
	 * Log a message in the debug channel
	 * @param log The message to log
	 */
	public static void d(String log)
	{
		if (ApplicationInformation.isDevelopmentBuild())
			Log.d("appzappy", log);
	}
	
	private L(){}
}
