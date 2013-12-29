package com.jaselogic.adamsonelearn;

import java.io.IOException;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
					btnLogin.setVisibility(View.INVISIBLE);
					txtStudNo.setVisibility(View.INVISIBLE);
					txtPassword.setVisibility(View.INVISIBLE);
					
					pb1.setVisibility(View.VISIBLE);
					
					new DocumentManager.DownloadDocumentTask(Main.this, DocumentManager.PAGE_BALINQ, null).execute(studNo, password);
					//temporary
					//startActivity(new Intent(Main.this, Dashboard.class));
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

	@Override
	public void onResourceReceived(DocumentCookie res) throws IOException {
		Intent intent = new Intent(Main.this, Dashboard.class);
		//changed to dashboard class
		String avatarSrc = res.document.select("img.avatar").get(0).attr("src");
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
	}	
}
