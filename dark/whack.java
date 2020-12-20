import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class whack 
{
	private ArrayList<String> attackperp; // init attack for perp
	private ArrayList<String> attackvic;  // init attacked for vic

	private ArrayList<String> whack;       // successful hit
	private ArrayList<String> whacker;    // apre attack for perp 
	private ArrayList<String> whacked;    // apre attack for vic
	
	private ArrayList<String> miss;       // failed hit
	private ArrayList<String> misser;     // apre miss for perp
	private ArrayList<String> missed;     // apre miss for vic
	
	private ArrayList<String> stumble;    // owie
	private ArrayList<String> msghdr;	// drums speak
	private ArrayList<String> snatch;	// stealing
	private ArrayList<String> snatched;	// stolen
	private ArrayList<String> snatchee;	// stealing
	private ArrayList<String> snatcher;	// stolen
 	
	public whack()
	{
		new whack( "xml/whacks.xml" );
	}

	public whack( String xmlfname )
	{
		attackperp = new ArrayList<String>();
		attackvic = new ArrayList<String>();
		whack = new ArrayList<String>();
		whacker = new ArrayList<String>();
		whacked = new ArrayList<String>();
		miss = new ArrayList<String>();
		misser = new ArrayList<String>();
		missed = new ArrayList<String>();

		stumble = new ArrayList<String>();		
		msghdr = new ArrayList<String>();
		
		snatch = new ArrayList<String>();
		snatched = new ArrayList<String>();
		snatchee = new ArrayList<String>();
		snatcher = new ArrayList<String>();
		
		new Saxer( xmlfname );
	}

	String say( Random rn )
	{
		return whack.get( rn.nextInt( whack.size())).toString();
	}
	
	// player attacks
	String attacker( Random rn, String weap, String perp, String vic ) // init for perp
	{
		return "" + vic + " " + attackperp.get( rn.nextInt( attackperp.size())).toString() + " " + weap + "."; 		
	}
	String attacked( Random rn, String weap, String perp, String vic ) // init for vic
	{
		return "" + perp + " " + attackvic.get( rn.nextInt( attackvic.size())).toString() + " " + weap + "."; 		
	}

	String whacks( Random rn, String weap, String perp, String vic ) // success for both
	{
		return "" + perp + "'s " + weap + " " + whack.get( rn.nextInt( whack.size())).toString() + " " + vic + "."; 		
	}

	String whacker( Random rn, String weap, String perp, String vic ) // success for perp
	{
		return "" + vic + " " + whacker.get( rn.nextInt( whacker.size())).toString() + " " + weap + "."; 		
	}
	String whacked( Random rn, String weap, String name ) // success for vic
	{
		return "" + name + " " + whacked.get( rn.nextInt( whacked.size())).toString(); 		
	}

	String miss( Random rn, String weap, String perp, String vic ) // failure for both
	{
		return "" + vic + " " + miss.get( rn.nextInt( miss.size())).toString() + " " + perp + "'s " + weap +  "."; 		
	}
	
	String misser( Random rn, String weap, String name ) // failure for perp
	{
		return "" + name + " " + misser.get( rn.nextInt( misser.size())).toString(); 		
	}
	String missed( Random rn, String weap, String perp, String vic ) // failure for vic
	{
		return "" + perp + "'s " + weap + " " + missed.get( rn.nextInt( missed.size())).toString(); 		
	}

	String stumble( Random rn ) // failure for perp
	{
		return "" + stumble.get( rn.nextInt( stumble.size())).toString(); 		
	}	
	String messenger(Random rn, String from, String msg )
	{
		return "" + msghdr.get(rn.nextInt(msghdr.size())).toString() + " " + from + ": &quot;" + msg + "&quot;.";
	}

	String theft(Random rn, String thief, String vic, String booty )
	{
		return "" + snatch.get(rn.nextInt(snatch.size())).toString() + " " + vic + "'s " + snatched.get(rn.nextInt(snatched.size())).toString() + " " + booty + "!";
	}

	String thief(Random rn, String thief, String vic, String booty )
	{
		return "" + snatchee.get(rn.nextInt(snatchee.size())).toString() + " " + thief + " " + snatcher.get(rn.nextInt(snatcher.size())).toString() + " " + booty + "!";
	}

	/*
	* inner saxer class to read our whacks
	*/
	public class Saxer extends DefaultHandler
	{
		private StringBuffer Content = new StringBuffer( 1024 );
		
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
				if ( sName.equals( "attackperp" ))
				{	
//					System.err.println( "attackperp: " + Content.toString() );
					attackperp.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "attackvic" ))
				{	
//					System.err.println( "attackvic: " + Content.toString() );
					attackvic.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "whack" ))
				{	
//					System.err.println( "Whack: " + Content.toString() );
					whack.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "whacked" ))
				{	
//					System.err.println( "Whacked: " + Content.toString() );
					whacked.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "whacker" ))
				{	
//					System.err.println( "Whacker: " + Content.toString() );
					whacker.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "miss" ))
				{	
//					System.err.println( "Miss: " + Content.toString() );
					miss.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "missed" ))
				{	
//					System.err.println( "Missed: " + Content.toString() );
					missed.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "misser" ))
				{	
//					System.err.println( "Misser: " + Content.toString() );
					misser.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "stumble" ))
				{	
//					System.err.println( "Stumble: " + Content.toString() );
					stumble.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "message" ))
				{	
//					System.err.println( "Msghdr: " + Content.toString() );
					msghdr.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "snatch" ))
				{	
//					System.err.println( "Snatch: " + Content.toString() );
					snatch.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "snatched" ))
				{	
//					System.err.println( "Snatched: " + Content.toString() );
					snatched.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "snatchee" ))
				{	
//					System.err.println( "Snatchee: " + Content.toString() );
					snatchee.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
				}
				if ( sName.equals( "snatcher" ))
				{	
//					System.err.println( "Snatcher: " + Content.toString() );
					snatcher.add( Content.toString() );
					Content.delete( 0, Content.length()); // clear old value
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
