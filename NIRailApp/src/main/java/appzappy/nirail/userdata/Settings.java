package appzappy.nirail.userdata;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import appzappy.nirail.R;
import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.geolocation.Geolocation;
import appzappy.nirail.mode.ProgramMode;
import appzappy.nirail.ui.theme.NIRailTheme;
import appzappy.nirail.util.L;

public class Settings
{
	public static void initialise(Context context)
	{
		if (preferences == null) // settings aren't set
		{
			Resources resources = context.getResources();
			EULA_ACCEPTED = resources.getString(R.string.preferences_eula);
			READ_MESSAGE_FROM_DEVS = resources.getString(R.string.preferences_read_message);
			DISTANCE_UNITS = resources.getString(R.string.preferences_distance);
			DISTANCE_UNITS_DEFAULT = resources.getString(R.string.preferences_distance_default);
			DISPLAY_FAVOURITES = resources.getString(R.string.preferences_display_favourites);
			DISPLAY_UPCOMING = resources.getString(R.string.preferences_display_upcoming);
			
			_24TIME = resources.getString(R.string.preferences_time24);
			_24TIME_DEFAULT = Boolean.parseBoolean(resources.getString(R.string.preferences_time24_default));
		
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
			
			ROUTE_EXPIRY = resources.getString(R.string.preferences_enable_expiry_dates);
		}
	}
	
	

	private static Geolocation northernIrelandCenter = new Geolocation(54.638876, -6.448975, 1);

	public static Geolocation getNorthernIrelandCenter()
	{
		return northernIrelandCenter;
	}

	private static Geolocation cityHall = new Geolocation(54.596543, -5.930074, 1);

	public static Geolocation getCityHall()
	{
		return cityHall;
	}

	
	public static boolean isUsingAds()
	{
		// TODO set this to true again
		return true;
	}

	private static SharedPreferences preferences = null;

	/*
	 * EULA PREFERENCES
	 */
	private static String EULA_ACCEPTED;

	public static boolean isEulaAccepted()
	{
		return preferences.getBoolean(EULA_ACCEPTED, false);
	}

	public static void setEulaAccepted(boolean value)
	{
		preferences.edit().putBoolean(EULA_ACCEPTED, value).commit();
	}
	
	/*
	 * MESSAGE FROM DEVS
	 */

	private static String READ_MESSAGE_FROM_DEVS;
	
	public static int getReadMessage() 
	{
	    return preferences.getInt(READ_MESSAGE_FROM_DEVS, 0);
	}
	
	public static void setReadMessage(int messageNumber)
	{
	    preferences.edit().putInt(READ_MESSAGE_FROM_DEVS, messageNumber).commit();
	}
	
	/*
	 * DISTANCE UNITS
	 */

	public static String DISTANCE_UNITS;
	private static String DISTANCE_UNITS_DEFAULT;
	
	public static String getDistanceUnits()
	{
		return preferences.getString(DISTANCE_UNITS, DISTANCE_UNITS_DEFAULT);
	}
	
	public static boolean isDistanceKM()
	{
		return getDistanceUnits().equals("km");
	}
	
	public static void setDistanceUnits(boolean km)
	{
		String value = km ? "km" : "miles";
		preferences.edit().putString(DISTANCE_UNITS, value).commit();
	}
	
	
	
	/*
	 * TIME AM/PM 24
	 */
	
	public static String _24TIME;
	private static boolean _24TIME_DEFAULT;
	public static boolean is24Time()
	{
		return preferences.getBoolean(_24TIME, _24TIME_DEFAULT);
	}
	public static boolean isAMPMTime()
	{
		return !is24Time();
	}
	public static void set24Time(boolean time24)
	{
		preferences.edit().putBoolean(_24TIME, time24).commit();
	}
	public static String get_24_OR_AMPM_TimeValue()
	{
		return is24Time() ? "24 Hour" : "AM/PM";
	}
	
	/*
	 * Server Version Number
	 */
	
	public static final String SERVER_VERSION_PREFERENCE = "server_version";
	
	public static final int SERVER_VERSION_DEFAULT = -1;
	
