/*******************************************************************************
 * LocGenie – An open source Android application that suggests users places of their preferred activity within their 
 * preferred distance in Map View along with their address.
 *
 * Copyright (C) 2014 Srividya Sundaram
 *
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. 
 * If not, see http://www.gnu.org/licenses/.
 *
 * Following is the link for the repository: https://github.com/Srividya2212/LocationFinder
 *
 * Please, see the file license in this distribution for license terms. Link is
 * https://github.com/Srividya2212/LocationFinder/blob/master/LICENSE.md
 *
 * References:
 * https://developers.google.com/maps/documentation/android/start#getting_the_google_maps_android_api_v2
 * https://developers.google.com/maps/documentation/android/
 * https://developers.google.com/places/documentation/
 * https://developers.google.com/places/documentation/search
 * http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android
 *
 * Author - Srividya Sundaram
 * email: srividya@pdx.edu
 *
 *  ******************************************************************************************/
package com.gmail.srivi.sundaram.locgenie;

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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.gmail.srivi.sundaram.locgenie.R;
import com.gmail.srivi.sundaram.locgenie.TouchableWrapper.UpdateMapAfterUserInterection;
//newly added
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;

public class DisplayPlacesActivity extends FragmentActivity implements UpdateMapAfterUserInterection {
	public static boolean mMapIsTouched = false;
	private int userIcon;

	private GoogleMap theMap;

	// private LocationManager locMan;

	private Marker userMarker;
	public static String radiusMain = null;
	public static String activity = null;
	public static String latitude = null;
	public static String longitude = null;
	private Marker[] placeMarkers;
	private final int MAX_PLACES = 20;// most returned from google
	// marker options
	private MarkerOptions[] places;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Intent intent = getIntent();
		Location loc = (Location) intent.getExtras().getParcelable(
				MainActivity.EXTRA_MESSAGE);
		String radius = intent.getStringExtra(MainActivity.RADIUS);
		String activityChosen = intent.getStringExtra(MainActivity.ACTIVITY);
		radiusMain = radius;
		activity = activityChosen;
		userIcon = R.drawable.user;

