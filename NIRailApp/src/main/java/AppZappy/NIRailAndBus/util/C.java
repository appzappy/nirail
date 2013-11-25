package AppZappy.NIRailAndBus.util;

/**
 * Contains a few basic constants
 * @author Kurru
 *
 */
public class C
{
	private static String _line_separator = null;
	/**
	 * Get the new line character, i.e. '\n'
	 * @return '\n' or '\r\n'
	 */
	public static String new_line ()
	{
		if (_line_separator == null)
			_line_separator = System.getProperty("line.separator");
		return _line_separator;
	}
	
	private static String _double = null;
	/**
	 * Return a double new line character
	 * @return "\n\n" or "\r\n\r\n"
	 */
	public static String new_line_double()
	{
		if (_double == null)
		{
			_double = new_line();
			_double += _double;
		}
		
		return _double;
	}
}
