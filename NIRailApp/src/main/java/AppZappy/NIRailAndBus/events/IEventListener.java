package AppZappy.NIRailAndBus.events;

/**
 * The generic format of an event listener
 * @author Kurru
 */
public interface IEventListener
{
	/**
	 * An action has been triggered
	 * @param optionalData Optional data for the event. May be null (No data)
	 */
	public void action(Object optionalData);
}