		if (theMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
			theMap=mapFrag.getMap();
			/*theMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();*/
			if (theMap != null) {
				theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				placeMarkers = new Marker[MAX_PLACES];
			}

		}
		
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		LatLng lastLatLng = new LatLng(lat, lng);
		theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 8));
		if (userMarker != null)
			userMarker.remove();
		userMarker = theMap.addMarker(new MarkerOptions().position(lastLatLng)
				.anchor(0.0f, 1.0f).title("You are currently here")
				.icon(BitmapDescriptorFactory.fromResource(userIcon))
				.snippet("Your most recent location"));
	
		displayPlaces(loc, radius, activityChosen);
		
		

	}

	private void displayPlaces(Location loc, String radius,
			String activityChosen) {
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		
		//CameraPosition cameraPosition = CameraPosition.builder()
				//.target(lastLatLng).zoom(12).bearing(90).build();
		/*//theMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPosition), 2000,
				null);*/
		 latitude = String.valueOf(lat);
		 longitude = String.valueOf(lng);
		String url;
		StringBuilder placesBuilder = new StringBuilder();
		
		
		// https://maps.googleapis.com/maps/api/place/nearbysearch/json?
		// location=45.5126003,-122.6855891&radius=482803.2&sensor=false&
		// types=park|campground|point_of_interest&rankby=prominence&keyword=trailhead&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
		// RV PARK
		// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=park%7Crv_park%7Cpoint_of_interest&name="rv+park"&rankby=prominence&keyword="rv+park"&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk

		try {
			if (activityChosen.equals("Hiking")) {
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
				Log.d("URL for Hiking-->", url);
				new GetPlaces().execute(url);

			} else if (activityChosen.equals("RV Park")) {
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode("park|rv_park|point_of_interest",
								"UTF-8")
						+ "&rankby=prominence"
						+ "&keyword=rv+park"
						+ "&name="
						+ "rv+park"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for RV Park-->", url);
				new GetPlaces().execute(url);
				// CAMPING
				// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=campground&name="campground"&rankby=prominence&keyword="campground"&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk

			} else if (activityChosen.equals("Camping")) {
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode("campground", "UTF-8")
						+ "&rankby=prominence"
						+ "&name=campground"
						+ "&keyword=campground"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for Camping-->", url);
				new GetPlaces().execute(url);
				// FISHING
				// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&types=park|campground|natural_feature
				// &name=lake&rankby=prominence&keyword=fishing&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
			} else if (activityChosen.equals("Fishing")) {
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode("park|campground|natural_feature",
								"UTF-8")
						+ "&rankby=prominence"
						+ "&name=lake"
						+ "&keyword=fishing"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for Fishing-->", url);
				new GetPlaces().execute(url);
				// BOWLING
				// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.5126003,-122.6855891&radius=482803.2&sensor=false&
				// types=bowling_alley&rankby=prominence&keyword=bowling&key=AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk
			} else if (activityChosen.equals("Bowling")) {
				url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
						+ URLEncoder.encode(latitude, "UTF-8")
						+ ","
						+ URLEncoder.encode(longitude, "UTF-8")
						+ "&radius="
						+ URLEncoder.encode(radius, "UTF-8")
						+ "&sensor="
						+ URLEncoder.encode("false", "UTF-8")
						+ "&types="
						+ URLEncoder.encode("bowling_alley", "UTF-8")
						+ "&rankby=prominence"
						+ "&keyword=bowling"
						+ "&key="
						+ URLEncoder.encode(
								"AIzaSyB7_xx6j2QJPaA_v2XjRTUV7yTYhXfkQgk",
								"UTF-8");
				Log.d("URL for bowling-->", url);
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
			StringBuilder placesBuilder = new StringBuilder();
			for (String placeSearchURL : placesURL) {
				HttpClient placesClient = new DefaultHttpClient();
				try {
					HttpGet placesGet = new HttpGet(placeSearchURL);
					HttpResponse placesResponse = placesClient
							.execute(placesGet);
					StatusLine placeSearchStatus = placesResponse
							.getStatusLine();
					if (placeSearchStatus.getStatusCode() == 200) {
						HttpEntity placesEntity = placesResponse.getEntity();
						InputStream placesContent = placesEntity.getContent();
						InputStreamReader placesInput = new InputStreamReader(
								placesContent);
						BufferedReader placesReader = new BufferedReader(
								placesInput);
						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							placesBuilder.append(lineIn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return placesBuilder.toString();
		}

		protected void onPostExecute(String result) {
			if (placeMarkers != null) {
				for (int pm = 0; pm < placeMarkers.length; pm++) {
					if (placeMarkers[pm] != null)
						placeMarkers[pm].remove();
				}
			}
			try {
				JSONObject resultObject = new JSONObject(result);
				JSONArray placesArray = resultObject.getJSONArray("results");
				places = new MarkerOptions[placesArray.length()];
				for (int p = 0; p < placesArray.length(); p++) {
					boolean missingValue = false;
					LatLng placeLL = null;
					String placeName = "";
					String vicinity = "";
					int currIcon = placesIcon;
					try {
						missingValue = false;
						JSONObject placeObject = placesArray.getJSONObject(p);
						JSONObject loc = placeObject.getJSONObject("geometry")
								.getJSONObject("location");
						placeLL = new LatLng(Double.valueOf(loc
								.getString("lat")), Double.valueOf(loc
								.getString("lng")));
						JSONArray types = placeObject.getJSONArray("types");

						vicinity = placeObject.getString("vicinity");
						placeName = placeObject.getString("name");
					} catch (JSONException jse) {
						Log.v("PLACES", "missing value");
						missingValue = true;
						jse.printStackTrace();
					}
					if (missingValue)
						places[p] = null;
					else
						places[p] = new MarkerOptions()
								.position(placeLL)
								.title(placeName)
								.icon(BitmapDescriptorFactory
										.fromResource(currIcon))
								.snippet(vicinity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (places != null && placeMarkers != null) {
				if(places.length == 0){
					Toast.makeText(getBaseContext(), "No places found within this area!",
							Toast.LENGTH_SHORT).show();
					
				}else{
				for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
					if (places[p] != null)
						placeMarkers[p] = theMap.addMarker(places[p]);
				}
			}
			}

		}
	}






	@Override
	public void onUpdateMapAfterUserInterection() {
		// TODO Auto-generated method stub
		theMap.setOnCameraChangeListener(new OnCameraChangeListener() {
		    @Override
		    public void onCameraChange(CameraPosition cameraPosition) {
		    	LatLng centerPosition = theMap.getCameraPosition().target;
		    	//latitude = Double.toString(centerPosition.latitude) ;
		    	//longitude = Double.toString(centerPosition.longitude);
		    	
		    	VisibleRegion vr = theMap.getProjection().getVisibleRegion();
		    	double left = vr.latLngBounds.southwest.longitude;
		    	double bottom = vr.latLngBounds.southwest.latitude;
		    	double top = vr.latLngBounds.northeast.latitude;
		    	double right = vr.latLngBounds.northeast.longitude;
		    	Location bottomLeft = new Location("try");
		    	bottomLeft.setLatitude(bottom);
		    	bottomLeft.setLongitude(left);
		    	Location center=new Location("center");
		    	center.setLatitude( vr.latLngBounds.getCenter().latitude);
		    	center.setLongitude( vr.latLngBounds.getCenter().longitude);
		    	float dis = center.distanceTo(bottomLeft);
		    	String newRadius = Double.toString(dis);
		    	
		    	
		    	//LatLngBounds curScreen = theMap.getProjection().getVisibleRegion().latLngBounds;
		    	displayPlaces(center, newRadius, activity);
		    	}
		    });
		
	}
}
