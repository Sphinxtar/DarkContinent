/*
 * Dark Continent items class
 * Copyright (c) Linus Sphinx 2003 
*/
import java.io.File;
import java.io.PrintStream;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class items
{
	private Hashtable<String, item> sack;
	
	public items()
	{
		new items( "xml/items.xml" );
	}

	public items( String xmlfname )
	{
		sack = new Hashtable<String, item>();
		new Saxer( xmlfname );
	}
	/* 
	 * toList
	 * takes: top tag name, item chars, compass location, string of "<tree/>" or null 
	 * reads character array of item codes
	 * makes xml items out of it  
	 * adds the extra tags
	 * encloses it inside top tags 
	 * adds location if item is a compass
	 * returns xml doc in a String
	 */

	public String toList( String top, char[] b, String location, String extra )
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "<" + top + ">" );
		for ( int i=0; i < b.length; i++ )
		{
			if ( b[ i ] != '\0' )
				sb.append( "<item code=\"" + b[ i ] + "\">" + name( "" + b[ i ] + "" ));
			if ( b[ i ] == '4' )
				sb.append( " " + location ); // ugh, make compass work
			if ( b[ i ] != '\0' )
				sb.append( "</item>" );
		}
		if ( extra != null ) // extra item such as a tree
			sb.append( extra );
		sb.append( "</" + top + ">" );
		return( sb.toString() );
	}	
	
	public String toList( String top, char[] b, String location )
	{
		return( toList( top, b, location, null ));
	}

	public item get( char thing )
	{
		return ((item)sack.get( new String( "" + thing ) ));
	}

	public String use( char thing )
	{
		return ((item)sack.get( new String( "" + thing ) )).use();
	}

	public String inspect( char thing )
	{
		return ((item)sack.get( new String( "" + thing ) )).inspect();
	}

	public String name( String thing )
	{
		if ( sack.containsKey( thing ))
			return ((item)sack.get( thing )).name();
		else
			return "darn thing";
	}

	public void dump( PrintStream out )
	{
		out.println( "<items>" );
		for (Enumeration<item> e = sack.elements(); e.hasMoreElements(); ) 
		{
			((item)e.nextElement()).dump(out);
		}
		out.println( "</items>" );

	}

	/*
	* inner item class to put in our hashtable
	*/
	public class item
	{
		char code; // key
		char morph;
		boolean evil;
		int hunger;
		int thirst;
		int savy;
		String name;
		String ascii;
		String inspect;
		String[] uses;

		public item()
		{
			code = '\0';
			morph = '\0';
			evil = true; // tough world
			hunger = 0;
			thirst = 0;
			savy = 1;
			name = null;
			ascii = null;
			inspect = null;
			uses = null;
		}

		public String use()
		{
			return uses[ 0 ]; 
		}

		public String use( int i )
		{
			if ( i < uses.length )
				return  uses[ i ];
			else
				return null;
		}

		public String inspect()
		{
			return inspect; 
		}

		public String name()
		{
			return name; 
		}

		public void dump( PrintStream out )
		{
			out.println( "<item>\n<code>" + code + "</code>\n" +
					"<morph>" + morph + "</morph>\n" +
					"<name>" + name + "</name>\n" +
					"<evil>" + (evil?"true":"false") + "</evil>\n" +
					"<hunger>" + hunger + "</hunger>\n" +
					"<thirst>" + thirst + "</thirst>\n" +
					"<savy>" + savy + "</savy>\n" +
					"<ascii>" + ascii + "</ascii>\n" +
					"<inspect>" + inspect + "</inspect>\n" );				
			for ( int g=0; g < uses.length; g++ )
			{
				out.println( "<use number=\"" + g + "\">" + uses[ g ].toString() + "</use>" );
			}
			out.println( "</item>" );
		}
	}
	// eo item inner class
	
	/*
	* inner saxer class to read our items
	*/
	public class Saxer extends DefaultHandler
	{
		private StringBuffer Content = new StringBuffer( 1024 );
		private StringBuffer CurrentCode = new StringBuffer( 4 );
		private StringBuffer CurrentEvil = new StringBuffer( 12 );
		private StringBuffer CurrentHunger = new StringBuffer( 12 );
		private StringBuffer CurrentThirst = new StringBuffer( 12 );
		private StringBuffer CurrentSavy = new StringBuffer( 12 );
		private StringBuffer CurrentMorph = new StringBuffer( 4 );
		private StringBuffer CurrentName = new StringBuffer( 1024 );
		private StringBuffer CurrentAscii = new StringBuffer( 1024 );
		private StringBuffer CurrentInspect = new StringBuffer( 1024 );
		private StringBuffer[] CurrentUsage= { 	new StringBuffer( 1024 ), new StringBuffer( 1024 ), 
												new StringBuffer( 1024 ), new StringBuffer( 1024 ), 
												new StringBuffer( 1024 ), new StringBuffer( 1024 ), 
												new StringBuffer( 1024 ), new StringBuffer( 1024 ), 
												new StringBuffer( 1024 ), new StringBuffer( 1024 ) 
												};
		private int usages = 0;
		
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
													// local name, qualified name
		public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) 
		throws SAXException
		{
/*			String eName = lName; // element name
			if ( "".equals( eName ))
				eName = qName; 
			System.err.println("START_ELM: " + eName );
*/		}

													// simple name, qualified name
		public void endElement(String namespaceURI, String sName, String qName ) throws SAXException
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
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "morph" ))
				{	
					CurrentMorph.delete( 0, CurrentMorph.length() );
//					System.err.println( "Morph: " + Content.toString() );
					CurrentMorph.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "evil" ))
				{	
					CurrentEvil.delete( 0, CurrentEvil.length() );
//					System.err.println( "Evil: " + Content.toString() );
					CurrentEvil.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "hunger" ))
				{	
					CurrentHunger.delete( 0, CurrentHunger.length() );
//					System.err.println( "Hunger: " + Content.toString() );
					CurrentHunger.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "thirst" ))
				{	
					CurrentThirst.delete( 0, CurrentThirst.length() );
//					System.err.println( "Thirst: " + Content.toString() );
					CurrentThirst.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "savy" ))
				{	
					CurrentSavy.delete( 0, CurrentSavy.length() );
//					System.err.println( "Savy: " + Content.toString() );
					CurrentSavy.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "name" ))
				{	
					CurrentName.delete( 0, CurrentName.length() );
//					System.err.println( "Name: " + Content.toString() );
					CurrentName.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "ascii" ))
				{	
					CurrentAscii.delete( 0, CurrentAscii.length() );
//					System.err.println( "Ascii: " + Content.toString() );
					CurrentAscii.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "inspect" ))
				{	
					CurrentInspect.delete( 0, CurrentInspect.length() );
//					System.err.println( "Inspect: " + Content.toString() );
					CurrentInspect.append( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				else if ( sName.equals( "usage" ))
				{	
					CurrentUsage[ usages ].delete( 0, CurrentUsage[ usages ].length() );
//					System.err.println( "Usage " + usages + ": " + Content.toString() );
					CurrentUsage[ usages ].append( Content.toString() );
					Content.delete( 0, Content.length() ); // clear old value
					usages++;
				}
			}
			if ( Content.length() == 0 )
			{
				if ( sName.equals( "item" ))
				{	
					item thing = new item();
					thing.code = CurrentCode.toString().charAt(0);
					if ( CurrentMorph.length() > 0 )
						thing.morph = CurrentMorph.toString().charAt(0);
					if ( CurrentEvil.toString().compareToIgnoreCase( "false") == 0 )
						thing.evil = false;
					thing.hunger = Integer.parseInt( CurrentHunger.toString() ); 
					thing.thirst = Integer.parseInt( CurrentThirst.toString() ); 
					thing.savy = Integer.parseInt( CurrentSavy.toString() );
					thing.name = new String( CurrentName ); 
					thing.ascii = new String( CurrentAscii ); 
					thing.inspect = new String( CurrentInspect );
					thing.uses = new String[ usages ];
					for ( int i = 0; i < usages; i++ )
					{
						thing.uses[ i ] = new String( CurrentUsage[ i ] );
					}
					sack.put( CurrentCode.toString(), thing ); // add item
					CurrentCode.delete( 0, CurrentCode.length() ); // clear old value
					CurrentEvil.delete( 0, CurrentEvil.length() ); // clear old value
					CurrentHunger.delete( 0, CurrentHunger.length() ); // clear old value
					CurrentThirst.delete( 0, CurrentThirst.length() ); // clear old value
					CurrentSavy.delete( 0, CurrentSavy.length() ); // clear old value
					CurrentMorph.delete( 0, CurrentMorph.length() ); // clear old value
					CurrentName.delete( 0, CurrentName.length() ); // clear old value
					CurrentAscii.delete( 0, CurrentAscii.length() ); // clear old value
					CurrentInspect.delete( 0, CurrentInspect.length() ); // clear old value
					for ( int i = 0; i < usages; i++ )
					{
						CurrentUsage[ i ].delete( 0, CurrentUsage[ i ].length() );
					}
					usages = 0;
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
}
