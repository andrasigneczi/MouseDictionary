package recognizer;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.common.collect.ImmutableList;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Created by Andras on 07/11/2016.
 */
public class GoogleDictionary implements DictionaryIF
{
	private Hashtable<String,String> mWords = new Hashtable<>(  );
	private String mSourceLanguage;
	private String mTargetLanguage;
	private static String mApiKey = null;

	public GoogleDictionary( String source, String target )
	{
		mSourceLanguage = source;
		mTargetLanguage = target;
		InitApiKey();
	}

	private void InitApiKey()
	{
		if( mApiKey != null )
			return;

		try
		{
			Scanner lScanner = new Scanner( new File( "apikey.txt" ), "UTF-8" );
			mApiKey = lScanner.next();
			lScanner.close();
		}
		catch( FileNotFoundException e )
		{
			mApiKey = "";
			e.printStackTrace();
		}
	}

	public String translate2( String word )
	{
		try {
			final TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(mApiKey);

			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

			final com.google.api.services.translate.Translate translate
					= new com.google.api.services.translate.Translate.Builder(httpTransport, jsonFactory, null)
					.setApplicationName("MouseDictionary")
					.setTranslateRequestInitializer(KEY_INITIALIZER)
					.build();

			// list languages
			{
				System.out.println(translate.languages().list().execute());
				// output: {"languages":[{"language":"af"},{"language":"ar"},{"language":"az"},{"language":"be"},{"language":"bg"},{"language":"bn"},{"language":"bs"},{"language":"ca"},{"language":"ceb"},{"language":"cs"},{"language":"cy"},{"language":"da"},{"language":"de"},{"language":"el"},{"language":"en"},{"language":"eo"},{"language":"es"},{"language":"et"},{"language":"eu"},{"language":"fa"},{"language":"fi"},{"language":"fr"},{"language":"ga"},{"language":"gl"},{"language":"gu"},{"language":"ha"},{"language":"hi"},{"language":"hmn"},{"language":"hr"},{"language":"ht"},{"language":"hu"},{"language":"hy"},{"language":"id"},{"language":"ig"},{"language":"is"},{"language":"it"},{"language":"iw"},{"language":"ja"},{"language":"jw"},{"language":"ka"},{"language":"kk"},{"language":"km"},{"language":"kn"},{"language":"ko"},{"language":"la"},{"language":"lo"},{"language":"lt"},{"language":"lv"},{"language":"mg"},{"language":"mi"},{"language":"mk"},{"language":"ml"},{"language":"mn"},{"language":"mr"},{"language":"ms"},{"language":"mt"},{"language":"my"},{"language":"ne"},{"language":"nl"},{"language":"no"},{"language":"ny"},{"language":"pa"},{"language":"pl"},{"language":"pt"},{"language":"ro"},{"language":"ru"},{"language":"si"},{"language":"sk"},{"language":"sl"},{"language":"so"},{"language":"sq"},{"language":"sr"},{"language":"st"},{"language":"su"},{"language":"sv"},{"language":"sw"},{"language":"ta"},{"language":"te"},{"language":"tg"},{"language":"th"},{"language":"tl"},{"language":"tr"},{"language":"uk"},{"language":"ur"},{"language":"uz"},{"language":"vi"},{"language":"yi"},{"language":"yo"},{"language":"zh"},{"language":"zh-TW"},{"language":"zu"}]}
			}


			// translate
			{
				// phrases
				final ImmutableList<String> phrasesToTranslate = ImmutableList.<String>builder()
						//.add("Hello world")
						.add("pussy")
						.build();
				// perform
				System.out.println(translate.translations().list(phrasesToTranslate, "hu").execute());
				// output: {"translations":[{"detectedSourceLanguage":"en","translatedText":"Bonjour le monde"},{"detectedSourceLanguage":"en","translatedText":"Où puis-je promener mon chien"}]}

				//TranslationsListResponse translateResponse = list.execute();
				//String response = translateResponse.getTranslations().get(0).getTranslatedText();

				//JSONObject json = new JSONObject(response);

				//String result = json.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		catch( GeneralSecurityException e )
		{
			e.printStackTrace();
		}
		return "error?!?";
	}

	public String translate3( String word )
	{
			//HttpRequest request = new HttpRequest();
			//String lUrl = "https://www.googleapis.com/language/translate/v2?key=YOUR_API_KEY&q=hello%20world&source=en&target=de";
			String lUrl = "https://www.googleapis.com/language/translate/v2?key=" + mApiKey + "&q=" + word + "&source=en&target=hu";

//			String response = request.sendGet( lUrl, 0 );
//			if( request.getResponseCode() != 200 )
//				return "Unknown word: " + word;
//			JSONTokener lTokener = null;
//			lTokener = new JSONTokener( IOUtils.toInputStream( response ));
//			JSONArray lRoot = new JSONArray( lTokener );

		String lReturnValue = "";
		try
		{
			URI lURI = new URI( lUrl );
			InputStreamReader reader = new InputStreamReader( lURI.toURL().openStream() );
			BufferedReader in = new BufferedReader( reader );

			String readed;
			while( ( readed = in.readLine() ) != null )
				lReturnValue += readed;
		}
		catch( URISyntaxException e )
		{
			e.printStackTrace();
		}
		catch( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}

		return lReturnValue;
	}

	private String Trim( String word )
	{
		while( word.endsWith( "," ) || word.endsWith( ":" ) )
		{
			word = word.substring( 0, word.length() - 1 );
		}
		return word;
	}

	@Override
	public String translate( String word )
	{
		word = Trim( word.toLowerCase().trim());

		//if(true) return translate3(word);

		if( mWords.containsKey( word ))
			return mWords.get( word );

		try
		{
			//.setHttpReferrer(/* Enter the URL of your site here */);

			Translate translate = TranslateOptions
					.newBuilder()
					.setApiKey(mApiKey)
					.setProjectId( "MouseDictionary" )
					.build()
					.getService();


			com.google.cloud.translate.Translation translation = translate.translate(
					word,
					com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(mSourceLanguage),
					com.google.cloud.translate.Translate.TranslateOption.targetLanguage(mTargetLanguage)
			);

			mWords.put( word, translation.getTranslatedText());
			return translation.getTranslatedText();
		}
		catch( Exception e )
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

	@Override
	public String getSourceLanguage()
	{
		return mSourceLanguage;
	}
}
