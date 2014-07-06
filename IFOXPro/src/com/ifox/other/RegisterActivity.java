package com.ifox.other;




import com.ifox.main.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class RegisterActivity extends Activity {
    private EditText username;
    private EditText paw;
    private EditText ipaw;
    private EditText school;
    private EditText phone;
    private EditText acm_id;
    private EditText email;
    private EditText identify;
    private Button register;
    private Button register_cancel;
    private ProgressBar pg_register;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.other_register);
	    initRegister();
	    register.setOnClickListener(new MyRegisterListener(0));
	    register_cancel.setOnClickListener(new MyRegisterListener(1));
	}
	private void initRegister() {
		username=(EditText) findViewById(R.id.register_username);
	    paw=(EditText) findViewById(R.id.register_password);
	    ipaw=(EditText) findViewById(R.id.register_ipassword);
	    school=(EditText) findViewById(R.id.register_school);
	    phone=(EditText) findViewById(R.id.register_phonenumber);
	    acm_id=(EditText) findViewById(R.id.register_acm_id);
	    email=(EditText) findViewById(R.id.register_email);
	    identify=(EditText) findViewById(R.id.register_identify);
	    register=(Button) findViewById(R.id.register);
	    register_cancel=(Button) findViewById(R.id.cancel);
	    pg_register=(ProgressBar) findViewById(R.id.pb_register);
	}
	public class MyRegisterListener implements OnClickListener{
		private int index=0;
		private String reg_uname;
		private String reg_paw;
		private String reg_ipaw;
		private String reg_school;
		private String reg_phone;
		private String reg_id;
		private String reg_email;
		private String reg_identify;
        public MyRegisterListener(int i){
        	this.index=i;
        	
        }
		@Override
		public void onClick(View v) {
			switch(index){
			case 0:{
				reg_uname= username.getText().toString();
				reg_paw= paw.getText().toString();
				reg_ipaw= ipaw.getText().toString();
				reg_school= school.getText().toString();
				reg_phone= phone.getText().toString();
				reg_id= acm_id.getText().toString(); 
				reg_email= email.getText().toString();
				reg_identify= identify.getText().toString();
				String semail="[a-zA-Z0-9]{8,15}@[a-zA-Z0-9]{3}.com";
		    	String spwd="[a-zA-Z0-9]{6,13}";
		    	if((reg_email.matches(semail))
			    		&&(reg_ipaw.matches(reg_paw))
			    		&&(reg_paw.matches(spwd))){
		    		
		    		
		    	}
		    	pg_register.setVisibility(View.VISIBLE);
				Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
				RegisterActivity.this.startActivity(intent);
				break;
			}
			case 1:{
				Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
				RegisterActivity.this.startActivity(intent);
				RegisterActivity.this.finish();
				break;
			}	
			
			}
		}
		
		
	}
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}

}

