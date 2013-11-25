package AppZappy.NIRailAndBus.test;

import AppZappy.NIRailAndBus.util.FileDownloading;
import junit.framework.TestCase;

public class FileDownloadingTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	
	public void test_downloadFileTOString()
	{
		String url = "http://www.appzappy.co.uk/applicationFiles/test_download.txt";
		String msg = FileDownloading.downloadFile(url);
		
		assertEquals("test file for downloading", msg);
	}

}
