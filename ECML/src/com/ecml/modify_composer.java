package com.ecml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;

public class modify_composer extends Activity implements OnClickListener {

	EditText et;
	Button edit_bt, delete_bt;

	long composer_id;

	composerDataSource dbcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_composer);

		dbcon = new composerDataSource(this);
		dbcon.open();

		et = (EditText) findViewById(R.id.edit_mem_id);
		edit_bt = (Button) findViewById(R.id.update_btn);
		delete_bt = (Button) findViewById(R.id.delete_btn);

		Intent i = getIntent();
		String composerID = i.getStringExtra("composerID");
		String memberName = i.getStringExtra("memberName");

		composer_id = Long.parseLong(composerID);

		et.setText(memberName);

		edit_bt.setOnClickListener(this);
		delete_bt.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.update_btn:
			String memName_upd = et.getText().toString();
			dbcon.updateData(composer_id, memName_upd);
			this.returnHome();
			break;
			
		case R.id.delete_btn:
			dbcon.deleteComposer(composer_id);
			this.returnHome();
			break;

		}
	}

	public void returnHome() {

		Intent home_intent = new Intent(getApplicationContext(),
				choose_composer.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(home_intent);
	}

}
