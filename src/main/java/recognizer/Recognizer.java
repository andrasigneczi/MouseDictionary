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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Created by Andras on 04/11/2016.
 */
public class Recognizer
{
	private final String mPath;
	private static final String fileName = "c:\\temp\\screenshot.bmp";
	private static BufferedImage mCapture = null;
	private static JPanel mContentPane;
	private static JFrame mFrame;
	private static JDialog mDialog;
	private static JButton capturButton;

	private static ITesseract instance = null;
	File imageFile;

	private Recognizer( String path )
	{
		mPath = path;
		imageFile = new File( mPath );
		//ITesseract instance = new Tesseract();  // JNA Interface Mapping
		instance = new Tesseract1(); // JNA Direct Mapping

		File tessDataFolder = LoadLibs.extractTessResources("tessdata");

		//Set the tessdata path
		instance.setDatapath(tessDataFolder.getAbsolutePath());
	}

	private String searchWord( Rectangle rec )
	{
		java.util.List<IIOImage> list = null;
		try
		{
			list = ImageIOHelper.getIIOImageList(imageFile);
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		String result = "";
		try {
			for( int width = rec.width; width < 100; width += rec.width )
			{
				for( int height = rec.height; height < 5*rec.height; height += rec.height )
				{
					//result = instance.doOCR( imageFile, new Rectangle( rec.x, rec.y, width, height ) );
					result = instance.doOCR( list, imageFile.getPath(), new Rectangle( rec.x, rec.y, width, height ) );
					if( result != null && result.length() > 0 )
						System.out.println( "'" + result + "'" );
				}
			}
//			result = instance.doOCR(imageFile, new Rectangle( 489, 628, 978 - 489, 32  ));
//			System.out.println(result.trim());
//
//			result = instance.doOCR(imageFile, new Rectangle( 114, 548, 188 - 114, 564 - 548 ));
//			System.out.println(result.trim());
//
//			result = instance.doOCR(imageFile, new Rectangle( 528, 458, 633 - 528, 486 - 458 ));
//			System.out.println(result);

		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
		return result;
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

	private static Rectangle DetectWordBorders( int x, int y )
	{
		Robot robot = null;
		try
		{
			robot = new Robot();
			Color color = robot.getPixelColor(20, 20);

			System.out.println("Red   = " + color.getRed());
			System.out.println("Green = " + color.getGreen());
			System.out.println("Blue  = " + color.getBlue());
		}
		catch( AWTException e )
		{
			e.printStackTrace();
		}
		return new Rectangle();
	}

	private static void OpenAlwaysOnTopWindow()
	{
		mFrame = new JFrame("MouseDict");
		mDialog = new JDialog(mFrame, true);
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

		capturButton = new JButton("Capture");
		capturButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Account account = createAccount(frame);
				// Displays created account's details
				//JOptionPane.showMessageDialog(frame, "Created Account: " + account);

				mFrame.setVisible( false );
				try
				{
					Thread.sleep( 300 );
				}
				catch( InterruptedException e1 )
				{
					e1.printStackTrace();
				}
				mFrame.dispose();
				SaveScreen( fileName );
				capturButton.setVisible( false );
				mFrame.setUndecorated(true);
				mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				mFrame.setVisible(true);
				InitBubble();

				//mFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				//mFrame.setUndecorated(true);
				//mContentPane.repaint();
				//mDialog.getRootPane().setBorder( BorderFactory.createLineBorder(Color.RED) );
				//mContentPane.setBorder( BorderFactory.createLineBorder(Color.RED) );
			}
		});
		mContentPane.add(capturButton);

		mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mFrame.add(mContentPane, BorderLayout.CENTER);
		mFrame.setSize(300, 175);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);
	}

	private static void InitBubble()
	{
		instance = new Tesseract1(); // JNA Direct Mapping
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");

		//Set the tessdata path
		instance.setDatapath(tessDataFolder.getAbsolutePath());

		// create an instance of my custom mouse cursor component
		final AlsXYMouseLabelComponent alsXYMouseLabel = new AlsXYMouseLabelComponent( mCapture, instance );

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

		mFrame.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				//super.keyTyped( e );
				if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
				{
					//System.exit( 1 );
					mFrame.dispose();
					capturButton.setVisible( true );
					mFrame.setUndecorated(false);
					mFrame.setExtendedState(JFrame.NORMAL);
					mFrame.setSize(300, 175);
					mFrame.setVisible(true);
					mCapture = null;
					mContentPane.repaint();
				}
			}
		} );
		// make the cursor a crosshair shape
		//mFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		mFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public static void main( String args[] )
	{
		//Recognizer recognizer = new Recognizer( "c:\\Users\\Andras\\OneDrive\\Pictures\\Screenshots\\2016-11-04.png" );

		OpenAlwaysOnTopWindow();
		return;
		//Recognizer recognizer = new Recognizer( fileName );
		//recognizer.searchWord( new Rectangle( 627, 357, 8, 32 ));
	}
}
