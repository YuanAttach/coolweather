package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.view.View;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ListView listView;
	private TextView titleText;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	private List<String> dataList= new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	private CoolWeatherDB coolWeatherDB;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SharedPreferences perfs = PreferenceManager.getDefaultSharedPreferences(this);
		if(perfs.getBoolean("city_selected", false)){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){
              @Override
              public void onItemClick(AdapterView<?> arg0,View view,int index,long arg3){
            	  if(currentLevel==LEVEL_PROVINCE){
            		  selectedProvince = provinceList.get(index);
            		  queryCities();
            	  }
            	  if(currentLevel==LEVEL_CITY){
            		  selectedCity = cityList.get(index);
            		  //queryCounties();
            		  String cityCode=new String(selectedCity.getCityCode());
            		  String provinceCode=new String(selectedProvince.getProvinceCode());
            		  Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
            		  intent.putExtra("city_code", cityCode);
            		  intent.putExtra("province_code", provinceCode);
            		  startActivity(intent);
            		  finish();
            	  }
              }
		});
		queryProvinces();
	}
/**
 * 查询全国所有的省，优先查询数据库，如果没有，再去服务器查询
 */
       private void queryProvinces(){
    	   provinceList=coolWeatherDB.loadProvinces();
    	   if(provinceList.size()>0){
    		   dataList.clear();
    		   for(Province province: provinceList){
    			   dataList.add(province.getProvinceName());
    		   }
    		   titleText.setText("中国");
    		   adapter.notifyDataSetChanged();
    		   listView.setSelection(0);
    		   currentLevel=LEVEL_PROVINCE;
    	   }else{
    		   queryFromServer(null,"province");
    	   }
       }
       /**
        * 查询省中所有的市，优先查询数据库，如果没有，再去服务器查询
        */
              private void queryCities(){
           	   cityList=coolWeatherDB.loadCities(selectedProvince.getId());
           	   if(cityList.size()>0){
           		   dataList.clear();
           		   for(City city: cityList){
           			   dataList.add(city.getCityName());
           		   }
           		   titleText.setText(selectedProvince.getProvinceName());
           		   adapter.notifyDataSetChanged();
           		   listView.setSelection(0);
           		   currentLevel=LEVEL_CITY;
           	   }else{
           		   queryFromServer(selectedProvince.getProvinceCode(),"city");
           	   }
              }
/**              /**
               * 查询市中所有的县，优先查询数据库，如果没有，再去服务器查询
               
                     private void queryCounties(){
                  	   countyList=coolWeatherDB.loadCounties(selectedCity.getId());
                  	   if(countyList.size()>0){
                  		   dataList.clear();
                  		   for(County county: countyList){
                  			   dataList.add(county.getCountyName());
                  		   }
                  		   titleText.setText(selectedCity.getCityName());
                  		   adapter.notifyDataSetChanged();
                  		   listView.setSelection(0);
                  		   currentLevel=LEVEL_COUNTY;
                  	   }else{
                  		   queryFromServer(selectedCity.getCityCode(),"county");
                  	   }
                     }
  
                     */
  /**
   * 根据代号和类型从服务器上查询省市县数据
   */
                     private void queryFromServer(final String code,final String type){
                    	 String address = null;
//                    	 if(!TextUtils.isEmpty(code)){
//                    		 address="http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";
//                    	 }else{
//                    		 address="http://www.weather.com.cn/data/city3jdata/china.html";
//                    	 }
                    	 switch(type){
                    	 case "province":
                    		 address="http://www.weather.com.cn/data/city3jdata/china.html";
                    	     break;
                    	 case "city":
                    		 address="http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";
                    	     break;
                    	 case "county":
                    		 String code2=new String(selectedProvince.getProvinceCode());
                    		 address="http://www.weather.com.cn/data/city3jdata/station/"+code2+code+".html";
                    		 
                    	     break; 
                    	  default:
                    		  break;	 
                    	 }
                    	 showProgressDialog();
                    	 HttpUtil.sendHttpRequest(address, new HttpCallBackListener(){
                    		@Override
                    		public void onFinish(String response){
                    			//通过runOnUiThread方法返回主线程
                    			boolean result = false;
                    			if("province".equals(type)){
                    				result=Utility.handleProvinceResponse(response, coolWeatherDB);
                    			}else if("city".equals(type)){
                    				result=Utility.handleCityResponse(response, coolWeatherDB, selectedProvince.getId());
                    			}else if("county".equals(type)){
                    				result=Utility.handleCountyResponse(response, coolWeatherDB, selectedCity.getId());
                    			}
                    			if(result){
                    				runOnUiThread(new Runnable(){
                    					@Override
                    					public void run(){
                    						closeProgressDialog();
                    						if("province".equals(type)){
                    							queryProvinces();
                    						}else if("city".equals(type)){
                    							queryCities();}
//                    						}else if("county".equals(type)){
//                    							queryCounties();
//                    						}
                    					}
                    				});
                    				
                    			}
                    			
                    		}
                    		@Override
                    		public void onError(Exception e){
                    			//通过runonUiThread方法返回主线程
                    			runOnUiThread(new Runnable(){
                    				@Override
                    				public void run(){
                    					closeProgressDialog();
                    					Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    				}
                    			});
                    		}
                    	 });
                     }
                    	 /**
                    	  * 显示进度对话框
                    	  */
                    	 private void showProgressDialog() {
                    		 if(progressDialog==null){
                    			 progressDialog=new ProgressDialog(this);
                    			 progressDialog.setMessage("正在加载");
                    			 progressDialog.setCanceledOnTouchOutside(false);
                    		 }
                    		 progressDialog.show();
                    	 }
                    	 
                    	 /**
                    	  * 关闭进度对话框
                    	  */
                    	 private void closeProgressDialog(){
                    		 if(progressDialog!=null){
                    			 progressDialog.dismiss();
                    		 }
                    	 }
                    	 /**
                    	  * 捕获back按键，根据当时级别来判断，是应该返回市列表，省列表，还是直接退出
                    	  */
                    	 @Override
                    	 public void onBackPressed(){
                    		 if(currentLevel==LEVEL_COUNTY){
                    			 queryCities();
                    		 }else if(currentLevel==LEVEL_CITY)
                    		 {
                    			 queryProvinces();
                    		 }else
                    		 {
                    			 finish();
                    		 }
                    	 }
                    	 
                     
}
