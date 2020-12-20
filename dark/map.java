/* 
 * Dark Continent Map Class
 * Copyright (c) Linus Sphinx 1990
 * the job of the map class is to hold the terrain MAPWIDE and MAPHIGH  
 * along with items on the ground at each spot up to MAPDEEP. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringBuffer;
import java.util.ArrayList;
import java.util.Random;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class map 
{	
	public static int MAPWIDE = 39;
	public static int MAPHIGH = 59;
	public static int MAPVIEW = 7;
	private static byte FULL = 1;
	private static byte TRAP = 4;               	
	private static byte OCCUPIED = 8;
	private static byte FIRE = 16;
	private static byte HUT = 32;
	private static int MAPDEEP = 10;

	public static char [][][] jungle = null;
	public ArrayList<String> tooMany = null;
	public String blocked;
	public String suicide;

	public map()
	{
		new map( "xml/jungle.xml", "xml/ground.xml" );
	}

	public map( String jxmlname, String gxmlname )
	{
		jungle = new char[ MAPWIDE + 1 ][ MAPHIGH + 1 ][ MAPDEEP + 1 ];
		tooMany = new ArrayList<String>();
		new Saxer( jxmlname );
		new Saxer( gxmlname );
	}

	public void save()
	{
		save( "xml/ground.xml" ); // just the ground, not map
	}
	
	public void save( String gname )
	{
		try
		{
			File f = new File( gname );
			FileWriter fw = new FileWriter( f );
			fw.write( "<ground>" + System.getProperty("line.separator") );
			for ( int r = 0; r < MAPHIGH; r++ )
			{
				for ( int c = 0; c < MAPWIDE; c++ )
				{
					StringBuffer sb = new StringBuffer();
					for ( int i = 1; i < MAPDEEP; i++ )
					{
						if ( jungle[ c ][ r ][ i ] != '\0' )
							sb.append( jungle[ c ][ r ][ i ] );
					}
					if ( sb.length() > 0)
					{
						fw.write( "<item><x>" + c + "</x><y>" + r + "</y><code>" + sb.toString() + "</code></item>" + 
								System.getProperty("line.separator") );
					}
				}
			}
			fw.write( "</ground>" + System.getProperty("line.separator") );
			fw.flush();
			fw.close();
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}
	// for debugging
	public void dump()
	{
		int i, j;
		for( i = 0; i < MAPHIGH; i++ )
		{
			for(j = 0; j < MAPWIDE; j++ )
			{
				System.err.print( jungle[ j ][ i ][ 0 ] );
			}
			System.err.println( "*" );
		}
	}

	/*
	 * return a self inflicted death scene code
	 */ 
	public char suicide( Random r )
	{
		return suicide.charAt( r.nextInt( suicide.length() ));
	}

	/*
	 * check and see if the terrain will block the way
	 */ 
	public boolean legalMove( int x, int y )
	{
		return ( blocked.indexOf( jungle[ x ][ y ][ 0 ] ) == -1 );
	}
	
	/*
	 * check and see if their is a tree in this terrain
	 * returns tree tag or null
	 */ 
	public String tree( int x, int y, boolean up )
	{
		if( jungle[ x ][ y ][ 0 ] == 't' )
		{
			if ( up )
				return "<treeup/>";
			else
				return "<treedown/>";
		}
		else
			return null;
	}

	/*
	 * check and see if their is a tree in this terrain
	 * returns boolean
	 */ 
	public boolean isTree( int x, int y )
	{
		return ( jungle[ x ][ y ][ 0 ] == 't' );
	}

	/*
	 * sweep this spot
	 */ 
	public void sweep( int x, int y )
	{
		for( int i = 1; i < MAPDEEP; i++ ) // skip terrain
		{
			jungle[ x ][ y ][ i ] = 0;
		}
	}

	/*
	 * try to pick an item off the ground
	 * returns:
	 *  true if it was found and deleted from ground
	 *  false if not there
	 */ 
	public boolean getItem( int x, int y, char item )
	{
		for( int i = 1; i < MAPDEEP; i++ ) // skip terrain
		{
			if ( jungle[ x ][ y ][ i ] == item )
			{
				jungle[ x ][ y ][ i ] = 0;
				return true;				
			}
		}
		return false;
	}
	
	/*
	 * put an item on the ground
	 * returns:
	 *  true if inserted in jungle
	 *  false if ground is full
	 */ 
	public boolean putItem( int x, int y, char item )
	{
		for( int i = 1; i < MAPDEEP; i++ ) // skip terrain 0
		{
			if ( jungle[ x ][ y ][ i ] == 0 )
			{
				jungle[ x ][ y ][ i ] = item;
				return true;
			}
		}
		return false;
	}

	/**
	 * int getCount( String code );
	 * return the number of items with this code on the ground
	 */
	public int getCount( String item )
	{
		int num = 0;
		char thing = item.charAt(0);
		for( int y = 0; y <= MAPHIGH; y++ )
		{
			for( int x = 0; x <= MAPWIDE; x++ )
			{
				for( int i = 1; i < MAPDEEP; i++ )
				{
					if (( jungle != null )&&( jungle[ x ][ y ][ i ] == thing  ))
						num++;
				}
			}
		}
		return num;
	}
	
	/*
	 * 	has the ground the item I want to use
	 */
	public boolean has( int x, int y, char item )
	{
		int i;
		for( i = 1; i < MAPDEEP; i++ )
		{
			if ( jungle[ x ][ y ][ i ] == item )
				return true;
		}
		return false;
	}

	/*
	 * display all the items laying around this spot
	 * returns:
	 *  String of formatted item list
	 */ 
	public String displayGround( int x, int y )
	{
		StringBuffer g = new StringBuffer();
		for( int i = 1; i < MAPDEEP; i++ )
		{
			if ( jungle[ x ][ y ][ i ] != 0 )
				g.append( jungle[ x ][ y ][ i ] );
		}
		return new String( g.toString() );
	}

	/*
	 * show the jungle terrain for 7 points in any direction
	 * returns:
	 *  String of formatted terrain with coordinates in parameters 
	 */
	public String displayMap( int x, int y )
	{
		StringBuffer v = new StringBuffer( 512 );
		int c, r; // col row for output tags
		int curx, cury; // actual coords
		
		v.append("<map>");
		for ( r = 0; r < MAPVIEW + 1; r++ ) // top half and me
		{
		
			v.append("<r" + r + ">");
			cury = y - MAPVIEW + r;
			for ( c = 0; c < MAPVIEW; c++ ) // top left
			{
				curx = x - MAPVIEW + c;
				
				if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c" + c + ">O</c" + c + ">");
				else
					v.append("<c" + c + ">" + jungle[ curx ][ cury ][ 0 ] + "</c" + c + ">");
//					v.append("<c" + c + " x=\"" + curx + "\" y=\"" + cury + "\">" + jungle[ curx ][ cury ][ 0 ] + "</c" + c + ">");
			}
			for ( c = 0; c < MAPVIEW; c++ ) // top right
			{
				curx = x + c;
				if ( curx > MAPWIDE || cury > MAPHIGH || curx < 0 || cury < 0 || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 )
					v.append("<c" + (c + MAPVIEW) + ">O</c" + (c + MAPVIEW) + ">");
				else
					v.append("<c" + (c + MAPVIEW) + ">" + jungle[ curx ][ cury ][ 0 ] + "</c" + (c + MAPVIEW) + ">");
//					v.append("<c" + (c + MAPVIEW) + " x=\"" + curx + "\" y=\"" + cury + "\">" + jungle[ curx ][ cury ][ 0 ] + "</c" + (c + MAPVIEW) + ">");
			}
			v.append("</r" + r + ">");
		}
		for ( r = 1; r < MAPVIEW; r++ ) // skip me
		{
		
			v.append("<r" + (r + MAPVIEW) + ">");
			cury = y + r;
			for ( c = 0; c < MAPVIEW; c++ ) // bottom left
			{
				curx = x - MAPVIEW + c;
				
				if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c" + c + ">O</c" + c + ">");
				else
					v.append("<c" + c + ">" + jungle[ curx ][ cury ][ 0 ] + "</c" + c + ">");
//					v.append("<c" + c + " x=\"" + curx + "\" y=\"" + cury + "\">" + jungle[ curx ][ cury ][ 0 ] + "</c" + c + ">");
			}
			for ( c = 0; c < MAPVIEW; c++ ) // bottom right
			{
				curx = x + c;
				if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c" + (c + MAPVIEW) + ">O</c" + (c + MAPVIEW) + ">");
				else
					v.append("<c" + (c + MAPVIEW) + ">" + jungle[ curx ][ cury ][ 0 ] + "</c" + (c + MAPVIEW) + ">");
//					v.append("<c" + (c + MAPVIEW) + " x=\"" + curx + "\" y=\"" + cury + "\">" + jungle[ curx ][ cury ][ 0 ] + "</c" + (c + MAPVIEW) + ">");
			}
			v.append("</r" + (r + MAPVIEW) + ">");	
		}
		v.append("</map>");
//		System.err.println( v.toString());
		return v.toString();
	}

	/*
	 * show the jungle terrain for 7 points in any direction
	 * returns:
	 *  String of not XHTML formatted terrain  
	 */
	public String notContent( int x, int y )
	{
		StringBuffer v = new StringBuffer( 512 );
		int c, r; // col row for output tags
		int curx, cury; // actual coords
		
		for ( r = 0; r < MAPVIEW + 1; r++ ) // top half and me
		{
			v.append("<r>");
			cury = y - MAPVIEW + r;
			for ( c = 0; c < MAPVIEW + 1; c++ ) // top left
			{
				curx = x - MAPVIEW + c;
				
				if ( curx == x && cury == y )
					v.append("<c>you</c>");
				else if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c>0</c>");
				else
					v.append("<c>" + jungle[ curx ][ cury ][ 0 ] + "</c>");
			}
			for ( c = 0; c < MAPVIEW; c++ ) // top right
			{
				curx = x + c + 1;
				if ( curx > MAPWIDE || cury > MAPHIGH || curx < 0 || cury < 0 ) // off the edge
					v.append("<c>0</c>");
				else
					v.append("<c>" + jungle[ curx ][ cury ][ 0 ] + "</c>");
			}
			v.append("</r>");
		}
		for ( r = 1; r < MAPVIEW + 1; r++ ) // skip me
		{
			v.append("<r>");
			cury = y + r;
			for ( c = 0; c < MAPVIEW + 1; c++ ) // bottom left
			{
				curx = x - MAPVIEW + c;		
				if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c>0</c>");
				else
					v.append("<c>" + jungle[ curx ][ cury ][ 0 ] + "</c>");
			}
			for ( c = 0; c < MAPVIEW; c++ ) // bottom right
			{
				curx = x + c + 1;
				if ( curx < 0 || cury < 0 || curx > MAPWIDE || cury > MAPHIGH || jungle[ curx ][ cury ][ 0 ] < 32 || jungle[ curx ][ cury ][ 0 ] > 126 ) // off the edge
					v.append("<c>0</c>");
				else
					v.append("<c>" + jungle[ curx ][ cury ][ 0 ] + "</c>");
			}
			v.append("</r>");	
		}
//		System.err.println( v.toString());
		return v.toString();
	}


	// -------------------------------------------------
	// inner xml parser class
	// -------------------------------------------------
	public class Saxer extends DefaultHandler
	{
		private StringBuffer Content = new StringBuffer( 1024 );
		private StringBuffer CurrentBlocked = new StringBuffer( 40 );
		private StringBuffer CurrentSuicide = new StringBuffer( 40 );
		private StringBuffer CurrentMap = new StringBuffer( 2512 );
		private StringBuffer CurrentTooMany = new StringBuffer( 256 );
		// ground
		private StringBuffer CurrentX = new StringBuffer( 4 );
		private StringBuffer CurrentY = new StringBuffer( 4 );
		private StringBuffer CurrentCode = new StringBuffer( 4 );

		public Saxer( String fname )
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			try 
			{
				SAXParser saxParser = factory.newSAXParser();
//				System.err.println( "FNAME: '" + fname + "'");
				saxParser.parse( new File( fname ), this );
			} 
			catch (Throwable t) 
			{
				t.printStackTrace();
			}
		}

		public void startElement(String namespaceURI,
								 String lName, // local name
								 String qName, // qualified name
								 Attributes attrs)
		throws SAXException
		{
	/*
			String eName = lName; // element name
			if ( "".equals( eName ))
				eName = qName; 
			System.err.println("START_ELM: " + eName );
	*/		}

		public void endElement(String namespaceURI,
							   String sName, // simple name
							   String qName  // qualified name
							  )
		throws SAXException
		{
			if ( "".equals( sName ))
				sName = qName;
			if ( Content.length() > 0 )
			{
				if ( sName.equals( "code" ))
				{	
					CurrentCode.delete( 0, CurrentCode.length() );
//					System.err.println( "Code: " + Content.toString() );
					CurrentCode.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "x" ))
				{	
					CurrentX.delete( 0, CurrentX.length() );
//					System.err.println( "X: " + Content.toString() );
					CurrentX.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "y" ))
				{	
					CurrentY.delete( 0, CurrentY.length() );
//					System.err.println( "Y: " + Content.toString() );
					CurrentY.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "map" ))
				{	
					CurrentMap.delete( 0, CurrentMap.length() );
//					System.err.println( "Map: " + Content.toString() );
					CurrentMap.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "toomany" ))
				{	
					CurrentTooMany.delete( 0, CurrentTooMany.length() );
//					System.err.println( "TooMany: " + Content.toString() );
					CurrentTooMany.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "blocked" ))
				{	
					CurrentBlocked.delete( 0, CurrentBlocked.length() );
//					System.err.println( "Blocked: " + Content.toString() );
					CurrentBlocked.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				if ( sName.equals( "suicide" ))
				{	
					CurrentSuicide.delete( 0, CurrentBlocked.length() );
//					System.err.println( "Suicide: " + Content.toString() );
					CurrentSuicide.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
			}
			if ( Content.length() == 0 )
			{
				if ( sName.equals( "item" ))
				{
					int x = Integer.parseInt( CurrentX.toString() );
					int y = Integer.parseInt( CurrentY.toString() );
					for ( int i = 1; i < MAPDEEP && i - 1 < CurrentCode.length(); i++ )
					{
						if ( jungle[ x ][ y ][ i ] == '\0' )
						{
							jungle[ x ][ y ][ i ] = CurrentCode.charAt( i - 1 );
						}
					}
					CurrentCode.delete( 0, CurrentCode.length() );
					CurrentX.delete( 0, CurrentX.length() );
					CurrentY.delete( 0, CurrentY.length() );
				}
				else if ( sName.equals( "jungle" ))
				{
					int h = 1; // index into compound string
					for ( int i = 0; i <= MAPHIGH; i++ )
					{
						for ( int j = 0; j <= MAPWIDE; j++ )
						{
							jungle[j][i][0] = CurrentMap.charAt( h++ );
							for ( int z = 1; z < MAPDEEP; z++ )
								jungle[j][i][z] = '\0';
						}
						h++;
					}

					CurrentMap.delete( 0, CurrentMap.length() );
				}
				else if ( sName.equals( "toomany" ))
				{
					tooMany.add( CurrentTooMany.toString() );
					CurrentTooMany.delete( 0, CurrentTooMany.length() );
				}
				else if ( sName.equals( "blocked" ))
				{
					blocked = new String( CurrentBlocked.toString() );
					CurrentBlocked.delete( 0, CurrentBlocked.length() );
				}
				else if ( sName.equals( "suicide" ))
				{
					suicide = new String( CurrentSuicide.toString() );
					CurrentSuicide.delete( 0, CurrentSuicide.length() );
				}
			}
	//			System.err.print("END_ELM: " + sName );
		}

		public void characters(char buf[], int offset, int len)	throws SAXException
		{
			String s = new String(buf, offset, len);
			if (!s.trim().equals("")) 
			{
				Content.append( s );
				// System.err.println("CHARS: " + s );
			}
		}

		public void startDocument() throws SAXException
		{
		}

		public void endDocument() throws SAXException
		{
		}
	}
} // eo class
