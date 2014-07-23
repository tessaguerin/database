package com.ecml;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class add_composer extends Activity implements OnClickListener {
	EditText et;
	Button add_bt, read_bt;
	composerDataSource dbcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_composer);
		et = (EditText) findViewById(R.id.member_et_id);
		add_bt = (Button) findViewById(R.id.add_btn);

		dbcon = new composerDataSource(this);
		dbcon.open();
		add_bt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_btn:
			String name = et.getText().toString();
			dbcon.insertData(name);
			Intent main = new Intent(add_composer.this, choose_composer.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(main);
			break;

		default:
			break;
		}
	}

}

