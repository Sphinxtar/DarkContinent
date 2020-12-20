
public class commandline 
{
	public commandline()
	{
	}

	public static void main(String[] args)
	{
//		commandline commandline = new commandline();
		if ( args.length < 1 )
			System.err.println( "Usage:\n\tjava -cp ./commando.jar commandline [save|quit|bail|scat]" );
		else
			System.err.println( new cmd().send( args[ 0 ] ));
	}
}