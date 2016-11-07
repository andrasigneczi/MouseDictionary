package recognizer;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by Andras on 07/11/2016.
 */
public interface DictionaryIF
{
	String translate( String word );
	void modify( String key, String value );
	void delete( String key );
	void loadDictionary( String langToLang );
}
