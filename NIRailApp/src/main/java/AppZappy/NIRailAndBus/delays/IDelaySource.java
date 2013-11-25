package AppZappy.NIRailAndBus.delays;

import java.util.List;

import AppZappy.NIRailAndBus.events.Event;


/**
 * A source of delay items
 */
public interface IDelaySource
{
	/**
	 * The event for listening to data downloading events
	 * @return
	 */
	public Event getDownloadedEvent();
	
	/**
	 * Start downloading the details
	 */
	public void start();
	
	/**
	 * Get the list of items
	 * @return
	 */
	public List<DelayItem> getItems();
	
}