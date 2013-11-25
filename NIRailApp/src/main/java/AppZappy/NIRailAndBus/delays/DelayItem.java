package AppZappy.NIRailAndBus.delays;

import AppZappy.NIRailAndBus.data.enums.TransportType;

/**
 * A delay item for the delays feed
 */
public class DelayItem 
{
	/**
	 * Create a delay item
	 * @param text The delay text
	 * @param type The delay type
	 */
	public DelayItem(String text, String time, String link, TransportType type, long guid)
	{
		this._text = text;
		this._type = type;
		this._link = link;
		this._time = time;
		this._guid = guid;
	}
	
	/**
	 * Create a delay item
	 * @param text The delay text
	 * @param type The delay type
	 */
	public DelayItem(String text, TransportType type)
	{
		this._text = text;
		this._type = type;
	}
	/**
	 * Create an empty/blank delay item
	 */
	public DelayItem()
	{
		this._type = TransportType.Train;
	}
	
	private String _link = "";
	private String _text = "";
	private String _time = "";
	private TransportType _type = null;


	/**
	 * Get the link for this item
	 */
	public String getLink()
	{
		return _link;
	}
	
	/**
	 * Set the text for this delay item
	 * @param text The text
	 */
	public void setText(String text) {
		this._text = text;
	}
	
	/**
	 * Get the text for this delay item
	 * @return The text
	 */
	public String getText() {
		return _text;
	}

	/**
	 * Set the timestamp for this delay item
	 * @param text The text
	 */
	public void setTime(String time) {
		this._time = time;
	}
	
	/**
	 * Get the timestamp for this delay item
	 * @return The text
	 */
	public String getTime() {
		return _time;
	}
	
	/**
	 * Set the target this delay item is related too
	 * @param type The type of transport this affects
	 */
	public void setType(TransportType type) {
		this._type = type;
	}
	
	/**
	 * Get the type of transport this delay affects
	 * @return
	 */
	public TransportType getType() {
		return _type;
	}
	
	
	private long _guid = 0;
	/**
	 * Get the GUID for this post
	 * @return
	 */
	public long getGUID()
	{
		return _guid;
	}

	/**
	 * Set the GUID for this post
	 * @return
	 */
	public void setGUID(long guid)
	{
		_guid = guid;
	}
	
	
	public String toString()
	{
		return "DelayItem: " + _guid + " " + _time + " " + _text + " " + _link + " " + _type;
	}
}
