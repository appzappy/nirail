package appzappy.nirail.ui.widgets;

import java.util.List;


import appzappy.nirail.R;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.mode.IUIInterface;
import appzappy.nirail.mode.UIInterfaceFactory;
import appzappy.nirail.pathfinding.Journey;
import appzappy.nirail.ui.adapters.JourneySet_Adapter;
import appzappy.nirail.util.AndroidVersion;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class JourneySet_PagerUIElement extends LinearLayout
{
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();
	
	JourneySet_Adapter adapter = null;
	ListView listView = null;
	
	public ListView getListView() {
      return listView;
    }

    public void setListView(ListView listView) {
      this.listView = listView;
    }

  @SuppressLint("NewApi")
    public JourneySet_PagerUIElement(Context context, List<Journey> journeys, OnItemClickListener clickEvent, boolean filter)
	{
		super(context);
		
		Activity activity = (Activity) context;
		
		this.setOrientation(LinearLayout.VERTICAL);
		// add the top row
		LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout row = (LinearLayout) inflater.inflate(R.layout.ui_journey_set_headers, this, false);
		this.addView(row);
		
		adapter = new JourneySet_Adapter(context, journeys, filter);
		
		listView = (ListView) inflater.inflate(R.layout.ui_journey_listview, this, false);
		
		short now = dataInterface.getCurrentTime();
		
		listView.setSelection(adapter.getFirstNew(now));

		if (AndroidVersion.isValid(8))
		{
			listView.smoothScrollToPosition(adapter.getCount(), adapter.getFirstNew(now)-1);
		}
		else
		{
			listView.setSelectionFromTop(adapter.getFirstNew(now), 20);
		}
		
		listView.setOnItemClickListener(clickEvent);
		listView.setAdapter(adapter);
		
		this.addView(listView);
		
		activity.registerForContextMenu(listView);
	}
	
	public void setHighLightedRoute(Route route, int hightlightedColor)
	{
		adapter.setHighlightedRoute(route, hightlightedColor);
	}
}
