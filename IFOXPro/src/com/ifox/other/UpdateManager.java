package com.ifox.other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ifox.constant.XmlConstant ;
import com.ifox.main.R;



public class UpdateManager { 
	/* 下载中 */ 
    private static final int DOWNLOAD = 1; 
    /* 下载结束 */ 
    private static final int DOWNLOAD_FINISH = 2;
    /* 保存解析的XML信息 */ 
    private HashMap<String, String> mHashMap = new HashMap<String, String>(); 
    /* 下载保存路径 */ 
    private String mSavePath; 
    /* 记录进度条数量 */ 
    private int progress; 
    /* 是否取消更新 */ 
    private boolean cancelUpdate = false; 
 
    private Context mContext; 
    /* 更新进度条 */ 
    private ProgressBar mProgress; 
    private Dialog mDownloadDialog; 
    //地址是服务器上version.xml链接地址 
    private String path = "http://" + XmlConstant.IP + "/wygl/xml/version.xml";  
    //当前版本
    private int versionCode = 0 ;
    //更新版本
    private int serviceCode = 0 ;
    //是否需要更新，默认为不需要
    private boolean target = false ;
    private static  final String FILENAME = "version" ;	//保存版本信息
    
    
    private Handler mHandler = new Handler() { 
        public void handleMessage(Message msg) { 
            switch (msg.what) { 
            // 正在下载 
            case DOWNLOAD: 
	            // 设置进度条位置 
	            mProgress.setProgress(progress); 
	            break; 
            case DOWNLOAD_FINISH: 
	            // 安装文件 
	            installApk(); 
	            break; 
            default: 
                break; 
            } 
        }; 
    }; 
 
    public UpdateManager(Context context) { 
        this.mContext = context; 
    } 

    /**
     * 检测软件更新
     */ 
    public void checkUpdate(boolean tg){
        if (tg) { 
            // 显示提示对话框 
            showNoticeDialog(); 
        } else { 
        	
            Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show(); 
        } 
    } 
 
    /**
     * 检查软件是否有更新版本
     * 
     * @return
     * @throws InterruptedException 
     */ 
    
