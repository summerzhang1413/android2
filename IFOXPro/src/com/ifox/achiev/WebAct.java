package com.ifox.achiev;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.ifox.main.R;

public class WebAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE)  ;
		super.setContentView(R.layout.web) ;
		
	}
	
	//设置返回按键的监听事件
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {	// 返回键
			WebAct.this.finish() ;
		}
		return false ;
	}
	
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}
	
}
