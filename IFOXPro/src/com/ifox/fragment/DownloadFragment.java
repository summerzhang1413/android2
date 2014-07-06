package com.ifox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ifox.achiev.AndroidAct;
import com.ifox.achiev.WebAct;
import com.ifox.main.R;

public class DownloadFragment extends Fragment{
	private LinearLayout android,web; 
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			
			return inflater.inflate( R.layout.download_fragment, null);
		}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.android = (LinearLayout)getView().findViewById(R.id.android);
		this.web = (LinearLayout)getView().findViewById(R.id.web);
		this.android.setOnClickListener(new OnClickListenerAndroid());
		this.web.setOnClickListener(new OnClickListenerWeb()) ;
		
	}
	
	private class OnClickListenerAndroid implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent it = new Intent(getActivity(), AndroidAct.class) ;
			getActivity().startActivity(it) ;
			getActivity().overridePendingTransition(R.anim.otherin, R.anim.hold);
			
		}
		
	}
	
	private class OnClickListenerWeb implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent it = new Intent(getActivity(), WebAct.class) ; 
			getActivity().startActivity(it) ;
			getActivity().overridePendingTransition(R.anim.otherin, R.anim.hold);
			
		}
		
	}
}


