package AppZappy.NIRailAndBus.util;

import android.os.Build;

/**
 * AndroidVersion contains information on the Android SDK version numbers
 * @author Kurru
 *
 */
public class AndroidVersion
{
	private AndroidVersion(){}
	
	/**
	 * Android - Cupcake
	 */
	public static final int ANDROID_1_5 = 3;
	/**
	 * Android - Donut
	 */
	public static final int ANDROID_1_6 = 4;
	/**
	 * Android - Eclair
	 */
	public static final int ANDROID_2_1 = 7;
	/**
	 * Android - Froyo
	 */
	public static final int ANDROID_2_2 = 8;
	
	/**
	 * Android - Gingerbread
	 */
	public static final int ANDROID_2_3__0_2 = 9;
	
	/**
	 * Android - Gingerbread MR1
	 */
	public static final int ANDROID_2_3__3_4 = 10;
	
	/**
	 * Android - Honeycomb
	 */
	public static final int ANDROID_3_0 = 11;

	/**
	 * Android - Honeycomb MR1
	 */
	public static final int ANDROID_3_1 = 12;
	
	/**
	 * Android - Honeycomb MR2
	 */
	public static final int ANDROID_3_2 = 13;
	
	
	/**
	 * Is the current Android device within valid version numbers
	 * @param requirement The miniumum SDK value
	 * @return If valid, true; otherwise false
	 */
	public static boolean isValid(int requirement)
	{
		return Build.VERSION.SDK_INT >= requirement;
	}
	
	/**
	 * Is the current Android device within valid version numbers
	 * @param minimum The minimum SDK value
	 * @param maximum The maximum SDK value
	 * @return If valid, true; otherwise false
	 */
	public static boolean isValid(int minimum, int maximum)
	{
		final int sdk = Build.VERSION.SDK_INT;
		final boolean res = sdk >= minimum && sdk <= maximum;
		return res;
	}
	
	/**
	 * Get the SDK version number of the current device
	 * @return Version value of Android. i.e Android 2.1 = 7 Android 2.2 = 8 
	 */
	public static int getSDKNumber()
	{
		return Build.VERSION.SDK_INT;
	}

}
