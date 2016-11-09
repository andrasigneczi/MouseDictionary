package recognizer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Andras on 09/11/2016.
 */
public class TextSelectionHandler
{
	private int mSelectionX1, mSelectionY1, mSelectionX2, mSelectionY2;

	public TextSelectionHandler()
	{
	}

	public Rectangle getSelectedBorders( BufferedImage capture )
	{
		int top, bottom;
		if( mSelectionY1 < mSelectionY2 )
		{
			top    = mSelectionY1;
			bottom = mSelectionY2;
		}
		else
		{
			bottom = mSelectionY1;
			top    = mSelectionY2;
		}

		int left, right;
		if( mSelectionX1 > mSelectionX2 )
		{
			left = mSelectionX2;
			right = mSelectionX1;
		}
		else
		{
			left = mSelectionX1;
			right = mSelectionX2;
		}

		return new Rectangle( left,
				top,
				right - left,
				bottom - top );
	}

	@Deprecated
	public Rectangle getSelectedBorders_( BufferedImage capture )
	{
		WordDetector mWordDetector = new WordDetector();
		int top, bottom;
		if( mSelectionY1 < mSelectionY2 )
		{
			top    = mSelectionY1;
			bottom = mSelectionY2;
		}
		else
		{
			bottom = mSelectionY1;
			top    = mSelectionY2;
		}

		int[] homogenUpperLine = mWordDetector.SearchHomogenHorizontalLine( mSelectionX1, top, -1, capture );
		int[] homogenLowerLine = mWordDetector.SearchHomogenHorizontalLine( mSelectionX1, bottom,  1, capture );

		if( homogenLowerLine[0] - homogenUpperLine[0] < 8 )
			return null;

		int left, right;
		if( mSelectionX1 > mSelectionX2 )
		{
			left = mSelectionX2;
			right = mSelectionX1;
		}
		else
		{
			left = mSelectionX1;
			right = mSelectionX2;
		}

		left  = mWordDetector.SearchHomogenXCoord( left, homogenUpperLine[0],
				homogenLowerLine[ 0 ], -1, homogenUpperLine[ 1 ], capture );
		right = mWordDetector.SearchHomogenXCoord( right, homogenUpperLine[0],
				homogenLowerLine[ 0 ],  1, homogenUpperLine[ 2], capture );

		return new Rectangle( left,
				homogenUpperLine[ 0 ],
				right - left,
				homogenLowerLine[ 0 ] - homogenUpperLine[ 0 ] );
	}

	public void selectionStart( int X, int Y )
	{
		mSelectionX1 = X;
		mSelectionY1 = Y;
	}

	public void selectionAdd( int X, int Y )
	{
		mSelectionX2 = X;
		mSelectionY2 = Y;
	}

	public void paintSelected( Graphics g, Rectangle selectedTextBorders, BufferedImage capture )
	{
		final int x      = (int)selectedTextBorders.getX();
		final int y      = (int)selectedTextBorders.getY();
		final int width  = (int)selectedTextBorders.getWidth();
		final int height = (int)selectedTextBorders.getHeight();

		if( width <= 0 || height <= 0 )
			return;

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );

		for( int i = x; i != x + width && x < capture.getWidth(); i++ )
		{
			for( int j = y; j != y + height && y < capture.getHeight(); j++ )
			{
				int rgb = capture.getRGB( i, j );
				img.setRGB( i - x, j - y, rgb ^ 0x00ffff00 );
			}
		}

		g.drawImage( img, x, y, null );
	}
}
