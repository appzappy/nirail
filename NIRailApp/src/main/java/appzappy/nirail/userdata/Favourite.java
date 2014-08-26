package appzappy.nirail.userdata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import appzappy.nirail.data.enums.TransportType;


public class Favourite
{
	public static final String XML_FAVOURITE_NAME = "Favourite";
	public static final String XML_DESTINATION_ATTRIBUTE = "destination";
	public static final String XML_SOURCE_ATTRIBUTE = "source";
	public static final String XML_TYPE_ATTRIBUTE = "type";
	
	private Favourite()
	{
	}

	private String _destination = null;

	public String getDestination()
	{
		return _destination;
	}

	private String _source = null;

	public String getSource()
	{
		return _source;
	}
	
	private TransportType _type = TransportType.Train;
	
	public TransportType getType() {return _type;} 
	
	public void setType(TransportType type) { _type = type;}

	private boolean _linkedRoute = false;

	public boolean isLinkedRoute()
	{
		return _linkedRoute;
	}


	public static Favourite create(String destination)
	{
		Favourite f = new Favourite();
		f._destination = destination;
		f._linkedRoute = false;
		return f;
	}

	public static Favourite create(String destination, String source)
	{
		Favourite f = new Favourite();
		f._destination = destination;
		f._source = source;
		f._linkedRoute = true;
		return f;
	}

	public static Favourite create(String destination, String source, TransportType type)
	{
		Favourite f = new Favourite();
		f._destination = destination;
		f._source = source;
		f._linkedRoute = true;
		f._type = type;
		return f;
	}
	
	public static Favourite create(Element element)
	{
		String destination = element.getAttribute(Favourite.XML_DESTINATION_ATTRIBUTE);
		String source = element.getAttribute(Favourite.XML_SOURCE_ATTRIBUTE);
		String type = element.getAttribute(Favourite.XML_TYPE_ATTRIBUTE);
		
		if (destination.equals(""))
			destination = null;
		if (source.equals(""))
			source = null;
		if (type.equals(""))
			type = null;
		
		Favourite fav = null;
		if (destination != null && source != null)
		{
			fav = Favourite.create(destination, source);
		}
		else if (destination != null)
		{
			fav = Favourite.create(destination);
		}
		
		if (type != null && fav != null)
			fav._type = TransportType.valueOf(type); 
		return fav;
	}

	

	public Element getXML(Document document)
	{
		Element ele = document.createElement(Favourite.XML_FAVOURITE_NAME);
		if (_destination != null && !_destination.equals(""))
			ele.setAttribute(Favourite.XML_DESTINATION_ATTRIBUTE, this._destination);
		if (_source != null && !_source.equals(""))
			ele.setAttribute(Favourite.XML_SOURCE_ATTRIBUTE, this._source);
		ele.setAttribute(Favourite.XML_TYPE_ATTRIBUTE, this._type.toString());
		return ele;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		
		if (this == obj)
			return true;
		
		if ( !(obj instanceof Favourite) ) return false;
	    
		Favourite fav = (Favourite) obj;
		
		if (!this._destination.equals(fav._destination))
			return false;
		
		if (this._source != null && fav._source !=null && !this._source.equals(fav._source))
			return false;
		else if (this._source== null && fav._source != null || this._source != null && fav._source == null)
			return false;
		
		if (this._type != fav._type)
			return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return "Favourite: " + this._source + " to " + this._destination + " Type: " + _type;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	

	public enum FavouriteType{
		journey,
		station
	};
	
}
