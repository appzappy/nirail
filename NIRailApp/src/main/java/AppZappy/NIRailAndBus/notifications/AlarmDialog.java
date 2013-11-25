package AppZappy.NIRailAndBus.notifications;

import java.io.IOException;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class AlarmDialog extends Activity {

	private TextView text;
	private final MediaPlayer mMediaPlayer = new MediaPlayer();
	
	private IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	public static final String BUNDLE_NAME = "alarmBundle";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.ui_alarm);
		
		text = (TextView) findViewById(R.id.alarm_Text);
		
		Bundle b = getIntent().getBundleExtra(BUNDLE_NAME);
		if (b != null)
		{
			 text.setText(b.getString("Text"));
		}
		
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		try {
			mMediaPlayer.setDataSource(dataInterface.getAndroidContext(), alert);
		} catch (IllegalArgumentException e) {
			
		} catch (SecurityException e) {
			
		} catch (IllegalStateException e) {
			
		} catch (IOException e) {
			
		}

		final AudioManager audioManager = (AudioManager) dataInterface.getAndroidContext().getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    mMediaPlayer.setLooping(true);
		    try {
				mMediaPlayer.prepare();
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
		    mMediaPlayer.start();
		}
	}
	
	public void onDimiss()
	{
		mMediaPlayer.stop();
		this.finish();
	}
	
	@Override
	public String toString()
	{
		return "Alarm Dialog";
	}
}
