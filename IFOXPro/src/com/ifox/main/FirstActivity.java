package com.ifox.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ifox.constant.XmlConstant;
import com.ifox.other.ParseXmlService;
import com.ifox.util.NetworkConnected;

public class FirstActivity extends Activity {
	private ViewPager pager;  // ���һ������
//	private LinearLayout points;  // ���ڴ�Ž���ͼ��Ĳ���
	private List<View> list;  // ���ڱ���4��ͼƬ��������Դ 
    private ImageView[] views;  // ���ڴ�ŵ���СԲ��
    // 4�������ļ�,�������
    private int[] ids = {R.layout.firstitem01, R.layout.firstitem02, R.layout.firstitem03, R.layout.firstitem04,R.layout.firstitem05};
    private SharedPreferences sp ;
    private SharedPreferences.Editor edit ;
    
    private NetworkConnected nc ;
	private boolean isCon ;
	private File cache  ;
	private String imageTitleInfoPath ;
	private HashMap<String, String> hashmap ;
	private static final String FILENAME = "imagetitle" ;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���α�����
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ����״̬��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.firstactivity_main);
		//����Ƿ��ǵ�һ�ΰ�װ
		sp = getSharedPreferences("check", Activity.MODE_PRIVATE) ;
		edit = sp.edit() ;
		boolean firstload = sp.getBoolean("firstload", true);
		if(firstload){
			//Ԥ��������ͼƬ����Ϣ
			imageTitleInfoPath = "http://" + XmlConstant.IP + "/wygl/xml/imagetitleinfo.xml" ;
			hashmap = new HashMap<String, String>() ;
			
			//�����������״̬
			nc = new NetworkConnected(FirstActivity.this) ;
			isCon = nc.isNetworkConnected() ;
			
			if(isCon){
				new Thread(new Runnable(){
		
							@Override
							public void run() {
								// TODO Auto-generated method stub
								loadRemoteImage();
								updateImageTitleInfo() ;
							}
					
						}
				).start() ;
			}
			
			init();
		}else{
			Intent it = new Intent(FirstActivity.this, StartActivity.class) ;
			startActivity(it) ;
			finish() ;
		}
		
		
	}

	private void init() {
		edit.putBoolean("firstload", false) ;
		edit.commit() ;
		
		pager = (ViewPager)this.findViewById(R.id.imagePages);
//		points = (LinearLayout)this.findViewById(R.id.pointGroup);
		// ��4��ͼƬ��Ӧ�Ĳ����ļ����ػ���,�ŵ�List��
		list = new ArrayList<View>();
		
		// ����ͼƬ�ļ��ϣ��������ó��˹̶����أ���ȻҲ�ɶ�̬���ء�
		int[] slideImages = {
				R.drawable.firstp1,
				R.drawable.firstp2,
				R.drawable.firstp3,
				R.drawable.firstp4,
				R.drawable.firstp5
		};
		for(int i = 0; i<5; i++){
			FrameLayout layout = (FrameLayout)getLayoutInflater().inflate(ids[i], null);
			ImageView image = (ImageView)layout.findViewById(R.id.imageView1);
			// ����Ҫ
			image.setImageResource(slideImages[i]);
			list.add(layout);
		}
		// ����list�ĳ��Ⱦ��������ĵ���ͼƬ����
		views = new ImageView[list.size()];
		for(int i=0;i<views.length;i++){
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(new LayoutParams(20,20));
			iv.setPadding(20, 0, 20, 0);
			views[i] = iv;
			if(i==0){
				iv.setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				iv.setBackgroundResource(R.drawable.page_indicator);
			}
			
			// ����Ҫ
			//points.addView(views[i]);
		}
				
//		for(int i =0;i<views.length;i++){
//			points.addView(views[i]);
//		}
		pager.setAdapter(new MyPagerAdapter());
		pager.setOnPageChangeListener(new OnPageChangeListenerImpl() );
	}
	
    private class OnPageChangeListenerImpl implements OnPageChangeListener{
    	@Override
		public void onPageSelected(int arg0) {
			for(int i=0;i<views.length;i++){
				if(i==arg0){
					views[i].setBackgroundResource(R.drawable.page_indicator_focused);
				}else{
					views[i].setBackgroundResource(R.drawable.page_indicator);
				}
			}
			//�������һҳ�Զ���ת
			if(arg0 == views.length-1){
				new Handler().postDelayed(
						new Runnable(){
							@Override
							public void run() {
								// T ODO Auto-generated method stub
								Intent it = new Intent(FirstActivity.this, MainActivity.class) ;
								startActivity(it) ;
								finish() ;
							}
						}
				, 1200) ;
			}
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
    }

    private class MyPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(View container, int position) {
			( (ViewPager)container ).addView(list.get(position));
			return list.get(position);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			( (ViewPager)container ).removeView(list.get(position));
		}
		
	}
    
    private void updateImageTitleInfo(){
		try {
			URL url  = new URL(imageTitleInfoPath) ;
			HttpURLConnection conn = (HttpURLConnection)url.openConnection() ;
			if(HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				System.out.println("########### ͼƬ������Ϣ #############") ;
				conn.setConnectTimeout(5000) ;
//				conn.setRequestMethod("GET") ;
//				conn.setDoInput(true) ;
				conn.connect() ;
				InputStream input = conn.getInputStream() ;
				//ʹ��DOM����
				ParseXmlService parse = new ParseXmlService() ;
				hashmap = parse.parseXmlImageTitleInfo(input) ;
				String imagetitle1 = hashmap.get("imagetitle1") ;
				String imagetitle2 = hashmap.get("imagetitle2") ;
				String imagetitle3 = hashmap.get("imagetitle3") ;
				String imagetitle4 = hashmap.get("imagetitle4") ;
				String imagetitle5 = hashmap.get("imagetitle5") ;
				String title1 = hashmap.get("title1") ;
				String title2 = hashmap.get("title2") ;
				String title3 = hashmap.get("title3") ;
				String title4 = hashmap.get("title4") ;
				String title5 = hashmap.get("title5") ;
				String title6 = hashmap.get("title6") ;
				String date1 = hashmap.get("date1") ;
				String date2 = hashmap.get("date2") ;
				String date3 = hashmap.get("date3") ;
				String date4 = hashmap.get("date4") ;
				String date5 = hashmap.get("date5") ;
				String date6 = hashmap.get("date6") ;
				String info1 = hashmap.get("info1") ;
				String info2 = hashmap.get("info2") ;
				String info3 = hashmap.get("info3") ;
				String info4 = hashmap.get("info4") ;
				String info5 = hashmap.get("info5") ;
				String info6 = hashmap.get("info6") ;
				System.out.println("***title 12345***" + title1 + "||" + title2);
				//���������Ϣ������
				SharedPreferences sp = this.getSharedPreferences(FILENAME, MODE_PRIVATE) ;
				SharedPreferences.Editor edit = sp.edit() ;
				edit.putString("imagetitle1", imagetitle1) ;
				edit.putString("imagetitle2", imagetitle2) ;
				edit.putString("imagetitle3", imagetitle3) ;
				edit.putString("imagetitle4", imagetitle4) ;
				edit.putString("imagetitle5", imagetitle5) ;
				edit.putString("title1", title1) ;
				edit.putString("title2", title2) ;
				edit.putString("title3", title3) ;
				edit.putString("title4", title4) ;
				edit.putString("title5", title5) ;
				edit.putString("title6", title6) ;
				edit.putString("date1", date1) ;
				edit.putString("date2", date2) ;
				edit.putString("date3", date3) ;
				edit.putString("date4", date4) ;
				edit.putString("date5", date5) ;
				edit.putString("date6", date6) ;
				edit.putString("info1", info1) ;
				edit.putString("info2", info2) ;
				edit.putString("info3", info3) ;
				edit.putString("info4", info4) ;
				edit.putString("info5", info5) ;
				edit.putString("info6", info6) ;
				edit.commit() ;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void loadRemoteImage() {
		// ����ͼƬ����Ŀ�ڲ�����
		cache = new File(getFilesDir(), "image_cache");
		if(!cache.exists()){
			cache.mkdirs();
		}
		// getFilesDir() ��ȡ��ǰ��Ŀ�ڲ����������·��
		for(int i=0;i<5;i++){
			//ImageView iv = new ImageView(this);
			Bitmap bitmap = getHttpBitmap(
					"http://" + XmlConstant.IP + "/wygl/images/image"+
			        (i+1)+".jpg");
			System.out.println("****bitmap****" + bitmap);
			if(bitmap != null){
				
				// ͬʱͼƬҲҪ�����ڱ��ػ���Ŀ¼��
				// �ڲ�����/image_cache/itemX.png
				File file = new File(cache,"item"+(i+1)+".png");
				try {
					file.createNewFile();
					OutputStream os = new FileOutputStream(file);
					System.out.println("*** file & os ***" + file + "||" + os) ;
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
					os.flush();
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("******** ����ͼƬ�ɹ��� *********");
			}
		}
	}
	
	public Bitmap getHttpBitmap(String url) {
		URL myFileURL;
        Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
            // �������
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			//������ӳɹ�����������
//			if(HttpURLConnection.HTTP_OK==conn.getResponseCode()){
				System.out.println("****** ���ӳɹ�������  ************");
				// ���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
				conn.setConnectTimeout(6000);
				// �������û��������
				conn.setDoInput(true);
				// ��ʹ�û���
//				conn.setUseCaches(false);
				// �����п��ޣ�û��Ӱ��
				conn.connect();
				// �õ�������
				InputStream is = conn.getInputStream();
				// �����õ�ͼƬ
				bitmap = BitmapFactory.decodeStream(is);
				// �ر�������
				is.close();
//			}else{
//				System.out.println("****** ����ʧ�ܣ�����  ************");
//				return null ;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
