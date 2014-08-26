package appzappy.nirail.data.db;

import java.io.File;


import appzappy.nirail.R;
import appzappy.nirail.mode.MetroMode;
import appzappy.nirail.mode.ProgramMode;
import appzappy.nirail.mode.TrainMode;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.userdata.Settings;
import appzappy.nirail.util.ApplicationInformation;
import appzappy.nirail.util.FileActions;
import appzappy.nirail.util.L;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


public class SQLiteManager
{
	//private static final String DB_PATH2 = "/data/data/AppZappy.NIRailAndBus/databases/";
	static final File trainDbDir = FileActions.getFileTarget(new TrainMode(), "");
    static final File metroDbDir = FileActions.getFileTarget(new MetroMode(), "");
    static final File trainDbFile = FileActions.getFileTarget(new TrainMode(), TrainMode.DATABASE_NAME);
    static final File metroDbFile = FileActions.getFileTarget(new MetroMode(), MetroMode.DATABASE_NAME);
    
    public static boolean shouldCreateDatabase()
    {
        boolean updateTrain = updateDatabase(trainDbDir, trainDbFile);
        boolean updateMetro = updateDatabase(metroDbDir, metroDbFile);
        
        if (updateTrain || updateMetro)
        {
        	trainDbFile.delete();
        	metroDbFile.delete();
        	return true;
        }
        else
        {
        	return false;
        }
    }
    
    private static boolean updateDatabase(File dbDir, File dbFile)
    {
    	boolean createDb = false;
    	if (!dbDir.exists())
        {
        	L.d("Program Folder Missing");
        	dbDir.mkdir();
            createDb = true;
        }
        else if (!dbFile.exists())
        {
        	L.d("Database file missing");
        	createDb = true;
        }
        else
        {
        	// Check that we have the latest version of the db
            boolean doUpgrade = false;

            int databaseVersion = Settings.getDatabaseVersionNumber();
            
            if (databaseVersion == -1)
            {
            	L.d("Update database: First Run");
            	doUpgrade = true;
            }
            int programVersion = ApplicationInformation.getVersionCode();
            
            if (databaseVersion != programVersion)
            {
            	L.d("Update database: invalid database version. Expected:" + programVersion + " Actual:" + databaseVersion);
            	doUpgrade = true;
            }
            
            // Insert your own logic here on whether to upgrade the db; I personally
            // just store the db version # in a text file, but you can do whatever
            // you want.  I've tried MD5 hashing the db before, but that takes a while.

            // If we are doing an upgrade, basically we just delete the db then
            // flip the switch to create a new one
            if (doUpgrade)
            {
            	for(File f: dbDir.listFiles())
            	{
            		// remove only the db files
            		if (f.getName().contains(".db"))
            		{
            			f.delete();
            		}
            	}
            	dbFile.delete();
            	createDb = true;
            }
        }
        return createDb;
    }
    
    /**
     * Delete the train and metro database from the memory. Called after exception.
     */
    public static void deleteDatabases() {
    	if (trainDbFile.exists()) {
    		trainDbFile.delete();
    	}
    	if (metroDbFile.exists()) {
    		metroDbFile.delete();
    	}
    }
    
    public static void createOrUpdateDatabaseIfRequired(Context context)
    {
    	final boolean createDb = shouldCreateDatabase();

        if (createDb)
        { 
        	L.d("Updating Databases");

        	// Extracting train database
        	final File trainZipData = FileActions.getFileTarget(new TrainMode(), TrainMode.ZIP_NAME);
        	FileActions.copyFileFromRawResource(R.raw.rail_data, "The compressed database. " + TrainMode.ZIP_NAME, trainZipData);
        	FileActions.extractFilesFromZipToFolder(trainZipData, trainDbDir);
        	trainZipData.delete(); 
        	
        	// Extracting metro database
//        	final File metroZipData = FileActions.getFileTarget(new MetroMode(), MetroMode.ZIP_NAME);
//        	FileActions.copyFileFromRawResource(R.raw.metro_data, "The compressed database. " + MetroMode.ZIP_NAME, metroZipData);
//        	FileActions.extractFilesFromZipToFolder(metroZipData, metroDbDir);
//        	metroZipData.delete(); 
        	
        	Settings.setDatabaseVersionNumber(ApplicationInformation.getVersionCode());
        }
    }

    public static SQLiteDatabase getDb()
    {
    	String database_to_return = ProgramMode.singleton().getDatabaseName();
    	
    	File database = FileActions.getFileTarget(ProgramMode.singleton(), database_to_return);
    	if (!database.exists())
    	{
    		Context context = UIInterfaceFactory.getInterface().getAndroidContext();
    		if (context != null)
    		{
    			Toast.makeText(context, "Failed to find database file. Is SD Card mounted?", Toast.LENGTH_LONG).show();
    		}
    		throw new RuntimeException("Database File Not Found: " + database.getAbsolutePath());
    	}
        return SQLiteDatabase.openDatabase(database.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
    }
    
}