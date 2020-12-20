/* 
 * Dark Continent terrain class
 * Copyright (c) Linus Sphinx 1990 
*/
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class terrain
{

	public static int TERRACOUNT = 44; // number of different surfaces
	turf[] terra = null; 

	public terrain()
	{
		new terrain( "xml/terrain.xml" );
	}
		
	public terrain( String tname )
	{
		terra = new turf[ TERRACOUNT + 1 ];
		new Saxer( tname );
	}

	public void addTurf( String ID, String Thirst, String Hunger, String Code, String Surface, String Desc, String Toll )
	{
		int id = Integer.valueOf(ID);
//		System.err.println( "ADDATURF: " + id );
		terra[ id ] = new turf();
		if ( Thirst.length() > 0 ) 
			terra[ id ].thirst = Integer.valueOf(Thirst);
		else
			terra[ id ].thirst = 0;
		if ( Hunger.length() > 0 ) 
			terra[ id ].hunger = Integer.valueOf(Hunger);
		else
			terra[ id ].hunger = 0;
		if ( Toll.length() > 0 )
			terra[ id ].toll = Integer.valueOf(Toll);
		else
			terra[ id ].toll = 0;
		if ( Code.length() > 0 ) 
			terra[ id ].code = Code.charAt( 0 );
		else
			terra[ id ].code = 'Z';
		terra[ id ].Surface = new String( Surface );
		terra[ id ].Desc = new String( Desc );
	}
	
	/*
	 * return the long description for a type of terrain 
	 */
	public String longdesc( char type ) // long description is xml formatted by action object
	{
		for ( int i = 0; i < TERRACOUNT; i++ )
		{
			if ( terra[ i ].code == type )
				return new String(  terra[ i ].Desc );
		}
		return "You seem to have entered a mysterious void!";
	}
	
	/*
	 * return the short description for a type of terrain 
	 */
	public String shortdesc( char type )
	{
		for ( int i = 0; i < TERRACOUNT; i++ )
		{
			if ( terra[ i ].code == type )
				return "<terra>" + terra[ i ].Surface + "</terra>";
		}
		return "<terra>mysterious void</terra>";
	}

	public turf turfType( char type )
	{
		for ( int i = 0; i < TERRACOUNT; i++ )
		{
			if ( terra[ i ].code == type )
				return terra[ i ];
		}
		return null;
	}

	public void dump()
	{
		System.err.println( "<terrains>" );
		
		for( int i = 0; i < TERRACOUNT; i++ )
		{
			System.err.println( "<terra><code>" + terra[ i ].code + "</code><toll>" + terra[i].toll +"</toll>"+"<surface>" + terra[ i ]. Surface + "</surface><description>" + terra[ i ].Desc + "</description></terra>" );
		}
		System.err.println( "</terrains>" );
	}
	//-------------------------------------------------
	// terrain inner inner terra class
	//-------------------------------------------------
	public class turf
	{
		int id;
		int thirst;
		int hunger;
		int toll;		
		char code;
		String Surface;
		String Desc;

		public turf()
		{
			id = 0;
			thirst = 0;
			hunger = 0;
			code = 0;
			toll = 0;
				
			Surface = null;
			Desc = null;
		}
	}
	
	// -------------------------------------------------
	// inner xml parser class
	// -------------------------------------------------
	public class Saxer extends DefaultHandler
	{
		private String OObj = new String( "terra" ); // objects name in the stream we're combing through
		private StringBuffer Content = new StringBuffer( 1024 );
		private StringBuffer CurrentID = new StringBuffer( 4 );
		private StringBuffer CurrentThirst = new StringBuffer( 6 );
		private StringBuffer CurrentHunger = new StringBuffer( 6 );
		private StringBuffer CurrentToll= new StringBuffer( 6 );
		private StringBuffer CurrentCode = new StringBuffer( 4 );
		private StringBuffer CurrentSurface = new StringBuffer( 80 );
		private StringBuffer CurrentDesc = new StringBuffer( 256 );

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

		public void endElement(String namespaceURI, String sName, String qName ) // simple name, qualified name
		throws SAXException
		{
			if ( "".equals( sName ))
				sName = qName;
			if ( Content.length() > 0 )
			{
				if ( sName.equals( "id" ))
				{
					CurrentID.delete( 0, CurrentID.length() );
//					System.err.println( "ID: " + Content.toString() );
					CurrentID.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "thirst" ))
				{	
					CurrentThirst.delete( 0, CurrentThirst.length() );
//					System.err.println( "Thirst: " + Content.toString() );
					CurrentThirst.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "hunger" ))
				{	
					CurrentHunger.delete( 0, CurrentHunger.length() );
//					System.err.println( "Hunger: " + Content.toString() );
					CurrentHunger.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "toll" ))
				{	
					CurrentToll.delete( 0, CurrentToll.length() );
//					System.err.println( "Toll: " + Content.toString() );
					CurrentToll.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "code" ))
				{	
					CurrentCode.delete( 0, CurrentCode.length() );
//					System.err.println( "Code: " + Content.toString() );
					CurrentCode.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "surface" ))
				{	
					CurrentSurface.delete( 0, CurrentSurface.length() );
//					System.err.println( "Surface: " + Content.toString() );
					CurrentSurface.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "desc" ))
				{	
					CurrentDesc.delete( 0, CurrentDesc.length() );
//					System.err.println( "Desc: " + Content.toString() );
					CurrentDesc.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
			}
			if ( Content.length() == 0 )
			{
				if ( sName.equals( OObj ))
				{	
					addTurf( CurrentID.toString(), CurrentThirst.toString(), CurrentHunger.toString(), CurrentCode.toString(), CurrentSurface.toString(), CurrentDesc.toString(), CurrentToll.toString() );
								// so we don't get old values when no tag
					CurrentID.delete( 0, CurrentID.length() );
					CurrentThirst.delete( 0, CurrentThirst.length() );
					CurrentHunger.delete( 0, CurrentHunger.length() );
					CurrentCode.delete( 0, CurrentCode.length() );
					CurrentToll.delete( 0, CurrentToll.length() );
					CurrentSurface.delete( 0, CurrentSurface.length() ); 
					CurrentDesc.delete( 0, CurrentDesc.length() );
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
