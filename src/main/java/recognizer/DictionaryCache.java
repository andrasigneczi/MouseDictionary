package recognizer;

import java.sql.*;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * Created by Andras on 10/11/2016.
 */
public class DictionaryCache
{
	private static DictionaryCache instance = null;
	private static Connection mConnection = null;
	private static final String mDBFileName = "DictionaryCache.db";
	private static Hashtable<String, TreeMap<String,String>> mTranslations = new Hashtable<>(  );

	private DictionaryCache()
	{
		ConnectionOpen();
		InitializeDatabase();
		LoadTranslations();
	}

	public static DictionaryCache getInstance()
	{
		if( instance == null )
		{
			instance = new DictionaryCache();
		}
		return instance;
	}

	public String translate( String lang1, String lang2, String word )
	{
		String lDictKey = lang1 + "-" + lang2;
		TreeMap<String, String> lDict = null;
		if( !mTranslations.containsKey( lDictKey ))
		{
			lDict = new TreeMap<String, String>( new DictionaryCacheComparator() );
			mTranslations.put( lDictKey, lDict );
		}
		else
		{
			lDict = mTranslations.get( lDictKey );
		}
		return lDict.get( word );
	}


	public void save( String lang1, String lang2, String key, String value )
	{
		String lDictKey = lang1 + "-" + lang2;
		TreeMap<String, String> lDict = null;
		String action = "";
		if( !mTranslations.containsKey( lDictKey ))
		{
			lDict = new TreeMap<>( new DictionaryCacheComparator() );
			mTranslations.put( lDictKey, lDict );
			action = "insert";
		}
		else
		{
			lDict = mTranslations.get( lDictKey );
			if( lDict.containsKey( key ))
				action = "update";
			else
				action = "insert";
		}

		String escapedKey = escapeSQL( key );
		String escapedValue = escapeSQL( value );

		if( action.equals( "insert" ))
		{
			// insert
			String sql = "INSERT INTO Translations (Lang_ID, Word, Translation) VALUES('"
					+ lDictKey + "', '" + escapedKey + "', " + "'" + escapedValue + "')";
			ExecuteStatement( sql );
			lDict.put( key, value );
		}
		else if( action.equals( "update" ))
		{
			// update
			String sql = "UPDATE TABLE Translations SET Translation='" + escapedValue + "' WHERE Lang_ID='"
					+ lDictKey + "' and Word='" + escapedKey + "'";
			ExecuteStatement( sql );
			lDict.replace( key, value );
		}
	}

	private void ConnectionOpen()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			mConnection = DriverManager.getConnection( "jdbc:sqlite:" + mDBFileName );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}

	public static void ConnectionClose()
	{
		try
		{
			if( mConnection != null )
				mConnection.close();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}

	private void InitializeDatabase()
	{
//		String sql = "CREATE TABLE IF NOT EXISTS Languages\n"
//				+ "(\n"
//				+ "\tID                     INTEGER PRIMARY KEY NOT NULL,\n"
//				+ "\tGoogle_code            CHAR(2),\n"
//				+ "\tISO639-2_code          CHAR(5),\n"
//				+ "\tName                   CHAR(50),\n"
//				+ ");\n";
//		ExecuteStatement( sql );

		String sql = "CREATE TABLE IF NOT EXISTS Translations\n"
				+ "(\n"
				+ "\tID                     INTEGER PRIMARY KEY NOT NULL,\n"
				+ "\tLang_ID                varchar(10) NOT NULL,\n"
				+ "\tWord                   VARCHAR(10) NOT NULL,\n"
				+ "\tTranslation            VARCHAR(10),\n"
				+ "\tUNIQUE( Lang_ID, Word )"
				+ ");\n";
		ExecuteStatement( sql );

		sql = "CREATE INDEX IF NOT EXISTS TranslationIndex ON Translations " +
				"(Lang_ID, Word );";
		ExecuteStatement( sql );
	}

//	private void LoadLanguages()
//	{
//		try
//		{
//			Statement lStmt = mConnection.createStatement();
//			ResultSet lResultSet = lStmt.executeQuery( "select ID, Google_code from Languages" );
//			int lID;
//			String lGoogle_code;
//			String lName;
//			while ( lResultSet.next() ) {
//				lID = lResultSet.getInt("ID");
//				lGoogle_code = lResultSet.getString( "Google_code" );
//				mLanguages.put( lGoogle_code, lID );
//				mLanguagesB.put( lID, lGoogle_code );
//				//lName = lResultSet.getString( "Name" );
//			}
//			lResultSet.close();
//			lStmt.close();
//		}
//		catch( SQLException e )
//		{
//			e.printStackTrace();
//		}
//	}

	private void LoadTranslations()
	{
		try
		{
			Statement lStmt = mConnection.createStatement();
			ResultSet lResultSet = lStmt.executeQuery( "select * from Translations" );
			int lID;
			String lLangId, lWord, lTranslation;
			while ( lResultSet.next() ) {
				lID = lResultSet.getInt("ID");
				lLangId = lResultSet.getString( "Lang_ID" );
				lWord         = lResultSet.getString( "Word" );
				lTranslation = lResultSet.getString( "Translation" );

				TreeMap<String, String> lDict = null;
				if( !mTranslations.containsKey( lLangId ))
				{
					lDict = new TreeMap<>( new DictionaryCacheComparator()  );
					mTranslations.put( lLangId, lDict );
				}
				else
				{
					lDict = mTranslations.get( lLangId );
				}
				lDict.put( lWord, lTranslation );
			}
			lResultSet.close();
			lStmt.close();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}

	private int ExecuteStatement( String aSql )
	{
		return ExecuteStatement( aSql, Statement.EXECUTE_FAILED );
	}

	private int ExecuteStatement( String aSql, int aReturnValueType )
	{
		Statement lStmt = null;
		int lReturnValue = -1;
		try
		{
			lStmt = mConnection.createStatement();

			// The executeUpdate with statement parameter isn't implemented in SQLite
			lReturnValue = lStmt.executeUpdate( aSql/*, aReturnValueType*/ );

			if( aReturnValueType == Statement.RETURN_GENERATED_KEYS )
			{
				lReturnValue = GetSearchId( "select last_insert_rowid() as ID;" );
			}
			lStmt.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 0 );
		}
		return lReturnValue;
	}

	private int GetSearchId( String aQuery )
	{
		Statement lStmt = null;
		try
		{
			int lID = -1;
			lStmt = mConnection.createStatement();
			ResultSet lResultSet = lStmt.executeQuery( aQuery );
			while ( lResultSet.next() ) {
				lID = lResultSet.getInt("ID");
			}
			lResultSet.close();
			lStmt.close();
			return lID;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 0 );
		}

		return -1;
	}

	public static String escapeSQL(String s){
		int length = s.length();
		int newLength = length;
		for (int i=0; i<length; i++){
			char c = s.charAt(i);
			switch(c){
				case '\'':
				{
					newLength += 1;
				} break;
			}
		}
		if (length == newLength){
			return s;
		}

		StringBuffer sb = new StringBuffer(newLength);
		for (int i=0; i<length; i++){
			char c = s.charAt(i);
			switch(c){
				case '\'':{
					sb.append("''");
				} break;
				default: {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}
}
