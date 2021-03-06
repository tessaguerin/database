package com.ecml;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


     public class choose_composer extends Activity {
     private composerDataSource composerDatasource;
     private ArrayList<String> results = new ArrayList<String>();
   	private String tableComposer = MySQLiteHelper.TABLE_COMPOSER;
 	private SQLiteDatabase database;

	Button addmem_bt;
	Button modify_bt;
	ListView lv;
	composerDataSource dbcon;
	TextView memID_tv, memName_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listcomposer);
		dbcon = new composerDataSource(this);
		dbcon.open();
		addmem_bt = (Button) findViewById(R.id.add_button);
		modify_bt = (Button) findViewById (R.id.modify_btn);
		
		lv = (ListView) findViewById(R.id.listView2);
		
		// onClickListiner for addmember Button
		addmem_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent add_mem = new Intent(choose_composer.this, add_composer.class);
				startActivity(add_mem);
			}
			
		});

		


		// Attach The Data From DataBase Into ListView Using Crusor Adapter
		Cursor cursor = dbcon.readData();
		String[] from = new String[] { MySQLiteHelper.KEY_ID, MySQLiteHelper.KEY_NAME_COMPOSER };
		int[] to = new int[] { R.id.composer_id, R.id.composer_name };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				choose_composer.this, R.layout.view_members, cursor, from, to);

		adapter.notifyDataSetChanged();
		lv.setAdapter(adapter);

		// OnCLickListiner For List Items
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
	
					int position, long id) {
				memID_tv = (TextView) view.findViewById(R.id.composer_id);
				memName_tv = (TextView) view.findViewById(R.id.composer_name);

				String memberID_val = memID_tv.getText().toString();
				String memberName_val = memName_tv.getText().toString();

				Intent modify_intent = new Intent(getApplicationContext(),
						modify_composer.class);
			modify_intent.putExtra("memberName", memberName_val);
				modify_intent.putExtra("memberID", memberID_val);
				startActivity(modify_intent);
			}
		});

	} // create end

}// class end
