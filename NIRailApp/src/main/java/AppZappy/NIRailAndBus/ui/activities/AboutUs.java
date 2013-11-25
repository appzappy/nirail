package AppZappy.NIRailAndBus.ui.activities;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.ApplicationInformation;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends Activity
{
	TextView application_details = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_aboutus);
		
		application_details = (TextView) findViewById(R.id.application_details);
		String application_value = "Application Version: ";
		try
		{
			application_value += ApplicationInformation.getVersionNumber();
		}
		catch(Exception e)
		{
			application_value += "Error";
		}
		finally
		{
			application_details.setText(application_value);
		}
	}

	public static void openNew(Context context)
	{
		context.startActivity(new Intent(context, AboutUs.class));
	}
	
	public void onLogoClick(View view)
	{
		// nothing?
	}
	
	public void onEmailButtonClick(View view)
	{
		UIUtils.displayEmailScreen(this);
	}
	
	public void onBugButtonClick(View view)
	{
		UIUtils.gotoAppzappyBugs(this);
	}
	
	public void onTwitterButtonClick(View view)
	{
		UIUtils.gotoTwitter(this);
	}
	
	public void onFacebookButtonClick(View view)
	{
		UIUtils.gotoFacebookPage(this);
	}
	
	public void closeAboutUs(View view)
	{
		this.finish();
	}
	
	public void onWebsiteButtonClick(View view)
	{
		UIUtils.gotoAppzappycouk(this);
	}
}
