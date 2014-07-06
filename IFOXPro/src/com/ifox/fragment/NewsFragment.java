package com.ifox.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ifox.layout.SlideImageLayout;
import com.ifox.main.R;
import com.ifox.parser.NewsXmlParser;
import com.ifox.util.NetworkConnected;

public class NewsFragment extends ListFragment {
	private com.ifox.util.ScrollViewExtend scroll; 
	private ImageView imageView1;
	private ListView news_listview;
	private ArrayList<HashMap<String, Object>> listItems ;    //存放文字、图片信息  
    private SimpleAdapter listItemAdapter;                  //适配器     
//	private DisplayMetrics dm;

	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0; 
	
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null; 
	
	// 滑动标题
	private TextView tvSlideTitle = null;
	
	// 布局设置类
	private SlideImageLayout slideLayout = null;
	// 数据解析类
	private NewsXmlParser parser = null; 

	LayoutInflater inflater;
	private float rawX ;
	private float rawY ;
	private float x ;
	private float y ;
	
	private Context context ;
	private int v;   // 存放从网络上面获取的version
	private int c;   // 存放从网络上面获取的呈现图片数量
	private String l;   // 存放从网络上面获取的图片更新时间
	
	private static final String FILENAME = "imagetitle" ;//取得本地图片主题信息
	private SharedPreferences sp ;
	private String imagetitle[] = null; 
	private String title[] = null ;
	private String date[] = null ;
	private String info[] = null ;
	
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	
	File cache  ;
	private int length ;
	private SlideImageAdapter slideImageAdapter  ;
	private ImagePageChangeListener imagePageChangeListener  ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater ;
		init(inflater); 
		//初始化上方的图片滑动区域
		initeViews() ;
		
