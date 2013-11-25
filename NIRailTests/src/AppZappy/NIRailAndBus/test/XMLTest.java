package AppZappy.NIRailAndBus.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;
import AppZappy.NIRailAndBus.util.XML;

public class XMLTest extends TestCase
{
	Document doc = null;
	protected void setUp() throws Exception
	{
		super.setUp();
		
		String xml = "<rootElement><innerElement attribut='yes' /></rootElement>";
		
		doc = XML.fromString(xml);
	}
	
	public void test_childnodes()
	{
		assertEquals(1, doc.getChildNodes().getLength());
	}
	
	public void test_name()
	{
		assertEquals("rootElement", doc.getFirstChild().getNodeName());
	}
	
	public void test_attribute()
	{
		Element ele = (Element) doc.getElementsByTagName("innerElement").item(0);
		
		assertEquals("yes", ele.getAttribute("attribut"));
	}
}