    public void sendM() {
    	// 获取当前软件版本 
    	versionCode = getVersionCode(mContext); 
    	System.out.println("**** 当前版本versionCode ****:" + versionCode)  ;
    	
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try{  
		            URL url = new URL(path);  
		            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		            conn.setReadTimeout(5*1000);  
		            conn.setRequestMethod("GET");  
		            InputStream inStream = conn.getInputStream();  
		            //使用DOM解析
		            ParseXmlService service = new ParseXmlService(); 
		            mHashMap = service.parseXml(inStream);  
		            serviceCode = Integer.parseInt(mHashMap.get("version")) ;
		            String name = mHashMap.get("name") ;
		            String urll = mHashMap.get("url") ;
		            System.out.println("**** 检查版本serviceCode ****:" + serviceCode)  ;
	            	if(serviceCode > versionCode){
	            		//有新版本，重新设置标志位
	            		target = true ;
	            		
	            	}
	            	//保存版本号到本地文件
            		SharedPreferences share = mContext.getSharedPreferences(FILENAME,
            				Activity.MODE_PRIVATE);	// 指定操作的文件名称
            		SharedPreferences.Editor edit = share.edit(); 	// 编辑文件
            		edit.putBoolean("version", target);			// 保存整型
            		edit.putString("name", name) ;
            		edit.putString("url", urll) ;
            		edit.commit() ;			// 提交更新
		        } catch (Exception e)  {  
		            e.printStackTrace();  
		        }
			}
        	
        }).start() ;
        System.out.println("*** UpdateManager里面的target ****:" + target) ;
    }
	/**
     * 获取软件版本号
     * 
     * @param context
     * @return
     */ 
    private int getVersionCode(Context context) 
    { 
        int versionCode = 0; 
        try 
        { 
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode 
            versionCode = context.getPackageManager()
            		.getPackageInfo("com.ifox.main", 0).versionCode; 
        } catch (NameNotFoundException e) 
        { 
            e.printStackTrace(); 
        } 
        return versionCode; 
    } 
 
    /**
     * 显示软件更新对话框
     */ 
    private void showNoticeDialog() 
    { 
        // 构造对话框 
        AlertDialog.Builder builder = new Builder(mContext); 
        builder.setTitle(R.string.soft_update_title); 
        builder.setMessage(R.string.soft_update_info); 
        // 更新 
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
                // 显示下载对话框 
                showDownloadDialog(); 
            } 
        }); 
        // 稍后更新 
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
            } 
        }); 
        Dialog noticeDialog = builder.create(); 
        noticeDialog.show(); 
    } 
 
    /**
     * 显示软件下载对话框
     */ 
    private void showDownloadDialog(){ 
        // 构造软件下载对话框 
        AlertDialog.Builder builder = new Builder(mContext); 
        builder.setTitle(R.string.soft_updating); 
        // 给下载对话框增加进度条 
        final LayoutInflater inflater = LayoutInflater.from(mContext); 
        View v = inflater.inflate(R.layout.softupdate_progress, null); 
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress); 
        builder.setView(v); 
        // 取消更新 
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
                // 设置取消状态 
                cancelUpdate = true; 
            } 
        }); 
        mDownloadDialog = builder.create(); 
        mDownloadDialog.show(); 
        // 下载文件 
        downloadApk(); 
    } 
 
    /**
     * 下载apk文件
     */ 
    private void downloadApk() 
    { 
        // 启动新线程下载软件 
        new downloadApkThread().start(); 
    } 
 
    /**
     * 下载文件线程
     */ 
    private class downloadApkThread extends Thread { 
        @Override 
        public void run() { 
            try { 
                // 判断SD卡是否存在，并且是否具有读写权限 
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
                    // 获得存储卡的路径 
                    String sdpath = Environment.getExternalStorageDirectory().toString() + File.separator;  //取得扩展的存储目录
                    mSavePath = sdpath + "iFOXUpdate"; 
                    System.out.println("******mHashMap.get(url)***:" +  mHashMap.get("url")) ;
                    
                    //读取本地版本更新文件
					SharedPreferences share = mContext.getSharedPreferences(FILENAME,
							Activity.MODE_PRIVATE);	// 指定操作的文件名称
					String urll = share.getString("url", null) ;
					System.out.println("**** share.getString(url) ****" + urll) ;
                    
                    URL url = new URL(urll); 
                    // 创建连接 
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
                    conn.connect(); 
                    if(HttpURLConnection.HTTP_OK==conn.getResponseCode()){
                    	System.out.println("******* 下载连接成功!!!  *********" ) ;
                    }
                    // 获取文件大小 
                    int length = conn.getContentLength(); 
                    // 创建输入流 
                    InputStream inputs = conn.getInputStream(); 
                    File file = new File(mSavePath); 
                    // 判断父文件目录是否存在 
                    if (!file.exists()){ 
                        file.mkdirs(); 
                    } 
                    System.out.println("******* File文件路径  *********" + file) ;
                    //读取本地版本更新文件
					String name = share.getString("name", null) ;
                    File apkName = new File(mSavePath + File.separator + name); 
                    System.out.println("******* apkFile路径  *********" + apkName) ;
                    
                    FileOutputStream foutputs = new FileOutputStream(apkName); 
                    int count = 0; 
                    // 缓存 
                    byte buf[] = new byte[length]; 
                    // 写入到文件中 
                    do{ 
                        int numread = inputs.read(buf); 
                        
                        System.out.println("******* numread  *********" + numread) ;
                        
                        count += numread; 
                        // 计算进度条位置 
                        progress = (int) (((float) count / length) * 100); 
                        // 更新进度 
                        mHandler.sendEmptyMessage(DOWNLOAD); 
                        if (numread <= 0) 
                        { 
                            // 下载完成 
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH); 
                            break; 
                        } 
                        // 写入文件 
                        foutputs.write(buf, 0, numread); 
                    } while (!cancelUpdate);// 点击取消就停止下载. 
                    foutputs.close(); 
                    inputs.close(); 
                } 
            }catch (MalformedURLException e) { 
            	System.out.println("******* MalformedURLException异常 *********") ;
                e.printStackTrace(); 
            }catch (IOException e) { 
            	System.out.println("******* IOException异常 *********") ;
                e.printStackTrace(); 
            } 
            // 取消下载对话框显示 
            mDownloadDialog.dismiss(); 
        } 
    }; 
 
    /**
     * 安装APK文件
     */ 
    private void installApk() {
    	//读取本地版本更新文件
		SharedPreferences share = mContext.getSharedPreferences(FILENAME,
				Activity.MODE_PRIVATE);	// 指定操作的文件名称
		String name = share.getString("name", null) ;
        File apkName = new File(mSavePath + File.separator + name); 
        if (!apkName.exists()) 
        { 
            return; 
        } 
        // 通过Intent安装APK文件 
        Intent it = new Intent(Intent.ACTION_VIEW); 
        it.setDataAndType(Uri.parse("file://" + apkName.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(it); 
    } 
}

