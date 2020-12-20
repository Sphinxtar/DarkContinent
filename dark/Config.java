import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class Config 
{
	String beasts = null;
	String birds = null;
	String bwanas = null;
	String item = null;
	String jungle = null;
	String story = null;
	String terrain = null;
	String whacks = null;
	String ground = null;
	String secret = null;
	String port = null;
	String address = null;
	String backlog = null;
	String scatter = null;
	int highscore;
	public Config()
	{
		File propsfile = new File( "bwana.properties" );
		FileInputStream fs = null;
		try
		{
			fs = new FileInputStream( propsfile );
		}
		catch( FileNotFoundException fnfe )
		{
			System.err.println( "ERROR: bwana.properties not found" );
			System.exit( 1 );
		}
		Properties props = new Properties();
		try
		{
			props.load( fs );
		}
		catch( IOException ioe )
		{
			System.err.println( "ERROR: loading of bwana.properties" );
			System.exit( 1 );
		}
	
		beasts = props.getProperty( "beasts" );
		birds = props.getProperty( "birds" );
		bwanas = props.getProperty( "bwanas" );
		item = props.getProperty( "item" );
		jungle = props.getProperty( "jungle" );
		story = props.getProperty( "story" );
		terrain = props.getProperty( "terrain" );
		whacks = props.getProperty( "whacks" );
		ground = props.getProperty( "ground" );
		scatter = props.getProperty( "scatter" );
		port = props.getProperty( "port" );
		address = props.getProperty( "address" );
		backlog = props.getProperty( "backlog" );
		secret = props.getProperty( "secret" );
		highscore = Integer.parseInt(props.getProperty("highscore"));
	}
}
