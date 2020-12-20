/* 
 * Dark Continent Critter Class
 * Copyright (c) Linus Sphinx 2003
 */
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class critter 
{
	public static int BULLCOUNT = 5;
	public static int CROCODILE = 0;
	public static int TIGER = 1;
	public static int GORILLA = 2;
	public static int PYTHON = 3;
	public static int BEASTCOUNT = 4;
	public beast[] beasts = new beast[ BEASTCOUNT ];
	
	public static final int APPEAR = 0;
	public static final int SPLIT  = 1;
	public static final int ATTACK = 2;
	public static final int THREATEN = 3;
	public static final int NOESCAPE = 4;
	public static final int ESCAPE = 5;
	public static final int KILLHUNTER = 6;
	public static final int KILLBEAST  = 7;
	public static final int ACTIONCOUNT = 8;
	public static final int BEASTODDS = 6;
	
	public critter()
	{
		new critter( "xml/beasts.xml" );
	}
	
	public critter( String bname )
	{
		beasts[ 0 ] = new beast( "crocodile", '0', 4 );
		beasts[ 1 ] = new beast( "tiger", '1', 4 );
		beasts[ 2 ] = new beast( "gorilla", '2', 4 );
		beasts[ 3 ] = new beast( "giant python", '3', 4 );
		new Saxer( bname );		
	}

	public String say( int animal, int action, int which )
	{
		// System.err.println( "animal: " + animal + " action: " + action );
		switch ( action )
		{
			case APPEAR:
				return beasts[ animal ].appear[ which ];
			case SPLIT:
				return beasts[ animal ].split[ which ];
			case ATTACK:
				return beasts[ animal ].attack[ which ];
			case THREATEN:
				return beasts[ animal ].threaten[ which ];
			case NOESCAPE:
				return beasts[ animal ].noescape[ which ];
			case ESCAPE:
				return beasts[ animal ].escape[ which ];
			case KILLHUNTER:
				return beasts[ animal ].killhunter[ which ];
			case KILLBEAST:
				return beasts[ animal ].killbeast[ which ];
			default:
				return null;
		}
	}
	
	public String name( int b )
	{
		return( beasts[ b ].name );
	}
	
	public int whichBeast( String bname )
	{
		int i;
		for(i = 0; i < BEASTCOUNT; i++ )
		{
			if ( bname.equals( beasts[ i ].name ))
			{
				break;
			}
		}
//		System.err.println( "bname " + bname + " returns: " + i );
		return( i );
	}

	public void addTextToBeast( int beastnum, String category, String text )
	{
		if ( beastnum < BEASTCOUNT )
			beasts[ beastnum ].addText( category, text );
	}
	
	public void dumpBeasts()
	{
		int i;
		for( i = 0; i < BEASTCOUNT; i++ )
		{
			System.err.println( "NAME: " + beasts[ i ].name );
			for( int j = 0; j < beasts[ i ].appearnum; j++ )
			{
				System.err.println( "\tappear: " + beasts[ i ].appear[ j ] );
			}
			for( int j = 0; j < beasts[ i ].attacknum; j++ )
			{
				System.err.println( "\tattack: " + beasts[ i ].attack[ j ] );
			}
			for( int j = 0; j < beasts[ i ].escapenum; j++ )
			{
				System.err.println( "\tescape: " + beasts[ i ].escape[ j ] );
			}
			for( int j = 0; j < beasts[ i ].noescapenum; j++ )
			{
				System.err.println( "\tnoescape: " + beasts[ i ].noescape[ j ] );
			}
			for( int j = 0; j < beasts[ i ].threatennum; j++ )
			{
				System.err.println( "\tthreaten: " + beasts[ i ].threaten[ j ] );
			}
			for( int j = 0; j < beasts[ i ].splitnum; j++ )
			{
				System.err.println( "\tsplit: " + beasts[ i ].split[ j ] );
			}
			for( int j = 0; j < beasts[ i ].killhunternum; j++ )
			{
				System.err.println( "\tkillhunter: " + beasts[ i ].killhunter[ j ] );
			}
			for( int j = 0; j < beasts[ i ].killbeastnum; j++ )
			{
				System.err.println( "\tkillbeast: " + beasts[ i ].killbeast[ j ] );
			}

		}

	}

	// -----------------------------------
	//	know your inner beast class
	// -----------------------------------
	private class beast
	{
		String name;
		char type;
		int turns;
		String[] appear = new String[ BULLCOUNT ];
		String[] split = new String[ BULLCOUNT ];
		String[] attack = new String[ BULLCOUNT ];
		String[] threaten = new String[ BULLCOUNT ];
		String[] noescape = new String[ BULLCOUNT ];
		String[] escape = new String[ BULLCOUNT ];
		String[] killhunter = new String[ BULLCOUNT ];
		String[] killbeast = new String[ BULLCOUNT ];

		int appearnum = 0;
		int splitnum = 0;
		int attacknum = 0;
		int threatennum = 0;
		int noescapenum = 0;
		int escapenum = 0;
		int killhunternum = 0;
		int killbeastnum = 0;

		public beast( String bname, char btype, int bturns )
		{
			name = new String( bname );
			type = btype;
			turns = bturns;
		}

		public void addText( String cat, String txt )
		{
			if ( cat.equals( "appear" ))
			{
				appear[ appearnum ] = new String( txt );
				appearnum++;
			}
			if ( cat.equals( "split" ))
			{
				split[ splitnum ] = new String( txt );
				splitnum++;
			}
			if ( cat.equals( "attack" ))
			{
				attack[ attacknum ] = new String( txt );
				attacknum++;
			}
			if ( cat.equals( "threaten" ))
			{
				threaten[ threatennum ] = new String( txt );
				threatennum++;
			}
			if ( cat.equals( "noescape" ))
			{
				noescape[ noescapenum ] = new String( txt );
				noescapenum++;
			}
			if ( cat.equals( "escape" ))
			{
				escape[ escapenum ] = new String( txt );
				escapenum++;
			}
			if ( cat.equals( "killhunter" ))
			{
				killhunter[ killhunternum ] = new String( txt );
				killhunternum++;
			}
			if ( cat.equals( "killbeast" ))
			{
				killbeast[ killbeastnum ] = new String( txt );
				killbeastnum++;
			}
		}
	}
	// -------------------------------------------------
	// inner xml parser class
	// -------------------------------------------------
	public class Saxer extends DefaultHandler
	{
		//private String OObj = new String( "beast" ); // objects name in the stream we're combing through
		private StringBuffer Content = new StringBuffer( 1024 );
		private StringBuffer CurrentBeast = new StringBuffer( 40 );
		
		public Saxer( String fname )
		{

			SAXParserFactory factory = SAXParserFactory.newInstance();
			try 
			{
				SAXParser saxParser = factory.newSAXParser();
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
//				System.err.println( "CHARS: " + Content.toString() );
				if ( sName.equals( "name" ))
				{
					CurrentBeast.delete( 0, CurrentBeast.length() );
					CurrentBeast.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else
				{		
					addTextToBeast( whichBeast( CurrentBeast.toString() ), sName, Content.toString() );
					Content.delete( 0, Content.length() );
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
