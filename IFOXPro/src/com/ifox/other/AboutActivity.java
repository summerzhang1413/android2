package com.ifox.other;

import com.ifox.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.other_about);
	}
	
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}

}
