package recognizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Andras on 07/11/2016.
 */
public class WordDetector
{
	private final int BoxHeight = 50;
	private final int BoxWidth  = 400;
	final int colorDifferenceTreshold = 20;
	private BufferedImage mCapture;

	private class HeightSpaceWidth
	{
		public int MinHeight;
		public int MaxHeight;
		public int SpaceWidthMinValue;
		public HeightSpaceWidth( int MinHeight, int MaxHeight, int SpaceWidthMinValue )
		{
			this.MinHeight = MinHeight;
			this.MaxHeight = MaxHeight;
			this.SpaceWidthMinValue = SpaceWidthMinValue;
		}
	}

	private ArrayList<HeightSpaceWidth> mHightSpaceWidthRelation;

	public WordDetector()
	{
		mHightSpaceWidthRelation = new ArrayList<>(  );
		mHightSpaceWidthRelation.add( new HeightSpaceWidth( 8, 18, 4 ) );
		mHightSpaceWidthRelation.add( new HeightSpaceWidth( 19, 30, 5 ) );
	}

	public int[] SearchHomogenHorizontalLine( int x, int y, int direction, BufferedImage capture )
	{
		mCapture = capture;
		return SearchHomogenHorizontalLine( x, y, direction );
	}

	/**
	 *
	 * @param y
	 * @param x
	 * @param direction
	 * @return an array: y coordinate, x1, x2
	 */
	private int[] SearchHomogenHorizontalLine( int x, int y, int direction )
	{
		int longestLineY  = y;
		int longestLineX1 = x;
		int longestLineX2 = x;

		for( int ycoord = y;
		     ycoord != Integer.max( 0, y - BoxHeight / 2 ) && ycoord != Integer.min( mCapture.getHeight(), y + BoxHeight / 2 );
		     ycoord += direction )
		{
			int xBeg = x;
			int xEnd = x;
			for( int xcoord = x; xcoord != Integer.max( 0, x - BoxWidth / 2 ); xcoord-- )
			{
				if( TestPoints( x, ycoord, xcoord, ycoord ))
				{
					xBeg = xcoord;
				}
				else
				{
					break;
				}
			}

			for( int xcoord = x + 1; xcoord != Integer.min( mCapture.getWidth(), x + BoxWidth / 2 ); xcoord++ )
			{
				if( TestPoints( x, ycoord, xcoord, ycoord ))
				{
					xEnd = xcoord;
				}
				else
				{
					break;
				}
			}

			if( longestLineX2 - longestLineX1 < xEnd - xBeg )
			{
				longestLineX1 = xBeg;
				longestLineX2 = xEnd;
				longestLineY  = ycoord;
			}
		}

		return new int[]{longestLineY, longestLineX1, longestLineX2};
	}

	private boolean TestSpace( final int height, final int width )
	{
		for( HeightSpaceWidth heightSpaceWidth : mHightSpaceWidthRelation )
		{
			if( height >= heightSpaceWidth.MinHeight
					&& height <= heightSpaceWidth.MaxHeight
					&& width >= heightSpaceWidth.SpaceWidthMinValue )
				return true;
		}
		return false;
	}

	public int SearchHomogenXCoord( int x, int y1, int y2, int direction, int xendpos, BufferedImage capture )
	{
		for( int xcoord = x; xcoord != xendpos; xcoord += direction )
		{
			int height = 0;
			for( int ycoord = y1; ycoord != y2; ycoord++ )
			{
				if( TestPoints( xcoord, y1, xcoord, ycoord ))
				{
					height++;
				}
				else
				{
					break;
				}
			}

			if( height == y2 - y1 )
			{
				return xcoord;
			}
		}
		return -1;
	}

	private ArrayList<Integer> SearchHomogenXCoords( int x, int y1, int y2, int direction, int xendpos)
	{
		ArrayList<Integer> xCoords = new ArrayList<>(  );
//		for( int xcoord = x; xcoord != Integer.max( 0, x - BoxWidth / 2 )
//				&& xcoord != Integer.min( mCapture.getWidth(), x + BoxWidth / 2 ); xcoord += direction )
		for( int xcoord = x; xcoord != xendpos; xcoord += direction )
		{
			int height = 0;
			for( int ycoord = y1; ycoord != y2; ycoord++ )
			{
				if( TestPoints( xcoord, y1, xcoord, ycoord ))
				{
					height++;
				}
				else
				{
					break;
				}
			}

			if( height == y2 - y1 )
			{
				xCoords.add( xcoord );
			}
		}
		return xCoords;
	}

