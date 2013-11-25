package AppZappy.NIRailAndBus.mode;

import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.ApplicationInformation;
import AppZappy.NIRailAndBus.util.FileDownloading;
import AppZappy.NIRailAndBus.util.timing.TimeFormatter;

/**
 * Used to download and update the timetable data
 */
public class ProjectUpdater
{
	/**
	 * Should the UI display the update prompt
	 * @return
	 */
	public boolean shouldDisplayPrompt()
	{
		if (Settings.getLastUpdatePrompt() >= TimeFormatter.getTodaysDayNumber())
			return false;
		if (Settings.getServerCode() == -1)
		{
			// havent got the server code yet
			return false;
		}
		
		if (Settings.getServerCode() > ApplicationInformation.getVersionCode())
		{
			return true;
		}
			
		return false;
	}
	
	private boolean checking = false;
	
	/**
	 * Check if an update is required
	 */
	public void check()
	{
		// already checking for an update
		if (checking) 
			return;
		
		if (Settings.getLastUpdatePrompt() != -1)
		{
			// don't continue if the prompt has already been displayed today
			if (Settings.getLastUpdatePrompt() < TimeFormatter.getTodaysDayNumber())
			{
				return;	
			}
		}
		
		if (Settings.getServerCode() != -1)
		{
			// if the server code is out of date, display the prompt!
			if (Settings.getServerCode() != ApplicationInformation.getVersionCode())
			{
				return;
			}
		}	
		
		
		// start downloading the webpage
		checking = true;
		
		Thread thread = new Thread() {
            public void run() {
            	startAndWait();
            }
        };
        thread.start();
	}
	
	
	/**
	 * Notify that the prompt has been displayed
	 */
	public void promptDisplayed()
	{
		Settings.setLastUpdatePrompt(TimeFormatter.getTodaysDayNumber());
	}
	
	
	/**
	 * Download new data if any available. Wait until complete
	 */
	private void startAndWait()
	{
		try
		{
			int serverCode = getServerCode();
			int localCode = ApplicationInformation.getVersionCode();
			
			if (serverCode == -1)
			{
				// failed to get number online
				return;
			}
			if (serverCode != localCode)
			{
				// new version has been released
				Settings.setServerCode(serverCode);
			}
			else
			{
				Settings.setLastUpdatePrompt(TimeFormatter.getTodaysDayNumber());
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			checking = false;
		}
	}


	/**
	 * The website folder where the application files are kept
	 */
	public static final String WEBSITE_FOLDER = "http://www.appzappy.co.uk/applicationFiles/";
	/**
	 * The file name that contains the current version code
	 */
	public static final String SERVER_VERSION_FILE = "version_code.txt";
	
	/**
	 * Get the code from the server page
	 * @return
	 */
	private int getServerCode()
	{
		String versionFileContent = FileDownloading.downloadFile(WEBSITE_FOLDER+SERVER_VERSION_FILE);
		int number = -1;
		try
		{
			number = Integer.parseInt(versionFileContent);
		}
		catch (Exception e)
		{
			
		}
		return number;
	}
	
	@Override
	public String toString()
	{
		return "ProjectUpdater";
	}
}