        System.out.println("********* onCreateView() **********") ;
		//初始化下方的新闻区域
  		initListview();
		return inflater.inflate(R.layout.suibian, main);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
		
	}

	private void init(LayoutInflater inflater) {
		listItems = new ArrayList<HashMap<String, Object>>();
		// 滑动图片区域
		imagePageViews = new ArrayList<View>();
		
		//LayoutInflater inflater = getLayoutInflater();  
		main = (ViewGroup)inflater.inflate(R.layout.news_fragment, null);
		viewPager = (ViewPager) main.findViewById(R.id.image_slide_page);  //得到ViewPager
		mPullRefreshScrollView=(com.handmark.pulltorefresh.library.PullToRefreshScrollView)main.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
				
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		//scroll.setOnTouchListener(new ImageOnTouchListener()) ;
		// 圆点图片区域
		parser = new NewsXmlParser(); 
		length = parser.getSlideImages().length;
//		int length = ids.length ;
		imageCircleViews = new ImageView[length];//实例化圆点图片 
		imageCircleView = (ViewGroup) main.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(getActivity());
		slideLayout.setCircleImageLayout(length);
		// 不需要加载网络图片,直接在内部缓存读就可以了
		cache = new File(getActivity().getFilesDir(),"image_cache");
		// 设置默认的滑动标题
		tvSlideTitle = (TextView) main.findViewById(R.id.tvSlideTitle);
		imagetitle = new String[5] ;
		title = new String[6] ;
		date = new String[6]; 
		info = new String[6] ;
		slideImageAdapter = new SlideImageAdapter() ;
		imagePageChangeListener = new ImagePageChangeListener() ;
		
		for(int i = 0;i < length;i++){
			File f = new File(cache,"item"+(i+1)+".png");
			Bitmap map = BitmapFactory.decodeFile(f.getPath());
			imagePageViews.add(slideLayout.getSlideImageLayout(map));//得到滚动图片
			imageCircleViews[i] = slideLayout.getCircleImageLayout(i);
			imageCircleView.addView(slideLayout.getLinearLayout(imageCircleViews[i], 10, 10));
		}
	}
	
	private void initeViews(){
		
		sp = getActivity().getSharedPreferences(FILENAME, Activity.MODE_PRIVATE) ;
		imagetitle[0] = sp.getString("imagetitle1", null) ;
		imagetitle[1] = sp.getString("imagetitle2", null) ;
		imagetitle[2] = sp.getString("imagetitle3", null) ;
		imagetitle[3] = sp.getString("imagetitle4", null) ;
		imagetitle[4] = sp.getString("imagetitle5", null) ;
		title[0] = sp.getString("title1", null) ;
		title[1] = sp.getString("title2", null) ;
		title[2] = sp.getString("title3", null) ;
		title[3] = sp.getString("title4", null) ;
		title[4] = sp.getString("title5", null) ;
		title[5] = sp.getString("title6", null) ;
		date[0] = sp .getString("date1", null);
		date[1] = sp .getString("date2", null);
		date[2] = sp .getString("date3", null);
		date[3] = sp .getString("date4", null);
		date[4] = sp .getString("date5", null);
		date[5] = sp .getString("date6", null);
		info[0] = sp.getString("info1", null) ;
		info[1] = sp.getString("info2", null) ;
		info[2] = sp.getString("info3", null) ;
		info[3] = sp.getString("info4", null) ;
		info[4] = sp.getString("info5", null) ;
		info[5] = sp.getString("info6", null) ;
		tvSlideTitle.setText(imagetitle[0]);
		// 设置ViewPager
        viewPager.setAdapter(slideImageAdapter);  
        viewPager.setCurrentItem(pageIndex) ;
        viewPager.setOnPageChangeListener(imagePageChangeListener) ;
        
	}

	//加载新闻列表内容
	private void initListview(){
		for(int i=0;i<6;i++){     
			HashMap<String, Object> map = new HashMap<String, Object>();
//           map.put("ItemImage", R.drawable.image03);
			map.put("ItemTitle" , title[i]);
			map.put("ItemContent" , info[i]);
			listItems.add(map);     
           
		}     
        //生成适配器的Item和动态数组对应的元素     
        listItemAdapter = new SimpleAdapter(getActivity(),listItems,//数据源      
                R.layout.news_list_item,//ListItem的XML布局实现     
                //动态数组与ImageItem对应的子项             
                new String[] {"ItemTitle","ItemContent"},      
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID     
                new int[] {R.id.newsTitle,R.id.newsContent}     
        ); 
        this.setListAdapter(listItemAdapter);
	}
	
	
	// 滑动图片数据适配器
    private class SlideImageAdapter extends PagerAdapter {  
    	
//    	private int mChildCount = 0 ;
//        @Override
//		public void notifyDataSetChanged() {
//			// TODO Auto-generated method stub
//        	mChildCount = getCount() ;
//			super.notifyDataSetChanged();
//		}

		@Override  
        public int getCount() { 
			System.out.println("**** imagePageViews.size() ***" + imagePageViews.size()) ;
            return imagePageViews.size();  
            
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
//        	if ( mChildCount > 0) {
//                mChildCount --;
//                return POSITION_NONE;
//            }
//            return super.getItemPosition(object);  
        	return POSITION_NONE;
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(imagePageViews.get(arg1));  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
        	((ViewPager) arg0).addView(imagePageViews.get(arg1));
            
            return imagePageViews.get(arg1);  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }
   
    
    // 滑动页面更改事件监听器
    private class ImagePageChangeListener implements OnPageChangeListener {
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
//        	if(arg0==1){
//        		menu.setSlidingEnabled(false);
//        	}else{
//        		menu.setSlidingEnabled(false);
//        	}
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
//        	menu.setSlidingEnabled(false);
        }  
  
        @Override  
        public void onPageSelected(int index) {  
        	pageIndex = index;
        	slideLayout.setPageIndex(index);
        	tvSlideTitle.setText(imagetitle[index]);
        	
            for (int i = 0; i < imageCircleViews.length; i++) {  
            	imageCircleViews[index].setBackgroundResource(R.drawable.dot_selected);
                
                if (index != i) {  
                	imageCircleViews[i].setBackgroundResource(R.drawable.dot_none);  
                }  
            }
        }  
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
//    	ActivityUtils.clearAll();
//    	imageCircleViews[pageIndex].setBackgroundResource(R.drawable.dot_selected);
    	System.out.println("**** onResume() *****") ;
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		intent.setClass(getActivity(), NewsDetailActivity.class);
		intent.putExtra("title", title[position]);
		intent.putExtra("info", info[position]);
		intent.putExtra("date", date[position]);
		
		startActivity(intent);
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1500);
//                mes.what = 1;
//                handler.sendMessage(mes);
				
//				initeViews() ;
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here

			// Call onRefreshComplete when the list has been refreshed.
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("ItemTitle" , title[2]);
//			map.put("ItemContent" , info[2]);
//			listItems.add(0, map) ;
//			listItemAdapter.notifyDataSetChanged() ;
			imagePageViews.removeAll(imagePageViews) ;
			for(int i = 0;i < length;i++){
				File f = new File(cache,"item"+(i+1)+".png");
				Bitmap map = BitmapFactory.decodeFile(f.getPath());
				
				imagePageViews.add(slideLayout.getSlideImageLayout(map));//得到滚动图片
			}
			viewPager.getAdapter().notifyDataSetChanged() ;
			//检查网络连接状态
			NetworkConnected nc ;
			boolean isCon ;
			nc = new NetworkConnected(getActivity()) ;
			isCon = nc.isNetworkConnected() ;
			if(isCon){
				Toast.makeText(getActivity(), "更新成功...", Toast.LENGTH_SHORT).show() ;
			}else{
				Toast.makeText(getActivity(), "请检查网络!!", Toast.LENGTH_SHORT).show() ;
			}
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
}
