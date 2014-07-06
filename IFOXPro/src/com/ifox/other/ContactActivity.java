package com.ifox.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ifox.main.R;

public class ContactActivity extends Activity {
	public static String data[][] = new String[][]{{"周老师","13658096068"},
		{"孙老师","18982275133"},{"陈伍亿","15680728781"},{"张    野","13488980160"},
		{"刘    璐","13608192547"},{"代    维","18108127673"},{"向珍兴","18215559761"},
		{"朱    涛","13882134140"},{"蒙平东","18215552232"},{"何    瑞","13618062160"},
		{"刘    行","13730880946"},{"唐    云","18215553123"},{"曾    涛","18280308591"},
		{"陈晓敏","13648045003"},{"李    红","18215686783"},{"张宇然","13688301300"}} ;
	private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	private ListView contact ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.contact);
		this.contact = (ListView)super.findViewById(R.id.contact) ;
		for(int x = 0; x < this.data.length; x++){
			Map<String,String> map = new HashMap<String,String>() ;
			map.put("linkman", data[x][0]);
			map.put("tel", data[x][1]);
			this.list.add(map);		
		}
		MyAdapter myadapter = new MyAdapter(this) ;
		this.contact.setAdapter(myadapter) ;
		
	}
	
	private class MyAdapter extends BaseAdapter{
		private LayoutInflater inflate ;
		
		public MyAdapter(Context con){
			inflate = LayoutInflater.from(con) ;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;  
			if (convertView == null) {  
	             holder=new ViewHolder();    
	             //可以理解为从vlist获取view  之后把view返回给ListView  
	             convertView = inflate.inflate(R.layout.contact_details, null);  
	             holder.linkman = (TextView)convertView.findViewById(R.id.linkman);  
	             holder.tel = (TextView)convertView.findViewById(R.id.tel);  
	             holder.call = (ImageView)convertView.findViewById(R.id.call);  
	             holder.message = (ImageView)convertView.findViewById(R.id.message);  
	             convertView.setTag(holder);               
	         }else {               
	             holder = (ViewHolder)convertView.getTag();  
	         }         
	           
	         holder.linkman.setText((String)list.get(position).get("linkman"));  
	         holder.tel.setText((String)list.get(position).get("tel"));  
	         holder.call.setTag(position);  
	         holder.message.setTag(position); 
	         //给Button添加单击事件  添加Button之后ListView将失去焦点  需要的直接把Button的焦点去掉  
	         final int p = position ;
	         holder.call.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					intoCall(p);
				}
				
	         });  
	         holder.message.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						intoMessage(p);
					}
					
		         });
	         return convertView;  
		}  
	}  
			
	public final class ViewHolder {  
	    public TextView linkman ;  
	    public TextView tel ;  
	    public ImageView call ;
	    public ImageView message ;
	}  
	
	private void intoCall(int position) {
		Map<String, String> map = list.get(position) ;
		String telStr = map.get("tel") ;
		Uri uri = Uri.parse("tel:" + telStr) ;
		Intent it = new Intent() ;
		it.setAction(Intent.ACTION_DIAL) ;
		it.setData(uri) ;
		ContactActivity.this.startActivity(it) ;
	}
	
	private void intoMessage(int position) {
		Map<String, String> map = list.get(position) ;
		String telStr = map.get("tel") ;
		Uri uri = Uri.parse("smsto:" + telStr) ;
		Intent it = new Intent() ;
		it.setAction(Intent.ACTION_SENDTO) ;
		it.setType("vnd.android-dir/mms-sms") ;
		it.setData(uri) ;
		ContactActivity.this.startActivity(it) ;
	}
	
	public void back(View v) {
		finish();
		overridePendingTransition(0, R.anim.out_news_text);
	}
	
}
