package recognizer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Created by Andras on 07/11/2016.
 */
public class ControlWindow implements WindowListener
{
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static JButton enghunButton;
	private static JButton gerhunButton;
	private static JButton frhunButton;
	private static JButton enbgButton;
	private static JButton gerbgButton;
	private static JButton frbgButton;
	private static JButton bghuButton;
	private static JButton rushuButton;
	private static ScreenshotWindow mSceenshotWindow = null;

	public ControlWindow()
	{
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
		mDialog.setModal( true );
		mFrame.setAlwaysOnTop( true );
		mContentPane = new JPanel();
		enghunButton = new JButton("English => Hungarian");
		enghunButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "En-Hu" );
			}
		});
		mContentPane.add( enghunButton );

		gerhunButton = new JButton("German => Hungarian");
		gerhunButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "de-hu" );
			}
		});
		mContentPane.add( gerhunButton );

		frhunButton = new JButton("French => Hungarian");
		frhunButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "fr-hu" );
			}
		});
		mContentPane.add( frhunButton );


		bghuButton = new JButton("Bulgarian => Hungarian");
		bghuButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "bg-hu" );
			}
		});
		mContentPane.add( bghuButton );

		rushuButton = new JButton("Russian => Hungarian");
		rushuButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "bg-hu" );
			}
		});
		mContentPane.add( rushuButton );


		enbgButton = new JButton("English => Bulgarian");
		enbgButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "en-bg" );
			}
		});
		mContentPane.add( enbgButton );

		gerbgButton = new JButton("German => Bulgarian");
		gerbgButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "de-bg" );
			}
		});
		mContentPane.add( gerbgButton );

		frbgButton = new JButton("French => Bulgarian");
		frbgButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_ActionPerformed( e, "fr-bg" );
			}
		});
		mContentPane.add( frbgButton );

		mFrame.addWindowListener( this );

		mFrame.setIconImage( Toolkit.getDefaultToolkit().getImage(getClass().getResource("/mouse.png")));
		mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mFrame.add(mContentPane, BorderLayout.CENTER);
		mFrame.setSize(300, 300);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);

	}

	public void _ActionPerformed(ActionEvent e, String activeDirectory )
	{
		mFrame.setVisible( false );
		try
		{
			Thread.sleep( 300 );
		}
		catch( InterruptedException e1 )
		{
			e1.printStackTrace();
		}
		if( mSceenshotWindow == null )
			mSceenshotWindow = new ScreenshotWindow( this, activeDirectory );
		else
			mSceenshotWindow.Show( activeDirectory );
	}

	public void ScreenshotClosed()
	{
		mFrame.setVisible( true );
	}

	public void ActivateLastDictionary()
	{
		if( mSceenshotWindow != null )
			mSceenshotWindow.ActivateLastDictionary();
	}

	public void TranslateClipboardContent()
	{
		if( mSceenshotWindow != null )
			mSceenshotWindow.TranslateClipboardContent();
	}

	public void WakUpScreenshot()
	{
		if( mSceenshotWindow != null )
			mSceenshotWindow.WakeUp();
	}

	@Override
	public void windowOpened( WindowEvent e )
	{
		try
		{
			GlobalScreen.registerNativeHook();
		}
		catch( NativeHookException e1 )
		{
			e1.printStackTrace();
		}
	}

	@Override
	public void windowClosing( WindowEvent e )
	{
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch( NativeHookException e1 )
		{
			e1.printStackTrace();
		}
	}

	@Override
	public void windowClosed( WindowEvent e )
	{
	}

	@Override
	public void windowIconified( WindowEvent e )
	{

	}

	@Override
	public void windowDeiconified( WindowEvent e )
	{

	}

	@Override
	public void windowActivated( WindowEvent e )
	{

	}

	@Override
	public void windowDeactivated( WindowEvent e )
	{

	}
}
