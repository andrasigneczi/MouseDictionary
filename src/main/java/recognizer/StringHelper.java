package recognizer;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Andras on 08/11/2016.
 */
public class StringHelper
{
	public static String getTraceInformation( Exception aException )
	{
		StringWriter lStringWriter = new StringWriter();
		PrintWriter lPrintWriter = new PrintWriter(lStringWriter);
		aException.printStackTrace( lPrintWriter );
		return lStringWriter.toString();
	}
}
