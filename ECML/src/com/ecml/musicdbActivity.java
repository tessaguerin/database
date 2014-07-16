package com.ecml;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class musicdbActivity extends ListActivity {
	private titleDataSource datasource;
 	private MySQLiteHelper chooseComposer;
 	private choose_composer selectComposer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);

		datasource = new titleDataSource(this);
		chooseComposer = new MySQLiteHelper(this);
		datasource.open();
		
		// Création et insertion d'un titre:
//	    title title = new title("fkoekor");
//		datasource.createTitle("Bach");
//		datasource.createTitle("Beethoven");

		List<title> values = datasource.getAllTitle();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<title> adapter = new ArrayAdapter<title>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
	


	// Will be called via the onClick attribute
	// of the buttons in database.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<title> adapter = (ArrayAdapter<title>) getListAdapter();
		title title = null;
		switch (view.getId()) {
		case R.id.by_composer:
			Intent goToChooseComposer = new Intent(getApplicationContext(),
					choose_composer.class);
			startActivity(goToChooseComposer);
			
//			chooseComposer.addTitle(title);
//			String[] titles = new String[] { "Cool", "Very nice", "Hate it" };
//			int nextInt = new Random().nextInt(3);
			// save the new comment to the database
//			title = datasource.createTitle(titles[nextInt]);
//			adapter.add(title);
			break;

		case R.id.by_difficulty:
			if (getListAdapter().getCount() > 0) {
				title = (title) getListAdapter().getItem(0);
				datasource.deleteTitle(title);
				adapter.remove(title);
			}
			break;

		case R.id.all_songs:
			Intent goToAllSongs = new Intent(getApplicationContext(),
					AllSongsActivity.class);
			startActivity(goToAllSongs);
			break;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}