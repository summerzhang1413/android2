package com.ifox.fragment;

import com.ifox.main.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetailActivity extends FragmentActivity {
	
	private TextView title,content,time ;
	
	@Override
	protected void onCreate(Bundle saveInatanceState) {
		super.onCreate(saveInatanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		super.setContentView(R.layout.news_detail_activity) ;
		
		Intent intent = getIntent() ;
		this.title =(TextView) findViewById(R.id.news_detail_title);
		this.content = (TextView) findViewById(R.id.news_detail_content);
		this.time = (TextView) findViewById(R.id.news_detail_time) ;
		
		this.title.setText(intent.getStringExtra("title"));
		this.time.setText(intent.getStringExtra("date")) ;
		this.content.setText(intent.getStringExtra("info"));
		
	}

	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}

}
