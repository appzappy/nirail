package appzappy.nirail.data.model;

public class Location_Link_Unit
{
	public Integer location_id = 0;
	public Integer previous_id = 0;
	
	public Location_Link_Unit(Integer location_id, Integer previous_id)
	{
		this.location_id = location_id;
		this.previous_id = previous_id;
	}
	
	
	@Override
	public String toString()
	{
		return "Location Link Unit: Location: " + location_id + " Previous: " + previous_id;
	}
	
}
