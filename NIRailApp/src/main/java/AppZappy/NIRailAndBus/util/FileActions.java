package AppZappy.NIRailAndBus.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.os.StatFs;
import AppZappy.NIRailAndBus.mode.IProgramMode;
import AppZappy.NIRailAndBus.mode.ProgramMode;
import AppZappy.NIRailAndBus.mode.UIInterfaceFactory;
import AppZappy.NIRailAndBus.util.exception.CustomExceptionHandler;
import AppZappy.NIRailAndBus.util.exception.NoSpaceLeftOnDeviceException;

/**
 * Set of functions relating to files
 * @author Kurru
 *
 */
public class FileActions
{
	/**
	 * Is there an SD card available
	 * @return True if SD is available
	 */
	public static boolean isSDCardAvailable()
	{
		return isExternalReadWriteable();
	}
	
	/**
	 * Get a file object within the AppZappy folder
	 * @param relativeFile The relative file location
	 * @return File object representing the file. If SD card is available, file will be placed on SD card, otherwise, on internal storage
	 */
	public static File getFileTarget(String relativeFile)
	{
		return getFileTarget(ProgramMode.singleton(), relativeFile);
	}
	
	public static File getFileTarget(IProgramMode programMode, String relativeFile)
	{
		if (isSDCardAvailable())
		{
			// use the SD card
			File root = Environment.getExternalStorageDirectory();
			File base = new File(root, "appzappy");
			File appMode = new File(base, programMode.getMode().toString());
			return new File(appMode, relativeFile);
		}
		else
		{
			// use the inbuilt memory
			//throw new RuntimeException("No SD card Detected");
			File root = UIInterfaceFactory.getInterface().getAndroidContext().getFilesDir();
			File appMode = new File(root, programMode.getMode().toString());
			return new File(appMode, relativeFile);
		}
	}

	/**
	 * Can both read and write to the SD card
	 * @return True if read and writable
	 */
	private static boolean isExternalReadWriteable()
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		final String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
		{
		    // We can read and write the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = true;
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		}
		else
		{
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = false;
		    mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}
	
	
	/**
	 * Write a file to the long term storage
	 * @param file The target location to write the file
	 * @param text The text to write into the file
	 * @return True if successful; False otherwise
	 */
	public static boolean writeFile(File file, String text)
	{
		if (file.exists()) // delete the old file
		{
			file.delete();
		}
		
		file = new File(file.getAbsolutePath()); // re-initialise the object again in case it caches values
		try // check the file and folder exists 
		{
			final File parent = file.getParentFile();
			if (parent != null)
				parent.mkdirs();
			file.createNewFile();
		}
		catch (IOException e1)
		{
			return false;
		}
		
		FileWriter fw = null;
		try 
		{
			fw = new FileWriter(file); 	// create the writer
			fw.write(text); 			// write the string
			fw.flush(); 				// flush the writer
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			try {if (fw != null) { fw.close(); fw = null;}} // close resources
			catch(IOException e) {}
		}
	}

