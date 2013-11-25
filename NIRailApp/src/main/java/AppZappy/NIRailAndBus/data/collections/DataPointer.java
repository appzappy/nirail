package AppZappy.NIRailAndBus.data.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AppZappy.NIRailAndBus.data.model.IID;
import AppZappy.NIRailAndBus.data.model.Route;
import AppZappy.NIRailAndBus.data.model.Stop;
import AppZappy.NIRailAndBus.data.timetable.Service;
import AppZappy.NIRailAndBus.data.timetable.Timetable;

public class DataPointer<T extends IID>
{
	public DataPointer (Class<T> type, int id)
	{
		this._type = type;
		this._id = id;
	}
	
	
	private int _id = 0;
	public int get_id()
	{
		return _id;
	}
	
	
	private Class<T> _type = null;
	public Class<T> get_type()
	{
		return _type;
	}
	
	
	public static void clearCache()
	{
		data.clear();
	}
	
	private static Map<String, Object> data = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public static <S extends IID> S get_Object(Class<S> type, int id)
	{
		String hash = type.getName() + id;
		
		S obj = (S)data.get(hash);
		
		if (obj == null)
		{
			load_Object(type, id);
			S obj2 = (S)data.get(hash);
			if (obj2 == null)
				throw new NullPointerException("Requested Object Not Found. Type: " + type.getName() + " ID: " + id);
			return obj2;
		}
		return obj;
	}
	
	public static <S extends IID> S get_Object_If_Exists(Class<S> type, int id)
	{
		String hash = type.getName() + id;
		
		@SuppressWarnings("unchecked")
		S obj = (S)data.get(hash);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <S extends IID> S add_Object(Class<S> type, S obj)
	{
		String key = type.getName() + obj.get_id();
		if (data.containsKey(key))
			return (S) data.get(key);
		
		data.put(key, obj);
		return obj;
	}
	
	public static <S extends IID> List<S> add_Object(Class<S> type, List<S> objects)
	{
		List<S> out = new ArrayList<S>();
		for(S s : objects)
		{
			S added_obj = add_Object(type, s);
			out.add(added_obj);
		}
		return out;
	}
	
	
	public static <S> boolean isCached (Class<S> type, int id)
	{
		String key = type.getName() + id;
		return data.containsKey(key);
	}
	
	private static <S> void load_Object(Class<S> type, int id)
	{
		if (type.equals(Timetable.class))
		{
			Timetable.from_SQL(id);
		}
		else if (type.equals(Route.class))
		{
			Route.from_SQL(id);
		}
		else if (type.equals(Service.class))
		{
			Service.from_SQL(id);
		}
		else if (type.equals(Stop.class))
		{
			Stop.from_SQL(id);
		}
		else
		{
			String name = type.getName();
			throw new RuntimeException("Not supported object type: " + name);
		}
	}
	
	public T get_Object_Cache()
	{
		return get_Object(this._type, this._id);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj instanceof DataPointer<?>)
		{
			@SuppressWarnings("unchecked")
			DataPointer<T> o = (DataPointer<T>) obj;
			if (this._id != o._id)
				return false;
			return o._type.equals(this._type);
		}
		return false;
	}
	@Override
	public String toString()
	{
		return "DataPointer<T>:" + _type.getName() + " " + _id;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
}
