package AppZappy.NIRailAndBus.TimetableData.test;

import AppZappy.NIRailAndBus.data.LoadData;
import android.test.AndroidTestCase;

public class LoadDataTest extends AndroidTestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
		
		LoadData.initialise(getContext());
	}
	
	

	
	public void test_check_network()
	{
		assertEquals(true, LoadData.getNetwork() != null);
	}
	
	public void test_check_timetable()
	{
		assertEquals(true, LoadData.getTimetable() != null);
	}

}
