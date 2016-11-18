package recognizer;

import java.util.Comparator;

/**
 * Created by Andras on 18/11/2016.
 */
public class DictionaryCacheComparator implements Comparator<String>
{
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase( s2 );
		}

}
