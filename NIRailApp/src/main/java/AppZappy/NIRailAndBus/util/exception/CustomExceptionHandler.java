package AppZappy.NIRailAndBus.util.exception;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import AppZappy.NIRailAndBus.data.db.SQLiteManager;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.util.ApplicationInformation;
import AppZappy.NIRailAndBus.util.C;
import AppZappy.NIRailAndBus.util.FileActions;
import AppZappy.NIRailAndBus.util.timing.TimestampFormatter;
import android.content.Context;
import android.os.Debug;

/**
 * The custom exception handler for the application. Is responsible for handling crash and stack data on Force Closes 
 * @author Kurru
 * @see http://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application/755151#755151
 */
public class CustomExceptionHandler implements UncaughtExceptionHandler
{
	private UncaughtExceptionHandler defaultUEH;

	private String url;

	/**
	 * The URL for the uploading of error details
	 */
	public static final String DEFAULT_URL = "http://www.appzappy.co.uk/applicationErrors/uploadErrors.php";

	/**
	 * Create this exception handler using the defaults for AppZappy
	 */
	public CustomExceptionHandler()
	{
		this(DEFAULT_URL);
	}

	/**
	 * Create a new CustomExceptionHandler
	 * @param url If url is null, error messages won't be uploaded
	 */
	public CustomExceptionHandler(String url)
	{
		this.url = url;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	/**
	 * Handle an exception
	 * @param e The exception
	 */
	public void uncaughtException(Throwable e)
	{
		uncaughtException(Thread.currentThread(), e);
	}

	/**
	 * Handle an exception
	 * @param thread The thread the exception originated from
	 * @param exception The exception
	 */
	public void uncaughtException(Thread thread, Throwable exception)
	{
		String timestamp = TimestampFormatter.getInstance().getTimestamp();
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);

		String stacktrace = result.toString();
		
		
		// formatter for byte numbers
		NumberFormat formater = NumberFormat.getNumberInstance(Locale.ENGLISH);
		
		DecimalFormat df = (DecimalFormat) formater;
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		

		String outputData = stacktrace + C.new_line_double();
		
		String heapdata = "";
		heapdata += "debug.heap native\t: allocated: " + bytesToString(Debug.getNativeHeapAllocatedSize(), df) + " of " + bytesToString(Debug.getNativeHeapSize(), df) + " (" + bytesToString(Debug.getNativeHeapFreeSize(), df) + " free)" + C.new_line();
		heapdata += "debug.memory\t\t: allocated: " + bytesToString(Runtime.getRuntime().totalMemory(), df) + " of " + bytesToString(Runtime.getRuntime().maxMemory(), df) + " (" + bytesToString(Runtime.getRuntime().freeMemory(), df) + " free)";
		outputData += heapdata;
		
		
		outputData += C.new_line_double();
		outputData += "Android Version\t\t: " + android.os.Build.VERSION.RELEASE + C.new_line();
		outputData += "Manufacturer\t\t: " + android.os.Build.MANUFACTURER + C.new_line();
		outputData += "Phone Model\t\t: " + android.os.Build.MODEL + C.new_line();
		outputData += "User\t\t\t: " + android.os.Build.USER + C.new_line();
		outputData += "Tags\t\t\t: " + android.os.Build.TAGS + C.new_line();
		
		outputData += C.new_line();
		
		Context context = UIInterfaceFactory.getInterface().getAndroidContext();

		try
		{
			outputData += "App Mode\t\t: " + ProgramMode.singleton().getMode().toString() + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{
			outputData += "App Package\t\t: " + context.getPackageName() + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{
			outputData += "App Version Code\t: " + ApplicationInformation.getVersionCode(context) + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{
			outputData += "App Version Name\t: " + ApplicationInformation.getVersionNumber(context) + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{

		outputData += "SD Card?\t\t: " + FileActions.isSDCardAvailable() + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{
			outputData += "SD Space\t\t: " + bytesToString(FileActions.getRemainingSpaceOnSDCard(), df) + " / " + bytesToString(FileActions.getSDCardSize(), df) + C.new_line();
		}
		catch(Exception ignore) {}
		try
		{
			outputData += "Local Space\t\t: " + bytesToString(FileActions.getRemainingLocalStorage(), df)  + C.new_line();
		}
		catch(Exception ignore) {}

		outputData += C.new_line();
	
		try
		{
			boolean isDev = ApplicationInformation.isDevelopmentBuild(context);
			String hashes = "######################################";
			if (isDev)
			{
				outputData += hashes + C.new_line();
			}
			outputData += "Development Build\t: " + ApplicationInformation.isDevelopmentBuild(context) + C.new_line();
			if (isDev)
			{
				outputData += hashes + C.new_line();
			}
		}
		catch(Exception ignore) {}
		
		
		printWriter.close();
		String filename = "NI_RailAndBus-" + timestamp + ".stacktrace";

		
		writeToFile(outputData, filename);
		
		if (url != null)
		{
			sendToServer(outputData);
		}

		// Delete the sql databases
		SQLiteManager.deleteDatabases();
		
		defaultUEH.uncaughtException(thread, exception);
	}


	/**
	 * Generate a human readable string for a byte value
	 * @param bytes The number of bytes
	 * @param formater Formater to handle number formatting
	 * @return "10B", "1.37KB", "9.12MB" etc
	 */
	private static String bytesToString(long bytes, DecimalFormat formater)
	{
		int kilo = 1024;
		long mega = kilo * 1024;
		long giga = mega * 1024;
		
		if (bytes < kilo)
		{
			return bytes + "B";
		}
		
		if (bytes < mega)
		{
			double number = (double) bytes / kilo;
			return formater.format(number) + "KB";
		}
		
		if (bytes < giga)
		{
			double number = (double) bytes / mega;
			return formater.format(number) + "MB";
		}
		
		double number = (double) bytes / giga;
		return formater.format(number) + "GB";

	}

	/**
	 * Write the data to a file in the errors folder
	 * @param details The crash details
	 * @param filename The filename for the error log
	 */
	private void writeToFile(String details, String filename)
	{
		FileActions.writeFile(FileActions.getFileTarget("error/"+filename), details);
	}
	
	/**
	 * Trigger an exception
	 * @param throwable The exception
	 */
	public static void triggerException(Throwable throwable)
	{
		CustomExceptionHandler handler = new CustomExceptionHandler();
		handler.uncaughtException(throwable);
	}

	/**
	 * Trigger an exception
	 * @param message The message to attach to the message
	 */
	public static void triggerException(String message)
	{
		CustomExceptionHandler handler = new CustomExceptionHandler();
		handler.uncaughtException(new RuntimeException(message));
	}

	/**
	 * Trigger an exception
	 * @param message The message to attach to the message
	 * @param throwable The exception
	 */
	public static void triggerException(String message, Throwable throwable)
	{
		CustomExceptionHandler handler = new CustomExceptionHandler();
		handler.uncaughtException(new RuntimeException(message, throwable));
	}
	
	/**
	 * Send the error message to the server
	 * @param details The details of the exception
	 * @param filename The filename on the server
	 */
	private void sendToServer(String details)
	{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("stacktrace", details));
		try
		{
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpClient.execute(httpPost);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return "CustomExceptionHandler: " + this.url;
	}
}
