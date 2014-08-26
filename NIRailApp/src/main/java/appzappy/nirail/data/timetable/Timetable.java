package appzappy.nirail.data.timetable;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import appzappy.nirail.data.collections.DataPointer;
import appzappy.nirail.data.db.SQLiteHelper;
import appzappy.nirail.data.db.SQLiteManager;
import appzappy.nirail.data.model.IID;


/**
 * A timetable object is a collection of services that make up a timetable
 */
public class Timetable implements IID
{
	private int _id = 0;
	public int get_id()
	{
		return _id;
	}

	public static DataPointer<Timetable> default_Timetable()
	{
		return from_SQL(SQLiteHelper.get_default_timetable_id());
	}
	
	public static DataPointer<Timetable> from_SQL(int timetable_id)
	{
		Timetable timetable = new Timetable();
		timetable._id = timetable_id;
		
		SQLiteDatabase db = SQLiteManager.getDb();
		timetable._services = SQLiteHelper.get_Services(db, timetable._id);
		db.close();
		
		DataPointer.add_Object(Timetable.class, timetable);
		
		return new DataPointer<Timetable>(Timetable.class, timetable_id);
	}
	
	private Timetable(){}
	
	private List<DataPointer<Service>> _services = new ArrayList<DataPointer<Service>>();

	/**
	 * The number of services in this timetable
	 * @return Number of services
	 */
	public int countServices()
	{
		return _services.size();
	}

	/**
	 * Get a service at a specific location
	 * @param index The index of the service to get
	 * @return The requested service
	 */
	public Service get(int index)
	{
		return _services.get(index).get_Object_Cache();
	}
}
