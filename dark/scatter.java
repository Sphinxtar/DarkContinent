import java.io.File;
import java.util.Random;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class scatter 
{
	// private ArrayList wack;
	private Random r;
	private map m;
	
	
	public scatter()
	{
		new scatter( "xml/scatter.xml", new Random(), new map() );
	}

	public scatter( String xmlfname, Random rn, map mm )
	{
		r = rn;
		m = mm;
		new Saxer( xmlfname );
	}

   /*
	* inner saxer class to read our scatter data
	*/
	public class Saxer extends DefaultHandler
	{
		private StringBuffer Content = new StringBuffer( 1024 );
		private String code;
		// private String name;
		private String turf;
		private int count;
		
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
					System.err.println( "code: " + Content.toString() );
					code = Content.toString();
					Content.delete( 0, Content.length() ); // clear old value
				}
				if ( sName.equals( "terrain" ))
				{	
					System.err.println( "terrain: " + Content.toString() );
					turf = Content.toString();
					Content.delete( 0, Content.length() ); // clear old value
				}
				if ( sName.equals( "name" )) // do nothing
				{	
					System.err.println( "name: " + Content.toString() );
					Content.delete( 0, Content.length() ); // clear old value
				}
				if ( sName.equals( "count" ))
				{	
					System.err.println( "count: " + Content.toString() );
					count = Integer.parseInt( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
			}
			if ( sName.equals( "item" )) // add to jungle
			{	
				System.err.println( "item: " + Content.toString() );
				Content.delete( 0, Content.length() ); // clear old value
				int numb = m.getCount( code );
				for ( int i = 0; i < count - numb; i++ ) // for desired total
				{
						boolean planted = false;
						while ( !planted )
						{
							int y = r.nextInt( map.MAPHIGH );
							int x = r.nextInt( map.MAPWIDE );
							if ( turf.indexOf( map.jungle[x][y][0] ) > -1 ) // terra's ok
							{
								planted = m.putItem( x, y, code.charAt(0) ); // if no room try again
							}
						}
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
	} // eo saxer

} // eo scatter
