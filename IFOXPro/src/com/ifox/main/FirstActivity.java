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
	private ViewPager pager;  // 左右滑动组件
//	private LinearLayout points;  // 用于存放焦点图标的布局
	private List<View> list;  // 用于保存4张图片布局数据源 
    private ImageView[] views;  // 用于存放导航小圆点
    // 4个布局文件,整体管理
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
		// 屏蔽标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 屏蔽状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.firstactivity_main);
		//检查是否是第一次安装
		sp = getSharedPreferences("check", Activity.MODE_PRIVATE) ;
		edit = sp.edit() ;
		boolean firstload = sp.getBoolean("firstload", true);
		if(firstload){
			//预加载网络图片等信息
			imageTitleInfoPath = "http://" + XmlConstant.IP + "/wygl/xml/imagetitleinfo.xml" ;
			hashmap = new HashMap<String, String>() ;
			
			//检查网络连接状态
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
		// 把4张图片对应的布局文件加载回来,放到List中
		list = new ArrayList<View>();
		
		// 滑动图片的集合，这里设置成了固定加载，当然也可动态加载。
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
			// 很重要
			image.setImageResource(slideImages[i]);
			list.add(layout);
		}
		// 根据list的长度决定做多大的导航图片数组
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
			
			// 很重要
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
			//滑到最后一页自动跳转
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
				System.out.println("########### 图片主题信息 #############") ;
				conn.setConnectTimeout(5000) ;
//				conn.setRequestMethod("GET") ;
//				conn.setDoInput(true) ;
				conn.connect() ;
				InputStream input = conn.getInputStream() ;
				//使用DOM解析
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
				//保存标题信息到本地
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
		// 保存图片至项目内部缓存
		cache = new File(getFilesDir(), "image_cache");
		if(!cache.exists()){
			cache.mkdirs();
		}
		// getFilesDir() 获取当前项目内部缓存的所在路径
		for(int i=0;i<5;i++){
			//ImageView iv = new ImageView(this);
			Bitmap bitmap = getHttpBitmap(
					"http://" + XmlConstant.IP + "/wygl/images/image"+
			        (i+1)+".jpg");
			System.out.println("****bitmap****" + bitmap);
			if(bitmap != null){
				
				// 同时图片也要保存在本地缓存目录中
				// 内部缓存/image_cache/itemX.png
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
				System.out.println("******** 保存图片成功！ *********");
			}
		}
	}
	
	public Bitmap getHttpBitmap(String url) {
		URL myFileURL;
        Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
            // 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			//如果连接成功，在做其他
//			if(HttpURLConnection.HTTP_OK==conn.getResponseCode()){
				System.out.println("****** 连接成功！！！  ************");
				// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
				conn.setConnectTimeout(6000);
				// 连接设置获得数据流
				conn.setDoInput(true);
				// 不使用缓存
//				conn.setUseCaches(false);
				// 这句可有可无，没有影响
				conn.connect();
				// 得到数据流
				InputStream is = conn.getInputStream();
				// 解析得到图片
				bitmap = BitmapFactory.decodeStream(is);
				// 关闭数据流
				is.close();
//			}else{
//				System.out.println("****** 连接失败！！！  ************");
//				return null ;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
