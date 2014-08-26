package appzappy.nirail.util.exception;

import java.io.IOException;

/**
 * Exception for no space remaining
 * @author Kurru
 *
 */
public class NoSpaceLeftOnDeviceException extends RuntimeException
{

	/**
	 * Check an exception for "No space left on device"
	 * @param e The IOException to check
	 */
	public static void throwNoSpaceOnDevice(IOException e)
	{
		if (e.getMessage().contains("No space left on device"))
			throw new NoSpaceLeftOnDeviceException();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1480609790662550369L;

}
