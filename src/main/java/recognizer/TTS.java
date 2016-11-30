package recognizer;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.JavaClipAudioPlayer;

/**
 * Created by Andras on 29/11/2016.
 */
public class TTS
{

	/**
	 * Example of how to list all the known voices.
	 */
	public static void listAllVoices() {
		System.out.println();
		System.out.println("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			System.out.println("    " + voices[i].getName()
					+ " (" + voices[i].getDomain() + " domain)");
		}
	}

	public static void play(String word) {

		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory," +
		"com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory" );
		//		+"de.dfki.lt.freetts.en.us.MbrolaVoiceDirectory");
		//System.setProperty("freetts.voicesfile", "c:\\Users\\Andras\\IdeaProjects\\MouseDictionary\\lib\\freetts\\voices.txt" );

		//System.setProperty( "mbrola.base", "c:\\Program Files (x86)\\Mbrola Tools" );

		listAllVoices();

		String voiceName = "kevin16";
		//String voiceName = "alan";
		//String voiceName = "mbrola_us1";

		//System.out.println();
		//System.out.println("Using voice: " + voiceName);

        /* The VoiceManager manages all the voices for FreeTTS.
         */
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice helloVoice = voiceManager.getVoice(voiceName);

		if (helloVoice == null) {
			System.err.println(
					"Cannot find a voice named "
							+ voiceName + ".  Please specify a different voice.");
			System.exit(1);
		}

        /* Allocates the resources for the voice.
         */
		helloVoice.allocate();

        /* Synthesize speech.
         */
		//helloVoice.speak("Thank you for giving me a voice. "+ "I'm so glad to say hello to this world.");
		helloVoice.speak( word );

        /* Clean up and leave.
         */
		helloVoice.deallocate();
	}
}
