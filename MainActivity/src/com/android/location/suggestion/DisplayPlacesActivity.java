package com.android.location.suggestion;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.location.suggestion.R;
//newly added
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class DisplayPlacesActivity extends Activity {
		private int userIcon;

		private GoogleMap theMap;

		//private LocationManager locMan;

		private Marker userMarker;
		public static String radiusMain = null;
		private Marker[] placeMarkers;
		private final int MAX_PLACES = 20;//most returned from google
		//marker options
		private MarkerOptions[] places;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Intent intent = getIntent();
		Location loc = (Location)intent.getExtras().getParcelable(MainActivity.EXTRA_MESSAGE);
	    String radius = intent.getStringExtra(MainActivity.RADIUS);
	    String activityChosen = intent.getStringExtra(MainActivity.ACTIVITY);
	    radiusMain = radius;
		userIcon = R.drawable.user;
		
		if (theMap == null) {
			theMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			if (theMap != null) {
				theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				placeMarkers = new Marker[MAX_PLACES];
			}

		}
		displayPlaces(loc,radius,activityChosen);
		
	}
	private void displayPlaces(Location loc, String radius, String activityChosen){
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		LatLng lastLatLng = new LatLng(lat, lng);
		theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
		//theMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		if(userMarker!=null) userMarker.remove();
		userMarker = theMap.addMarker(new MarkerOptions()
		.position(lastLatLng)
		.anchor(0.0f, 1.0f)
		.title("You are currently here")
		.icon(BitmapDescriptorFactory.fromResource(userIcon))
		.snippet("Your most recent location"));
		//theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
		CameraPosition cameraPosition = CameraPosition.builder()
                .target(lastLatLng)
                .zoom(12)
                .bearing(90)
                .build();
        
        // Animate the change in camera view over 2 seconds
		theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
		
		
		String latitude = String.valueOf(lat);
		String longitude = String.valueOf(lng);
		String url;
		StringBuilder placesBuilder = new StringBuilder();
		// https://maps.googleapis.com/maps/api/place/nearbysearch/json?
		//location=45.5126003,-122.6855891&radius=482803.2&sensor=false&
		//types=park|campground|point_of_interest&rankby=prominence&keyword=trailhead&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
	     //RV PARK https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=park%7Crv_park%7Cpoint_of_interest&name="rv+park"&rankby=prominence&keyword="rv+park"&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
		
		try {
			if(activityChosen.equals("Hiking")){
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode(
								"park|campground|point_of_interest", "UTF-8")
						+ "&rankby=prominence"
						+ "&keyword=trailhead"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for Hiking-->",url);
				new GetPlaces().execute(url);
				
			}else if(activityChosen.equals("RV Park")){
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode(
								"park|rv_park|point_of_interest", "UTF-8")
						+ "&rankby=prominence"
						+ "&keyword=\"rv+park\""
						+ "&name="
						+ "\"rv+park\""
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for RV Park-->",url);
				new GetPlaces().execute(url);
				// CAMPING https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=campground&name="campground"&rankby=prominence&keyword="campground"&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk

				
			}else if(activityChosen.equals("Camping")){
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode(
								"campground", "UTF-8")
						+ "&rankby=prominence"
						+ "&name=\"campground\""
						+ "&keyword=\"campground\""
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for Camping-->",url);
				new GetPlaces().execute(url);
				//FISHING https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=park|campground|natural_feature
				//&name=lake&rankby=prominence&keyword=fishing&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
			}else if(activityChosen.equals("Fishing")){
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode(
								"park|campground|natural_feature", "UTF-8")
						+ "&rankby=prominence"
						+ "&name=lake"
						+ "&keyword=fishing"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for Fishing-->",url);
				new GetPlaces().execute(url);
				//BOWLING  https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&
				//types=bowling_alley&rankby=prominence&keyword=bowling&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
			}else if(activityChosen.equals("Bowling")){
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode(
								"bowling_alley", "UTF-8")
						+ "&rankby=prominence"
						+ "&keyword=bowling"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for bowling-->",url);
				new GetPlaces().execute(url);
			}
				
			
			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private class GetPlaces extends AsyncTask<String, Void, String> {
		int placesIcon = R.drawable.small_flag;
		@Override
		protected String doInBackground(String... placesURL) {
			//fetch places
			
			//build result as string
			StringBuilder placesBuilder = new StringBuilder();
			//process search parameter string(s)
			for (String placeSearchURL : placesURL) {
				HttpClient placesClient = new DefaultHttpClient();
				try {
					//try to fetch the data
					
					//HTTP Get receives URL string
					HttpGet placesGet = new HttpGet(placeSearchURL);
					//execute GET with Client - return response
					HttpResponse placesResponse = placesClient.execute(placesGet);
					//check response status
					StatusLine placeSearchStatus = placesResponse.getStatusLine();
					//only carry on if response is OK
					if (placeSearchStatus.getStatusCode() == 200) {
						//get response entity
						HttpEntity placesEntity = placesResponse.getEntity();
						//get input stream setup
						InputStream placesContent = placesEntity.getContent();
						//create reader
						InputStreamReader placesInput = new InputStreamReader(placesContent);
						//use buffered reader to process
						BufferedReader placesReader = new BufferedReader(placesInput);
						//read a line at a time, append to string builder
						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							placesBuilder.append(lineIn);
						}
					}
				}
				catch(Exception e){ 
					e.printStackTrace(); 
				}
			}
			return placesBuilder.toString();
		}

		//process data retrieved from doInBackground
		protected void onPostExecute(String result) {
			//parse place data returned from Google Places
			//remove existing markers
			if(placeMarkers!=null){
				for(int pm=0; pm<placeMarkers.length; pm++){
					if(placeMarkers[pm]!=null)
						placeMarkers[pm].remove();
				}
			}
			try {
				//parse JSON
				
				//create JSONObject, pass stinrg returned from doInBackground
				JSONObject resultObject = new JSONObject(result);
				//get "results" array
				JSONArray placesArray = resultObject.getJSONArray("results");
				//marker options for each place returned
				places = new MarkerOptions[placesArray.length()];
				//loop through places
				for (int p=0; p<placesArray.length(); p++) {
					//parse each place
					//if any values are missing we won't show the marker
					boolean missingValue=false;
					LatLng placeLL=null;
					String placeName="";
					String vicinity="";
					int currIcon = placesIcon;
					try{
						//attempt to retrieve place data values
						missingValue=false;
						//get place at this index
						JSONObject placeObject = placesArray.getJSONObject(p);
						//get location section
						JSONObject loc = placeObject.getJSONObject("geometry")
								.getJSONObject("location");
						//read lat lng
						placeLL = new LatLng(Double.valueOf(loc.getString("lat")), 
								Double.valueOf(loc.getString("lng")));	
						//get types
						JSONArray types = placeObject.getJSONArray("types");
						//loop through types
						/*for(int t=0; t<types.length(); t++){
							//what type is it
							String thisType=types.get(t).toString();
							//check for particular types - set icons
							if(thisType.contains("food")){
								currIcon = foodIcon;
								break;
							}
							else if(thisType.contains("bar")){
								currIcon = drinkIcon;
								break;
							}
							else if(thisType.contains("store")){
								currIcon = shopIcon;
								break;
							}
						}*/
						//vicinity
						vicinity = placeObject.getString("vicinity");
						//name
						placeName = placeObject.getString("name");
					}
					catch(JSONException jse){
						Log.v("PLACES", "missing value");
						missingValue=true;
						jse.printStackTrace();
					}
					//if values missing we don't display
					if(missingValue)	places[p]=null;
					else
						places[p]=new MarkerOptions()
					.position(placeLL)
					.title(placeName)
					.icon(BitmapDescriptorFactory.fromResource(currIcon))
					.snippet(vicinity);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if(places!=null && placeMarkers!=null){
				for(int p=0; p<places.length && p<placeMarkers.length; p++){
					//will be null if a value was missing
					if(places[p]!=null)
						placeMarkers[p]=theMap.addMarker(places[p]);
				}
			}
			
		}
	}
}