	/**
	 *
	 * @param x
	 * @param y1
	 * @param y2
	 * @param direction
	 * @return x, width
	 */
	private int[] SearchTickestHomogenVerticalLines( int x, int y1, int y2, int direction, int xendpos )
	{
		ArrayList<Integer> xCoords = SearchHomogenXCoords(x, y1, y2, direction, xendpos);
		int maxwidth = 0, maxstartx = -1;
		int width = 0, startx = -1;
		//java.util.List<Integer> sortedX = xCoords.stream().sorted().collect( Collectors.toList());
		for( int i = 0; i < xCoords.size(); i++ )
		{
			if( i == 0 )
			{
				width = 1;
				startx = xCoords.get( i );
				continue;
			}

			if( Math.abs( xCoords.get( i ) - xCoords.get( i - 1 ) ) == 1 )
			{
				width++;
			}
			else
			{
				// compare with the maxwidth and the maxstartx
				if( maxwidth < width )
				{
					maxwidth = width;
					maxstartx = startx;
					if( TestSpace( y2- y1, width ))
						break;
				}
				width = 1;
				startx = xCoords.get( i );
			}
		}

		if( maxwidth < width )
		{
			maxwidth = width;
			maxstartx = startx;
		}
		if( direction < 0 )
			maxstartx -= maxwidth;

		return new int[]{maxstartx,maxwidth};
	}

	boolean TestPoints( int x1, int y1, int x2, int y2 )
	{
		int rgb1 = mCapture.getRGB( x1, y1 );

		int red1   = (rgb1 & 0x00ff0000) >> 16;
		int green1 = (rgb1 & 0x0000ff00) >> 8;
		int blue1  = rgb1 & 0x000000ff;
		//int alpha1 = (rgb1>>24) & 0xff;

		int rgb2 = mCapture.getRGB( x2, y2 );

		int red2   = (rgb2 & 0x00ff0000) >> 16;
		int green2 = (rgb2 & 0x0000ff00) >> 8;
		int blue2  = rgb2 & 0x000000ff;
		//int alpha2 = (rgb2>>24) & 0xff;

		if(( Math.abs( red1 - red2 ) * 100 / 256 < colorDifferenceTreshold ) &&
				( Math.abs( green1 - green2 ) * 100 / 256 < colorDifferenceTreshold ) &&
				( Math.abs( blue1 - blue2 ) * 100 / 256 < colorDifferenceTreshold ))
			return true;
		return false;
	}

	public Rectangle DetectWordBorders( int x, int y, /*Graphics g, */BufferedImage capture )
	{
		mCapture = capture;
		int[] homogenUpperLine = SearchHomogenHorizontalLine( x, y, -1 );
		int[] homogenLowerLine = SearchHomogenHorizontalLine( x, y,  1 );

		if( homogenLowerLine[0] - homogenUpperLine[0] < 8 )
			return null;

//		g.drawLine( homogenUpperLine[1], homogenUpperLine[0], homogenUpperLine[2], homogenUpperLine[0] );
//		g.drawLine( homogenLowerLine[1], homogenLowerLine[0], homogenLowerLine[2], homogenLowerLine[0] );
//		if( true ) return null;

		int[] homogenLeftLine  = SearchTickestHomogenVerticalLines( x, homogenUpperLine[0],
				homogenLowerLine[ 0 ], -1, homogenUpperLine[ 1 ] );
		int[] homogenRightLine = SearchTickestHomogenVerticalLines( x, homogenUpperLine[0],
				homogenLowerLine[ 0 ],  1, homogenUpperLine[ 2] );

		return new Rectangle( homogenLeftLine[0]+ homogenLeftLine[1],
				homogenUpperLine[ 0 ],
				homogenRightLine[0] - homogenLeftLine[0] - homogenLeftLine[1] ,
				homogenLowerLine[ 0 ] - homogenUpperLine[ 0 ] );
	}
}
