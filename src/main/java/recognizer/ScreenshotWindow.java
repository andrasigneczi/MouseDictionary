package recognizer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Andras on 07/11/2016.
 */
public class ScreenshotWindow
{
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static BufferedImage mCapture = null;
	//private static final String fileName = "c:\\temp\\screenshot.bmp";
	private static ITesseract instance = null;
	private static ControlWindow mParent;
	private static AlsXYMouseLabelComponent alsXYMouseLabel;
	private static String mActiveDictionary;

	enum LastHidingOperation
	{
		CLOSED,
		SUSPENDED,
		NOTHING
	};

	private static LastHidingOperation mLastHidingOperation = LastHidingOperation.CLOSED;

	public ScreenshotWindow( ControlWindow p, String activeDirectory )
	{
		mParent = p;
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
		mActiveDictionary = activeDirectory;

		mContentPane = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if( mCapture != null )
				{
					g.drawImage( mCapture, 0, 0, null );
				}
			}

		};

		SaveScreen();

		mFrame.setIconImage( Toolkit.getDefaultToolkit().getImage(getClass().getResource("/mouse.png")));
		mFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mFrame.add(mContentPane, BorderLayout.CENTER);
		mFrame.setSize(300, 175);
		mFrame.setLocationRelativeTo(null);
		mFrame.setUndecorated(true);
		mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mFrame.setVisible(true);
		InitBubble();
	}

	private static void SaveScreen()
	{
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		try
		{
			mCapture = new Robot().createScreenCapture(screenRect);
			//ImageIO.write(mCapture, "bmp", new File( fileName ));
		}
		catch( AWTException e )
		{
			e.printStackTrace();
		}
	}

	private static void InitBubble()
	{
		instance = new Tesseract1(); // JNA Direct Mapping
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");

		//Set the tessdata path
		//instance.setDatapath(tessDataFolder.getAbsolutePath());
		instance.setDatapath( "./tessdata" );

		// create an instance of my custom mouse cursor component
		alsXYMouseLabel = new AlsXYMouseLabelComponent( mCapture, instance );
		alsXYMouseLabel.setActiveDictionary( mActiveDictionary );

		// add my component to the DRAG_LAYER of the layered pane (JLayeredPane)
		JLayeredPane layeredPane = mFrame.getRootPane().getLayeredPane();
		layeredPane.add(alsXYMouseLabel, JLayeredPane.DRAG_LAYER);
		alsXYMouseLabel.setBounds(0, 0, mFrame.getWidth(), mFrame.getHeight());

		// add a mouse motion listener, and update my custom mouse cursor with the x/y
		// coordinates as the user moves the mouse
		mFrame.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me)
			{
				alsXYMouseLabel.mouseMoved(me);
			}

			public void mouseDragged(MouseEvent e)
			{
				alsXYMouseLabel.mouseDragged(e);
			}
		});

		mFrame.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				//super.mouseClicked( e );
				alsXYMouseLabel.Translate();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				alsXYMouseLabel.mousePressed( e );
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				alsXYMouseLabel.mouseReleased( e );
			}
		} );

		mFrame.addMouseWheelListener( new MouseAdapter()
		{
			@Override
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				//super.mouseWheelMoved( e );
				mLastHidingOperation = LastHidingOperation.SUSPENDED;
				hideWindow();
			}
		} );
		mFrame.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				mLastHidingOperation = LastHidingOperation.SUSPENDED;
				hideWindow();
				//super.keyTyped( e );
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
				{
					mParent.ScreenshotClosed();
				}
			}
		} );
		// make the cursor a crosshair shape
		//mFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		mFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private static void hideWindow()
	{
		//System.exit( 1 );
		mFrame.setVisible(false);
		mCapture = null;
	}

	public void Show( String activeDictionary )
	{
		mActiveDictionary = activeDictionary;
		SaveScreen();
		alsXYMouseLabel.ChangeCapturedImage( mCapture );
		alsXYMouseLabel.setActiveDictionary( mActiveDictionary );
		mFrame.setVisible( true );
	}

	public void ActivateLastDictionary()
	{
		SaveScreen();
		alsXYMouseLabel.ChangeCapturedImage( mCapture );
		alsXYMouseLabel.setActiveDictionary( mActiveDictionary );
		mFrame.setVisible( true );
		mFrame.toFront();
		mFrame.repaint();
	}

	public void WakeUp()
	{
		if( !mFrame.isVisible() && mLastHidingOperation == LastHidingOperation.SUSPENDED )
		{
			ActivateLastDictionary();
			mLastHidingOperation = LastHidingOperation.NOTHING;
		}
	}
}
