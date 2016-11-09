package recognizer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;

/**
 * This is the class that draws the x/y coordinates
 * near the mouse cursor/pointer.
 */
class AlsXYMouseLabelComponent extends JComponent
{
	private BufferedImage mCapture;
	private ITesseract mITesseract;
	private DictionaryIF mActiveDictionary;
	private HashMap<String, DictionaryIF> mDirectories = new HashMap<>();
	private Font mBubleFont;
	private Rectangle mFocusedWordBorders = null;
	private Rectangle mSelectedCharactersBorders = null;
	private Rectangle mTranslatedRectangle = null;
	private String mCapturedText = null;
	private WordDetector mWordDetector = new WordDetector();

	private int mX;
	private int mY;

	enum Selection
	{
		NONE,
		STARTED,
		SELECTED
	};

	private static boolean mMousePressed  = false;
	private static Selection mSelectionState = Selection.NONE;
	private static TextSelectionHandler mTextSelectionHandler = new TextSelectionHandler();

	public AlsXYMouseLabelComponent( BufferedImage capture, ITesseract instance ) {
		mCapture = capture;
		this.setBackground(Color.blue);

		mITesseract = instance;
		//mDictionary = new DemoDictionary();
		//mDictionary = new GoogleDictionary();
		//mDictionary = new XlsxDictionary();

		HashMap<? extends AttributedCharacterIterator.Attribute,?> fontmap = new HashMap<>();
		mBubleFont = new Font("Verdana", Font.BOLD, 16);
		//Font font = Font.createFont(Font.TRUETYPE_FONT, new File("A.ttf"));
		//return font.deriveFont(12f);
	}

	private void DrawBubble( Graphics g, String word, Rectangle rect )
	{
		String translation = mActiveDictionary.translate( word.trim());
		g.setFont( mBubleFont );
		int lWidth = 0;
		int lHeight = 0;

		//Rectangle2D r2d = g.getFontMetrics().getStringBounds( translation, 0, translation.length(), g );

		String[] translationLines = translation.split("\n");
		for (String line : translationLines)
		{
			Rectangle2D r2d = g.getFontMetrics().getStringBounds( line, 0, line.length(), g );
			if( r2d.getHeight() > lHeight )
				lHeight = (int)r2d.getHeight();
			if( r2d.getWidth() > lWidth )
				lWidth = (int)r2d.getWidth();
		}
		g.setColor( Color.orange );
		int y = (int)(rect.getY() + rect.getHeight());
		g.fill3DRect( mX + 8, y, lWidth + 17, translationLines.length * lHeight + 12, true );
		g.setColor( Color.black );

		//g.drawString( translation, mX + 16, y + (int)r2d.getHeight() + 3 );
		int LineY = y + 3;
		for (String line : translationLines)
			g.drawString(line, mX + 16, LineY += lHeight);
	}

	private void DrawRect(Graphics g, Rectangle rect )
	{
		g.setColor( Color.red);
		g.draw3DRect( (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(), false );
		g.draw3DRect( (int)rect.getX() - 1, (int)rect.getY() - 1,
				(int)rect.getWidth() + 2, (int)rect.getHeight() + 2, false );
	}

	// use the xy coordinates to update the mouse cursor text/label
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		mFocusedWordBorders = mWordDetector.DetectWordBorders( mX, mY, mCapture );
		if( mFocusedWordBorders != null )
			DrawRect( g, mFocusedWordBorders );

		if( mSelectionState != Selection.NONE )
		{
			mSelectedCharactersBorders = mTextSelectionHandler.getSelectedBorders( mCapture );
			if( mSelectedCharactersBorders != null )
			{
				mTextSelectionHandler.paintSelected( g, mSelectedCharactersBorders, mCapture );

				if( mSelectedCharactersBorders.equals( mTranslatedRectangle ) && mCapturedText != null )
					DrawBubble( g, mCapturedText, mSelectedCharactersBorders );
			}
		}
		else
		{
			if( mFocusedWordBorders != null )
			{
				if( mFocusedWordBorders.equals( mTranslatedRectangle ) && mCapturedText != null )
					DrawBubble( g, mCapturedText, mFocusedWordBorders );
			}
		}
	}

	private void Capture( Rectangle rect )
	{
		try
		{
			if( mActiveDictionary != null )
			{
				String lang = mActiveDictionary.getSourceLanguage();
				if( lang.equalsIgnoreCase( "en" ))
					mITesseract.setLanguage( "eng" );
				else if( lang.equalsIgnoreCase( "hu" ))
					mITesseract.setLanguage( "hun" );
				if( lang.equalsIgnoreCase( "fr" ))
					mITesseract.setLanguage( "fra" );
				if( lang.equalsIgnoreCase( "de" ))
					mITesseract.setLanguage( "deu" );
				if( lang.equalsIgnoreCase( "bg" ))
					mITesseract.setLanguage( "bul" );
				if( lang.equalsIgnoreCase( "ru" ))
					mITesseract.setLanguage( "rus" );
			}

			BufferedImage image = mCapture.getSubimage(
					(int)rect.getX(), (int)rect.getY(),
					(int)rect.getWidth(), (int)rect.getHeight());

			BufferedImage grayImage = ImageHelper.convertImageToGrayscale(image);
			mCapturedText = mITesseract.doOCR( grayImage );
			mTranslatedRectangle = rect;
			System.out.println( "mCapturedText:" + mCapturedText );
		}
		catch( TesseractException e )
		{
			e.printStackTrace();
		}
	}

	public void Translate()
	{
		System.out.println( "translate" );
		if( mSelectionState == Selection.SELECTED )
		{
			mSelectedCharactersBorders = mTextSelectionHandler.getSelectedBorders( mCapture );
			if( mSelectedCharactersBorders == null )
				return;
			Capture( mSelectedCharactersBorders );
		}
		else
		{
			if( mFocusedWordBorders == null )
				return;
			Capture( mFocusedWordBorders );
		}

		repaint();
	}

	public void ChangeCapturedImage( BufferedImage capture )
	{
		mCapture = capture;
	}

	public void setActiveDictionary( String activeDictionary )
	{
		if( !mDirectories.containsKey( activeDictionary ))
		{
			String[] langs = activeDictionary.split( "-" );
			mActiveDictionary = new GoogleDictionary( langs[0], langs[1] );
			mDirectories.put( activeDictionary, mActiveDictionary );
		}
		else
		{
			mActiveDictionary = mDirectories.get( activeDictionary );
		}
	}

	public void mousePressed(MouseEvent e)
	{
		mMousePressed  = true;
		mSelectionState = Selection.NONE;
	}

	public void mouseReleased(MouseEvent e)
	{
		if( mMousePressed )
		{
			mMousePressed = false;
			if( mSelectionState == Selection.STARTED )
			{
				mSelectionState = Selection.SELECTED;
				Translate();
			}
		}
	}

	public void mouseDragged( MouseEvent e )
	{
		if( mMousePressed && mSelectionState == Selection.NONE )
		{
			mSelectionState = Selection.STARTED;
			mTextSelectionHandler.selectionStart( mX, mY );
			mTextSelectionHandler.selectionAdd( e.getX(), e.getY() );
		}
		else if( mSelectionState == Selection.STARTED )
		{
			mTextSelectionHandler.selectionAdd( e.getX(), e.getY() );
		}

		mX = e.getX();
		mY = e.getY();
		repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
		mX = e.getX();
		mY = e.getY();
		repaint();
	}
}
