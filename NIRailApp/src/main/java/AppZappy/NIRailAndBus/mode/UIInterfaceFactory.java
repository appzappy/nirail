package AppZappy.NIRailAndBus.mode;



/**
 * Responsible for creating the interface to data
 * @author Kurru
 *
 */
public final class UIInterfaceFactory
{
	private static IUIInterface _singleton = null;
	
	private static Object _lock = new Object();
	
	/**
	 * Get the interface instance
	 * @return The IUIInterface being used
	 */
	public static IUIInterface getInterface()
	{
		if (_singleton == null)
		{
			synchronized (_lock)
			{
				if (_singleton == null)
				{	
					_singleton = UIInterfaceInitial.getInstance();
				}
			}
		}
		return _singleton; 
	}
}
