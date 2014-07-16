package com.ecml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class choose_composer extends Activity {
	private titleDataSource datasource;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.listcomposer);
	    
	    addButtonCLickListener();
	
	    // TODO Auto-generated method stub
	}
	
	public void addButtonCLickListener() {
		
		Button button_bach = (Button)findViewById(R.id.bach);
		button_bach.setOnClickListener(new View.OnClickListener(){
			public void OnClick(View arg)
			{
				Intent goToAllSongs = new Intent(getApplicationContext(),
						AllSongsActivity.class);
				startActivity(goToAllSongs);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bach:
					Intent goToAllSongs = new Intent(getApplicationContext(),
							AllSongsActivity.class);
					startActivity(goToAllSongs);
					break;
				case R.id.beethoven:
					Intent goToAllSong = new Intent(getApplicationContext(),
							AllSongsActivity.class);
					startActivity(goToAllSong);
					break;
				case R.id.Chopin:
					Intent goToAllSongss = new Intent(getApplicationContext(),
							AllSongsActivity.class);
					startActivity(goToAllSongss);
					break;
				case R.id.borodin:
					Intent goToAllSon = new Intent(getApplicationContext(),
							AllSongsActivity.class);
					startActivity(goToAllSon);
					break;
				}		
			}
		});
		}
	// Will be called via the onClick attribute
	// of the buttons in listcomposer.xml
	
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.bach:
			Intent goToAllSongs = new Intent(getApplicationContext(),
					AllSongsActivity.class);
			startActivity(goToAllSongs);
			break;
		case R.id.beethoven:
			Intent goToAllSong = new Intent(getApplicationContext(),
					AllSongsActivity.class);
			startActivity(goToAllSong);
			break;
		case R.id.Chopin:
			Intent goToAllSongss = new Intent(getApplicationContext(),
					AllSongsActivity.class);
			startActivity(goToAllSongss);
			break;
		case R.id.borodin:
			Intent goToAllSon = new Intent(getApplicationContext(),
					AllSongsActivity.class);
			startActivity(goToAllSon);
			break;
			
		}
	}

	}
