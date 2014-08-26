package appzappy.nirail.ui;


import appzappy.nirail.R;
import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.data.model.Location;
import appzappy.nirail.data.model.Route;
import appzappy.nirail.data.model.Stop;
import appzappy.nirail.pathfinding.Journey;
import appzappy.nirail.pathfinding.JourneyPortion;
import appzappy.nirail.ui.activities.AboutUs;
import appzappy.nirail.ui.activities.Favourites;
import appzappy.nirail.ui.activities.JourneySet;
import appzappy.nirail.ui.activities.PreferencesWindow;
import appzappy.nirail.ui.activities.RouteWindow;
import appzappy.nirail.ui.activities.SearchWindow;
import appzappy.nirail.ui.activities.StationWindow;
import appzappy.nirail.userdata.Favourite;
import appzappy.nirail.userdata.Favourite.FavouriteType;
import appzappy.nirail.util.C;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class UIUtils
{
	public static void goHome(Context context)
	{
		final Intent intent = new Intent(context, FragmentedDashboard.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	/**
	 * Get the resource ID for the bus or train icon
	 * 
	 * @param type
	 * @return
	 */
	public static int getImageForLocationType(TransportType type)
	{
		switch (type)
		{
			case Metro:
				return R.drawable.dash_bus_btn_default;
			case Train:
				return R.drawable.dash_train_btn_default;
			default:
				throw new UnsupportedOperationException("TransportType not supported: " + type.toString());
		}
	}

	/**
	 * Get the resource ID for the bus or train icon
	 * 
	 * @param type
	 * @return
	 */
	public static int getImageForFavouriteType(FavouriteType type)
	{
		switch (type)
		{
			case station:
				return R.drawable.dash_fav_station_btn;
			case journey:
				return R.drawable.dash_fav_journey_btn;
			default:
				throw new UnsupportedOperationException("FavouriteType not supported: " + type.toString());
		}
	}

	/**
	 * Get the resource ID for the bus or train station
	 * 
	 * @param type
	 * @return
	 */
	public static int getImageForStationType(TransportType type)
	{
		switch (type)
		{
			case Metro:
				return R.drawable.btn_station_marker_default;
			case Train:
				return R.drawable.btn_station_marker_default;
			default: 
				throw new UnsupportedOperationException("No support for TransportType." + type.toString());
		}
	}

	public static void hideKeyboard(Context context, View view)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showKeyboard(Context context, View view)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	public static void goFavourite(Context context, Favourite favourite)
	{
		if (favourite.isLinkedRoute())
		{
			// linked route
			JourneySet.openNew(context, favourite.getSource(), favourite.getDestination());
		}
		else
		{
			// only destination
			SearchWindow.openNew(context, null, favourite.getDestination());
		}
	}

	public static boolean setUpContextMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	public static boolean handleContextMenuClick(Activity activity, MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.context_menu_favourites:
				Favourites.openNew(activity);
				return true;
			case R.id.context_menu_aboutus:
				AboutUs.openNew(activity);
				return true;
			case R.id.context_menu_preferences:
				PreferencesWindow.openNew(activity);
				return true;
			case R.id.context_menu_issues:
				display_issues_dialog(activity);
				return true;
			default:
				return false;
		}
	}
	
	public static final void display_issues_dialog(Activity activity)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.title_issues);
		builder.setCancelable(true);
		builder.setMessage(R.string.title_issues_text);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	public static final String FAVOURITE_MENU_OPTION__STATION_DETAILS = "View Station Details";
	public static final String FAVOURITE_MENU_OPTION__TO_HERE = "Search to here";
	public static final String FAVOURITE_MENU_OPTION__FROM_HERE = "Search from here";
	public static final String ROUTE_MENU_OPTION__ROUTE_PAGE = "View full route";
	public static final String ROUTE_MENU_OPTION__TRAIN_REMINDER = "Set Train Reminder";
	public static final String ROUTE_MENU_OPTION__COPY_TRAIN = "Copy info to Clipboard";


	public static boolean handleLocationClickAction(Context context, MenuItem item, Location location)
	{
		if (item.getTitle().equals(FAVOURITE_MENU_OPTION__STATION_DETAILS))
		{
			StationWindow.openNew(context, location);
			return true;
		}
		else if (item.getTitle().equals(FAVOURITE_MENU_OPTION__TO_HERE))
		{
			SearchWindow.openNew(context, null, location);
			return true;
		}
		else if (item.getTitle().equals(FAVOURITE_MENU_OPTION__FROM_HERE))
		{
			SearchWindow.openNew(context, location, null);
			return true;
		}
		return false;
	}
	
	public static void createLocationMenu(ContextMenu menu, View view, Location location)
	{
		menu.setHeaderTitle(location.getRealName());
		menu.add(0, view.getId(), 0, FAVOURITE_MENU_OPTION__STATION_DETAILS);
		menu.add(0, view.getId(), 0, FAVOURITE_MENU_OPTION__TO_HERE);
		menu.add(0, view.getId(), 0, FAVOURITE_MENU_OPTION__FROM_HERE);
	}
	
	public static void createJourneyMenu(ContextMenu menu, View view, Journey journey)
	{
		menu.setHeaderIcon(UIUtils.getImageForLocationType(journey.getFirstPortion().getTransportType()));
		menu.setHeaderTitle(journey.getStartingLocationName() + " to " + journey.getEndingLocationName() +
			C.new_line() + journey.getStartingTimeFormated() + " - " + journey.getEndingTimeFormated());
		
		menu.add(1, view.getId(), 0, ROUTE_MENU_OPTION__ROUTE_PAGE);
		menu.add(1, view.getId(), 0, ROUTE_MENU_OPTION__TRAIN_REMINDER);
		menu.add(1, view.getId(), 0, ROUTE_MENU_OPTION__COPY_TRAIN);
		
	}
	
	public static final String MAP_ROUTE__TO_ROUTE = "View Route";
	
	public static boolean handleTrainClickAction(Context context, MenuItem item, Route route)
	{
		if (item.getTitle().equals(MAP_ROUTE__TO_ROUTE))
		{
			Stop s = route.getFirstStop();
			Stop e = route.getFinalStop();
			JourneyPortion portion = JourneyPortion.create(s.getLocation(), s.getTime(), e.getLocation(), e.getTime(), route);
			RouteWindow.openNew(context, portion);
			return true;
		}
		return false;
	}
	
	public static void createTrainMenu(ContextMenu menu, View view, Route route, short currentTime)
	{
		Stop next = route.getNextStop(currentTime);
		Stop end = route.getFinalStop();
		String title_text = "Next: ";
		title_text += next.getLocation().getRealName();
		title_text += " @ " + next.getFormatedTime() + C.new_line();
		title_text += "End: ";
		title_text += end.getLocation().getRealName();
		title_text += " @ " + end.getFormatedTime();
		
		menu.setHeaderTitle(title_text);
		menu.add(0, view.getId(), 0, MAP_ROUTE__TO_ROUTE);
	}


	public static void displayEmailScreen(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "info@appzappy.co.uk", null));
		context.startActivity(intent);
	}
	
	public static void displayBugFeatureEmailScreen(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "requests@appzappy.co.uk", null));
		context.startActivity(intent);
	}

	public static void gotoURL(Context context, String url)
	{
		Intent website = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(website);
	}

	public static void gotoAppzappycouk(Context context)
	{
		gotoURL(context, "http://www.appzappy.co.uk");
	}
	
	public static void gotoAppzappyBugs(Context context)
	{
		gotoURL(context, "http://www.appzappy.co.uk/bugsfeedback");
	}

	public static void gotoTwitter(Context context)
	{
		gotoURL(context, "http://twitter.com/appzappy");
	}

	public static void gotoFacebookPage(Context context)
	{
		gotoURL(context, "http://www.facebook.com/pages/AppZappy/141240655932573");
	}

	
	
	/**
	 * Trim a string so that it fits within a set width
	 * @param painter The painter used for displaying the text
	 * @param initialText The string to shorten
	 * @param width Width in px's of the string
	 * @param shortedString The string to add if the string is shortened. ie "..." 
	 * @return 
	 */
	public static String trimStringToLength(TextPaint painter, String initialText, String shortenedString, double width)
	{
		String message = initialText;
		String output = initialText;
		
		float currentWidth = painter.measureText(output);
		while (currentWidth > width)
		{
			message = message.substring(0, message.length()-1);
			
			output = message + shortenedString;
			
			currentWidth = painter.measureText(output);
		}
		
		return output;
	}
	
	public static String trimStringToLength(TextPaint painter, String initialText, String joiner, String endingText, String shortenedString, double width)
	{
		String first = initialText;
		String second = endingText;
		
		boolean firstShortened = false;
		boolean secondShortened = false;
		
		String message = first + joiner + second;
		
		
		float currentWidth = painter.measureText(message);
		while (currentWidth > width)
		{
			if (first.length() > second.length())
			{
				first = first.substring(0, first.length()-1);
				firstShortened = true;
			}
			else
			{
				second = second.substring(0, second.length()-1);
				secondShortened = true;
			}
			
			message = first;
			if (firstShortened)
				message += shortenedString;
			message += joiner;
			
			message += second;
			if (secondShortened)
				message += shortenedString;
			
			currentWidth = painter.measureText(message);
		}
		
		return message;
	}
	
	public static void setGallerySize_Resource(Activity activity, Gallery gallery, int gallery_item_dimension_id, int gallery_height_dimen_id)
	{
		Resources resources = activity.getResources();
		// dimens returns value in pixels
		float gallery_item_width = resources.getDimension(gallery_item_dimension_id);
		float gallery_height = resources.getDimension(gallery_height_dimen_id);
		setGalleryWidth(activity, gallery, (int)gallery_item_width, (int) gallery_height);
	}
	
	private static void setGalleryWidth(Activity activity, Gallery gallery, int gallery_item_width, int gallery_height_dimen)
	{
		final Display display = activity.getWindowManager().getDefaultDisplay();
		
		final DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		final float itemWidthpx = gallery_item_width;
		final float gallery_height = gallery_height_dimen; 
			
		final int screenWidth = metrics.widthPixels;
		
		// how to make gallery hack
		// gallery width = 2 * screen width - gallery_item_width;
		
		final float gallery_width = screenWidth * 2 - itemWidthpx;
		
		//L.d("Set gallery; width: " + gallery_width + " ; height: " + gallery_height);
		gallery.setLayoutParams(new LinearLayout.LayoutParams((int)gallery_width, (int)gallery_height));
		
	}
}
