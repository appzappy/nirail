package appzappy.nirail.userdata;

import java.io.File;

import org.w3c.dom.Document;

import appzappy.nirail.util.FileActions;
import appzappy.nirail.util.XML;



/**
 * Used for accessing the current favourite set and all loading and saving actions for favourites
 */
public class FavouriteManager
{
	public static final String FAVOURITES_FILE_NAME ="favourites.xml";
	private static FavouriteSet singleton = null;
	/**
	 * Get the current instance of the favourite set object
	 * @return
	 */
	public static FavouriteSet getInstance()
	{
		return singleton;
	}
	
	
	/**
	 * Load the favourites from long term memory
	 */
	public static void loadFavourites()
	{
		File favouritesFile = FileActions.getFileTarget(FAVOURITES_FILE_NAME);
		
		try {
			if (favouritesFile.exists())
			{
				Document favouriteData = XML.fromFile(favouritesFile);
				if (favouriteData == null)
				{
					singleton = FavouriteSet.create();
					saveFavourites();
				}
				else {
					FavouriteSet out = FavouriteSet.fromXml(favouriteData);
					singleton = out;
				}
			}
		}
		catch(Exception e) {
			//oops
		}
		finally {
			if (singleton == null) {
				singleton = FavouriteSet.create();
				saveFavourites();
			}
		}
	}
	
	private static Object lock = new Object();
	private static int count = 0;
	
	
	/**
	 * Save the current favourite set to long term memory
	 */
	public static void saveFavourites()
	{
		if (singleton == null)
			return;
		
		
		synchronized (lock)
		{
			if (count > 2)
				return;
			
			count++;
		}

		
		Thread thread = new Thread() {
            public void run() {
            	saveFavourites2();
            }
        };
        thread.start();
	}
	
	private static void saveFavourites2()
	{
		File targetFile = FileActions.getFileTarget(FAVOURITES_FILE_NAME);
		String saveText = singleton.toXMLString();
		
		FileActions.writeFile(targetFile, saveText);
		
		synchronized (lock)
		{
			count--;
		}
	}
	
	
}
