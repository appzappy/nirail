package AppZappy.NIRailAndBus.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import AppZappy.NIRailAndBus.util.exception.CustomExceptionHandler;

/**
 * Some general XML functions
 */
public class XML
{
	/**
	 * Get an XML Document file from its string representation
	 * @param xml The xml string
	 * @return Document object for the XML representation
	 */
	public static Document fromString(String xml)
	{
		if (xml == null)
			throw new NullPointerException("The xml string passed in is null");
		
		// from http://www.rgagnon.com/javadetails/java-0573.html
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
	
			Document doc = db.parse(is);
			
			return doc;
		}
		catch (SAXException e)
		{
			return null;
		}
		catch(Exception e)
		{
			CustomExceptionHandler han = new CustomExceptionHandler();
			han.uncaughtException(Thread.currentThread(), e);
			return null;
		}
	}

	/**
	 * Get a XML Document object from a file
	 * @param location The location where the file is found
	 * @return An XML Document object
	 */
	public static Document fromFile(String location)
	{
		return fromFile(new File(location));
	}

	/**
	 * Get a XML Document object from a file
	 * @param location The file object to load
	 * @return An XML Document object
	 */
	public static Document fromFile(File file)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Method to convert Document to String
	 * @param doc
	 * @return
	 */
	public static String getStringFromDocument(Document doc)
	{
		String output = null;
		
		try
		{
			output = getStringFromNode(doc);
		}
		catch (IOException e)
		{
			output = null;
		}
		
		return output;
	}
	
	/**
	 * Convert a XML structure into a string
	 * @param root The root element
	 * @return The string representing the node
	 * @throws IOException
	 */
	private static String getStringFromNode(Node root) throws IOException
	{
        StringBuilder result = new StringBuilder();

        if (root.getNodeType() == 3)
            result.append(root.getNodeValue());
        else {
            if (root.getNodeType() != 9) {
                StringBuffer attrs = new StringBuffer();
                for (int k = 0; k < root.getAttributes().getLength(); ++k) {
                    attrs.append(' ').append(
                            root.getAttributes().item(k).getNodeName()).append(
                            "=\"").append(
                            root.getAttributes().item(k).getNodeValue())
                            .append("\" ");
                }
                result.append('<').append(root.getNodeName()).append(' ')
                        .append(attrs).append('>');
            } else {
                result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            }

            NodeList nodes = root.getChildNodes();
            for (int i = 0, j = nodes.getLength(); i < j; i++) {
                Node node = nodes.item(i);
                result.append(getStringFromNode(node));
            }

            if (root.getNodeType() != 9) {
                result.append("</").append(root.getNodeName()).append('>');
            }
        }
        return result.toString();
    }
}
