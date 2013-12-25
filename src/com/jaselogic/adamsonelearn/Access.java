package com.jaselogic.adamsonelearn;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Access extends Activity {
	private TextView tvName;
	private TextView tvStudNo;
	private TextView tvCourse;
	private TextView tvYear;
	private ImageView imgAvatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_access);
		
		Intent intent = getIntent();
		String avatarSrc = intent.getExtras().getString("avatarSrc");
		String name = intent.getExtras().getString("name");
		String studNo = intent.getExtras().getString("studNo");
		String course = intent.getExtras().getString("course");
		String year = intent.getExtras().getString("year");
		
		tvName = (TextView) findViewById(R.id.tv_name);
		tvStudNo = (TextView) findViewById(R.id.tv_studno);
		tvCourse = (TextView) findViewById(R.id.tv_course);
		tvYear = (TextView) findViewById(R.id.tv_yearlevel);
		
		tvName.setText(name);
		tvStudNo.setText(studNo);
		tvCourse.setText(course);
		tvYear.setText(year);
		
		imgAvatar = (ImageView) findViewById(R.id.img_avatar);
		UrlImageViewHelper.setUrlDrawable(imgAvatar, avatarSrc);
	}
}
