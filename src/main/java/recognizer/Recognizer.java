package recognizer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;
import org.joda.time.DateTime;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Created by Andras on 04/11/2016.
 */
public class Recognizer extends Thread
		implements NativeKeyListener, NativeMouseMotionListener, NativeMouseWheelListener
{
	private static ControlWindow controlWindow;
	private long mLastAction = 0;

	public static void main( String args[] )
	{
		controlWindow = new ControlWindow();
		Recognizer lRecognizer = new Recognizer();
		//GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(lRecognizer);
		GlobalScreen.addNativeMouseWheelListener(lRecognizer);
		GlobalScreen.addNativeMouseMotionListener(lRecognizer);
		lRecognizer.start();
		//lRecognizer.join();
	}

	@Override
	public void nativeKeyPressed( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
		if( nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_M
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.SHIFT_MASK ) != 0
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.ALT_MASK   ) != 0
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.CTRL_MASK  ) != 0 )
				// NativeInputEvent.ALT_MASK
			//NativeInputEvent.CTRL_MASK
		{
			controlWindow.ActivateLastDictionary();
		}

		if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
//			try
//			{
//				GlobalScreen.unregisterNativeHook();
//			}
//			catch( NativeHookException e )
//			{
//				e.printStackTrace();
//			}
		}
	}

	@Override
	public void nativeKeyReleased( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
		System.out.println( "nativeKeyReleased");
	}

	@Override
	public void nativeKeyTyped( NativeKeyEvent nativeKeyEvent )
	{
		saveTimeStamp();
		System.out.println( "nativeKeyTyped");
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
		System.out.println( "nativeMouseDragged");
	}

	@Override
	public void nativeMouseWheelMoved( NativeMouseWheelEvent nativeMouseWheelEvent )
	{
		saveTimeStamp();
		System.out.println( "nativeMouseWheelMoved");
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
}
