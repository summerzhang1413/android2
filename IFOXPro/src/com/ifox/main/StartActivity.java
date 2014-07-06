package com.ifox.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.ifox.constant.XmlConstant;
import com.ifox.other.ParseXmlService;
import com.ifox.util.NetworkConnected;

public class StartActivity extends Activity {
	private NetworkConnected nc ;
	private boolean isCon ;
	private File cache  ;
	private String imageTitleInfoPath ;
	private HashMap<String, String> hashmap ;
	private static final String FILENAME = "imagetitle" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		super.setContentView(R.layout.start_acti) ;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN) ;
		
		imageTitleInfoPath = "http://" + XmlConstant.IP + "/wygl/xml/imagetitleinfo.xml" ;
		hashmap = new HashMap<String, String>() ;
		
		//�����������״̬
		nc = new NetworkConnected(StartActivity.this) ;
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
		
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent it = new Intent(StartActivity.this, MainActivity.class) ;
				StartActivity.this.startActivity(it) ;
				StartActivity.this.finish() ;
			}
			
		}, 1000) ;
		
		
		
		
		
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
					bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
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
//				return null ;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
