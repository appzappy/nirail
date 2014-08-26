package appzappy.nirail.delays;

import java.util.List;

import appzappy.nirail.events.Event;


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