package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;
	//Large marker value
	public static final float LARGE_MARKER = 24;
	//Medium marker value
	public static final float MEDIUM_MARKER = 12;
	//Small marker value
	public static final float SMALL_MARKER = 6;
	//Set the colors for the markers
	public final int YELLOW = color(255, 255, 0);
	public final int BLUE = color(0, 0, 255);
	public final int RED = color(255, 0, 0);
	
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    //int yellow = color(255, 255, 0);
	    
	
	    for (PointFeature quake : earthquakes){
	    	markers.add(createMarker(quake));
	    }
	    map.addMarkers(markers);
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	
	private SimplePointMarker createMarker(PointFeature feature)
	{
		
		//create the marker 
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		//Get the magnitude field and parse it to a float via a string
		float mag = Float.parseFloat(feature.getProperty("magnitude").toString());
		
		if (mag > THRESHOLD_MODERATE){
			marker.setRadius(LARGE_MARKER);
			marker.setColor(RED);
		}else if(mag > THRESHOLD_LIGHT){
			marker.setRadius(MEDIUM_MARKER);
			marker.setColor(YELLOW);
		}else{
			marker.setRadius(SMALL_MARKER);
			marker.setColor(BLUE);
		}
		
		return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	
	private void addKey() 
	{	
		fill(255, 255, 255);
		rect(25, 50, 150, 300);
		fill(0);
		textSize(16);
		text("Earthquake Key", 35, 75);
		fill(RED);
		ellipse(50, 125, LARGE_MARKER, LARGE_MARKER);
		fill(YELLOW);
		ellipse(50, 175, MEDIUM_MARKER, MEDIUM_MARKER);
		fill(BLUE);
		ellipse(50, 225, SMALL_MARKER, SMALL_MARKER);
		fill(0);
		textSize(12);
		text("5.0+ Magnitude", 65, 130);
		text("4.0+ Magnitude", 65, 180);
		text("<4.0 Magnitude", 65, 230);
	}
}
