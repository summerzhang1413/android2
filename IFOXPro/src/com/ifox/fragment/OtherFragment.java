package com.ifox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ifox.main.R;
import com.ifox.other.AboutActivity;
import com.ifox.other.LoginActivity;
import com.ifox.other.UpdateManager;
import com.ifox.util.NetworkConnected;

public class OtherFragment extends Fragment {
	
	private LinearLayout other_login, other_about, other_update, other_cancel ;
	private static  final String FILENAME = "version" ; //读取是否需要更新文件信息
	private boolean target = false ;
	private UpdateManager up ;	//更新检查
	private NetworkConnected nc ;//检查网络连接状态
	private boolean isCon ; //网络连接成功与否的标志位
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.other_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		intiLinearLayout();
		
	}

	private void intiLinearLayout() {
		View view = getView();
		other_login=(LinearLayout) view.findViewById(R.id.other_login);
		other_about=(LinearLayout) view.findViewById(R.id.other_about);
		other_update=(LinearLayout) view.findViewById(R.id.other_update);
		
		
		other_login.setOnClickListener(new LinearLayoutOnClickListener(0));
		other_about.setOnClickListener(new LinearLayoutOnClickListener(2));
		other_update.setOnClickListener(new LinearLayoutOnClickListener(3));
		
	}
	
	public class LinearLayoutOnClickListener implements View.OnClickListener{
		
		private int index = 0;
		
		public LinearLayoutOnClickListener(int i){
			this.index=i;
		}  
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (index) {
				case 0 :{
					Intent intent = new Intent(getActivity(),LoginActivity.class);
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.otherin, R.anim.hold);
					break;
					}
				case 2 :{
					Intent intent = new Intent(getActivity(),AboutActivity.class);
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.otherin, R.anim.hold);
					break;
					}
				case 3:{
					nc = new NetworkConnected(getActivity()) ;
					isCon = nc.isNetworkConnected() ;
					if(isCon){
						up = new UpdateManager(getActivity()) ;
						//读取本地版本是否更新文件
						SharedPreferences share = getActivity().getSharedPreferences(FILENAME,
								Activity.MODE_PRIVATE);	// 指定操作的文件名称
						target = share.getBoolean("version", false) ;
						System.out.println("**** share.getBoolean ****" + target) ;
						up.checkUpdate(target) ; //检查更新
					}else{
						Toast.makeText(getActivity(), R.string.unconnected, Toast.LENGTH_LONG).show() ;
					}
					
					break;
				}
				case 1 :{ 
					
					break;
				}
			}
		}
		
	}
	
}
