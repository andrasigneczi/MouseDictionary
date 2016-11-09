package recognizer;

import java.util.Hashtable;

/**
 * Created by Andras on 07/11/2016.
 */
public class DemoDictionary implements DictionaryIF
{
	private Hashtable<String,String> mWords = new Hashtable<>(  );

	public DemoDictionary()
	{
		mWords.put( "I", "én" );
		mWords.put( "prefer", "előnyben részesít" );
		mWords.put( "this", "ez, ezt" );
		mWords.put( "shorter", "rövidebb" );
		mWords.put( "form", "forma" );
		mWords.put( "of", "-nak, -nek" );
		mWords.put( "declaration", "deklaráció" );
		mWords.put( "we", "mi" );
		mWords.put( "can", "képes vmire" );
		mWords.put( "go", "megy" );
		mWords.put( "further", "további, következő" );
		mWords.put( "still", "még" );
		mWords.put( "with", "-val, -vel" );
		mWords.put( "last", "utolsó, utóbbi" );
		mWords.put( "version", "verzió" );
		mWords.put( "the", "a (határozott névelő)" );
		mWords.put( "home", "otthon" );
		mWords.put( "convert", "átalakít" );
		mWords.put( "edit", "szerkeszt" );
		mWords.put( "comment", "megjegyzés" );
		mWords.put( "view", "nézet" );
		mWords.put( "form", "sablon" );
		mWords.put( "project", "projekt" );
		mWords.put( "protect", "védelem" );
		mWords.put( "share", "megoszt, megosztás" );
		mWords.put( "help", "segítség" );
	}

	@Override
	public String translate( String word )
	{
		return mWords.getOrDefault( word.toLowerCase(), "Unknown word: " + word );
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

	@Override
	public String getSourceLanguage()
	{
		return "eng";
	}
}
