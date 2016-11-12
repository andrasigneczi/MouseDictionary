package recognizer;

import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReader;

import java.io.*;
import java.util.*;

/**
 * Created by Andras on 07/11/2016.
 */
public class XlsxDictionary implements DictionaryIF
{
	private Hashtable<String, String> mWords = new Hashtable<>();

	public XlsxDictionary()
	{
		loadDictionary( "Book1.xlsx" );
	}

	@Override
	public String translate( String word )
	{
		//return mWords.getOrDefault( word.toLowerCase(), "Unknown word: " + word );
		String wordLC = word.toLowerCase();
		if( !mWords.containsKey( wordLC ))
		{

			for( int i = 2; i < 5 && i < wordLC.length()-2; i++ )
			{
				String wordprefix = wordLC.substring( 0, wordLC.length() - i );
				for( Enumeration<String> e = mWords.keys(); e.hasMoreElements(); )
				{
					String key = e.nextElement();
					if( key.startsWith( wordprefix ))
						return "Unknown word: " + word + "\nSimilar: " + key + "\n" + mWords.get( key );
				}
			}
		}
		return "Unknown word: " + word;
	}

	@Override
	public boolean wasLastWordSaved()
	{
		return false;
	}

	@Override
	public void modify( String key, String value )
	{

	}

	@Override
	public void save( String key, String value )
	{

	}

	@Override
	public void delete( String key )
	{

	}

	/**
	 * Parses an excel file into a list of beans.
	 *
	 * @param <T> the type of the bean
	 * @param xlsFile the excel data file to parse
	 * @param jxlsConfigFile the jxls config file describing how to map rows to beans
	 * @return the list of beans or an empty list there are none
	 * @throws Exception if there is a problem parsing the file
	 */
	public static <T> List<T> parseExcelFileToBeans(final File xlsFile,
	                                                final File jxlsConfigFile)
			throws Exception {
		final XLSReader xlsReader = ReaderBuilder.buildFromXML(jxlsConfigFile);
		final List<T> result = new ArrayList<>();
		final Map<String, Object> beans = new HashMap<>();
		beans.put("result", result);
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(xlsFile))) {
			xlsReader.read(inputStream, beans);
		}
		return result;
	}

	private void loadDictionary( String filename )
	{
		try
		{
			List<recognizer.Translation> translations = parseExcelFileToBeans(new File("a.xlsx"),
					new File("dict_xlsx_config.xml"));

			for( recognizer.Translation t : translations )
			{
				if( t.getFrom().equals( "angol"))
				{
					if( t.getTo().equals( "magyar" ))
					{
						mWords.put( t.getKey().toLowerCase(), t.getValue() );
					}
				}
				else if( t.getTo().equals( "angol" ))
				{
					if( t.getFrom().equals( "magyar"))
					{
						mWords.put( t.getValue().toLowerCase(), t.getKey() );
					}
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public String getSourceLanguage()
	{
		return "eng";
	}
}
