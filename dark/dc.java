/*
 * Dark Continent Game Server Class
 * Copyright (c) Linus Sphinx 2003
 */
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

public class dc {
	static Random rn = new Random();
	static Config config = new Config();
	static critter beasts = new critter(config.beasts);
	static terrain terra = new terrain(config.terrain);
	static items stuff = new items(config.item);
	static map jungle = new map(config.jungle, config.ground);
	static hunter player = new hunter(config.bwanas);
	static birdsnbees fauna = new birdsnbees(config.birds);
	static action log = new action();
	static whack whacks = new whack(config.whacks);
	static boolean CEASEANDDESIST = false;
	static String secret = config.secret;
	static String scatters = config.scatter;

	static class ClientThread extends Thread {
		SocketChannel client;

		static Config config = new Config();

		ClientThread(SocketChannel client) {
			this.client = client;
		}

		public void run() 
		{
			try 
			{
				boolean pass = false;
				Charset charset = StandardCharsets.UTF_8;
				CharsetEncoder encoder = charset.newEncoder();
				CharsetDecoder decoder = charset.newDecoder();
				ByteBuffer buff = ByteBuffer.allocate(512);
				CharBuffer cbuff = CharBuffer.allocate(512);
				
				client.read(buff); 
				buff.flip();
				decoder.decode(buff, cbuff, false);
				cbuff.flip();
				String[] lines = cbuff.toString().split("\n");
				buff.clear();
				label:
				for(int g = 0; g < lines.length; g++ )
				{
					System.out.println( "line[" + g +"]:'" + lines[ g ] + "' " );
					if ( secret.equals( lines[ g ] ))
					{
						pass = true;
						continue; // get another line
					}
					if ( pass )
					{
						if ( lines[ g ].equals( "bail" ))
						{
							CEASEANDDESIST = true;
							System.exit( 0 );
							
							break;
						}
						if ( lines[ g ].equals( "quit" ))
						{
							CEASEANDDESIST = true;
							player.save( config.bwanas );
							jungle.save( config.ground );
							System.exit( 0 );
							break;
						}
						if ( lines[ g ].equals( "save" ))
						{
							player.save( config.bwanas );
							jungle.save( config.ground );
							break;
						}
						if ( lines[ g ].equals( "scat" ))
						{
							new scatter( scatters, rn, jungle );
							break;
						}
					}
					else // its the servlet talking
					{	
						StringTokenizer t = new StringTokenizer( lines[ g ], "|" );
						if ( t.countTokens() > 5 )
						{
							String id = t.nextToken();  // never read
							String name = t.nextToken();
							String pwrd = t.nextToken();
							String start = t.nextToken();
							String lasthit = t.nextToken();
							String amt = t.nextToken();
							String cmd = t.nextToken();
                                                	// System.err.println( " id: '" + id + "'" );
                                                	// System.err.println( " name: '" + name + "'" );
                                                	// System.err.println( " pwrd: '" + pwrd + "'" );
                                                	// System.err.println( "start: '" + start + "'" );
                                                	// System.err.println( " last: '" + lasthit + "'" );
                                                	// System.err.println( " amt: '" + amt + "'" );
                                                	// System.err.println( " cmd: '" + cmd + "'" );
							// possible to have cookies but not be in game if
							// bailed and not saved so just add them again
							if ( ! player.exists( name ) || cmd.equals( "adb" )) // special case
							{	
								String mail = "";
								if ( t.hasMoreTokens() )
									mail = t.nextToken();
								int newx = rn.nextInt( map.MAPWIDE );
								int newy = rn.nextInt( 20 ) + 40; // map high 60
								if ( name.compareToIgnoreCase( "null" ) != 0 )
								{
									log.addAction( name, "info", player.addBwana( start, lasthit, name, mail, pwrd, newx, newy ));
								}
							}
							hunter.bwana guy = player.get( name ); // should be there now
							if ( guy != null )
							{
								try
								{
									guy.last = DateFormat.getDateInstance().parse( lasthit );
								}
								catch( ParseException pe )
								{
									guy.last = new Date();
								}
							}
							if ( guy == null || ! guy.password.equalsIgnoreCase( pwrd ))
							{
								System.out.println( "Null Guy: " + name + " pword: " + pwrd );
								client.write( encoder.encode(CharBuffer.wrap("<darkContinent>")));
								client.write( encoder.encode(CharBuffer.wrap("<noaccess>Null Bwana</noaccess>")));
								client.write( encoder.encode(CharBuffer.wrap("</darkContinent>")));
								client.close();
								return;
							}
							switch (cmd) {
								case "mov":
									// a beast awakens
									if (guy.attack < 0 && (rn.nextInt(1000) % critter.BEASTODDS) == 0) {
										guy.beast = rn.nextInt(critter.BEASTCOUNT);
										if ((guy.beast == critter.CROCODILE && map.jungle[guy.x][guy.y][0] != 'd') || (map.jungle[guy.x][guy.y][0] == 'd' && guy.beast != critter.CROCODILE)) // nevermind
										{
											guy.attack = -1;
										} else {
											log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.APPEAR, rn.nextInt(critter.BULLCOUNT)));
											guy.attack = critter.APPEAR;
										}
									} else if (guy.attack >= critter.APPEAR) {
										switch ((rn.nextInt(1000) % 4)) // roll dice
										{
											case 0:
												log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.ESCAPE, rn.nextInt(critter.BULLCOUNT)));
												guy.attack = -1;
												break;
											case 1:
												log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.SPLIT, rn.nextInt(critter.BULLCOUNT)));
												guy.attack = -1;
												break;
											default:
												if (guy.attack > 3) {
													log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.KILLHUNTER, rn.nextInt(critter.BULLCOUNT)));
													guy.attack = -1;
													guy.kill();
												} else {
													log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.NOESCAPE, rn.nextInt(critter.BULLCOUNT)));
													guy.attack++;
												}
												break;
										}
									}
									if (guy.attack < 0) {
										String rection = t.nextToken();
										char scenery = mov(guy, lasthit, rection); // scenery is turf where you wanted to go

										if (scenery != map.jungle[guy.x][guy.y][0]) // but couldn't
										{
											log.addAction(guy.handle, "blocked", terra.longdesc(scenery));
											guy.smacks++; // hit the wall, twice, thrice

										} else {
											guy.smacks = 0;
											if (guy.upTree) // safely swing over
												log.addAction(guy.handle, "vine", stuff.get('V').use(2));
										}
										if (guy.smacks > 1) // gonna hafta hurtcha
										{
											switch (scenery) {
												case '0':        // suicide desc chars
													// stored in map
													log.addAction(guy.handle, "smack", terra.longdesc(jungle.suicide(rn)));
													guy.kill();
													break;
												case '1':        // too hungry to move
												default:
													log.addAction(guy.handle, "stumble", whacks.stumble(rn));
													break;
											}
										}
										if (guy.turf != map.jungle[guy.x][guy.y][0])
											log.addAction(guy.handle, "terra", terra.longdesc(map.jungle[guy.x][guy.y][0]));
										guy.turf = map.jungle[guy.x][guy.y][0];
									}
									if (log.noAction(guy.handle)) // if no action blurt birds and bees
										log.addAction(guy.handle, "fauna", fauna.describe(rn));
									break;
								case "put": {
									String toy = t.nextToken();
									log.addAction(guy.handle, "put", put(guy, toy.charAt(0)));
									break;
								}
								case "get": {
									String toy = t.nextToken();
									log.addAction(guy.handle, "get", get(guy, toy.charAt(0)));
									break;
								}
								case "use": {
									String toy = t.nextToken();
									if (guy.attack >= critter.APPEAR) {
										if ((rn.nextInt(1000) % 2) == 0) // miss
										{
											log.addAction(guy.handle, "kil", whacks.miss(rn, stuff.name(toy), guy.handle, "the " + beasts.name(guy.beast)));
											log.addAction(guy.handle, "kil", whacks.misser(rn, stuff.name(toy), "the " + beasts.name(guy.beast)));
										} else // hit
										{
											log.addAction(guy.handle, "kil", whacks.whacks(rn, stuff.name(toy), guy.handle, "the " + beasts.name(guy.beast)));
											log.addAction(guy.handle, "kil", whacks.whacker(rn, stuff.name(toy), guy.handle, "the " + beasts.name(guy.beast)));
										}
										switch ((rn.nextInt(1000) % 3)) // roll dice
										{
											case 0:
												log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.SPLIT, rn.nextInt(critter.BULLCOUNT)));
												guy.attack = -1;
												break;
											case 1:
												if (guy.attack > 3) {
													log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.KILLHUNTER, rn.nextInt(critter.BULLCOUNT)));
													guy.attack = -1;
												} else {
													log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.ATTACK, rn.nextInt(critter.BULLCOUNT)));
													guy.attack++;
												}
												break;
											case 2:
												log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.NOESCAPE, rn.nextInt(critter.BULLCOUNT)));
												guy.attack++;
												break;
											default:
												log.addAction(guy.handle, "beast", beasts.say(guy.beast, critter.KILLBEAST, rn.nextInt(critter.BULLCOUNT)));
												guy.attack = -1;
												break;
										}
									} else {
										log.addAction(guy.handle, "use", use(guy, toy.charAt(0)));

									}
									break;
								}
								case "set": {
									String toy = t.nextToken();
									guy.setScript(toy);
									break;
								}
								case "stl": {
									String vic = t.nextToken(); // victim

									if (!player.exists(vic))
										break label;
									char i = player.get(vic).filch();
									String booty;
									if (i == 0)
										booty = "pocket lint";
									else {
										booty = stuff.get(i).name(); // only tell vic if he succeeds

										log.addAction(vic, "stl", whacks.thief(rn, guy.handle, vic, booty));
									}
									log.addAction(guy.handle, "stl", whacks.theft(rn, guy.handle, vic, booty));
									if (i != 0) {
										if (!guy.grab(i)) {
											log.addAction(guy.handle, "stl", "Bag full, you fumble and " + vic + "'s " + stuff.name("" + i) + " falls to the ground.");
											if (!jungle.putItem(guy.x, guy.y, i)) {
												int toss = rn.nextInt(jungle.tooMany.size());
												log.addAction(guy.handle, "stl", jungle.tooMany.get(toss));
												if (toss == 4) // gorilla surprise!
													jungle.sweep(guy.x, guy.y);
											}
										}
									}
									break;
								}
								case "kil": {
									String toy = t.nextToken();
									String vic = t.nextToken();
									if (!player.exists(vic))
										break label;
									log.addAction(vic, "kil", whacks.attacked(rn, stuff.name(toy), guy.handle, vic));
									log.addAction(guy.handle, "kil", whacks.attacker(rn, stuff.name(toy), guy.handle, vic));
									if ((rn.nextInt(1000) % 2) == 0) // miss
									{
										log.addAction(vic, "kil", whacks.miss(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(guy.handle, "kil", whacks.miss(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(vic, "kil", whacks.missed(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(guy.handle, "kil", whacks.misser(rn, stuff.name(toy), vic));
									} else // hit
									{
										log.addAction(vic, "kil", whacks.whacks(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(guy.handle, "kil", whacks.whacks(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(guy.handle, "kil", whacks.whacker(rn, stuff.name(toy), guy.handle, vic));
										log.addAction(vic, "kil", whacks.whacked(rn, stuff.name(toy), guy.handle));
									}
									break;
								}
								case "dig": {
									String toy = t.nextToken();
									log.addAction(guy.handle, "dig", dig(guy, toy.charAt(0)));
									break;
								}
								case "bry": {
									String toy = t.nextToken();
									log.addAction(guy.handle, "bry", bry(guy, toy.charAt(0)));
									break;
								}
								case "clm":
									// String toy = t.nextToken(); it's always a
									// vine
									log.addAction(guy.handle, "clm", clm(guy, 'V'));
									break;
								case "snd":
									String to = t.nextToken();
									String msg = t.nextToken();
									log.addAction(to, "msg", whacks.messenger(rn, guy.handle, msg));
									break;
							}

							// now spit
							if ( amt.length() > 0 )
							{
								client.write( encoder.encode(CharBuffer.wrap("<darkContinent>")));
								if ( amt.indexOf( 'f' ) != -1 ) 
									client.write( encoder.encode(CharBuffer.wrap("<frame>hello</frame>")));
								if ( amt.indexOf( 'b' ) != -1 ) 
								{
									client.write( encoder.encode(CharBuffer.wrap("<bwana>" + guy.shortForm())));
									if ( guy.withBag() )
										client.write( encoder.encode(CharBuffer.wrap(stuff.toList( "sack", guy.bag, guy.x + ":" + guy.y ))));
									else
										client.write( encoder.encode(CharBuffer.wrap("<sack/>")));
									client.write(encoder.encode(CharBuffer.wrap("</bwana>")));
								}
								if ( amt.indexOf( 'm' ) != -1 ) // map
								{
									client.write(encoder.encode(CharBuffer.wrap("<map>")));
									client.write(encoder.encode(CharBuffer.wrap("<content>")));
									client.write(encoder.encode(CharBuffer.wrap(jungle.notContent( guy.x, guy.y ))));
									client.write(encoder.encode(CharBuffer.wrap("</content>")));
									client.write(encoder.encode(CharBuffer.wrap(terra.shortdesc(map.jungle[ guy.x ][ guy.y ][ 0 ]))));
									client.write(encoder.encode(CharBuffer.wrap("</map>")));
								}
								if ( amt.indexOf( 'g' ) != -1 ) // ground
								{
									client.write(encoder.encode(CharBuffer.wrap("<terra>"))); // compass only visible in bag
									client.write(encoder.encode(CharBuffer.wrap(stuff.toList( "ground", jungle.displayGround( guy.x, guy.y ).toCharArray(), "X:Y", jungle.tree( guy.x, guy.y, guy.inTree() )))));
									client.write(encoder.encode(CharBuffer.wrap(player.here( guy ))));
									client.write(encoder.encode(CharBuffer.wrap("</terra>")));
								}
								if ( amt.indexOf( 'v' ) != -1 ) // other players
								{
									client.write(encoder.encode(CharBuffer.wrap("<view>")));
									player.activeList( client, encoder);
									client.write(encoder.encode(CharBuffer.wrap(player.headCount())));
									client.write(encoder.encode(CharBuffer.wrap("</view>")));
								}
								if ( amt.indexOf( 'a' ) != -1 ) // action theater
								{
									client.write(encoder.encode(CharBuffer.wrap("<bull>")));
									log.getAction( guy.handle, client, encoder);
									client.write(encoder.encode(CharBuffer.wrap("</bull>")));
								}
								if ( amt.indexOf( 'x' ) != -1 )
								{
									client.write(encoder.encode(CharBuffer.wrap(guy.describe())));
									log.getAction( guy.handle, client, encoder);
									player.activeList( client, encoder);
									client.write(encoder.encode(CharBuffer.wrap(player.headCount())));
									client.write(encoder.encode(CharBuffer.wrap(stuff.toList( "ground", jungle.displayGround( guy.x, guy.y ).toCharArray(), "X:Y" ))));
									client.write(encoder.encode(CharBuffer.wrap(player.here( guy ))));
									client.write(encoder.encode(CharBuffer.wrap(jungle.displayMap( guy.x, guy.y ))));
									client.write(encoder.encode(CharBuffer.wrap(terra.shortdesc( map.jungle[ guy.x ][ guy.y ][ 0 ] ))));
								}
								client.write( encoder.encode(CharBuffer.wrap("</darkContinent>")));
							}
						}
						break;
					}
				}
				client.close();
			}
			catch ( IOException ioe ) 
			{ 
				System.err.println( ioe.getMessage() );
			}
		}

		public synchronized String use(hunter.bwana guy, char item) 
		{
			items.item thing = null;
			if (jungle.has(guy.x, guy.y, item)) // look on the ground first
			{
				thing = stuff.get(item);
				if (thing.morph != item) // it's morphin' time!
				{
					jungle.getItem(guy.x, guy.y, item); // delete
					jungle.putItem(guy.x, guy.y, thing.morph); // add
				}
			} else if (guy.has(item)) // then check our pockets
			{
				thing = stuff.get(item);
				if (thing.morph != item) // it's morphin' time!
				{
					guy.drop(item); // delete
					guy.grab(thing.morph); // add
				}
			}
			if (thing != null) {
				if (thing.evil) {
					guy.puke(thing.hunger, thing.thirst, thing.savy, config.highscore);
				} else {
					if ( thing.code == '3' ) // canteen
					{
						if (guy.turf == 'd' || guy.turf == 'w')
							guy.drinks = 10;
						if ( guy.drinks > 0 )
							guy.drinks--;
						else
							return "Your canteen is empty. Use it at a watering hole or river to refill.";
					}
					guy.dine(thing.hunger, thing.thirst, thing.savy, config.highscore);
				}
				return thing.use();
			} else
				return "You reach for your " + stuff.name("" + item)
						+ " but can't seem to find it.";
		}

		public synchronized String put(hunter.bwana guy, char item) {
			if (guy.drop(item)) {
				if (jungle.putItem(guy.x, guy.y, item)) {
					return "Your " + stuff.name("" + item) + " falls to the ground.";
				} else {
					int r = rn.nextInt(jungle.tooMany.size());
					return jungle.tooMany.get(r);
				}
			}
			return "You grope for but can't seem to find a " + stuff.name("" + item) + " in your bag.";
		}

		public synchronized String get(hunter.bwana guy, char item) {
			if (jungle.getItem(guy.x, guy.y, item)) {
				if (guy.grab(item)) {
					return "You stuff a " + stuff.name("" + item) + " into your bag.";
				} else {
					jungle.putItem(guy.x, guy.y, item); // put it back
					return "Your bag is too full, the " + stuff.name("" + item) + " just falls back onto the ground.";
				}
			}
			return "You grope around but can't find a " + stuff.name("" + item) + " here.";
		}

		public char mov(hunter.bwana guy, String last, String rect) {
			int x = guy.x, y = guy.y;
			for (int i = 0; i < rect.length(); i++) {
				if (rect.charAt(i) == 'n') {
					if (guy.upTree)
						y = guy.y - 4;
					else
						y = guy.y - 1;
				} else if (rect.charAt(i) == 's') {
					if (guy.upTree)
						y = guy.y + 4;
					else
						y = guy.y + 1;
				} else if (rect.charAt(i) == 'w') {
					if (guy.upTree)
						x = guy.x - 4;
					else
						x = guy.x - 1;
				} else if (rect.charAt(i) == 'e') {
					if (guy.upTree)
						x = guy.x + 4;
					else
						x = guy.x + 1;

				}
				if (x < 0 || x > map.MAPWIDE || y < 0 || y > map.MAPHIGH) // falling off the edge of our world
				{
					return '0';
				}
				if (jungle.legalMove(x, y)) // hit a barrier terrain
				{
					synchronized (player.bwanas) {
						guy.x = x;
						guy.y = y;
						guy.turns++;
						terrain.turf toll = terra.turfType(map.jungle[guy.x][guy.y][0]);
						guy.puke(toll.hunger, toll.thirst, toll.toll, config.highscore);
						if (guy.upTree) {
							if (!jungle.isTree(guy.x, guy.y)) {
								if ((rn.nextInt(1000) % 2) == 0) {
									log.addAction(guy.handle, "vine", stuff.get('V').use(3));
									guy.brokenleg(true);
								} else {
									log.addAction(guy.handle, "vine", stuff.get('V').use(4));
								}
								guy.upTree = false;
							}
						}
					}
				}
			}
			return (map.jungle[x][y][0]); // so we can show where they could've gone but can't
		}

		public synchronized String dig(hunter.bwana guy, char item) {
			return "dug";
		}

		public synchronized String bry(hunter.bwana guy, char item) {
			return "buried";
		}

		public synchronized String clm(hunter.bwana guy, char item) {
			guy.upTree = !guy.upTree;
			items.item thing = stuff.get(item);
			if (thing != null) {
				if (thing.evil) // nobody rides for free
				{
					guy.puke(thing.hunger, thing.thirst, thing.savy, config.highscore);
				} 
				else // may change my mind
				{
					guy.dine(thing.hunger, thing.thirst, thing.savy, config.highscore);
				}
				if (guy.upTree)
					return thing.use(0);
				else
					return thing.use(1);
			} else
				return "";
		}
	} // eo clientthread class

	public static void main(String[] args) {
		try {
			// map.dump();
			System.err.println("dc://" + config.address + ":" + config.port + "/");
			// ServerSocket ss = new ServerSocket( Integer.parseInt( config.port ), Integer.parseInt( config.backlog ), Inet4Address.getByName( config.address ));
			ServerSocketChannel ss = ServerSocketChannel.open();
			ss.configureBlocking(true);
			ss.socket().bind( new InetSocketAddress(config.address, Integer.parseInt(config.port)));
			for (;;) 
			{ // loop forever or until CEASEANDDESIST set
				SocketChannel client = ss.accept(); // Wait for a connection
				if (CEASEANDDESIST)
					break;
				while (!client.finishConnect()) 
				{
					System.out.println("Duh");
				}
				ClientThread t = new ClientThread(client); // A thread to handle it
				t.start(); // Start the thread running
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.exit(0);
	}
}
