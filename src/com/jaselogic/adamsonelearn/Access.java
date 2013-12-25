package com.jaselogic.adamsonelearn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Access extends Activity {
	private TextView tv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_access);
		
		tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setText(getIntent().getExtras().getString("outerHTML"));
	}
}
