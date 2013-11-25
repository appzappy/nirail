package AppZappy.NIRailAndBus.delays;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.text.util.Linkify.MatchFilter;

import AppZappy.NIRailAndBus.events.Event;
import AppZappy.NIRailAndBus.mode.IProgramMode;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.util.FileDownloading;
import AppZappy.NIRailAndBus.util.FindURL;
import AppZappy.NIRailAndBus.util.L;
import AppZappy.NIRailAndBus.util.XML;


public class TwitterSource implements IDelaySource
{
	private IProgramMode data_mode = null;
	private List<DelayItem> _data = null;
	
	private Event dataDownloaded = new Event();
	
	public void start()
	{
		Thread thread = new Thread() {
            public void run() {
            	IProgramMode newMode = ProgramMode.singleton();
            	if (newMode != data_mode)
            	{
            		_data = null;
            	}
            	data_mode = newMode;
                _data = download();
                dataDownloaded.triggerEvent();
            }
        };
        thread.start();
	}
	
	public Event getDownloadedEvent()
	{
		return dataDownloaded;
	}

	public List<DelayItem> getItems()
	{
		if (data_mode == null)
			return null;
		if (data_mode != ProgramMode.singleton())
			return null;
		return _data;
	}
	
	/**
	 * Download the data for this twitter source
	 * @return
	 */
	private List<DelayItem> download()
	{
		IProgramMode data_mode = this.data_mode; // this should stop race conditions when swapping transport mode
		
		String text = null;
		try {
			text = FileDownloading.downloadFile(data_mode.twitterURL());
		}
		catch (Exception e) { // Might die if can't reach twitter?
			return null;
		}
		
		if (text == null || text.equals(""))
		{
			return null;
		}
		Document xmlDocument = null;
		
		try
		{
			xmlDocument = XML.fromString(text);
		}
		catch (DOMException exception)
		{
			return null;
		}
		
		if (xmlDocument == null)
			return null;
		NodeList nodes = xmlDocument.getElementsByTagName("item");
		
		List<DelayItem> items = new ArrayList<DelayItem>();
		
		
		// the time format
		// DATE FORMAT = Wed, 10 Aug 2011 17:01:52 +0000
		SimpleDateFormat parser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
		SimpleDateFormat formater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
		
		
		for(int i=0;i<nodes.getLength();i++)
		{
			Element element = (Element) nodes.item(i);
			
//			String title = element.getElementsByTagName("title").item(0).getTextContent();
			
			// go over all elements incase text is broken into multiple parts
			Element des = ((Element) element.getElementsByTagName("description").item(0));
			String description = "";
			NodeList childNodes = des.getChildNodes();
			for(int j=0;j<childNodes.getLength();j++)
			{
				Node child = childNodes.item(j);
				description += child.getNodeValue();
			}
			
			// remove the nirailways: text from the start
			description = description.substring(data_mode.twitterNumberCharactersToRemoveFromDescription()); 
			description = description.trim(); // trim the text
			
			String descriptionLink = FindURL.find(description); // find a url in the text
			
			if (descriptionLink != null)
			{
				// find the link. if at end of message remove it
				int index = description.indexOf(descriptionLink);
				int finalIndex = description.length() - descriptionLink.length();
				
				if (index == finalIndex)
				{
					description = description.substring(0, finalIndex-1).trim();
				}
			}
			
			Boolean withinLastDay = true;
			String pubDate = ((Element) element.getElementsByTagName("pubDate").item(0)).getFirstChild().getNodeValue().trim();
			
			try
			{
				// Take into account the timeoffset. IE summer time is +0100
				Date d = parser.parse(pubDate);
				Calendar gc = new GregorianCalendar();
				gc.setTime(d);
				Calendar local = new GregorianCalendar();
				local.setTimeInMillis(gc.getTimeInMillis());
				pubDate = formater.format(local.getTime());
				withinLastDay = isWithinLastDay(local);
			}
			catch(ParseException e)
			{
				// program falls back on pubDate from xml
			}
			
			String guid = ((Element) element.getElementsByTagName("guid").item(0)).getFirstChild().getNodeValue().trim();
			
			String guid_number = guid.substring(data_mode.twitterGUIDStartLength());
			long this_guid = Long.parseLong(guid_number);
//			String link = element.getElementsByTagName("link").item(0).getTextContent();
			
			if (withinLastDay) {
				DelayItem item = new DelayItem(description, pubDate, descriptionLink, data_mode.getMode(), this_guid);
				items.add(item);
			}
		}
		
		return items;
	}
	
	protected Boolean isWithinLastDay(Calendar tweetDate) {
		return tweetDate.get(Calendar.DAY_OF_YEAR) == new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public String toString()
	{
		return "TwitterSource";
	}

}
