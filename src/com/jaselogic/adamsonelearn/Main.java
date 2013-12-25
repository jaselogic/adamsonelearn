package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get views
		btnLogin = (Button) findViewById(R.id.btn_login);
		txtStudNo = (EditText) findViewById(R.id.txt_studno);
		txtPassword = (EditText) findViewById(R.id.txt_password);
		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		
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
					btnLogin.setVisibility(View.INVISIBLE);
					txtStudNo.setVisibility(View.INVISIBLE);
					txtPassword.setVisibility(View.INVISIBLE);
					
					pb1.setVisibility(View.VISIBLE);	
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class DownloadDocumentTask extends AsyncTask<String, Void, Document> {

		@Override
		protected Document doInBackground(String... arg0) {
			// TODO Auto-generated method stub
	        Document doc = null;
	        Response res = null;
	        try {                                              
	            res = Jsoup.connect("http://learn.adamson.edu.ph/V4/")
	            		.data("TXTusername", "201013888", "TXTpassword", "288785", "BTNlogin", "Login")
	            		.method(Method.POST)
	            		.execute();
	            Map<String, String> loginCookies = res.cookies();
	            
	            doc = Jsoup.connect("http://learn.adamson.edu.ph/V4/?page=welcome")
	            		.cookies(loginCookies)
	            		.get();
	        } catch (IOException e) {                          
	            e.printStackTrace();                           
	        }
	        
	        return doc;
		}
		
		protected void onPostExecute(Document result) {

		}
		
	}
	
}
