package appzappy.nirail.ui.activities;

import appzappy.nirail.R;
import appzappy.nirail.data.LoadData;
import appzappy.nirail.ui.UIUtils;
import appzappy.nirail.userdata.Settings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

/**
 * 
 * @author http://www.kaloer.com/android-preferences http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum/531927#531927
 *
 */
public class PreferencesWindow extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	public static void openNew(Context context)
	{
		Intent res = new Intent(context, PreferencesWindow.class);
		context.startActivity(res);
	}


	private ListPreference distance_list;
	private ListPreference program_theme;
	private CheckBoxPreference _24hour;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		addPreferencesFromResource(R.xml.preferences);
		
		distance_list = (ListPreference) getPreferenceScreen().findPreference(Settings.DISTANCE_UNITS);
		program_theme = (ListPreference) getPreferenceScreen().findPreference(Settings.PROGRAM_THEME);
		_24hour = (CheckBoxPreference) getPreferenceScreen().findPreference(Settings._24TIME);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		if (key.equals(Settings.DISTANCE_UNITS)) {
            distance_list.setSummary(Settings.getDistanceUnits()); 
        }
		else if (key.equals(Settings.PROGRAM_THEME))
		{
			program_theme.setSummary(Settings.getProgramTheme().toString());
		}
		else if (key.equals(Settings._24TIME))
		{
			_24hour.setSummary(Settings.get_24_OR_AMPM_TimeValue());
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		LoadData.initialise(this);
		
		// set the values to the correct initial values
		distance_list.setSummary(Settings.getDistanceUnits());
		program_theme.setSummary(Settings.getProgramTheme().toString());
		_24hour.setSummary(Settings.get_24_OR_AMPM_TimeValue());
		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) // && event.getRepeatCount() == 0
		{
			UIUtils.goHome(this);
		}

		return super.onKeyDown(keyCode, event);
	}
}