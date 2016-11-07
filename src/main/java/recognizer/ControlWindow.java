package recognizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by Andras on 07/11/2016.
 */
public class ControlWindow
{
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static JButton capturButton;
	private static ScreenshotWindow mSceenshotWindow = null;

	public ControlWindow()
	{
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
		mDialog.setModal( true );
		mFrame.setAlwaysOnTop( true );
		mContentPane = new JPanel();
		ControlWindow THIS = this;
		capturButton = new JButton("Capture");
		capturButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
					mSceenshotWindow = new ScreenshotWindow( THIS );
				else
					mSceenshotWindow.Show();
			}
		});
		mContentPane.add(capturButton);

		mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mFrame.add(mContentPane, BorderLayout.CENTER);
		mFrame.setSize(300, 175);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);

	}

	public void ScreenshotClosed()
	{
		mFrame.setVisible( true );
	}
}
