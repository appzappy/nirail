package AppZappy.NIRailAndBus.notifications;

import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.ui.activities.RouteWindow;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DistantReceiver extends BroadcastReceiver 
{
	public static final int HELLO_ID = 2;
	public static final String BUNDLE_NAME = "journey_data";
	public static final String BUNDLE_ROUTE_ID = "route_id";
	public static final String BUNDLE_START_POSITION = "start_position";
	public static final String BUNDLE_END_POSITION = "end_position";
	
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//Long time = Long.parseLong(intent.getStringExtra("Time"));
		String timeAsString = intent.getStringExtra("StringTime");
		long when = System.currentTimeMillis();         // notification time
		//int timer = (int) (((time - when) / 60 )/ 1000);
		int icon = R.drawable.icon;        // icon from resources
		boolean tomorrow = intent.getBooleanExtra("tomorrow", false);
		CharSequence tickerText;
		CharSequence contentText;
		
		if (tomorrow)
		{
			tickerText = "Train will leave " + intent.getStringExtra("Station") + " at " + timeAsString + " tomorrow."; 
			contentText = "Leaves " + intent.getStringExtra("Station") + " at " + timeAsString + " tomorrow."; 
		}
		else
		{
			tickerText = "Train will leave " + intent.getStringExtra("Station") + " at " + timeAsString + "."; 
			contentText = "Leaves " + intent.getStringExtra("Station") + " at " + timeAsString + "."; 
		}
		// expanded message text             // ticker-text
		CharSequence contentTitle = "NI Rail - " + intent.getStringExtra("Destination");  // expanded message title    // expanded message text

		Intent notificationIntent = new Intent(context, RouteWindow.class);
		Bundle b = new Bundle();
		
		b.putInt(BUNDLE_ROUTE_ID, intent.getIntExtra("route", -1));
		b.putInt(BUNDLE_START_POSITION, intent.getIntExtra("start", -1));
		b.putInt(BUNDLE_END_POSITION, intent.getIntExtra("end", -1));

		b.putBoolean("IsReminder", true);
		
		notificationIntent.putExtra(BUNDLE_NAME, b);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		// the next two lines initialize the Notification, using the configurations above
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		
		mNotificationManager.notify(HELLO_ID, notification);		
	}
	
	@Override
	public String toString()
	{
		return "CountdownReceiver";
	}
}