	/**
	 * Extract text files from zip file
	 * @param zipFile The zip file
	 * @return Map of file data, key's to map are the filenames of the files in the zip
	 */
	public static Map<String, String> extractFilesFromZip(File zipFile)
	{
		Map<String, String> results = new HashMap<String, String>();
		
		ZipInputStream zip_in = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		
		InputStreamReader isr = null;
		BufferedReader din = null;
		try
		{
			if (!zipFile.exists())
			{
				throw new FileNotFoundException("File does not exist: " + zipFile.getAbsolutePath());
			}

			fis = new FileInputStream(zipFile);
		    bis = new BufferedInputStream(fis);
		    
			zip_in = new ZipInputStream(bis);
			
			isr = new InputStreamReader(zip_in);
			din = new BufferedReader(isr);
			
			for (ZipEntry entry = zip_in.getNextEntry(); entry != null; entry = zip_in.getNextEntry())
			{
				try
				{
					din.readLine(); // throw away the 1st line
					
					String str = din.readLine();
					StringWriter writer = new StringWriter();
					while (str != null)
					{
						// str is one line of text; 
						// readLine() strips the newline character(s)
						writer.write(str);
						writer.write(C.new_line());
						str = din.readLine();
					}
					String writerOutput = writer.toString(); 
					writer.close();

					String finalValue = writerOutput;
					String fileName = entry.getName();
					results.put(fileName,finalValue);
				}
				catch(ZipException e)
				{
					CustomExceptionHandler.triggerException(e);
				}
				catch(IOException e)
				{
					CustomExceptionHandler.triggerException(e);
				}
				
			}
		}
		catch (IOException e)
		{
			CustomExceptionHandler.triggerException(e);
		}
		finally
		{
			try {if (zip_in != null) {zip_in.close();zip_in = null;}}
			catch (IOException ignored){}
			try {if (fis != null) {fis.close();fis = null;}}
			catch (IOException ignored){}
			try {if (bis != null) {bis.close();bis = null;}}
			catch (IOException ignored){}
			
			try{if(isr != null) {isr.close(); isr = null;}}
			catch (IOException e) {}
			try{if(din != null) {din.close(); din = null;}}
			catch (IOException e) {}
			
		}
		
		System.gc();
		
		return results;
	}
	
	
	/**
	 * Extract files from a zip file into a folder
	 * @param file The zip file
	 * @param containingDirectory The containing directory
	 */
	public static void extractFilesFromZipToFolder(File file, File containingDirectory)
	{
		if (!file.exists())
		{
			throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
		}
		
		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(file);
		}
		catch (ZipException e1)
		{
			throw new RuntimeException("Zip Exception. Creating zip object: " + file.getAbsolutePath(), e1);
		}
		catch (IOException e1)
		{
			NoSpaceLeftOnDeviceException.throwNoSpaceOnDevice(e1);
			throw new RuntimeException("IO Exception. Creating zip object: " + file.getAbsolutePath(), e1);
		}
		ZipInputStream zip_in = null;
		
