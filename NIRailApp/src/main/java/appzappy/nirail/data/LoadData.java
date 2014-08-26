package appzappy.nirail.data;

import java.io.*;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import appzappy.nirail.R;
import appzappy.nirail.data.collections.DataPointer;
import appzappy.nirail.data.db.SQLNetworkSource;
import appzappy.nirail.data.db.SQLiteManager;
import appzappy.nirail.data.timetable.Network;
import appzappy.nirail.data.timetable.Timetable;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.ProgramMode;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.userdata.FavouriteManager;
import appzappy.nirail.userdata.Settings;
import appzappy.nirail.util.FileActions;
import appzappy.nirail.util.exception.CustomExceptionHandler;
import appzappy.nirail.util.exception.NoSpaceLeftOnDeviceException;

/**
 * Basic way to load data from XML file
 */
public class LoadData
{
	private LoadData(){}
		
	private static boolean _loaded = false;
	
	public static void initialiseReset(Context context)
	{
		_loaded = false;
		
		DataPointer.clearCache();
		initialise(context);
	}
	
	public static void initialise(Context context)
	{
		if (_loaded)
			return;

		_loaded = true;
		
		
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());

		Settings.initialise(context);
		
		IUIInterface dataInterface = UIInterfaceFactory.getInterface();
		dataInterface.setAndroidContext(context.getApplicationContext());


		// check theres space
		int requiredSpace = 0;
		if (SQLiteManager.shouldCreateDatabase())
		{
			requiredSpace = 2*1024*1024;
		}
		if ((FileActions.isSDCardAvailable() && FileActions.getRemainingSpaceOnSDCard() <= requiredSpace) || (!FileActions.isSDCardAvailable() && FileActions.getRemainingLocalStorage() <= requiredSpace)) // ~1.5MB
		{
			Toast toast = Toast.makeText(context.getApplicationContext(), R.string.out_of_space_error, Toast.LENGTH_LONG);
			toast.show();
			if (context instanceof Activity)
			{
				((Activity) context).finish();
			}
			
			_loaded = false;
			return;
		}
		
		deleteOldFiles();
		
		
		
		
		
		try
		{
			SQLiteManager.createOrUpdateDatabaseIfRequired(context.getApplicationContext());
		}
		catch (NoSpaceLeftOnDeviceException e)
		{
			_loaded = false;
			Toast toast = Toast.makeText(context.getApplicationContext(), R.string.out_of_space_error, Toast.LENGTH_LONG);
			toast.show();
			if (context instanceof Activity)
			{
				((Activity) context).finish();
			}
			return;
		}
		
		load_network();
		load_timetable();
		
		
		dataInterface.startListeningForLocation(context.getSystemService(Context.LOCATION_SERVICE));
		
		dataInterface.startDownloadingDelays();
		
		
		// Load the favourites into the phone memory
		FavouriteManager.loadFavourites();
	}
	

	
	
	
	private static Timetable _timetable = null;

	/**
	 * Get the created timetable object
	 * 
	 * @return The timetable object
	 */
	public static Timetable getTimetable()
	{
		return _timetable;
	}


	private static Network _network = null;

	/**
	 * Get the created network object
	 * 
	 * @return The network object
	 */
	public static Network getNetwork()
	{
		return _network;
	}
	
	private static void load_network()
	{
		SQLNetworkSource network_source = new SQLNetworkSource();
		_network = network_source.createNetwork(); 
	}
	private static void load_timetable()
	{
		_timetable = Timetable.default_Timetable().get_Object_Cache();
	}
	
	private static void deleteOldFiles()
	{
		// delete all files BUT NOT the favourite file or the database file
		File favouriteFile = FileActions.getFileTarget(FavouriteManager.FAVOURITES_FILE_NAME);
		File databaseFile = FileActions.getFileTarget(ProgramMode.singleton().getDatabaseName());
		if (favouriteFile == null)
		{
			return;
		}
		File parent = favouriteFile.getParentFile();
		if (parent == null || databaseFile == null || favouriteFile == null)
		{
			return;
		}
		parent.mkdirs();
		File[] files = parent.listFiles();
		if (files == null)
		{
			return;
		}
		for(File f : files)
		{
			if (f==null)
			{
				continue;
			}
			if (f.isDirectory())
			{
				continue;
			}
			if (f.equals(favouriteFile))
			{
				continue;
			}
			if (f.equals(databaseFile))
			{
				continue;
			}
			
			f.delete();
			f.delete();
		}
	}

}

