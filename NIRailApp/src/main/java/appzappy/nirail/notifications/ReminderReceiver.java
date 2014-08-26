package appzappy.nirail.notifications;

import java.io.IOException;

import appzappy.nirail.R;
import appzappy.nirail.ui.activities.RouteWindow;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class ReminderReceiver extends BroadcastReceiver 
{
	// TODO improve text
	public static final int HELLO_ID = 2;
	public static final String BUNDLE_NAME = "journey_data";
	public static final String BUNDLE_ROUTE_ID = "route_id";
	public static final String BUNDLE_START_POSITION = "start_position";
	public static final String BUNDLE_END_POSITION = "end_position";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Long time = Long.parseLong(intent.getStringExtra("Time"));
		long when = System.currentTimeMillis();         // notification time
		int timer = (int) (((time - when) / 60 )/ 1000);
		int icon = R.drawable.icon;        // icon from resources

		CharSequence tickerText = "Train will leave " + intent.getStringExtra("Station") + " in " + timer + " minutes.";      // expanded message text             // ticker-text
		CharSequence contentTitle = "NI Rail - " + intent.getStringExtra("Destination");  // expanded message title
		CharSequence contentText = "Leaves " + intent.getStringExtra("Station") + " in " + timer + " minutes.";      // expanded message text

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
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		//notification.flags |= Notification.FLAG_INSISTENT;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);		
		mNotificationManager.notify(HELLO_ID, notification);
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mMediaPlayer = new MediaPlayer();

		try {
			mMediaPlayer.setDataSource(context, alert);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}

		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    mMediaPlayer.setLooping(false);
		    try {
				mMediaPlayer.prepare();
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
		    mMediaPlayer.start();
		}
	}
	
	@Override
	public String toString()
	{
		return "ReminderReceiver";
	}
}