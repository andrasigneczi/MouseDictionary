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
public class ControlWindow implements WindowListener, ActionListener
{
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static JButton enghunButton;
	private static JButton gerhunButton;
	private static JButton gerengButton;
	private static JButton frhunButton;
	private static JButton frengButton;
	private static JButton enbgButton;
	private static JButton gerbgButton;
	private static JButton frbgButton;
	private static JButton bghuButton;
	private static JButton rushuButton;
	private static ScreenshotWindow mSceenshotWindow = null;
	private static String mSourceLanguage = null;
	private static String mTargetLanguage = null;

	public ControlWindow()
	{
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
		mDialog.setModal( true );
		mFrame.setAlwaysOnTop( true );
		mContentPane = new JPanel();

		GroupLayout layout = new GroupLayout(mContentPane);
		mContentPane.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel label1 = new JLabel( "From" );
		JRadioButton buttonEng1  = new JRadioButton( "English" );
		JRadioButton buttonGer1  = new JRadioButton( "German" );
		JRadioButton buttonFra1  = new JRadioButton( "French" );
		JRadioButton buttonBul1  = new JRadioButton( "Bulgarian" );
		JRadioButton buttonHun1  = new JRadioButton( "Hungarian" );
		JRadioButton buttonAuto  = new JRadioButton( "Detect" );

		JLabel label2 = new JLabel( "To" );
		JRadioButton buttonEng2  = new JRadioButton( "English" );
		JRadioButton buttonGer2  = new JRadioButton( "German" );
		JRadioButton buttonFra2  = new JRadioButton( "French" );
		JRadioButton buttonBul2  = new JRadioButton( "Bulgarian" );
		JRadioButton buttonHun2  = new JRadioButton( "Hungarian" );

		ButtonGroup group1 = new ButtonGroup();
		group1.add( buttonEng1 );
		group1.add( buttonGer1 );
		group1.add( buttonFra1 );
		group1.add( buttonBul1 );
		group1.add( buttonHun1 );
		group1.add( buttonAuto );

		ButtonGroup group2 = new ButtonGroup();
		group2.add( buttonEng2 );
		group2.add( buttonGer2 );
		group2.add( buttonFra2 );
		group2.add( buttonBul2 );
		group2.add( buttonHun2 );

		JButton startButton = new JButton( "START" );
		startButton.addActionListener( this );
		startButton.setActionCommand( "START" );

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						//.addComponent(buttonEng1)
						//.addComponent(buttonGer1)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(label1)
								.addComponent(buttonEng1)
								.addComponent(buttonGer1)
								.addComponent(buttonFra1)
								.addComponent(buttonBul1)
								.addComponent(buttonHun1)
								.addComponent(buttonAuto))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(startButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(label2)
								.addComponent(buttonEng2)
								.addComponent(buttonGer2)
								.addComponent(buttonFra2)
								.addComponent(buttonBul2)
								.addComponent(buttonHun2))
		);

		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(label1)
								.addComponent(label2)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonEng1)
								.addComponent(buttonEng2)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonGer1)
								.addComponent(buttonGer2)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonFra1)
								.addComponent(buttonFra2)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonBul1)
								.addComponent(buttonBul2)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonHun1)
								.addComponent(buttonHun2)
						)
						.addComponent(buttonAuto)
						.addComponent(startButton)
		);

		buttonEng1.addActionListener(this);
		buttonGer1.addActionListener(this);
		buttonFra1.addActionListener(this);
		buttonBul1.addActionListener(this);
		buttonHun1.addActionListener(this);
		buttonAuto.addActionListener(this);

		buttonEng2.addActionListener(this);
		buttonGer2.addActionListener(this);
		buttonFra2.addActionListener(this);
		buttonBul2.addActionListener(this);
		buttonHun2.addActionListener(this);

		buttonEng1.setActionCommand( "en1" );
		buttonGer1.setActionCommand( "de1" );
		buttonFra1.setActionCommand( "fr1" );
		buttonBul1.setActionCommand( "bg1" );
		buttonHun1.setActionCommand( "hu1" );
		buttonAuto.setActionCommand( "Auto" );
		buttonEng2.setActionCommand( "en2" );
		buttonGer2.setActionCommand( "de2" );
		buttonFra2.setActionCommand( "fr2" );
		buttonBul2.setActionCommand( "bg2" );
		buttonHun2.setActionCommand( "hu2" );

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
			DictionaryCache.ConnectionClose();
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

	@Override
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand() == "START" ) {
			if( mTargetLanguage != null && mSourceLanguage != null )
				_ActionPerformed( e, mSourceLanguage + "-" + mTargetLanguage );
		} else if( e.getActionCommand() == "Auto" || e.getActionCommand().endsWith( "1" )) {
			if( e.getActionCommand() == "Auto" )
				mSourceLanguage = e.getActionCommand();
			else
				mSourceLanguage = e.getActionCommand().substring( 0, e.getActionCommand().length() - 1 );
		} else if( e.getActionCommand().endsWith( "2" )) {
			mTargetLanguage = e.getActionCommand().substring( 0, e.getActionCommand().length() - 1 );
		}
	}
}
