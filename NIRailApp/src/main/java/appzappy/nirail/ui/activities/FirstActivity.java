package appzappy.nirail.ui.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import appzappy.nirail.R;
import appzappy.nirail.userdata.Settings;
import appzappy.nirail.util.C;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class FirstActivity extends Activity
{
	private static Activity _activeActivity = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_splashscreen);
		
		_activeActivity = this;
		Settings.initialise(_activeActivity);
		if (!Settings.isEulaAccepted())
		{
			showEULA(this);
		}
		else
		{
			accept(this);
		}
	}
	
		/**
	 * Displays the EULA if necessary. This method should be called from the
	 * onCreate() method of your main Activity.
	 * 
	 * @param activity The Activity to finish if the user rejects the EULA.
	 * @return Whether the user has agreed already.
	 */
	static void showEULA(final Activity activity)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.eula_title);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.eula_accept, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				accept(activity);
			}
		});
		builder.setNegativeButton(R.string.eula_refuse, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				refuse(activity);
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface dialog)
			{
				refuse(activity);
			}
		});
		CharSequence msg = readEula(activity);
		builder.setMessage(msg);
		builder.create().show();
	}

	private static void accept(Activity activity)
	{
		Settings.setEulaAccepted(true);
		MessageActivity.start(activity);
		activity.finish();
	}
	
	private static void refuse(Activity activity)
	{
		activity.finish();
	}

	private static CharSequence readEula(Activity activity)
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.eula)));
			
			String line = in.readLine();
			StringBuilder buffer = new StringBuilder();
			
			while (line != null)
			{
				buffer.append(line).append(C.new_line());
				line = in.readLine();
			}
			
			return buffer;
		}
		catch (IOException e)
		{
			return "Failed to load EULA";
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}
	}

}
