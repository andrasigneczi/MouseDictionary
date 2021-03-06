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
import java.util.ArrayList;
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
	private HashMap<String, DictionaryIF> mDictionaries = new HashMap<>();
	private Font mBubleFont;
	private Rectangle mFocusedWordBorders = null;
	private Rectangle mSelectedCharactersBorders = null;
	private Rectangle mTranslatedRectangle = null;
	private String mCapturedText = null;
	private String mTranslation = null;
	private WordDetector mWordDetector = new WordDetector();
	private String mClipboardText = null;
	private boolean mWordSaved = true;

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

	private void DrawBubble( Graphics g, Rectangle rect )
	{
		g.setFont( mBubleFont );
		int lWidth = 0;
		int lHeight = 0;

		//Rectangle2D r2d = g.getFontMetrics().getStringBounds( translation, 0, translation.length(), g );

		String[] translationLines = mTranslation.split("\n");
		ArrayList<String> brokenLines = new ArrayList<>();
		for (String line : translationLines)
		{
			if( line.length() > 40 )
			{
				int lPos = line.indexOf( " ", 40 );
				while( lPos != -1 )
				{
					brokenLines.add( line.substring( 0, lPos ) );
					line = line.substring( lPos );
					lPos = line.indexOf( " ", 40 );
				}
			}
			brokenLines.add( line.trim() );
		}

		for (String line : brokenLines)
		{
			Rectangle2D r2d = g.getFontMetrics().getStringBounds( line, 0, line.length(), g );
			if( r2d.getHeight() > lHeight )
				lHeight = (int)r2d.getHeight();
			if( r2d.getWidth() > lWidth )
				lWidth = (int)r2d.getWidth();
		}
		g.setColor( Color.orange );
		int y = (int)(rect.getY() + rect.getHeight()) + 10;
		int x = mX + 38;
		g.fill3DRect( x, y, lWidth + 17, brokenLines.size() * lHeight + 12, true );
		g.setColor( Color.black );

		//g.drawString( translation, mX + 16, y + (int)r2d.getHeight() + 3 );
		int LineY = y + 3;
		for (String line : brokenLines)
			g.drawString(line, x + 12, LineY += lHeight);
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
		if( !mWordSaved && mCapture != null )
		{
			g.drawImage( Toolkit.getDefaultToolkit().getImage(getClass().getResource("/save.png")),
					mCapture.getWidth() - 50, 10, null );
		}

		if( mClipboardText != null )
		{
			DrawBubble( g, new Rectangle( mX, mY, 0, 0) );
			return;
		}

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
				{
					Rectangle newRect = new Rectangle( mX, mY,
							(int)mSelectedCharactersBorders.getWidth(),
							(int)mSelectedCharactersBorders.getHeight());
					DrawBubble( g, newRect );
				}
			}
		}
		else
		{
			if( mFocusedWordBorders != null )
			{
				if( mFocusedWordBorders.equals( mTranslatedRectangle ) && mCapturedText != null )
					DrawBubble( g, mFocusedWordBorders );
			}
		}

		//g.drawString( "" + mX + ", " + mY, mX - 20, mY + 40 );
	}

	private void Capture( Rectangle rect )
	{
		try
		{
			if( mActiveDictionary != null )
			{
				String lang = mActiveDictionary.getSourceLanguage();
				if( lang.equalsIgnoreCase( "Auto" ))
					lang = mActiveDictionary.getLastDetectedLanguage();

				if( lang == null )
					lang = "en";

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
			mCapturedText = mITesseract.doOCR( grayImage ).trim();
			mTranslation = mActiveDictionary.translate( mCapturedText );
			mWordSaved = mActiveDictionary.wasLastWordSaved();
			mClipboardText = null;
			mTranslatedRectangle = rect;
		}
		catch( TesseractException e )
		{
			e.printStackTrace();
		}
	}

	public void Translate()
	{
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

	public void TranslateFromClipboard( String text )
	{
		mClipboardText = text;
		mTranslation = mActiveDictionary.translate( mClipboardText );
		mWordSaved = mActiveDictionary.wasLastWordSaved();
		repaint();
	}

	public void ChangeCapturedImage( BufferedImage capture )
	{
		mCapture = capture;
		mFocusedWordBorders = null;
		mSelectedCharactersBorders = null;
		mSelectionState = Selection.NONE;
		mClipboardText = null;
	}

	public void setActiveDictionary( String activeDictionary )
	{
		if( !mDictionaries.containsKey( activeDictionary ))
		{
			String[] langs = activeDictionary.split( "-" );
			mActiveDictionary = new GoogleDictionary( langs[0], langs[1] );
			mDictionaries.put( activeDictionary, mActiveDictionary );
		}
		else
		{
			mActiveDictionary = mDictionaries.get( activeDictionary );
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

	public void mouseClicked( MouseEvent  e)
	{
		if( mCapture == null )
			return;


		if( e.getX() > mCapture.getWidth() - 60 && e.getX() < mCapture.getWidth() - 28
				&& e.getY() > 10 && e.getY() < 52 )
		{
			saveTranslation();
		}
		else
		{
			Translate();
			repaint();
		}
	}

	public void saveTranslation()
	{
		if( !mWordSaved && mCapturedText != null && mTranslation != null )
		{
			mActiveDictionary.save( mCapturedText, mTranslation );
			mWordSaved = true;
			repaint();
		}
	}

	public String getSelectedText()
	{
		return mCapturedText;
	}
}
