/*
* Dark Continent Client Servlet - Pass args to dc server and fetch xml, convert to dom and apply style.
*/
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class bwana extends HttpServlet 
{
	private final static long serialVersionUID = 1;
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

	/**
	* Process the HTTP doGet request.
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doRequest( request, response );
	}

	/**
	* Process the HTTP doPost request.
	*/
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doRequest( request, response );
	}

	/**
	* Process the HTTP request.
	*/
	public void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Socket dc; // connection to dark
		PrintWriter dcOut; // for dc input
		PrintWriter out; // connection to the browser
		Document document;
		long dt;
		String[] commands = { "mov", "get", "put", "use", "see", "jmp", "stl", "kil", "off", "set", "clm" };
		Hashtable<String, String> crumbs = new Hashtable<>();

		try
		{
			String amt; // mount to refresh limits size of return doc
			String cmd; // user command to perform
			String parm; // parameter of user command
			String msg = ""; // message to send dark continent server who replies with amt of doc at current user state
			Cookie[] cookies = request.getCookies();
			HttpSession session = request.getSession();
			
			if ( cookies != null )
			{
				for (Cookie cookie : cookies) {
					crumbs.put(cookie.getName(), cookie.getValue());
				}
			}

			// if new here or no command posted start the party
			cmd = request.getParameter( "cmd" ); // what bwana does
			amt = request.getParameter( "amt" ); // what bwana wants

	 		if ( session.isNew() || cmd == null )
			{
				response.sendRedirect( "login.html" );
				return;
			}
			for (String command : commands) {
				if (cmd.equals(command)) // it's a simple command
				{
					if ((parm = request.getParameter("parm")) == null)
						parm = "0"; // default - first item

					msg = session.getId() + "|" + ((crumbs.size() > 0) ? crumbs.get("name") : "") + "|" +
							((crumbs.size() > 0) ? crumbs.get("pwrd") : "") + "|" + session.getCreationTime() + "|" +
							(((dt = session.getLastAccessedTime()) < 0) ? "never" : new Date(dt).toString()) +
							"|" + ((amt == null) ? "f" : amt) + "|" + cmd + "|" + parm + "|";
				}
			}
			if ( msg.equals( "" )) // check further
			{
				if ( cmd.equals( "adb" )) // special case
				{
					String name = request.getParameter( "name" );
					String pwrd = request.getParameter( "pwrd" );
					String mail = request.getParameter( "mail" );
					if (( name.length() > 2 )&&(pwrd.length() > 4 )) // no login form?
					{
						response.addCookie( new Cookie( "name", name ));
						response.addCookie( new Cookie( "pwrd", pwrd ));
						if ( mail.length() > 8 ) // i@my.com // m$ie6 adds a ; for null cookie so don't do it
							response.addCookie( new Cookie( "mail", mail ));
						msg = session.getId() + "|" + name + "|" + pwrd + "|" + session.getCreationTime() +
								"|" + ((( dt = session.getLastAccessedTime()) < 0 )?"never": new Date(dt).toString()) +
								"|" + ((amt==null)?"f":amt) + '|' + cmd + "|"  + mail + "|";
					}
					else
					{
						response.sendRedirect( "login.html" );
						return;
					}
				}
			}
			if ( msg.length() > 1 ) // we have something to say
			{
				String ss;
				if (amt != null && amt.equals("x"))
					ss = getServletContext().getInitParameter("bwanaPlain");
				else
					ss = getServletContext().getInitParameter("bwanaStyle");
				String address = getServletContext().getInitParameter("address");
				String port = getServletContext().getInitParameter("port");
				dc = new Socket( address, Integer.parseInt( port ));
				dcOut = new PrintWriter( dc.getOutputStream(), true ); // set up for two way talking
				out = response.getWriter();
				response.setContentType(CONTENT_TYPE);
				dcOut.println( msg );
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				//factory.setNamespaceAware(true);
				//factory.setValidating(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(dc.getInputStream());
				TransformerFactory tFactory = TransformerFactory.newInstance();
				StreamSource stylesource = new StreamSource(ss);
				Transformer transformer = tFactory.newTransformer(stylesource);
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(out);
				transformer.transform(source, result);
				out.close();
				dcOut.close();
				dc.close();
			}
		}
		catch (TransformerConfigurationException tce) // Error generated by the parser
		{
			System.out.println ("\n** TransformerFactory error");
			System.out.println("   " + tce.getMessage() );
			Throwable x = tce; // Use the contained exception, if any
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
		} 
		catch (TransformerException te) // Error generated by the parser
		{
			System.out.println ("\n** Transformation error");
			System.out.println("   " + te.getMessage() );
			Throwable x = te; // Use the contained exception, if any
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();
		} 
		catch (SAXException sxe) // Error generated by this application or parser-initialization error
		{
			Exception  x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} 
		catch (ParserConfigurationException pce) // Parser with specified options can't be built 
		{ 
			pce.printStackTrace();
		}
	}
}
