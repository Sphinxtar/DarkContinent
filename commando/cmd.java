import java.net.Socket;
import java.io.PrintWriter;

public class cmd 
{
	Config cfg = null;
	PrintWriter out = null;
	
	public cmd()
	{
			cfg = new Config();
	}

	public String send( String command )
	{
		try
		{
			Socket d = new Socket( cfg.address, Integer.parseInt( cfg.port )); // connect to dc
			out = new PrintWriter( d.getOutputStream(), true ); // set up for one way talking
			out.println( cfg.secret );
			out.println( command );
			out.flush();
			out.close();
		}
		catch ( java.io.IOException ioe )
		{
			return( ioe.getMessage() );
		}
		return( " Command sent" );
	}
}