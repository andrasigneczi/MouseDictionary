package recognizer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.time.Duration;
import java.util.logging.LogManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;

/**
 * Created by Andras on 04/11/2016.
 */
public class Recognizer extends Thread
		implements NativeKeyListener, NativeMouseMotionListener, NativeMouseWheelListener,
		ClipboardOwner
{
	private static ControlWindow controlWindow;
	private long mLastAction = 0;

	public static void main( String args[] )
	{
		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(java.util.logging.Level.OFF);

		controlWindow = new ControlWindow();
		Recognizer lRecognizer = new Recognizer();
		//GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(lRecognizer);
		GlobalScreen.addNativeMouseWheelListener(lRecognizer);
		GlobalScreen.addNativeMouseMotionListener(lRecognizer);

		// Change the level for all handlers attached to the default logger.
		java.util.logging.Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(java.util.logging.Level.OFF);
		}

		lRecognizer.start();
		//lRecognizer.join();
	}

	@Override
	public void nativeKeyPressed( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
		if( ( nativeKeyEvent.getModifiers() & NativeInputEvent.SHIFT_MASK ) != 0
			&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.ALT_MASK   ) != 0
			&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.CTRL_MASK  ) != 0 )
		{
			if( nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_M )
				controlWindow.ActivateLastDictionary();
			else if( nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_V )
				controlWindow.TranslateClipboardContent();
		}

	}

	@Override
	public void nativeKeyReleased( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
	}

	@Override
	public void nativeKeyTyped( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
	}

	@Override
	public void nativeMouseMoved( NativeMouseEvent nativeMouseEvent )
	{
		saveTimeStamp();
	}

	@Override
	public void nativeMouseDragged( NativeMouseEvent nativeMouseEvent )
	{
		saveTimeStamp();
	}

	@Override
	public void nativeMouseWheelMoved( NativeMouseWheelEvent nativeMouseWheelEvent )
	{
		saveTimeStamp();
	}

	public void run()
	{
		while( true )
		{
			try
			{
				if( Duration.ofMillis( System.currentTimeMillis() - mLastAction ).getSeconds() >= 1 )
					controlWindow.WakUpScreenshot();
				Thread.sleep( 500 );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}

	private void saveTimeStamp()
	{
		mLastAction = System.currentTimeMillis();
	}

	@Override
	public void lostOwnership( Clipboard clipboard, Transferable contents )
	{

	}
}
