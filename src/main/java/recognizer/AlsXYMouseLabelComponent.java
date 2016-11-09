package recognizer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
	private Rectangle mWordBorders = null;
	private Rectangle mTranslatedRectangle = null;
	private String mTranslatedText = null;
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
		mWordBorders = mWordDetector.DetectWordBorders( mX, mY, mCapture );
		if( mWordBorders != null )
		{
			DrawRect( g, mWordBorders );
			if( mWordBorders.equals( mTranslatedRectangle ) && mTranslatedText != null )
				DrawBubble( g, mTranslatedText, mWordBorders );
		}
	}

	public void Translate()
	{
		if( mWordBorders == null )
			return;
		//String s = "X:" + rect.getX() + ", Y: " + rect.getY() + ", W:" + rect.getWidth() + ", H:" + rect.getHeight();

		try
		{
			mTranslatedText = mITesseract.doOCR( mCapture, mWordBorders );
			mTranslatedRectangle = mWordBorders;
		}
		catch( TesseractException e )
		{
			e.printStackTrace();
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
	}

	public void mouseReleased(MouseEvent e)
	{
		if( mMousePressed )
		{
			mMousePressed = false;
			mSelectionState = Selection.SELECTED;
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		if( mMousePressed )
			mSelectionState = Selection.STARTED;

		mX = e.getX();
		mY = e.getY();
		repaint();
	}
}
