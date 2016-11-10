package recognizer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Created by Andras on 04/11/2016.
 */
public class Recognizer implements NativeKeyListener
{
	private static ControlWindow controlWindow;

	public static void main( String args[] )
	{
		controlWindow = new ControlWindow();
		try
		{
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new Recognizer());
		}
		catch( NativeHookException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void nativeKeyPressed( NativeKeyEvent nativeKeyEvent )
	{
		if( nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_M
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.SHIFT_MASK ) != 0
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.ALT_MASK) != 0
				&& ( nativeKeyEvent.getModifiers() & NativeInputEvent.CTRL_MASK) != 0 )
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

	}

	@Override
	public void nativeKeyTyped( NativeKeyEvent nativeKeyEvent )
	{

	}
}