	public static int getServerCode ()
	{
		return preferences.getInt(SERVER_VERSION_PREFERENCE, SERVER_VERSION_DEFAULT);
	}
	public static void setServerCode(int servercode)
	{
		preferences.edit().putInt(SERVER_VERSION_PREFERENCE, servercode).commit();
	}
	
	
	/*
	 * Last Update Prompt Date
	 */
	
	public static final String LAST_UPDATE_PROMPT = "last_update_prompt";
	
	public static int LAST_UPDATE_PROMPT_DEFAULT = -1;
	
	public static int getLastUpdatePrompt()
	{
		return preferences.getInt(LAST_UPDATE_PROMPT, LAST_UPDATE_PROMPT_DEFAULT);
	}
	
	public static void setLastUpdatePrompt(int dayNumber)
	{
		preferences.edit().putInt(LAST_UPDATE_PROMPT, dayNumber).commit();
	}
	
	
	public static final String DATABASE_VERSION_NUMBER = "database_version_number";
	public static int getDatabaseVersionNumber()
	{
		return preferences.getInt(DATABASE_VERSION_NUMBER, -1);
	}
	
	public static void setDatabaseVersionNumber(int number)
	{
		preferences.edit().putInt(DATABASE_VERSION_NUMBER, number).commit();
	}
	
	
	public static final String PROGRAM_THEME = "program_theme";
	public static NIRailTheme getProgramTheme()
	{
		String text = preferences.getString(PROGRAM_THEME, NIRailTheme.Dark.toString());
		return NIRailTheme.valueOf(text);
	}
	public static void setProgramTheme(NIRailTheme theme)
	{
		preferences.edit().putString(PROGRAM_THEME, theme.toString()).commit();
	}
	public static int getProgramThemeID()
	{
		switch(getProgramTheme())
		{
			case Dark:
				return R.style.DarkTheme;
			case Light:
				return R.style.LightTheme;
			case Blue:
				return R.style.BlueTheme;
			case Black:
				return R.style.BlackTheme;
			case Orange:
				return R.style.OrangeTheme;
			case Green:
				return R.style.GreenTheme;
			case OtherGreen:
				return R.style.OtherGreenTheme;
			case Pink:
				return R.style.PinkTheme;
			case OtherPink:
				return R.style.OtherPinkTheme;
			case Purple:
				return R.style.PurpleTheme;
			case Yellow:
				return R.style.YellowTheme;
			case OtherYellow:
				return R.style.OtherYellowTheme;
			default:
				throw new RuntimeException("Invalid program theme selected");
		}
	}
	public static boolean isLightTheme()
	{
		return getProgramTheme() != NIRailTheme.Dark;	
	}
	
	
	public static final String DISPLAYED_DELAY_GUID = "displayed_delay_guid_";
	
	public static void setDisplayedDelayGUID(long guid)
	{
		String type = ProgramMode.singleton().getMode().toString();
		L.d("Setting Type GUID: " + type + " " + guid);
		preferences.edit().putLong(DISPLAYED_DELAY_GUID+type, guid).commit();
	}
	public static long getDisplayedDelayGUID()
	{
		String type = ProgramMode.singleton().getMode().toString();
		long value = preferences.getLong(DISPLAYED_DELAY_GUID+type, 0);
		L.d("Getting Type GUID: " + type + " " + value);
		return value;
	}
	
	
	private static String DISPLAY_UPCOMING = null;
	
	public static boolean isDisplaying_Upcoming()
	{
		return preferences.getBoolean(DISPLAY_UPCOMING, true);
	}
	
	private static String DISPLAY_FAVOURITES = null;

	public static boolean isDisplaying_Favourites()
	{
		return preferences.getBoolean(DISPLAY_FAVOURITES, true);
	}
	
	
	private static String ROUTE_EXPIRY = null;
	
	public static boolean isUsingExpiryDates()
	{
		return preferences.getBoolean(ROUTE_EXPIRY, true);
	}
	
	private static final String PROGRAM_MODE = "PROGROM_MODE";
	
	public static TransportType getMode()
	{
		String value = preferences.getString(PROGRAM_MODE, TransportType.Train.toString());
		try
		{
			TransportType type = TransportType.valueOf(value);
			return type;
		}
		catch (IllegalArgumentException e)
		{
			return TransportType.Train;
		}
	}
	public static void setMode(TransportType type)
	{
		preferences.edit().putString(PROGRAM_MODE, type.toString()).commit();
	}
}
