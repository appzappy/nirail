package AppZappy.NIRailAndBus.events;

import java.util.ArrayList;
import java.util.List;



/**
 * Custom event class
 */
public class Event
{
	private List<IEventListener> listeners = new ArrayList<IEventListener>();
	
	/**
	 * Add a listener to this class
	 * @param listener The listener to add
	 */
	public void addListener(IEventListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener from this event
	 * @param listener The listener to remove
	 */
	public void removeListener(IEventListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Remove all listeners from this event
	 */
	public void clearAllListeners()
	{
		listeners.clear();
	}
	
	/**
	 * Trigger the event
	 */
	public void triggerEvent()
	{
		triggerEvent(null);
	}
	
	/**
	 * Trigger the event
	 * @param optionalData The data object passed through when the event is triggered
	 */
	public void triggerEvent(Object optionalData)
	{
		for (int i=0;i<listeners.size();i++)
		{
			IEventListener listener = listeners.get(i);
			listener.action(optionalData);
		}
	}
	
	@Override
	public String toString()
	{
		return "Event Source";
	}
}
