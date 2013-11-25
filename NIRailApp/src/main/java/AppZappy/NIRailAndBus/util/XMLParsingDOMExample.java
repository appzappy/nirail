package AppZappy.NIRailAndBus.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParsingDOMExample
{

	public static void example(File filelocation)
	{
		// from
		// http://www.androidpeople.com/android-xml-parsing-tutorial-%E2%80%93-using-domparser/
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(filelocation);
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("item");


			for (int i = 0; i < nodeList.getLength(); i++)
			{

				Node node = nodeList.item(i);

				Element firstElement = (Element) node;
				NodeList nameList = firstElement.getElementsByTagName("name");
				Element nameElement = (Element) nameList.item(0);
				nameList = nameElement.getChildNodes();

				String nameValue = (nameList.item(0)).getNodeValue();
				nameValue.charAt(0); // stupid thing to stop "unused" eclipse
										// mini-error

				NodeList websiteList = firstElement
						.getElementsByTagName("website");
				Element websiteElement = (Element) websiteList.item(0);
				websiteList = websiteElement.getChildNodes();

				String websiteValue = (websiteList.item(0)).getNodeValue();
				String websiteAttribute = websiteElement
						.getAttribute("category");

				websiteValue.charAt(0);// stupid thing to stop "unused" eclipse
										// mini-error
				websiteAttribute.charAt(0);// stupid thing to stop "unused"
											// eclipse mini-error
			}
		}
		catch (Exception e)
		{
			System.out.println("XML Pasing Excpetion = " + e);
		}

	}
}
