package recognizer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Andras on 07/11/2016.
 */
public class ScreenshotWindow
{
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static BufferedImage mCapture = null;
	private static final String fileName = "c:\\temp\\screenshot.bmp";
	private static ITesseract instance = null;
	private static ControlWindow mParent;
	private static AlsXYMouseLabelComponent alsXYMouseLabel;
	private static String mActiveDirectory;

	public ScreenshotWindow( ControlWindow p, String activeDirectory )
	{
		mParent = p;
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
		mActiveDirectory = activeDirectory;

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

		SaveScreen( fileName );

		mFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mFrame.add(mContentPane, BorderLayout.CENTER);
		mFrame.setSize(300, 175);
		mFrame.setLocationRelativeTo(null);
		mFrame.setUndecorated(true);
		mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mFrame.setVisible(true);
		InitBubble();
	}

	private static void SaveScreen( String fileName )
	{
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		try
		{
			mCapture = new Robot().createScreenCapture(screenRect);
			ImageIO.write(mCapture, "bmp", new File( fileName ));
		}
		catch( AWTException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	private static void InitBubble()
	{
		instance = new Tesseract1(); // JNA Direct Mapping
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");

		//Set the tessdata path
		instance.setDatapath(tessDataFolder.getAbsolutePath());

		// create an instance of my custom mouse cursor component
		alsXYMouseLabel = new AlsXYMouseLabelComponent( mCapture, instance );
		alsXYMouseLabel.setActiveDictionary( mActiveDirectory );

		// add my component to the DRAG_LAYER of the layered pane (JLayeredPane)
		JLayeredPane layeredPane = mFrame.getRootPane().getLayeredPane();
		layeredPane.add(alsXYMouseLabel, JLayeredPane.DRAG_LAYER);
		alsXYMouseLabel.setBounds(0, 0, mFrame.getWidth(), mFrame.getHeight());

		// add a mouse motion listener, and update my custom mouse cursor with the x/y
		// coordinates as the user moves the mouse
		mFrame.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me)
			{
				alsXYMouseLabel.mX = me.getX();
				alsXYMouseLabel.mY = me.getY();
				alsXYMouseLabel.repaint();
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
		} );
		mFrame.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				//super.keyTyped( e );
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
				{
					//System.exit( 1 );
					mFrame.setVisible(false);
					mCapture = null;
					mParent.ScreenshotClosed();
				}
			}
		} );
		// make the cursor a crosshair shape
		//mFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		mFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void Show( String activeDirectory )
	{
		mActiveDirectory = activeDirectory;
		SaveScreen( fileName );
		alsXYMouseLabel.ChangeCapturedImage( mCapture );
		alsXYMouseLabel.setActiveDictionary( mActiveDirectory );
		mFrame.setVisible( true );
	}
}
