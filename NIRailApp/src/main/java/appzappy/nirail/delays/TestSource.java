package appzappy.nirail.delays;

import java.util.ArrayList;
import java.util.List;

import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.events.Event;


/**
 * A source of test delay items for the UI to use in development times
 */
public class TestSource implements IDelaySource
{
	private List<DelayItem> data = null;
	private Event event = new Event();
	
	public List<DelayItem> getDelays()
	{
		return getDelays(50);
	}
	
	public void start()
	{
		getDelays();
	}


	public List<DelayItem> getDelays(int max)
	{
		List<DelayItem> items = new ArrayList<DelayItem>();
		
		for (int i=0; i<max; i++)
		{
			DelayItem item = new DelayItem("This is a test tweet. I hope it will work", TransportType.Train);
			if (i % 2 == 0)
				item.setType(TransportType.Metro);
			
			items.add(item);
		}
		items.get(2).setText("This is a really long test tweet. I hope it will work. Theoretically that means that the list item will be bigger because of this text. Still, hopefully it will work becuase if not then I have a problem!");
		items.get(2).setType(TransportType.Train);
		
		data = items;
		event.triggerEvent();
		return items;
	}


	public Event getDownloadedEvent()
	{
		return event;
	}


	public List<DelayItem> getItems()
	{
		return data;
	}
	
	public String toString()
	{
		return "TestDelaySource";
	}
}
