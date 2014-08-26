package appzappy.nirail.notifications;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.pathfinding.Journey;

public class TrainReminder {

	private IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	private int timeInMinutes;
	private int minsBeforeTrain;
	private String source;
	private String destination;
	private Context context = dataInterface.getAndroidContext();
	private int route_id;
	private int start_position;
	private int end_position;
	private PendingIntent alertIntent;
	private PendingIntent repeatIntent;
	private String timeFormatted;
	private boolean tomorrow;
	
	private AlarmManager alarm_manager = (AlarmManager) dataInterface.getAndroidContext().getSystemService(Context.ALARM_SERVICE);
	
	public TrainReminder()
	{
	}
	
	public void setReminder(Journey journey, int minutesBefore)
	{
		// TODO make this support mutlijourney routes
		this.timeInMinutes = journey.getStartingTime();
		this.timeFormatted = journey.getStartingTimeFormated();
		
		this.source = journey.getFirstPortion().getStart().getRealName();
		this.destination = journey.getFirstPortion().getEnd().getRealName();
		
		this.route_id = journey.getFirstPortion().getRoute().get_id();
		this.start_position = journey.getFirstPortion().getStartPositionInRoute();
		this.end_position = journey.getFirstPortion().getEndPositionInRoute();
		
		minsBeforeTrain = minutesBefore;
		

		
		tomorrow = false;
		long timeToFire = timeInMinutes - dataInterface.getCurrentTime();
		if (timeToFire < 0)
		{
			//Add 24 hours on so alarm fires tomorrow
			timeInMinutes = timeInMinutes + (24 * 60);
			Toast.makeText(dataInterface.getAndroidContext(), "Alarm set for tomorrow", Toast.LENGTH_SHORT).show();
			tomorrow = true;
		}
		else
		{
			Toast.makeText(dataInterface.getAndroidContext(), "Alarm set for today", Toast.LENGTH_SHORT).show();
		}
	}
	

	public void fire()
	{
		setReminder();
		setCounter();
	}
	
	public void setReminder()
	{
		Intent intent = new Intent(dataInterface.getAndroidContext(), ReminderReceiver.class);
		intent.putExtra("Time", "" + (System.currentTimeMillis() + ((timeInMinutes - dataInterface.getCurrentTime()) * 60 * 1000)));
		intent.putExtra("Station", source);
		intent.putExtra("Destination", destination);
		intent.putExtra("route", route_id);
		intent.putExtra("start", start_position);
		intent.putExtra("end", end_position);
		intent.putExtra("StringTime", timeFormatted);
		alertIntent = PendingIntent.getBroadcast(dataInterface.getAndroidContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		long timeToFire = timeInMinutes - dataInterface.getCurrentTime();
		
		timeToFire = (timeToFire - minsBeforeTrain) * 60 * 1000;
		
		alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToFire - 1000, alertIntent);
	}
	
	public void setDistant()
	{
		Intent intent = new Intent(dataInterface.getAndroidContext(), DistantReceiver.class);
		intent.putExtra("Time", "" + (System.currentTimeMillis() + ((timeInMinutes - dataInterface.getCurrentTime()) * 60 * 1000)));
		intent.putExtra("Station", source);
		intent.putExtra("Destination", destination);
		intent.putExtra("route", route_id);
		intent.putExtra("start", start_position);
		intent.putExtra("end", end_position);
		intent.putExtra("StringTime", timeFormatted);
		intent.putExtra("tomorrow", tomorrow);
		alertIntent = PendingIntent.getBroadcast(dataInterface.getAndroidContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alertIntent);
	}
	
	public void setCounter()
	{
		Intent intent = null;

		intent = new Intent(context, CountdownReceiver.class);
		intent.putExtra("Time", "" + (System.currentTimeMillis() + ((timeInMinutes - dataInterface.getCurrentTime()) * 60 * 1000)));
		intent.putExtra("Station", source);
		intent.putExtra("Destination", destination);
		intent.putExtra("route", route_id);
		intent.putExtra("start", start_position);
		intent.putExtra("end", end_position);
		intent.putExtra("Alert", minsBeforeTrain);
		intent.putExtra("distant", "false");
		intent.putExtra("StringTime", timeFormatted);
		
		long timeToFire = timeInMinutes - dataInterface.getCurrentTime();
		
		if (timeToFire > 60)
		{
			timeToFire = (timeToFire - 60) * 60 * 1000; // - 60 to fire initial intent at 60 mins to go
			
			repeatIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToFire - 1000, 60*1000, repeatIntent);
			setDistant();
		}
		else
		{
			repeatIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60*1000, repeatIntent);
		}
	}

	public void cancelReminders(Context context) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		mNotificationManager.cancelAll();

		if (repeatIntent != null && alarm_manager != null)
		{
			alarm_manager.cancel(repeatIntent);
		}
		
		if (alertIntent != null && alarm_manager != null)
		{
			alarm_manager.cancel(alertIntent);
		}
	}
	
	@Override
	public String toString()
	{
		return "TrainReminder";
	}
}
