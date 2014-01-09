package com.jaselogic.adamsonelearn;

import java.io.IOException;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main extends Activity implements DocumentManager.ResponseReceiver {

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
					setViewVisibility(View.INVISIBLE);
					
					//If internet connection is present
					if(isNetworkConnected()) {
						new DocumentManager.DownloadDocumentTask(Main.this, DocumentManager.PAGE_BALINQ, null).execute(studNo, password);
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

	@Override
	public void onResourceReceived(DocumentCookie res) throws IOException {
		//if document has been successfully retrieved
		if(res != null) {
			//Check if img.avatar exists
			Elements avatar = res.document.select("img.avatar");
			if(avatar.size() > 0) { //kung nakalogin successfully.
				Intent intent = new Intent(Main.this, Dashboard.class);
				//changed to dashboard class
				String avatarSrc = avatar.get(0).attr("src");
				Elements studinfo = res.document.select("div.studinfo");
				avatarSrc = "http://learn.adamson.edu.ph/" + avatarSrc.substring(3,
						(avatarSrc.indexOf('#') > 0 ? avatarSrc.indexOf('#') : avatarSrc.length()));
				
				intent.putExtra("PHPSESSID", res.cookie);
				intent.putExtra("avatarSrc", avatarSrc);
				intent.putExtra("name", studinfo.get(0).text());
				intent.putExtra("studNo", studinfo.get(1).text());
				intent.putExtra("course", studinfo.get(2).text());
				intent.putExtra("year", studinfo.get(3).text());
				startActivity(intent);
				setViewVisibility(View.VISIBLE);
				txtPassword.setText("");
			} else { //kung hindi nakalogin, mali user pass.
				new AlertDialogBuilder.NeutralDialog("Mali password", 
						"Invalid username/password", Main.this);
				setViewVisibility(View.VISIBLE);				
			}
		} else { //if no document has been retrieved, possibly from faulty connection
			new AlertDialogBuilder.NeutralDialog("Sira net", 
					"May problema net connection mo. Ayusin mo.", Main.this);
			setViewVisibility(View.VISIBLE);
		}
	}	
}
