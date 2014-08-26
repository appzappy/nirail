package appzappy.nirail.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TitleProvider;

import appzappy.nirail.R;
import appzappy.nirail.data.enums.DayOfWeek;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.pathfinding.Journey;
import appzappy.nirail.ui.activities.JourneyWindow;
import appzappy.nirail.ui.widgets.JourneySet_PagerUIElement;
import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class JourneySet_PagerAdapter extends PagerAdapter implements TitleProvider
{
	private static final IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	private final int NUMBER_PAGER_VIEWS = 3;
	public List<JourneySet_PagerUIElement> pager_collection = new ArrayList<JourneySet_PagerUIElement>();
	
	private List<Journey> weekJourneys, satJourneys, sunJourneys;
	
	private Context context = null;

	public JourneySet_PagerAdapter (List<List<Journey>> journey_data, Context context, Route highlightedRoute, int highlightedColor)
	{
		this.context = context;
		weekJourneys = journey_data.get(0);
		satJourneys = journey_data.get(1);
		sunJourneys = journey_data.get(2);
		
		// click events
		OnItemClickListener weekdayListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
			{
				Journey journey = weekJourneys.get(position);
				JourneyWindow.openNew(JourneySet_PagerAdapter.this.context, journey);
			}
		};
		OnItemClickListener saturdayListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
			{
				Journey journey = satJourneys.get(position);
				JourneyWindow.openNew(JourneySet_PagerAdapter.this.context, journey);
			}
		};
		OnItemClickListener sundayListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
			{
				Journey journey = sunJourneys.get(position);
				JourneyWindow.openNew(JourneySet_PagerAdapter.this.context, journey);
			}
		};
		
		// build the data adapters
		DayOfWeek today = dataInterface.getCurrentDayOfWeek();
		boolean filterWeek = false, filterSat = false, filterSun = false;

		switch (today)
		{
			case Sunday:
				filterSun = true;
				break;
			case Saturday:
				filterSat = true;
				break;
			default:
				filterWeek = true;
				break;
		}
		
		
		JourneySet_PagerUIElement weekday_element = new JourneySet_PagerUIElement(context, weekJourneys, weekdayListener, filterWeek);
		JourneySet_PagerUIElement satday_element = new JourneySet_PagerUIElement(context, satJourneys, saturdayListener, filterSat);
		JourneySet_PagerUIElement sunday_element = new JourneySet_PagerUIElement(context, sunJourneys, sundayListener, filterSun);
		
		// set up highlighting data
		if (highlightedRoute != null)
		{
			weekday_element.setHighLightedRoute(highlightedRoute, highlightedColor);
			satday_element.setHighLightedRoute(highlightedRoute, highlightedColor);
			sunday_element.setHighLightedRoute(highlightedRoute, highlightedColor);
		}
		
		pager_collection.add(weekday_element);
		pager_collection.add(satday_element);
		pager_collection.add(sunday_element);
	}
	
	
	@Override
	public int getCount()
	{
		return NUMBER_PAGER_VIEWS;
	}

	/**
	 * Create the page for the given position. The adapter is responsible
	 * for adding the view to the container given here, although it only
	 * must ensure this is done by the time it returns from
	 * {@link #finishUpdate()}.
	 * 
	 * @param container The containing View in which the page will be shown.
	 * @param position The page position to be instantiated.
	 * @return Returns an Object representing the new page. This does not
	 *         need to be a View, but can be some other container of the
	 *         page.
	 */
	@Override
	public Object instantiateItem(View collection, int position)
	{
		((ViewPager) collection).addView(pager_collection.get(position), 0);

		return pager_collection.get(position);
	}

	@Override
	public void destroyItem(View collection, int position, Object view)
	{
		((ViewPager) collection).removeView((View) view);
	}


	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == ((View) object);
	}


	@Override
	public void finishUpdate(View arg0)
	{
	}


	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}


	public String getTitle(int position)
	{
		Resources res = context.getResources();
		
		switch(position)
		{
			case 0: return res.getString(R.string.journey_week);
			case 1: return res.getString(R.string.journey_sat);
			case 2: return res.getString(R.string.journey_sun);
			default: throw new UnsupportedOperationException("Position of " + position + " is invalid");
		}
	}
	
	

}