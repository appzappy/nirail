package AppZappy.NIRailAndBus.ui.activities;


import java.util.List;


import AppZappy.NIRailAndBus.R;
import AppZappy.NIRailAndBus.data.LoadData;
import AppZappy.NIRailAndBus.data.model.Location;
import AppZappy.NIRailAndBus.delays.DelayItem;
import AppZappy.NIRailAndBus.events.IEventListener;
import AppZappy.NIRailAndBus.mode.IUIInterface;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.mode.ProjectUpdater;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.pathfinding.Journey;
import AppZappy.NIRailAndBus.ui.UIUtils;
import AppZappy.NIRailAndBus.ui.adapters.Dashboard_DelayAdapter;
import AppZappy.NIRailAndBus.ui.adapters.Dashboard_FavouritesAdapter;
import AppZappy.NIRailAndBus.ui.adapters.Dashboard_UpcomingAdapter;
import AppZappy.NIRailAndBus.userdata.Favourite;
import AppZappy.NIRailAndBus.userdata.Settings;
import AppZappy.NIRailAndBus.util.ApplicationInformation;
import AppZappy.NIRailAndBus.util.L;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ListView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class Dashboard extends Activity
{
	private SlidingDrawer slidingDrawer;
	private ListView delayList;
	private Dashboard_DelayAdapter delayAdapter;
	private Gallery favouritesGallery = null;
	private ListView upcomingList = null;
	private Dashboard_FavouritesAdapter favouritesAdapter = null;
	private Dashboard_UpcomingAdapter upcomingAdapter = null;
	private Favourite _fav = null;
	private LinearLayout dashboard = null;
	private TextView expandMessage = null;
	private LinearLayout upcoming_bit = null;
	
	private TextView delaybar_notifications = null;

	private ImageButton delayRefreshButton;
	private TextView delays_failed;

	private RelativeLayout delayLoading, favouritesMissing, upcomingMissing;

	IEventListener delaysLoadedListener = new IEventListener()
	{
		public void action(Object optionalData)
		{
			Dashboard.this.runOnUiThread(new Runnable()
			{
				public void run()
				{
					// update the delay window with this data
					updateDelayList();
				}
			});
		}
	};



	/**
	 * Used to retrieve raw data from the system
	 */
	private static IUIInterface dataInterface = UIInterfaceFactory.getInterface();

	
	
	Handler slidingDrawer_handler = new Handler();
	int handleInterval = 300;

	Runnable refreshMonitorer = new Runnable()
	{
		public void run()
		{
			if (slidingDrawer.isMoving())
			{
				hideDelayRefresh();
				// still moving so check again
				disableHandleCallback(); 
				enableHandleCallback();
			}

			if (!slidingDrawer.isMoving())
			{
				if (slidingDrawer.isOpened())
				{
					showDelayRefresh();
					if (_newest_guid > 0)
					{
						Settings.setDisplayedDelayGUID(_newest_guid);
					}
				}
				else
				{
					hideDelayRefresh();
				}
				// sliding drawer not moving anymore so stop checking
				disableHandleCallback(); 
			}

		}
	};
	boolean isHandlerActive = false;

	void enableHandleCallback()
	{
		if (isHandlerActive)
			return;
		isHandlerActive = true;
		// register a new one
		slidingDrawer_handler.postDelayed(refreshMonitorer, handleInterval); 
	}

	void disableHandleCallback()
	{
		if (!isHandlerActive)
			return;
		isHandlerActive = false;
		// remove the old callback
		slidingDrawer_handler.removeCallbacks(refreshMonitorer); 
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		LoadData.initialise(this);
		setTheme(Settings.getProgramThemeID());
		setContentView(R.layout.ui_dashboard);
		
		dashboard = (LinearLayout) findViewById(R.id.dashboardPanel);

		slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);		
		
		delayRefreshButton = (ImageButton) findViewById(R.id.delay_refresh);
		delaybar_notifications = (TextView) findViewById(R.id.delaybar_notifications);
		
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener()
		{
			public void onDrawerOpened()
			{
				enableHandleCallback();
			}
		});
		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener()
		{
			public void onDrawerClosed()
			{
				enableHandleCallback();
				if (!Dashboard.this.slidingDrawer.isOpened())
				{
					// not open?
					// hide the delay count
					delaybar_notifications.setText("");
					delaybar_notifications.setBackgroundResource(0);
				}
			}
		});
		slidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener()
		{
			public void onScrollStarted()
			{
				hideDelayRefresh();
				enableHandleCallback();
			}

			public void onScrollEnded()
			{
				
			}
		});

		delayLoading = (RelativeLayout) findViewById(R.id.delay_loadingPanel);

		delays_failed = (TextView) findViewById(R.id.delays_failedtext);

		delayList = (ListView) this.findViewById(R.id.contentLayoutList);
		delayAdapter = new Dashboard_DelayAdapter(this);
		delayList.setAdapter(delayAdapter);

		delayList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				DelayItem item = delayAdapter.getItem(position);
				String link = item.getLink();
				if (link != null)
				{
					UIUtils.gotoURL(Dashboard.this, link);
				}
			}
		});

		upcoming_bit = (LinearLayout) findViewById(R.id.dash_upcoming_section);
		
		upcomingList = (ListView) this.findViewById(R.id.mainwindow_upcomingList);
		upcomingAdapter = new Dashboard_UpcomingAdapter(this, this);
		upcomingList.setAdapter(upcomingAdapter);
		upcomingList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Journey rsp = upcomingAdapter.getItem(position);
				JourneySet.openNew(Dashboard.this, rsp.getFirstPortion().getStart(), rsp.getFinalPortion().getEnd(), rsp.getFirstPortion().getRoute());
			}
		});
		
		registerForContextMenu(upcomingList);
		
		expandMessage = (TextView) findViewById(R.id.upcomingExpandText);
		

		favouritesGallery = (Gallery) this.findViewById(R.id.mainwindow_favouritesGallery);
		favouritesAdapter = new Dashboard_FavouritesAdapter(this);
		favouritesGallery.setAdapter(favouritesAdapter);
		favouritesGallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Favourite f = favouritesAdapter.getItem(position);
				if (f.isLinkedRoute())
					UIUtils.goFavourite(Dashboard.this, f);
				else
				{
					registerForContextMenu(favouritesGallery);
					_fav = f;
					openContextMenu(view);
				}
			}
		});

		favouritesMissing = (RelativeLayout) findViewById(R.id.favourites_missingPanel);
		favouritesMissing.setVisibility(View.GONE);

		upcomingMissing = (RelativeLayout) findViewById(R.id.upcoming_missingPanel);
		upcomingMissing.setVisibility(View.GONE);
	}


	private static ProjectUpdater UpdateChecker = null;

	private void displayUpdateNotice()
	{
		// record the prompt has been displayed for today
		UpdateChecker.promptDisplayed();

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.update_title);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.update_accept, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse(ApplicationInformation.MARKET_URL) ) );
			}
		});
		builder.setNegativeButton(R.string.update_cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
			}
		});

		builder.setMessage(R.string.update_message);
		builder.create().show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		// ensure the system is initialised
		LoadData.initialise(this);
		
	
		
		if (UpdateChecker == null)
		{
			UpdateChecker = new ProjectUpdater();
		}
		UpdateChecker.check();
		if (UpdateChecker.shouldDisplayPrompt())
		{
			displayUpdateNotice();
		}

		
		
		if (Settings.isDisplaying_Favourites())
		{
			// show and update the favourites
			favouritesGallery.setVisibility(View.VISIBLE);
			favouritesMissing.setVisibility(View.VISIBLE);
			updateFavourites();
		}
		else
		{
			// hide the favourites
			favouritesGallery.setVisibility(View.GONE);
			favouritesMissing.setVisibility(View.GONE);
		}
		
		
		
		if (Settings.isDisplaying_Upcoming())
		{
			// show and update the upcoming display
			upcomingMissing.setVisibility(View.VISIBLE);
			upcoming_bit.setVisibility(View.VISIBLE);
			updateUpcoming();
		}
		else
		{ 
			// hide the upcoming display
			upcomingMissing.setVisibility(View.GONE);
			upcoming_bit.setVisibility(View.GONE);
		}
		
		
		updateDelayList();
		
		dataInterface.getDelayLoadedEvent().addListener(delaysLoadedListener);

		
		if (dataInterface.getLoadedDelays() != null)
		{
			updateDelayList();
		}
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		dataInterface.getDelayLoadedEvent().removeListener(delaysLoadedListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return UIUtils.setUpContextMenu(menu, getMenuInflater());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean res = UIUtils.handleContextMenuClick(this, item);
		if (!res)
			return super.onOptionsItemSelected(item);
		else
			return true;
	}

	
	private short previous_upcoming_update = -1;
	
	private void updateUpcoming()
	{
		if (upcomingAdapter.isEmpty())
		{
			upcomingMissing.setVisibility(View.VISIBLE);
			upcoming_bit.setVisibility(View.GONE);
		}
		else
		{
			upcomingMissing.setVisibility(View.GONE);
			upcoming_bit.setVisibility(View.VISIBLE);
		}
		
		
		short currentTime = dataInterface.getCurrentTime();
		if (previous_upcoming_update == -1 || previous_upcoming_update != currentTime)
		{
			previous_upcoming_update = currentTime;
			
			
			// upcoming data may have changed, re-calculate and display
			
			List<Journey> upcomingJourneys = dataInterface.getUpcomingDepartures(3);
			upcomingAdapter.clear();
			for (Journey journey : upcomingJourneys)
			{
				upcomingAdapter.add(journey);
			}
			
			if (upcomingJourneys.isEmpty())
			{
				upcomingMissing.setVisibility(View.VISIBLE);
				upcoming_bit.setVisibility(View.GONE);
			}
			else
			{
				upcomingMissing.setVisibility(View.GONE);
				upcoming_bit.setVisibility(View.VISIBLE);
			}
			
		}
		
	}

	public void updateFavourites()
	{
		List<Favourite> favourites = dataInterface.getFavourites();
		favouritesAdapter.clear();
		for (Favourite f : favourites)
		{
			favouritesAdapter.add(f);
		}

		int middleOfFavourites = favouritesAdapter.getCount() / 2;
		favouritesGallery.setSelection(middleOfFavourites);

		if (favourites.isEmpty())
		{
			favouritesMissing.setVisibility(View.VISIBLE);
			favouritesGallery.setVisibility(View.GONE);
		}
		else
		{
			favouritesMissing.setVisibility(View.GONE);
			favouritesGallery.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) // && event.getRepeatCount() == 0
		{
			if (slidingDrawer.isMoving())
			{
				return true;
			}
			if (slidingDrawer.isOpened())
			{
				slidingDrawer.animateClose();
				return true;
			}
			if (upcomingDeparturesActive)
			{
				upcomingBarClick(null);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}


	private long _newest_guid = -1;
	
	/**
	 * Update the delay list with the delays found in the delay downloader
	 */
	public void updateDelayList()
	{
		List<DelayItem> items = dataInterface.getLoadedDelays();
		if (items == null)
		{
			// ERROR loading delays
			delays_failed.setVisibility(View.VISIBLE);
			delayLoading.setVisibility(View.GONE);
			delayList.setVisibility(View.GONE);
		}
		else
		{
			// Delays retrieved successfully
			delays_failed.setVisibility(View.GONE);
			delayLoading.setVisibility(View.GONE);
			delayList.setVisibility(View.VISIBLE);

			delayAdapter.clear();
			for (DelayItem i : items)
			{
				delayAdapter.add(i);
			}
			
			long saved_guid = Settings.getDisplayedDelayGUID();
			
			int new_items = 0;
			
			L.d("Saved GUID: " + saved_guid);
			for (int i=0;i<items.size();i++)
			{
				L.d("Delay GUID: " + items.get(i).getGUID());
				if (items.get(i).getGUID() == saved_guid)
					break;
				if (items.get(i).getGUID() < saved_guid)
					break;
				new_items++;
			}
			
			if (new_items > 0)
			{
				delaybar_notifications.setText(new_items + "");
				delaybar_notifications.setBackgroundResource(R.drawable.dash_notification);
				//delaybar_notifications.setBackgroundResource(android.R.drawable.btn);
			}
			
			if (items.size() > 0)
			{
				_newest_guid = items.get(0).getGUID();
			}
			
			
		}
	}

	public void onReloadDelays(View v)
	{
		delayLoading.setVisibility(View.VISIBLE);
		delayList.setVisibility(View.GONE);

		dataInterface.startDownloadingDelays();
	}

	public void showDelayRefresh()
	{
		delayRefreshButton.setVisibility(View.VISIBLE);
	}

	public void hideDelayRefresh()
	{
		delayRefreshButton.setVisibility(View.GONE);
	}


	int upcoming_position_lower = -1;
	int upcoming_position_higher = -1;
	int upcoming_moving_difference = 0;
	private static final int UPCOMING_ANIMATION_TIME = 500;
	boolean upcomingDeparturesActive = false;

	public void upcomingBarClick(View v)
	{	
		if (upcoming_position_lower == -1)
		{
			upcoming_position_lower = dashboard.getTop();
			upcoming_position_higher = upcoming_bit.getTop();
			upcoming_moving_difference = upcoming_position_lower - upcoming_position_higher;
		}
		if (upcomingDeparturesActive)
		{
			// shrink the upcoming items
			
			// animation to move the upcoming bar down
			TranslateAnimation trans = new TranslateAnimation(0, 0, 0, -upcoming_moving_difference);
			trans.setDuration(UPCOMING_ANIMATION_TIME);
			trans.setFillEnabled(true);
			trans.setFillBefore(true);
			trans.setInterpolator(new AccelerateDecelerateInterpolator());
			trans.setAnimationListener(new AnimationListener()
			{
				public void onAnimationStart(Animation arg0)
				{
				}
				public void onAnimationRepeat(Animation arg0)
				{
				}
				public void onAnimationEnd(Animation arg0)
				{
					dashboard.setVisibility(View.VISIBLE);
				}
			});
			upcoming_bit.startAnimation(trans);
			
			
			// animation to show the dashboard again
			AlphaAnimation fade_in = new AlphaAnimation(0.0f, 1.0f);
			fade_in.setDuration(UPCOMING_ANIMATION_TIME);
			fade_in.setAnimationListener(new AnimationListener()
			{
				public void onAnimationStart(Animation arg0)
				{
				}
				public void onAnimationRepeat(Animation arg0)
				{
				}
				
				public void onAnimationEnd(Animation arg0)
				{
					if (dashboard.getVisibility() == View.INVISIBLE)
						dashboard.setVisibility(View.VISIBLE);
				}
			});
			dashboard.startAnimation(fade_in);
			
			expandMessage.setText(R.string.dash_UpcomingExpand);
			upcomingDeparturesActive = false;
		}
		else
		{
			// expand the upcoming items
			
			// animation to move the upcoming section up
			TranslateAnimation trans = new TranslateAnimation(0, 0, 0, upcoming_moving_difference);
			trans.setDuration(UPCOMING_ANIMATION_TIME);
			trans.setFillEnabled(true);
			trans.setFillBefore(true);
			trans.setInterpolator(new AccelerateDecelerateInterpolator());
			trans.setAnimationListener(new AnimationListener()
			{
				public void onAnimationStart(Animation arg0)
				{
				}
				
				public void onAnimationRepeat(Animation arg0)
				{
				}
				
				public void onAnimationEnd(Animation arg0)
				{
					dashboard.setVisibility(View.GONE);
				}
			});
			upcoming_bit.startAnimation(trans);
			
			// animation to hide the dashboard
			AlphaAnimation fade_out = new AlphaAnimation(1.0f, 0.0f);
			fade_out.setDuration(UPCOMING_ANIMATION_TIME);
			fade_out.setAnimationListener(new AnimationListener()
			{
				public void onAnimationStart(Animation arg0)
				{
				}
				public void onAnimationRepeat(Animation arg0)
				{
				}
				
				public void onAnimationEnd(Animation arg0)
				{
					if (dashboard.getVisibility() == View.VISIBLE)
						dashboard.setVisibility(View.INVISIBLE);
				}
			});
			dashboard.startAnimation(fade_out);
			
			expandMessage.setText(R.string.dash_UpcomingContract);
			upcomingDeparturesActive = true;
		}
	}
	
	
	/** Handle "Map" action **/
	public void onMapClick(View v)
	{
		startActivity(new Intent(this, StationMap.class));
	}

	/** Handle "AppZappy" action **/
	public void onAppZappyClick(View v)
	{
		AboutUs.openNew(this);
	}

	/**
	 * Toggle Transport Mode 
	 */
	public void onTransportModeClick(View v)
	{
		ProgramMode.toggleMode();
		LoadData.initialiseReset(this);
		UIUtils.goHome(this);
	}
	
	/** Handle "Favourite" action **/
	public void onStationListClick(View v)
	{
		StationList.openNew(this);
	}

	/** Handle "Search" action **/
	public void onSearchClick(View v)
	{
		SearchWindow.openNew(this);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.mainwindow_upcomingList)
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
			Journey journey = upcomingAdapter.getItem(info.position);
			UIUtils.createJourneyMenu(menu, v, journey);
		}
		
		if (v.getId() == R.id.mainwindow_favouritesGallery)
		{
			Location location = dataInterface.getLocation(_fav.getDestination());
			UIUtils.createLocationMenu(menu, v, location);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getGroupId() == 0)
		{
			Location location = dataInterface.getLocation(_fav.getDestination());
			UIUtils.handleLocationClickAction(this, item, location);
		}
		
		if (item.getGroupId() == 1)
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Journey journey = upcomingAdapter.getItem(info.position);
			dataInterface.handleJourneyClickAction(this, item, journey);
		}
		return super.onContextItemSelected(item);
	}

}