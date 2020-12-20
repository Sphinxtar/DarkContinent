/* 
 * Dark Continent terrain class
 * Copyright (c) Linus Sphinx 2003 
*/
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Random;

public class birdsnbees 
{
	ArrayList<String> fauna = null;
	
	public birdsnbees()
	{
		new birdsnbees( "xml/birds.xml" );
	}

	public birdsnbees( String bnb )
	{
		fauna = new ArrayList<String>();
		new Saxer( bnb );
	}

	public String describe( Random r ) // xml formatted by action
	{
		return (String)(fauna.get( r.nextInt(fauna.size() - 1)));
	}
	
	// -------------------------------------------------
	// inner xml parser class
	// -------------------------------------------------
	public class Saxer extends DefaultHandler
	{
		private StringBuffer Content = new StringBuffer( 256 );
		private StringBuffer CurrentBird = new StringBuffer( 256 );

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
				if ( sName.equals( "bird" ))
				{
					if ( CurrentBird.length() > 0 )
					{
						fauna.add( CurrentBird.toString() );
						CurrentBird.delete( 0, CurrentBird.length() );
					}
//					System.err.println( "ID: " + Content.toString() );
					CurrentBird.append( Content.toString() );
					Content.delete( 0, Content.length());
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
