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
 * ��ѯȫ�����е�ʡ�����Ȳ�ѯ���ݿ⣬���û�У���ȥ��������ѯ
 */
       private void queryProvinces(){
    	   provinceList=coolWeatherDB.loadProvinces();
    	   if(provinceList.size()>0){
    		   dataList.clear();
    		   for(Province province: provinceList){
    			   dataList.add(province.getProvinceName());
    		   }
    		   titleText.setText("�й�");
    		   adapter.notifyDataSetChanged();
    		   listView.setSelection(0);
    		   currentLevel=LEVEL_PROVINCE;
    	   }else{
    		   queryFromServer(null,"province");
    	   }
       }
       /**
        * ��ѯʡ�����е��У����Ȳ�ѯ���ݿ⣬���û�У���ȥ��������ѯ
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
               * ��ѯ�������е��أ����Ȳ�ѯ���ݿ⣬���û�У���ȥ��������ѯ
               
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
   * ���ݴ��ź����ʹӷ������ϲ�ѯʡ��������
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
                    			//ͨ��runOnUiThread�����������߳�
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
                    			//ͨ��runonUiThread�����������߳�
                    			runOnUiThread(new Runnable(){
                    				@Override
                    				public void run(){
                    					closeProgressDialog();
                    					Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
                    				}
                    			});
                    		}
                    	 });
                     }
                    	 /**
                    	  * ��ʾ���ȶԻ���
                    	  */
                    	 private void showProgressDialog() {
                    		 if(progressDialog==null){
                    			 progressDialog=new ProgressDialog(this);
                    			 progressDialog.setMessage("���ڼ���");
                    			 progressDialog.setCanceledOnTouchOutside(false);
                    		 }
                    		 progressDialog.show();
                    	 }
                    	 
                    	 /**
                    	  * �رս��ȶԻ���
                    	  */
                    	 private void closeProgressDialog(){
                    		 if(progressDialog!=null){
                    			 progressDialog.dismiss();
                    		 }
                    	 }
                    	 /**
                    	  * ����back���������ݵ�ʱ�������жϣ���Ӧ�÷������б�ʡ�б�����ֱ���˳�
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
