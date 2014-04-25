import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.api.events.MessageEvent;
import org.parabot.environment.input.Mouse;
import org.rev317.api.events.listeners.MessageListener;
import org.rev317.api.methods.Calculations;
import org.rev317.api.methods.Camera;
import org.rev317.api.methods.Interfaces;
import org.rev317.api.methods.Inventory;
import org.rev317.api.methods.Npcs;
import org.rev317.api.methods.Players;
import org.rev317.api.methods.SceneObjects;
import org.rev317.api.methods.Skill;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.interactive.Npc;
import org.rev317.api.wrappers.scene.Area;
import org.rev317.api.wrappers.scene.SceneObject;
import org.rev317.api.wrappers.scene.Tile;

@ScriptManifest( author = "Brookpc", category = Category.THIEVING, description = "Steals and Sells items on UltimateScape 2", name = "USThiever", servers = { "UltimateScape" }, version = 3.0 )
public class USThiever extends Script implements Paintable, MessageListener
{

	private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
	public static Area TA = new Area( new Tile( 2676, 3324, 0 ), new Tile( 2648, 3324, 0 ), new Tile( 2645, 3289, 0 ), new Tile( 2672, 3289, 0 ) );
	public int npcID;
	public int stallID;
	public int startlvl;
	public int[] sellIDs = { 950, 1891, 1901, 2309, 958, 4658, 2007, 1641, 1639, 1643 ,1637};
	public int curlvl;
	public int lvlcount;
	public int cashMade;
	public int itemsStolen;
	private final Color color1 = new Color( 229, 255, 59 );
	private final Font font2 = new Font( "Arial", 0, 14 );
	private final Timer RUNTIME = new Timer();
	public static Image img1;


	@Override
	public boolean onExecute()
	{
		img1 = getImage( "http://i.imgur.com/dQB0c5Y.png" );
		startlvl = Skill.THIEVING.getLevel();
		curlvl = Skill.THIEVING.getLevel();
		if( curlvl < 20 ) {
			stallID = 1616;
		} // Bread
		if( curlvl >= 20 && curlvl < 35 ) {
			stallID = 1615;
		} // Silk
		if( curlvl >= 35 && curlvl < 50 ) {
			stallID = 1619;
		} // Fur
		if( curlvl >= 50 && curlvl < 65 ) {
			stallID = 1614;
		} // Silver
		if( curlvl >= 65 && curlvl < 75 ) {
			stallID = 1618;
		} // Spice
		if( curlvl >= 75 ) {
			stallID = 1617;
		} // Gems

		Camera.setRotation(45);
		strategies.add( new tele() );
		strategies.add( new steal() );
		strategies.add( new trade() );
		provide( strategies );
		return true;
	}


	public static Image getImage( String url )
	{
		try {
			return ImageIO.read( new URL( url ) );
		} catch( IOException e ) {
			return null;
		}
	}


	public void atlvlchange()
	{
		curlvl = Skill.THIEVING.getLevel();
		lvlcount = ( curlvl - startlvl );
		return;
	}


	@Override
	public void onFinish()
	{

	}
	public class tele implements Strategy 
	{

		@Override
		public boolean activate() 
		{
			int DISTANCE = (int) Calculations.distanceBetween(Players.getLocal().getLocation(), new Tile(2665, 3311));
			return DISTANCE > 50;
		}

		@Override
		public void execute() 
		{
			Point SPELL_BOOK = new Point(743, 186);
			Point SKILL_ZONE = new Point(641, 288);
			Point THIEF = new Point(260, 400);
			Point INV = new Point(659,187);
			boolean NTF = true;
			if (NTF == true){
				Mouse.getInstance().click(SPELL_BOOK);
				Time.sleep(300);
				Mouse.getInstance().click(SKILL_ZONE);
				Time.sleep(300);
				Mouse.getInstance().click(THIEF);
				Time.sleep(5000);
				Mouse.getInstance().click(INV);
				Time.sleep(300);
				NTF = false;
			}

		}

	}
	public class steal implements Strategy
	{

