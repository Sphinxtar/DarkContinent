import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.io.IOException;

public class action 
{
	List<act> history;
	
	public action()
	{
		history = Collections.synchronizedList( new LinkedList<act>() );
		// List<String> stringList = new ArrayList<String>();
	}

	public void addAction( String name, String type, String news )
	{
		synchronized ( history )
		{
			act a = new act();
			a.name = new String( name );
			a.type = new String( type );
			a.news = new String( news );
			history.add( a );
		}
	}

	public void getAction( String name, SocketChannel sc, CharsetEncoder encoder )
	{
		try
		{
			synchronized( history)
			{
				Iterator<act> i = history.iterator(); // Must be in synchronized block
				while ( i.hasNext() )
				{
					act a = (act)(i.next());
					if ( a.name.equals( name ))
					{
						sc.write( encoder.encode(CharBuffer.wrap("<action type=\"" + a.type + "\">" + a.news + "</action>")));
						i.remove();
					}
				}
			}
		}
		catch (IOException e) 
		{ 
			System.err.println( e.getMessage());
		}
	}

/**
 * boolean noAction( String name )
 * param: name of player in string
 * return: true if no action is waiting
 */

	public boolean noAction( String name )
	{
		synchronized( history)
		{
			Iterator<act> i = history.iterator(); // Must be in synchronized block
			while ( i.hasNext() )
			{
				act a = (act)(i.next());
				if ( a.name.equals( name ))
				{
					return false;
				}
			}
		}
		return true;
	}

	public void dump( SocketChannel sc, CharsetEncoder encoder )
	{
		try
		{
			synchronized( history)
			{
				Iterator<act> i = history.iterator(); // Must be in synchronized block
				while ( i.hasNext() )
				{
					act a = (act)(i.next());
					sc.write( encoder.encode(CharBuffer.wrap("<action type=\"" + a.type + "\">" + a.news + "</action>")));
				}
			}

		}
		catch (IOException e) 
		{ 
			System.err.println( e.getMessage());
		}
	}

	private class act
	{
		String name;
		String type;
		String news;
		public act()
		{
		}
	}
	
/* 
	public static void main( String[] args )
	{
		action x = new action();
		x.addAction("bob", "long", "duck me");
		x.addAction("bob", "easy", "buck me");
		x.addAction("rick", "slow", "tuck me");
		x.getAction( "bob", System.err );
	//	x.getAction( "rick", System.err );
		x.dump( System.err ); // change all to printstream for this to work
	}
*/
}