package com.ifox.other;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ifox.main.R;

public class LoginActivity extends Activity {
	private EditText username, password;
	private Button login ;
	private CheckBox checkbox = null ;
	private String isMemory = null ;
	private static final String FILE = "saveUserNamePwd";
	private SharedPreferences sp = null;
	
	private static String YES = "yes";
	private static String NO = "no";
	private String name , psw ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.login);
		
		intiButton();
	}
	private void intiButton() {
		username = (EditText) findViewById(R.id.other_login_username);		
		password = (EditText) findViewById(R.id.other_login_password);	
		login = (Button) findViewById(R.id.other_login_login);
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		sp = getSharedPreferences(FILE, MODE_PRIVATE) ;
		isMemory = sp.getString("isMemory", NO);
		if (isMemory.equals(YES)) {
			name = sp.getString("name", "");
			psw = sp.getString("psw", "");
			username.setText(name);
			password.setText(psw);
		}
		
		login.setOnClickListener(new ButtonOnclickListener());
	}
	
	public class ButtonOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String un = username.getText().toString();
			String pw = password.getText().toString();
			remenber(un, pw);
			if(un.equals("ifox")&&pw.equals("123")){
				
				Intent intent = new Intent(LoginActivity.this,ContactActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.overridePendingTransition(R.anim.otherin, R.anim.hold);
				finish();
			}else{
				Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
		
	public void remenber(String un, String pw) {
		if (checkbox.isChecked()) {
			if (sp == null) {
				sp = getSharedPreferences(FILE, MODE_PRIVATE);
			}
			Editor edit = sp.edit();
			edit.putString("name", un);
			edit.putString("psw", pw);
			edit.putString("isMemory", YES);
			edit.commit();
		} else if (!checkbox.isChecked()) {
			if (sp == null) {
				sp = getSharedPreferences(FILE, MODE_PRIVATE);
			}
			Editor edit = sp.edit();
			edit.putString("isMemory", NO);
			edit.commit();
		}
	}
	
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}
}
