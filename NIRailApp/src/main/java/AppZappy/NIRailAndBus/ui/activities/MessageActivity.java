package AppZappy.NIRailAndBus.ui.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.C;
import AppZappy.NIRailAndBus.util.L;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

public class MessageActivity extends Activity
{

  public static void start(Context context) {
    final Intent intent = new Intent(context, MessageActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(intent);
  }
  
	private static Activity _activeActivity = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_splashscreen);
		
		_activeActivity = this;
		Settings.initialise(_activeActivity);
	    if (Settings.getReadMessage() != getMessageNumber(this))
	    {
			showMessage(this, getMessageNumber(this));
		}
		else
		{
			UIUtils.goHome(this);
			finish();
		}
	}
	
	private static int getMessageNumber(final Activity activity) 
	{
	  return Integer.parseInt(activity.getResources().getString(R.string.message_from_devs_number));
	}

	static void showMessage(final Activity activity, final int messageNumber) 
	{ 
  	    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
          .setTitle(R.string.message_title_from_devs)
          .setMessage(R.string.message_from_devs)
          .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              // TODO Auto-generated method stub
              accept(messageNumber, activity);
            }
          });
	    builder.create().show();
	}

	private static void accept(int messageNum, Activity activity)
	{
        Settings.setReadMessage(messageNum);
		UIUtils.goHome(activity);
		activity.finish();
	}
}
