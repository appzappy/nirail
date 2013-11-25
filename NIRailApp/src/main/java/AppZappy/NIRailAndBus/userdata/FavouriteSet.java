package AppZappy.NIRailAndBus.userdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.events.Event;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.util.XML;

public class FavouriteSet
{
	public static final String XML_SET_NAME = "FavouriteSet";
	
	
	
	private List<Favourite> _favourites = new ArrayList<Favourite>();
	
	public List<Favourite> getList()
	{
		int numItems = _favourites.size();
		List<Favourite> favs = new ArrayList<Favourite>(numItems);
		for(int i=0;i<numItems;i++)
		{
			favs.add(_favourites.get(i));
		}
		return favs;
	}
	
	public int size()
	{
		return _favourites.size();
	}
	public Favourite get(int index)
	{
		return _favourites.get(index);
	}
	
	
	
	public boolean isFavourite(Location source, Location destination)
	{
		int pos = findFavourite(source, destination);
		boolean res = pos != -1;
		return res;
	}
	
	
	public int findFavourite(Location source, Location destination)
	{
		int number = _favourites.size();
		
		for(int i=0;i<number;i++)
		{
			Favourite f = _favourites.get(i);
			
			String destinationFav = f.getDestination();
			String sourceFav = f.getSource();
			if (destination.isAliasOf(destinationFav))
			{
				if (source != null)
				{
					// source is set
					// make sure its a linked route
					if (f.isLinkedRoute() && source.isAliasOf(sourceFav))
						return i;
				}
				else
				{
					// source is not set. NOT A LINKED ROUTE
					if (!f.isLinkedRoute())
					{
						return i;	
					}
				}
			}
		}
		return -1;
	}
	
	public void setFavourite(Location source, Location destination, boolean value)
	{
		int position = findFavourite(source, destination);
		if (value)
		{
			// then should be a favourite location
			if (position == -1)
			{
				// isn't currently a favourite location.
				// add it
				Favourite newFavourite = null;
				
				if (source == null)
				{
					newFavourite = Favourite.create(destination.getRealName());
				}
				else
				{
					newFavourite = Favourite.create(destination.getRealName(), source.getRealName());
				}
				_favourites.add(newFavourite);
				sort();
				
				setChanged.triggerEvent();
			}
		}
		else
		{
			// shouldn't be a favourite location
			if (position != -1)
			{
				// current is a favourite location
				// so remove it
				_favourites.remove(position);
				setChanged.triggerEvent();
			}
		}
	}
	
	public boolean toggleFavourite(Location source, Location destination)
	{
		boolean oldState = isFavourite(source, destination);
		boolean newState = !oldState;
	
		setFavourite(source, destination, newState);
		return newState;
	}
	
	public Event setChanged = new Event();
	
	private FavouriteSet()
	{
		setChanged.addListener(new IEventListener()
		{
			public void action(Object optionalData) // register for the favourites to be saved whenever they're changed
			{
				FavouriteManager.saveFavourites();
			}
		});
	}
	public static FavouriteSet fromXmlString(String xml)
	{
		Document doc = XML.fromString(xml);
		return fromXml(doc);
	}

	public static FavouriteSet fromXml(Document favouriteData)
	{
		if (favouriteData == null)
			throw new NullPointerException("FavouriteData is null");
		FavouriteSet favouriteSet = new FavouriteSet();
		NodeList list = favouriteData.getElementsByTagName(Favourite.XML_FAVOURITE_NAME);
		
		for(int i=0;i<list.getLength();i++)
		{
			Node node = list.item(i);
			Element element = (Element) node;
			
			Favourite fav = Favourite.create(element);
			if (fav != null)
				favouriteSet._favourites.add(fav);
		}
		return favouriteSet;
	}
	
	
	
	
	public String toXMLString()
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement(FavouriteSet.XML_SET_NAME);
			document.appendChild(rootElement);
			
			for(Favourite f: _favourites)
			{
				Element staElement = f.getXML(document);
				rootElement.appendChild(staElement);
			}
			return XML.getStringFromDocument(document);
		}
		catch(ParserConfigurationException e)
		{
			
		}
		
		return "";
		
	}
	public static FavouriteSet create()
	{
		return new FavouriteSet();
	}

	public void remove(Favourite favourite)
	{
		_favourites.remove(favourite);
		setChanged.triggerEvent();
	}
	
	private void sort()
	{
		Collections.sort(_favourites, new FavouritesSorter());
	}
	
	static class FavouritesSorter implements Comparator<Favourite>
	{
		public int compare(Favourite f1, Favourite f2)
		{
			String d1 = f1.getDestination();
			if (f1.isLinkedRoute())
				d1 = f1.getSource();
			String d2 = f2.getDestination();
			if (f2.isLinkedRoute())
				d2 = f2.getSource();
			
			return d1.compareTo(d2);
		}
	}
}
