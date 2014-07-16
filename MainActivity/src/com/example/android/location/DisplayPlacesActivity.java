package com.example.android.location;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayPlacesActivity extends Activity {
	private TextView place1;
	private TextView place2;
	private TextView place3;
	private TextView place4;
	private TextView place5;
	private TextView place6;
	private TextView place7;
	private TextView place8;
	private TextView place9;
	private TextView place10;
	private TextView place11;
	private TextView place12;
	private TextView place13;
	private TextView place14;
	private TextView place15;
	private TextView place16;
	private TextView place17;
	private TextView place18;
	private TextView place19;
	private TextView place20;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_display_places);
		 // Get the message from the intent
		//checking commit again
	    Intent intent = getIntent();
	    String[] message = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);
	    place1 = (TextView) findViewById(R.id.place1);
	    place2 = (TextView) findViewById(R.id.place2);
	    place3 = (TextView) findViewById(R.id.place3);
	    place4 = (TextView) findViewById(R.id.place4);
	    place5 = (TextView) findViewById(R.id.place5);
	  //  for(int i=0;i<10;i++){
	    	
	 	place1.setText(message[0]);
	 	place2.setText(message[1]);
	 	place3.setText(message[2]);
	 	place4.setText(message[3]);
	 	place5.setText(message[4]);
	 	   
	    //}
	    // Create the text view
	   
	   // setContentView(textView);
	    // Set the text view as the activity layout
	    
		//setContentView(R.layout.activity_display_places);
	}
}