		@Override
		public boolean activate()
		{
			return ! Inventory.isFull()
					&& TA.contains( Players.getLocal().getLocation() );
		}


		@Override
		public void execute()
		{
			atlvlchange();
			for( SceneObject i: SceneObjects.getNearest( stallID ) ) {
				;
				if(i != null &&  i.isOnScreen() ) {
					i.interact( "Steal-from" );
					Time.sleep( 200 );
				} else {
					i.getLocation().clickMM();
					Time.sleep( 200 );
				}
			}
			curlvl = Skill.THIEVING.getLevel();
			if( curlvl < 20 ) {
				stallID = 1616;
			} // Bread
			if( curlvl >= 20 && curlvl < 35 ) {
				stallID = 1615;
			} // Silk
			if( curlvl >= 35 && curlvl < 50 ) {
				stallID = 1619;
			} // Fur
			if( curlvl >= 50 && curlvl < 65 ) {
				stallID = 1614;
			} // Silver
			if( curlvl >= 65 && curlvl < 75 ) {
				stallID = 1618;
			} // Spice
			if( curlvl >= 75 ) {
				stallID = 1617;
			} // Gems
		}
	}

	public class trade implements Strategy{

		@Override
		public boolean activate() {
			return Inventory.isFull()
					&& TA.contains(Players.getLocal().getLocation());
		}

		@Override
		public void execute() {
			for (Npc m : Npcs.getNearest(2270)) {;
			if( m != null && !m.isOnScreen()){
				Tile NLoc = m.getLocation();
				NLoc.clickMM();
				Time.sleep(500);
			}
			if (m != null && Interfaces.getOpenInterfaceId() != 3824) {
				try {
					m.interact("Trade");
				}catch (Exception e){

				}

				Time.sleep(200);
			} else if (Interfaces.getOpenInterfaceId() == 3824) {
				for (Item i : Inventory.getItems(sellIDs)) {
					if(sellIDs != null){
						try {
							i.interact("Sell 50");
						} catch(Exception e) {
						}
					}
				}
				Time.sleep(200);
			} else if (m == null) {
				Time.sleep(200);
			}
			}
		}


	}


	@Override
	public void messageReceived( MessageEvent me )
	{
		if (me.getMessage().contains("cake(s)")) {
			itemsStolen += 1;
			cashMade += 7500;
		} else if (me.getMessage().contains("bread(s)")) {
			itemsStolen += 1;
			cashMade += 3500;
		} else if (me.getMessage().contains("steal chocolate slice(s)")) {
			itemsStolen += 1;
			cashMade += 1750;
		} else if (me.getMessage().contains("steal silk(s)")) {
			itemsStolen += 1;
			cashMade += 729;
		} else if (me.getMessage().contains("steal grey wolf fur(s)")) {
			itemsStolen += 1;
			cashMade += 13100;
		} else if (me.getMessage().contains("steal silver pot(s)")) {
			itemsStolen += 1;
			cashMade += 16000;
		} else if (me.getMessage().contains("steal spice(s)")) {
			itemsStolen += 1;
			cashMade += 22500;
		} else if (me.getMessage().contains("steal diamond ring(s)")) {
			itemsStolen += 1;
			cashMade += 38000;
		} else if (me.getMessage().contains("steal ruby ring(s)")) {
			itemsStolen += 1;
			cashMade += 31000;
		} else if (me.getMessage().contains("steal sappire ring(s)")) {
			itemsStolen += 1;
			cashMade += 25000;
		} else if (me.getMessage().contains("steal emerald ring(s).")) {
			itemsStolen += 1;
			cashMade += 27500;
		}
	}


	@Override
	public void paint( Graphics arg0 )
	{
		Graphics2D g = (Graphics2D) arg0;
		g.drawImage(img1, 4, 23, null);
		g.setFont(font2);
		g.setColor(color1);
		g.drawString( "" + lvlcount, 82, 57);
		g.drawString( "" + RUNTIME, 82, 83);
		g.drawString( "" + cashMade, 82, 70);

	}

}
