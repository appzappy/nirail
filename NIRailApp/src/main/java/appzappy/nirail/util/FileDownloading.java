package appzappy.nirail.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * Simplifies downloading of files from the internet
 * @author Kurru
 */
public class FileDownloading
{
	/**
	 * Download a text file from the Internet
	 * @param targetUrl The URL of the file to download
	 * @return The string contents of the files OR NULL if error occurred
	 * @see http://stackoverflow.com/questions/2922210/reading-text-file-from-server-on-android/2922340#2922340
	 */
	public static String downloadFile(String targetUrl)
	{
		BufferedReader in = null;
		try
		{
			// Create a URL for the desired page
			URL url = new URL(targetUrl);

			// Read all the text returned by the server
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			StringBuilder sb = new StringBuilder(16384); // 16kb
			String str = in.readLine();
			if (str != null)
			{
				sb.append(str);
				str = in.readLine();
			}
			while (str != null)
			{
				// str is one line of text; readLine() strips the newline
				// character(s)
				sb.append(C.new_line());
				sb.append(str);
				str = in.readLine();
			}
			
			String output = sb.toString();
			return output;
		}
		catch (MalformedURLException e)
		{}
		catch (IOException e)
		{}
		finally
		{
			try
			{
				if (in != null) in.close();
			}
			catch (IOException e)
			{
				
			}
		}
		return null;
	}

	private FileDownloading()
	{}
	
}
