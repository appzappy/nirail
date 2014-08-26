package appzappy.nirail.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to contain code for finding a url within a string
 * @author Kurru
 *
 */
public class FindURL
{
	
	/**
	 * Find the 1st url in a string if any exist
	 * @see http://stackoverflow.com/questions/285619/java-how-to-detect-the-presence-of-url-in-a-string/285880#285880
	 */
	public static String find(String message)
	{
        String s = message;
        // separate input by spaces ( URLs don't have spaces )
        String [] parts = s.split("\\s");

        // Attempt to convert each item into an URL.   
        for(String item : parts )
        {
        	String i = item;
        	if (i.length() > 0 && i.charAt(0) == '(' && i.endsWith(")"))
        	{
        	    i = s.substring(1, i.length() - 2);
        	}
        	
        	if (i.length() == 0)
        		continue;
        	try
        	{
        		new URL(i);// If possible then replace with anchor...
        		return i;    
    	    }
        	catch (MalformedURLException e)
        	{
	            // If there was an URL that was not it!...
	            
	        }
        	catch (StringIndexOutOfBoundsException e)
        	{
        		//Bypass current issue of string being out of bounds 
        		//(likely due to malformed url like string in twitter feed, fucking Translink!)
        	}
		}

        return null;
    }
	
	private FindURL(){}
}