		try
		{
			// get all the files out of the zip
			List<ZipEntry> zip_files = new ArrayList<ZipEntry>();
			for (@SuppressWarnings("rawtypes")
			Enumeration e = zipFile.entries(); e.hasMoreElements();)
			{
				ZipEntry entry = (ZipEntry) e.nextElement();
				zip_files.add(entry);
			}
			// copy each file out
			for (ZipEntry entry : zip_files)
			{
				File thisFile = new File(containingDirectory, entry.getName());
				//Log.d("appzappy", "FileName:" + entry.getName());
				if (thisFile.exists())
					thisFile.delete();
				
				
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(thisFile));
				
				BufferedInputStream reader = new BufferedInputStream(zipFile.getInputStream(entry));

				byte[] buffer = new byte[32768]; // 32kb buffer
				int count = reader.read(buffer, 0, buffer.length);
				
				while (count >= 0)
				{
					writer.write(buffer, 0, count);
					count = reader.read(buffer, 0, buffer.length);
				}
				writer.flush();
				writer.close();
				writer = null;
				reader.close();
				reader = null;
			}
		}
		catch (IOException e)
		{
			CustomExceptionHandler.triggerException(e);
		}
		finally
		{
			try {if (zip_in != null) {zip_in.close();zip_in = null;}}
			catch (IOException ignored){}
		}
	}
	
	/**
	 * Reads a file and returns its string
	 * @param file The file to read
	 * @return The contents of the file
	 */
	public static String readFile(File file)
	{
		BufferedReader din = null;
		try
		{
			if (!file.exists())
			{
				throw new FileNotFoundException("File does not exist: " + file.getAbsolutePath());
			}

			din = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
			
			
			try
			{
				din.readLine(); // throw away the 1st line
				
				StringWriter writer = new StringWriter();
				
				String str = din.readLine();
				if (str != null)
				{
					writer.write(str);
				}
				str = din.readLine();
				
				while (str != null)
				{
					// str is one line of text; 
					// readLine() strips the newline character(s)
					writer.write(C.new_line());
					writer.write(str);
					str = din.readLine();
				}
				String writerOutput = writer.toString(); 
				writer.close();

				return writerOutput;
			}
			catch(IOException e)
			{
				CustomExceptionHandler.triggerException(e);
			}
			
		}
		catch (IOException e)
		{
			CustomExceptionHandler.triggerException(e);
		}
		finally
		{
			try{if(din != null) {din.close(); din = null;}}
			catch (IOException e) {}
		}
		
		return "FAILED";
	}
	
	
	/**
	 * Copy a file from the Raw resources
	 * @param rawResourceID The resouce ID
	 * @param description Description of the file being copied
	 * @param target The file to write the raw resources into
	 */
	public static void copyFileFromRawResource(int rawResourceID, String description, File target)
	{
		InputStream inputFile = null;
		try
		{
			// Get the input stream for the default file
			Context con = UIInterfaceFactory.getInterface().getAndroidContext();
			if (con == null)
				throw new NullPointerException("The Context object is not set");
			
			Resources resources = con.getResources(); 
			if (resources == null)
				throw new NullPointerException("The resources file is not set");
			
			inputFile = resources.openRawResource(rawResourceID);
			
			
			// check the containing folder exists
			
			File parent = target.getParentFile();
			if (parent != null)
			{
				if (!parent.exists())
					parent.mkdirs();
			}
			
			
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try
			{
				// create the target file to save into
				boolean created = false;
				
				try
				{
					created = target.createNewFile();
				}
				catch (IOException e)
				{
					// check that theres space on the storage to save this file
					NoSpaceLeftOnDeviceException.throwNoSpaceOnDevice(e);
					
					String outputMessage = "Can't create target file: " + target.getAbsolutePath();
					
					try
					{
						StatFs stat = new StatFs(target.getAbsolutePath());
						double sdAvailSize = (double)stat.getAvailableBlocks() *(double)stat.getBlockSize();
						double mb_available = sdAvailSize / (1024*1024);
						outputMessage += C.new_line()+"Free Space : " + mb_available;
					}
					catch (Exception ex)
					{
						
					}
					throw new RuntimeException(outputMessage);
				}
				if (!created)
					throw new FileNotFoundException("Target file was not created : " + target.getAbsolutePath());
				
				target = target.getAbsoluteFile(); // clear target object
				
				if (!target.exists())
				{
					throw new FileNotFoundException("Create file not created: " + target.getAbsolutePath());
				}
				
				try
				{
					fos = new FileOutputStream(target);
				}
				catch (IOException e)
				{
					throw new NullPointerException("Target file has problem in FileOutputStream");
				}
				bos = new BufferedOutputStream(fos);
				
				byte[] buffer = new byte[16*1024];
				int count = 0;
				try
				{
					count = inputFile.read(buffer);
				}
				catch (IOException e)
				{
					throw new NullPointerException("InputFile first read caused crash");
				}
				while(count >= 0)
				{
					// read into the buffer, then write this data out into the buffered out stream
					try
					{
						bos.write(buffer, 0, count);
					}
					catch(IOException e)
					{
						// delete the failed file
						try
						{
							bos.close();
							target.delete();
						}
						finally
						{
							target.delete();
						}
						
						// check that theres space on the storage to save this file
						NoSpaceLeftOnDeviceException.throwNoSpaceOnDevice(e);
						throw new NullPointerException("Writing to output file is cauing crash: " + target.getAbsolutePath());
					}
					try
					{
						count = inputFile.read(buffer);
					}
					catch(IOException e)
					{
						throw new NullPointerException("InputFile reads caused crash: " + description);
					}
				}
				
				bos.flush();
				fos.flush();
				
			}
			catch(IOException e)
			{
				// catch any exceptions and make sure the streams are closed properly
				throw new RuntimeException("IOEXCEPTION " + e);
			}
			finally
			{
				try {if (fos != null) {fos.close();fos = null;}}
				catch(IOException er) {}
				try {if (bos != null) {bos.close();bos = null;}}
				catch(IOException er) {}
			}
		}
		catch(NotFoundException e)
		{
			throw new RuntimeException("Failed to locate and copy data RAW file. Target: " + target.getAbsolutePath() + " Description: " + description);
		}
		finally
		{
			try {if (inputFile != null) {inputFile.close();inputFile = null;}}
			catch(IOException er) {}
		}
	}
	
	/**
	 * Get the remaining space on the SD card
	 * @return Remaining space in bytes. -1 if not available
	 */
	public static long getRemainingSpaceOnSDCard() 
	{
		if (!isSDCardAvailable())
		{
			return -1;
		}
		
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		stat.restat(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getAvailableBlocks();
		return bytesAvailable;
	}
	
	/**
	 * Get the size of the SD card
	 * @return Size in bytes. -1 if not available
	 */
	public static long getSDCardSize()
	{
		if (!isSDCardAvailable())
		{
			return -1;
		}
		
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		stat.restat(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		return bytesAvailable;
	}
	
	/**
	 * Get the remaining space of the local storage
	 * @return Remaining internal space in bytes
	 */
	public static long getRemainingLocalStorage()
	{
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		stat.restat(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getAvailableBlocks();
		return bytesAvailable;
	}
	
	
	private FileActions()
	{}
}

