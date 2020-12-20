/* 
 * Dark Continent hunter Class
 * Copyright (c) Linus Sphinx 2003
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//-----------------------------------------------
// Hunters class - the keeper of big bwanas list
//-----------------------------------------------
public class hunter 
{
	private static byte DEAD = 64;
	private static byte EL_CALL = 32;
	private static byte PURE = 16;
	private static byte CHIMP = 8;
	private static byte LEG = 4;
	private static byte BITE = 2;
	private static byte BAG = 1;
		// ill  stuff
	private static byte LEECH = 64; // too many maggots
	private static byte TSI = 32;	// tsi fly bite
	private static byte SPLINTER = 16;	// sleeping sickness
	private static byte DYS = 8; // dysentery
	private static byte MAL = 4; // malaria
	private static byte AMULET = 2; // wearing the amulet
	private static byte FVR = 1;	// jungle fever

	private static int animal = -1; // = not under attack else beast[animal]
	private static int BAGSIZE = 10;
	private static String Welcome = new String( "Welcome to The Dark Continent. Click the compass, log and buttons to control your bwana. The script is what your bwana will do when you are offline and some other bwana steps in your space. Commands are NSEW to move and K to attack.\n\nWhen under attack by the beasts, using an item attacks with it and any compass direction to escape.");
	private static String Pissoff = new String( "Bad password or vine not found" );
	// private static int SCRIPTSIZE = 30;
	
	List<bwana> bwanas = null;
	map Jungle = null;
	
	/**
	 * default constructor
	 */
	public hunter()
	{
		new hunter( "xml/bwanas.xml" );
	}

	/**
	 * constructor with path and file name to xml file
	 */
	public hunter( String bname )
	{
		bwanas = Collections.synchronizedList( new ArrayList<bwana>() );
		new Saxer( bname );
	}

	/**
	 * add new or update a returning player 
	 */
	public String addBwana( String start, String last, String name, String mail, String pwrd, int x, int y  )
	{
		String retStr = null;
		if ( ! exists( name ))
		{
			addBwana( mail, name, pwrd, Integer.toString(x), Integer.toString(y), "0", "0", "10", "0", "0", start, last,
					"false", "false", "false", "false", "false", "false", "false", "false", "false", "false", "false",
					"false", "false", "true", "", "SKKNNEEENEENN", "0", "25", "false" );
			retStr = Welcome;
		}
		else
		{
			if ( updateBwana( start, last, name, mail, pwrd ))
				retStr = Welcome;
			else
				retStr = Pissoff;
		}
		return retStr;
	}

	/**
	 * is player of this name in game?
	 */
	public boolean exists( String name )
	{
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				if ( b.handle.equalsIgnoreCase( name ) )
				{
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * player roster and last live move date
	 */
	public void activeList( SocketChannel client, CharsetEncoder encoder )
	{
		Date now = new Date();
		try
		{
			client.write( encoder.encode(CharBuffer.wrap("<active>")));
			client.write( encoder.encode(CharBuffer.wrap("<now>" + now + "</now>")));
			synchronized( bwanas )
			{
				Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
				while ( i.hasNext() )
				{
					bwana b = (bwana)i.next();
					client.write( encoder.encode(CharBuffer.wrap("<player><name>" + b.handle + "</name><last>" + b.last.toString() + "</last></player>")));
				}
			}
			client.write( encoder.encode(CharBuffer.wrap("</active>")));
		}
		catch (IOException e) 
		{ 
			System.err.println( e.getMessage());
		}
	}			

	/**
	 *  number of players
	 */
	public String headCount()
	{
		return "<headcount>" + bwanas.size() + "</headcount>";
	}
	
	/**
	 * update a player at start of session
	 */
	public boolean updateBwana( String start, String last, String name, String mail, String pwrd )
	{
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				if ( b.handle.equals( name ) )
				{
					if ( b.password.compareToIgnoreCase( pwrd ) == 0 )
					{
						try
						{
							b.last = DateFormat.getDateInstance().parse( last );
						}
							catch( ParseException pe )
						{
							b.last = new Date();
						}
						b.mail = mail;
						return true;
					}
				}
			}
			return false;
		}
	}
	
	/**
	 * add a new player 
	 */
	public void addBwana( String mail, String handle, String pwrd, String x, String y, 
							String turns, String thirst, String drinks, String hunger, 
							String read, String daze, String last, String dead, String elcall, 
							String pure, String chimp, String leg, String bite, 
							String leeches, String tsi, String splinter, String dys, 
							String malaria, String amulet, String fever, String hasBag,
							String bag, String script, String lastcmd, String savy, String tree )
	{
		DateFormat df = DateFormat.getDateInstance();
		bwana b = new bwana();
		b.mail = mail;
		b.handle = handle;
		b.password = pwrd;
		b.x = Integer.parseInt( x );
		b.y = Integer.parseInt( y );
		b.turns = Integer.parseInt( turns );
		b.thirst = Integer.parseInt( thirst );
		b.drinks = Integer.parseInt( drinks );
		b.hunger = Integer.parseInt( hunger );
		b.savy = Integer.parseInt( savy );
		b.smacks = 0;
		b.read = Long.parseLong( read, 10 );
		try
		{
			b.daze = df.parse( daze );
			b.last = df.parse( last );
		}
		catch( ParseException pe )
		{
			b.daze = new Date();
			b.last = new Date();
		}
		b.lastcmd = Byte.parseByte( lastcmd );
		for ( int i=0; i < bag.length() && i < b.bag.length; i++ )
		{
			b.bag[ i ] = bag.charAt( i );
		}
		b.script = script;
		if ( dead.equals( "true" ))
			b.misc |= DEAD;
		if ( elcall.equals( "true" ))
			b.misc |= EL_CALL;
		if ( pure.equals( "true" ))
			b.misc |= PURE;
		if ( chimp.equals( "true" ))
			b.misc |= CHIMP;
		if ( leg.equals( "true" ))
			b.misc |= LEG;
		if ( bite.equals( "true" ))
			b.misc |= BITE;
		if ( hasBag.equals( "true" ))
			b.misc |= BAG;

		if ( leeches.equals( "true" ))
			b.ill |= LEECH;
		if ( tsi.equals( "true" ))
			b.ill |= TSI;
		if ( splinter.equals( "true" ))
			b.ill |= SPLINTER;
		if ( dys.equals( "true" ))
			b.ill |= DYS;
		if ( malaria.equals( "true" ))
			b.ill |= MAL;
		if ( amulet.equals( "true" ))
			b.ill |= AMULET;
		if ( fever.equals( "true" ))
			b.ill |= FVR;
		if ( tree.equals( "true" ))
			b.upTree = true;
		b.turf = '\0';
		synchronized ( bwanas )
		{
			bwanas.add( b );
		}
	}

	/**
	 * who else is in my spot?
	 */
	public String here( bwana me )
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "<others>" );
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				if ( b.equals( me ))
					continue;
				if ( b.x == me.x && b.y == me.y )
					sb.append( "<other>" + b.handle + "</other>" );
			}
		}
		sb.append( "</others>" );
		return sb.toString();
	}

	/**
	 * delete a player
	 */
	public void delBwana( String bwaname )
	{
		// hasn't come up yet
	}

	/**
	 * describe a player looked up by handle
	 */
	public String describe( String name )
	{
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				if ( b.handle.equals( name ))
					return b.describe();
			}
		}
		return "<bwana><handle>" + name + " unknown</handle></bwana>";
	}
	
	/**
	 * fetch a player looked up by name
	 */
	public hunter.bwana get( String name )
	{
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				if (b.handle.equalsIgnoreCase( name ))
					return( b );
			}
		}
		return null;
	}

	/**
	 * describe each player in the entire plaver list
	 */
	public void dump( PrintWriter out )
	{
		out.println( "<bwanas>" );
		synchronized( bwanas )
		{
			Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				bwana b = (bwana)i.next();
				out.println( b.describe() );
			}
		}
		out.println( "</bwanas>" );
	}

	/**
	 * default save to xml
	 */
	public void save()
	{
		save( "xml/bwanas.xml" );
	}

	/**
	 * save a player in a file named bname
	 */
	public void save( String bname )
	{
		try
		{
			File f = new File( bname );
			FileWriter fw = new FileWriter( f );
			fw.write( "<bwanas>" + System.getProperty("line.separator") );
			synchronized( bwanas )
			{
				Iterator<bwana> i = bwanas.iterator(); // Must be in synchronized block
				while ( i.hasNext() )
				{
					bwana b = (bwana)i.next();
					fw.write( b.describe() + System.getProperty("line.separator") );
				}
			}
			fw.write( "</bwanas>" + System.getProperty("line.separator") );
			fw.flush();
			fw.close();
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}

	// -------------------------------------------
	// inner class representing individual player
	// --------------------------------------------
	public class bwana
	{
		String mail;
		String handle;
		String password;
		int x;
		int y;
		int turns;
		int thirst;
		int drinks;
		int hunger;
		int smacks; // how many times to hit your head before the penalty is paid
		int savy; // score
		long read;
		int attack = -1; // -1 = not under attack - 0 = .beast appears 1 = it's on bitch
		int beast = 0; // what's attacking me when attack -1
		Date daze;
		Date last;
		byte misc;
		byte ill; 							// guy.ill && AMULET not guy.misc
		byte lastcmd;
		char turf;
		char[] bag;
		String script = null;
		boolean upTree = false; // you always wake up on the ground - tree state not saved

		/**
		 * default constructor
		 */
		public bwana()
		{
			bag = new char[ BAGSIZE ];
		}

		/**
		 * off a player
		 */
		public void kill()
		{
			misc |= DEAD;
		}

		/**
		 * true false quiz of all player attributes returning a string
		 */
		public String dead(){ return((( misc & DEAD) == DEAD ) ? "true" : "false" );}
		public String elephantCall(){ return((( misc & EL_CALL) == EL_CALL ) ? "true" : "false" );}
		public String pureWater(){ return((( misc & PURE) == PURE ) ? "true" : "false" );}
		public String chimp(){ return((( misc & CHIMP) == CHIMP ) ? "true" : "false" );}
		public String legBroken(){ return((( misc & LEG ) == LEG ) ? "true" : "false" );}
		public String bitten(){	return((( misc & BITE ) == BITE ) ? "true" : "false" );}
		public String hasBag(){ return((( misc & BAG ) == BAG ) ? "true" : "false" );}
		public String leeches(){ return((( ill & LEECH ) == LEECH ) ? "true" : "false" );}
		public String tsi(){ return((( ill & TSI ) == TSI ) ? "true" : "false" );}
		public String splinter(){ return((( ill & SPLINTER ) == SPLINTER ) ? "true" : "false" );}
		public String dysentary(){ return((( ill & DYS ) == DYS ) ? "true" : "false" );}
		public String malaria(){ return((( ill & MAL ) == MAL ) ? "true" : "false" );}
		public String amulet(){ return((( ill & AMULET ) == AMULET ) ? "true" : "false" );}
		public String fever(){ return((( ill & FVR ) == FVR ) ? "true" : "false" );}
		public String tree(){ return( upTree ? "true" : "false" );}

		/**
		 * give a full description of this player
		 */
		public String describe()
		{
			return new String( "<bwana><mail>" + mail + "</mail>" + 
				"<handle>" + handle + "</handle>" +
				"<password>" + password + "</password>" +
				"<x>" + x + "</x>" +
				"<y>" + y + "</y>" +
				"<turns>" + turns + "</turns>" +
				"<thirst>" + thirst + "</thirst>" +
				"<drinks>" + drinks + "</drinks>" +
				"<hunger>" + hunger + "</hunger>" +
				"<savy>" + savy + "</savy>" +
				"<read>" + read + "</read>" +
				"<daze>" + daze.toString() + "</daze>" +
				"<last>" + last.toString() + "</last>" +
				"<dead>" + dead() + "</dead>" +
				"<elephant>" + elephantCall() + "</elephant>" +
				"<pure>" + pureWater() + "</pure>" +
				"<chimp>" + chimp() + "</chimp>" +
				"<leg>" + legBroken() + "</leg>" +
				"<bite>" + bitten() + "</bite>" +
				"<hasbag>" + hasBag() + "</hasbag>" +
				"<leech>" + leeches() + "</leech>" +
				"<tsi>" + tsi() + "</tsi>" +
				"<splinter>" + splinter() + "</splinter>" +
				"<dysentary>" + dysentary() + "</dysentary>" +
				"<malaria>" + malaria() + "</malaria>" +
				"<amulet>" + amulet() + "</amulet>" +
				"<fever>" + fever() + "</fever>" +
				"<lastcmd>" + lastcmd + "</lastcmd>" +
				"<bag>" + bagPrint() + "</bag>" +
				"<script>" + script + "</script></bwana>" +
				"<tree>" + tree() + "</tree>"
			);
		}

		/**
		 * true false quiz of all player attributes returning boolean
		 */
		public boolean isDead(){ return((( misc & DEAD) == DEAD ));}
		public boolean isElephantCall(){ return((( misc & EL_CALL) == EL_CALL ));}
		public boolean isPureWater(){ return((( misc & PURE) == PURE ));}
		public boolean isChimp(){ return((( misc & CHIMP) == CHIMP ));}
		public boolean isLegBroken(){ return((( misc & LEG ) == LEG ));}
		public boolean isBitten(){	return((( misc & BITE ) == BITE ));}
		public boolean isLeeches(){ return((( ill & LEECH ) == LEECH ));}
		public boolean isTsi(){ return((( ill & TSI ) == TSI ));}
		public boolean isSplinter(){ return((( ill & SPLINTER ) == SPLINTER ));}
		public boolean isDysentary(){ return((( ill & DYS ) == DYS ));}
		public boolean isMalaria(){ return((( ill & MAL ) == MAL ));}
		public boolean isAmulet(){ return((( ill & AMULET ) == AMULET ));}
		public boolean isFever(){ return((( ill & FVR ) == FVR ));}
		public boolean withBag(){ return((( misc & BAG ) == BAG ));}
		public boolean inTree(){ return upTree; }
		
		/**
		 * short description of a player, just enough for display b
		 */
		public String shortForm()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("<handle>" + handle + "</handle>" +
				"<turns>" + turns  + "</turns>" +
				"<thirst>" + (thirst / 2 > 220 ? 220 : thirst / 2) + "</thirst>" +
				"<drinks>" + drinks * 20 + "</drinks>" +
				"<hunger>" + (hunger / 2 > 220 ? 220 : hunger / 2) + "</hunger>" +
				"<savy>" + (savy / 2 > 220 ? 220 : savy / 2) + "</savy>"
			);
			sb.append("<health>" );
			if ( isDead() )
				sb.append( "<ill>You're Dead!</ill>" );
			else if ( isBitten()||isLegBroken()||isLeeches()||isTsi()||isSplinter()||isDysentary()||isMalaria()||isFever() )
			{
				if ( isFever() )
					sb.append( "<ill>jungle fever</ill>" );
				if ( isSplinter() )
					sb.append( "<ill>a nasty splinter</ill>" );
				if ( isDysentary() )
					sb.append( "<ill>dysentary</ill>" );
				if ( isMalaria() )
					sb.append( "<ill>malaria</ill>" );
				if ( isTsi() )
					sb.append( "<ill>sleeping sickness</ill>" );
				if ( isLeeches() )
					sb.append( "<ill>leeches</ill>" );
				if ( isBitten() )
					sb.append( "<ill>an infected bite</ill>" );
				if ( isLegBroken() )
					sb.append( "<ill>a broken leg</ill>" );
			}
			else
				sb.append( "<ill>You're Alive!</ill>" );
			sb.append("</health><skills>" );
			if ( isElephantCall() )
				sb.append( "<skill>Power Of The Elephant Call</skill>" );
			if ( isChimp() )
				sb.append( "<skill>A Chimp Sidekick</skill>" );
			if( isAmulet() )
				sb.append( "<skill>THE AMULET<skill>" );
			sb.append("</skills>" );		
			sb.append( "<script>" + script + "</script>");
			return sb.toString();
		}

		/*
		 * turn the bag into a string
		 */ 
		private String bagPrint()
		{
			StringBuffer sb = new StringBuffer();
			for ( int i = 0; i < bag.length; i++ )
				if ( bag[ i ] != '\0' )
					sb.append( bag[ i ] );
			return sb.toString();
		}
		/*
		 * drop an item from bag
		 * returns: 
		 * 	true if exists
		 *  false if not
		 */ 
		public boolean drop( char item )
		{
			for ( int i = 0; i < bag.length; i++ )
			{
				if ( bag[ i ] == item )
				{
					bag[ i ] = 0;
					return true;
				}
			}
			return false;
		}
		
		/*
		 * filch an item from bag - same as drop but returns the character
		 * returns: 
		 * 	item if any exists
		 *  nul if empty
		 */ 
		public char filch()
		{
			char b;
			for ( int i = 0; i < bag.length; i++ )
			{
				if ( bag[ i ] != 0 )
				{
					b = bag[ i ];
					bag[ i ] = 0;
					return b;
				}
			}
			return 0;
		}

		/*
		 * grab an item into bag
		 * returns: 
		 * 	true if successful
		 *  false if not
		 */ 
		public boolean grab( char item )
		{
			for ( int i = 0; i < bag.length; i++ )
			{
				if ( bag[ i ] == 0 )
				{
					bag[ i ] = item;
					return true;
				}
			}
			return false;
		}

		/** 
		 * is there an item in my sack?
		 */
		public boolean has( char item )
		{
			for ( int i = 0; i < bag.length; i++ )
			{
				if ( bag[ i ] == item )
				{
					return true;
				}
			}
			return false;
		}

		/**
		 * penalize a player by incrementing hunger and thirst and decrementing their jungle know-how
		 */  
		public void puke( int food, int drink, int sense, int highscore ) // increase hunger, thirst, misery
		{
			hunger += food;
			thirst += drink;
			savy -= sense;
			if ( hunger > highscore )
				hunger = highscore;
			if ( thirst > highscore )
				thirst = highscore;
			if ( savy < 0 )
				savy = 0;
		}

		/**
		 * reduce a players hunger and thirst and increment their jungle know-how
		 */
		public void dine( int food, int drink, int sense, int highscore )
		{
			hunger -= food;
			thirst -= drink;
			savy += sense;
			if ( hunger < 0 )
				hunger = 0;
			if ( thirst < 0 ) // every child needs limits
				thirst = 0;
			if ( savy > highscore )
				savy = highscore;
		}

		/**
		 * update a players script
		 */
		public void setScript( String str )
		{
			script = new String( str );
		}
		
		/**
		 * break his leg if fractured true, heal if false
		 */
		public void brokenleg( boolean fractured )
		{
			if ( fractured = true )
				misc |= LEG;
			else
				misc &= LEG;
		}
		
	} // eo bwana inner class

	// -------------------------------------------------
	// inner xml parser class - load all files for this object
	// -------------------------------------------------
	public class Saxer extends DefaultHandler
	{
		// private String OObj = new String( "bwana" ); // objects name in the stream we're combing through
		private StringBuffer Content = new StringBuffer( 1024 );

		private StringBuffer CurrentMail = new StringBuffer( 80 );
		private StringBuffer CurrentHandle = new StringBuffer( 80 );
		private StringBuffer CurrentPassword = new StringBuffer( 80 );
		private StringBuffer CurrentX = new StringBuffer( 4 );
		private StringBuffer CurrentY = new StringBuffer( 4 );
		private StringBuffer CurrentTurns = new StringBuffer( 9 );
		private StringBuffer CurrentThirst = new StringBuffer( 9 );
		private StringBuffer CurrentDrinks = new StringBuffer( 9 );
		private StringBuffer CurrentHunger = new StringBuffer( 4 );
		private StringBuffer CurrentRead = new StringBuffer( 16 );
		private StringBuffer CurrentDaze = new StringBuffer( 10 );
		private StringBuffer CurrentLast = new StringBuffer( 10 );
		private StringBuffer CurrentDead = new StringBuffer( 10 );
		private StringBuffer CurrentElephant = new StringBuffer( 6 );
		private StringBuffer CurrentPure = new StringBuffer( 6 );
		private StringBuffer CurrentChimp = new StringBuffer( 6 );
		private StringBuffer CurrentLeg = new StringBuffer( 6 );
		private StringBuffer CurrentBite = new StringBuffer( 6 );
		private StringBuffer CurrentHasbag = new StringBuffer( 6 );
		private StringBuffer CurrentLeech = new StringBuffer( 6 );
		private StringBuffer CurrentTsi = new StringBuffer( 6 );
		private StringBuffer CurrentSplinter = new StringBuffer( 6 );
		private StringBuffer CurrentDysentary = new StringBuffer( 6 );
		private StringBuffer CurrentMalaria = new StringBuffer( 6 );
		private StringBuffer CurrentAmulet = new StringBuffer( 6 );
		private StringBuffer CurrentFever = new StringBuffer( 6 );
		private StringBuffer CurrentLastcmd = new StringBuffer( 6 );
		private StringBuffer CurrentSavy = new StringBuffer( 6 );
		private StringBuffer CurrentBag = new StringBuffer( 13 );
		private StringBuffer CurrentScript = new StringBuffer( 40 );
		private StringBuffer CurrentTree = new StringBuffer( 6 );

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
				if ( sName.equals( "mail" ))
				{
					CurrentMail.delete( 0, CurrentMail.length() ); // clear last obj's contents
					// System.err.println( "Mail: " + Content.toString() );
					CurrentMail.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "handle" ))
				{	
					CurrentHandle.delete( 0, CurrentHandle.length() );
					// System.err.println( "Handle: " + Content.toString() );
					CurrentHandle.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "password" ))
				{	
					CurrentPassword.delete( 0, CurrentPassword.length() );
					// System.err.println( "assword: " + Content.toString() );
					CurrentPassword.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "x" ))
				{	
					CurrentX.delete( 0, CurrentX.length() );
					// System.err.println( "X: " + Content.toString() );
					CurrentX.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "y" ))
				{	
					CurrentY.delete( 0, CurrentY.length() );
					// System.err.println( "Y: " + Content.toString() );
					CurrentY.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "turns" ))
				{	
					CurrentTurns.delete( 0, CurrentTurns.length() );
					// System.err.println( "Turns: " + Content.toString() );
					CurrentTurns.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "thirst" ))
				{	
					CurrentThirst.delete( 0, CurrentThirst.length() );
					// System.err.println( "thirst: " + Content.toString() );
					CurrentThirst.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "drinks" ))
				{	
					CurrentDrinks.delete( 0, CurrentDrinks.length() );
					// System.err.println( "Drinks: " + Content.toString() );
					CurrentDrinks.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "hunger" ))
				{	
					CurrentHunger.delete( 0, CurrentHunger.length() );
					// System.err.println( "Hunger: " + Content.toString() );
					CurrentHunger.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "read" ))
				{	
					CurrentRead.delete( 0, CurrentRead.length() );
					// System.err.println( "Read: " + Content.toString() );
					CurrentRead.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "daze" ))
				{	
					CurrentDaze.delete( 0, CurrentDaze.length() );
					// System.err.println( "Daze: " + Content.toString() );
					CurrentDaze.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "last" ))
				{	
					CurrentLast.delete( 0, CurrentLast.length() );
					// System.err.println( "Last: " + Content.toString() );
					CurrentLast.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "dead" ))
				{	
					CurrentDead.delete( 0, CurrentRead.length() );
					// System.err.println( "Dead: " + Content.toString() );
					CurrentDead.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "elephant" ))
				{	
					CurrentElephant.delete( 0, CurrentElephant.length() );
					// System.err.println( "Elephant: " + Content.toString() );
					CurrentElephant.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "pure" ))
				{	
					CurrentPure.delete( 0, CurrentPure.length() );
					// System.err.println( "Pure: " + Content.toString() );
					CurrentPure.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "chimp" ))
				{	
					CurrentChimp.delete( 0, CurrentChimp.length() );
					// System.err.println( "Chimp: " + Content.toString() );
					CurrentChimp.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "leg" ))
				{	
					CurrentLeg.delete( 0, CurrentLeg.length() );
					// System.err.println( "Leg: " + Content.toString() );
					CurrentLeg.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "bite" ))
				{	
					CurrentBite.delete( 0, CurrentBite.length() );
					// System.err.println( "Bite: " + Content.toString() );
					CurrentBite.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "hasbag" ))
				{	
					CurrentHasbag.delete( 0, CurrentHasbag.length() );
					// System.err.println( "Hasbag: " + Content.toString() );
					CurrentHasbag.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "leech" ))
				{	
					CurrentLeech.delete( 0, CurrentLeech.length() );
					// System.err.println( "Leech: " + Content.toString() );
					CurrentLeech.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "tsi" ))
				{	
					CurrentTsi.delete( 0, CurrentTsi.length() );
					// System.err.println( "Tsi: " + Content.toString() );
					CurrentTsi.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "splinter" ))
				{	
					CurrentSplinter.delete( 0, CurrentSplinter.length() );
					// System.err.println( "splinter: " + Content.toString() );
					CurrentSplinter.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "dysentary" ))
				{	
					CurrentDysentary.delete( 0, CurrentDysentary.length() );
					// System.err.println( "Dysentary: " + Content.toString() );
					CurrentDysentary.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "malaria" ))
				{	
					CurrentMalaria.delete( 0, CurrentMalaria.length() );
					// System.err.println( "Malaria: " + Content.toString() );
					CurrentMalaria.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "amulet" ))
				{	
					CurrentAmulet.delete( 0, CurrentAmulet.length() );
					// System.err.println( "Amulet: " + Content.toString() );
					CurrentAmulet.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "fever" ))
				{	
					CurrentFever.delete( 0, CurrentFever.length() );
					// System.err.println( "Fever: " + Content.toString() );
					CurrentFever.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "lastcmd" ))
				{	
					CurrentLastcmd.delete( 0, CurrentLastcmd.length() );
					// System.err.println( "Lastcmd: " + Content.toString() );
					CurrentLastcmd.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "savy" ))
				{	
					CurrentSavy.delete( 0, CurrentSavy.length() );
 					// System.err.println( "Savy: " + Content.toString() );
					CurrentSavy.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "bag" ))
				{	
					CurrentBag.delete( 0, CurrentBag.length() );
 					// System.err.println( "Bag: " + Content.toString() );
					CurrentBag.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "script" ))
				{	
					CurrentScript.delete( 0, CurrentScript.length() );
 					// System.err.println( "Script: " + Content.toString() );
					CurrentScript.append( Content.toString() );
					Content.delete( 0, Content.length());
				}
				else if ( sName.equals( "tree" ))
				{	
					CurrentTree.delete( 0, CurrentTree.length() );
					// System.err.println( "Tree: " + Content.toString() );
					CurrentTree.append( Content.toString() );
					Content.delete( 0, Content.length());
				}

			}
			if ( sName.equals( "bwana" )) // end tag time to add all our collected parts
			{
				addBwana( CurrentMail.toString(), CurrentHandle.toString(), CurrentPassword.toString(), 
						CurrentX.toString(), CurrentY.toString(), 
						CurrentTurns.toString(), CurrentThirst.toString(), CurrentDrinks.toString(), 
						CurrentHunger.toString(), CurrentRead.toString(), CurrentDaze.toString(), 
						CurrentLast.toString(), CurrentDead.toString(),	CurrentElephant.toString(), 
						CurrentPure.toString(), CurrentChimp.toString(), CurrentLeg.toString(), 
						CurrentBite.toString(), CurrentLeech.toString(), CurrentTsi.toString(), 
						CurrentSplinter.toString(),	CurrentDysentary.toString(), CurrentMalaria.toString(), 
						CurrentAmulet.toString(), CurrentFever.toString(), CurrentHasbag.toString(), 
						CurrentBag.toString(), CurrentScript.toString(), CurrentLastcmd.toString(), 
						CurrentSavy.toString(), CurrentTree.toString() );
			}
			if ( sName.equals( "bwanas" )) // end tag eof clean up
			{	
				CurrentMail.delete( 0, CurrentMail.length() ); // clear last obj's contents
				CurrentHandle.delete( 0, CurrentHandle.length() );
				CurrentPassword.delete( 0, CurrentHandle.length() );
				CurrentX.delete( 0, CurrentX.length() );
				CurrentY.delete( 0, CurrentY.length() );
				CurrentTurns.delete( 0, CurrentTurns.length() );
				CurrentThirst.delete( 0, CurrentThirst.length() );
				CurrentDrinks.delete( 0, CurrentDrinks.length() );
				CurrentHunger.delete( 0, CurrentHunger.length() );
				CurrentRead.delete( 0, CurrentRead.length() );
				CurrentDead.delete( 0, CurrentDead.length() );
				CurrentDaze.delete( 0, CurrentDaze.length() );
				CurrentLast.delete( 0, CurrentLast.length() );
				CurrentElephant.delete( 0, CurrentElephant.length() );
				CurrentPure.delete( 0, CurrentPure.length() );
				CurrentChimp.delete( 0, CurrentChimp.length() );
				CurrentLeg.delete( 0, CurrentLeg.length() );
				CurrentBite.delete( 0, CurrentBite.length() );
				CurrentHasbag.delete( 0, CurrentHasbag.length() );
				CurrentLeech.delete( 0, CurrentLeech.length() );
				CurrentTsi.delete( 0, CurrentTsi.length() );
				CurrentSplinter.delete( 0, CurrentSplinter.length() );
				CurrentDysentary.delete( 0, CurrentDysentary.length() );
				CurrentMalaria.delete( 0, CurrentMalaria.length() );
				CurrentAmulet.delete( 0, CurrentAmulet.length() );
				CurrentFever.delete( 0, CurrentFever.length() );
				CurrentLastcmd.delete( 0, CurrentLastcmd.length() );
				CurrentSavy.delete( 0, CurrentSavy.length() );
				CurrentBag.delete( 0, CurrentBag.length() );
				CurrentScript.delete( 0, CurrentScript.length() );
				CurrentTree.delete( 0, CurrentTree.length() );

			}
//			System.err.print("END_ELM: " + sName );
		}

		public void characters(char buf[], int offset, int len)	throws SAXException
		{
			String s = new String(buf, offset, len);
			if (!s.trim().equals("")) 
			{
				Content.append( s );
				// // System.err.println("CHARS: " + s );
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
