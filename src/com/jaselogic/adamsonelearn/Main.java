package com.jaselogic.adamsonelearn;

import java.io.IOException;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main extends Activity {

	private Button btnLogin;
	private EditText txtStudNo;
	private EditText txtPassword;
	private ProgressBar pb1;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String status = bundle.getString(LoginIntentService.EXTRA_STATUS);
			if(status.equals(LoginIntentService.STATUS_VALID)) { //Valid username/password
				Intent dash = new Intent(context.getApplicationContext(), Dashboard.class);
				dash.putExtra("PHPSESSID", bundle.getString(LoginIntentService.EXTRA_COOKIE));
				dash.putExtra("avatarSrc", bundle.getString(LoginIntentService.EXTRA_AVATAR));
				dash.putExtra("name", bundle.getString(LoginIntentService.EXTRA_NAME));
				dash.putExtra("studNo", bundle.getString(LoginIntentService.EXTRA_STUDNO));
				dash.putExtra("course", bundle.getString(LoginIntentService.EXTRA_COURSE));
				dash.putExtra("year", bundle.getString(LoginIntentService.EXTRA_YEAR));
				context.startActivity(dash);
				Main.this.finish();
			} else if (status.equals(LoginIntentService.STATUS_INVALID)) { //Invalid user/pass
				new AlertDialogBuilder.NeutralDialog("Mali password", 
						"Invalid username/password", Main.this);
				setViewVisibility(View.VISIBLE);		
			} else { //Document was not successfully downloaded.
				new AlertDialogBuilder.NeutralDialog("Sira net", 
						"May problema net connection mo. Ayusin mo.", Main.this);
				setViewVisibility(View.VISIBLE);				
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("PAUSE", "PUSA");
		unregisterReceiver(mBroadcastReceiver);
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mBroadcastReceiver, new IntentFilter(LoginIntentService.NOTIFICATION));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get views
		btnLogin = (Button) findViewById(R.id.btn_login);
		txtStudNo = (EditText) findViewById(R.id.txt_studno);
		txtPassword = (EditText) findViewById(R.id.txt_password);
		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		
		//temp
		txtStudNo.setText("201013888");
		txtPassword.setText("288785");
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String studNo = txtStudNo.getText().toString().trim();
				String password = txtPassword.getText().toString();
						
				//check if student number edittext is empty
				if(studNo.equals("")) {
					Toast.makeText(Main.this, "Please provide your Adamson University Student Number", Toast.LENGTH_SHORT).show();
				} else if(password.equals("")) { //else if password is empty
					Toast.makeText(Main.this, "Please provide your E-Learning password", Toast.LENGTH_SHORT).show();
				} else { //ready to send login details
					//set visibilities				
					setViewVisibility(View.INVISIBLE);
					
					//If internet connection is present
					if(isNetworkConnected()) {
						Intent intent = new Intent(Main.this, LoginIntentService.class);
						intent.putExtra(LoginIntentService.EXTRA_STUDNO, studNo);
						intent.putExtra(LoginIntentService.EXTRA_PASSWORD, password);
						startService(intent);
					} else {
						setViewVisibility(View.VISIBLE);
						new AlertDialogBuilder.NeutralDialog("Walang net", 
								"Wala kang net kumonek ka kaya muna.", Main.this);
					}
				}
			}
		});
	}
	
	//Set visibility states of views on connection
	private void setViewVisibility(int viewState) {
		btnLogin.setVisibility(viewState);
		txtStudNo.setVisibility(viewState);
		txtPassword.setVisibility(viewState);
		
		pb1.setVisibility(viewState ^ 4);
	}

	//Check internet state
	private boolean isNetworkConnected() {
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  return (cm.getActiveNetworkInfo() != null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
