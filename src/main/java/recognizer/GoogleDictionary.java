package recognizer;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

/**
 * Created by Andras on 07/11/2016.
 */
public class GoogleDictionary implements DictionaryIF
{
	private Hashtable<String,String> mWords = new Hashtable<>(  );

	@Override
	public String translate( String word )
	{
		// google url
		// https://translate.google.com/translate_a/single?client=t&sl=auto&tl=hu&hl=hu&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&otf=1&srcrom=0&ssel=0&tsel=0&kc=7&tk=506885.130552&q=sick

		word = word.toLowerCase();

		if( mWords.containsKey( word ))
			return mWords.get( word );

		try
		{
			String lUrl = "https://translate.google.com/translate_a/single?client=t&sl=auto&tl=hu&hl=hu&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&otf=1&srcrom=0&ssel=0&tsel=0&kc=7&tk=506885.130552&q=" + word;

			URI lURI = null;
			JSONTokener lTokener = null;
			lURI = new URI( lUrl );
			lTokener = new JSONTokener( lURI.toURL().openStream() );
			//lTokener = new JSONTokener(new ByteArrayInputStream( lTestJsonString.getBytes() ));
			//JSONObject root = new JSONObject( lTokener );
			JSONArray lRoot = new JSONArray( lTokener );
			int iDebug = 10;
		}
		catch( URISyntaxException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void modify( String key, String value )
	{

	}

	@Override
	public void delete( String key )
	{

	}

	@Override
	public void loadDictionary( String langToLang )
	{

	}
}
