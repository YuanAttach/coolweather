package com.coolweather.app.activity;

import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp2Text;
	private TextView temp1Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.weather_layout);
	    weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
	    cityNameText=(TextView) findViewById(R.id.city_name);
	    publishText=(TextView) findViewById(R.id.publish_text);
	    weatherDespText=(TextView) findViewById(R.id.weather_desp);
	    temp1Text= (TextView) findViewById(R.id.temp1);
	    temp2Text=(TextView) findViewById(R.id.temp2);
	    currentDateText= (TextView) findViewById(R.id.current_date);
	    switchCity=(Button) findViewById(R.id.switch_city);
	    refreshWeather =(Button) findViewById(R.id.refresh_weather);
	    String cityCode=getIntent().getStringExtra("city_code");
	    String provinceCode=getIntent().getStringExtra("province_code");
 	    if(!TextUtils.isEmpty(cityCode)){
	    	//有市级代码就去查询天气
	    	publishText.setText("同步中。。。");
	    	weatherInfoLayout.setVisibility(View.INVISIBLE);
	    	cityNameText.setVisibility(View.INVISIBLE);
	    	queryWeatherInfo(cityCode,provinceCode);
	    }else{
	    	//没有县级代码的时候就直接显示本地天气
	    	showWeather();
	    }
	    switchCity.setOnClickListener(this);
	    refreshWeather.setOnClickListener(this);
	    
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity",true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中。。。。。。");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryFromServer("http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html");
			}
			break;
			default:
				break;}
		}
		
	/**
	 * 查询市级代号所对应的天气信息。
	 */
	private void queryWeatherInfo(String cityCode,String provinceCode){
		int i=Integer.parseInt(provinceCode);
		String address;
		if( i<10105)
		{ //因为直辖市的接口查询时中间的数字是 中国101+省级（01 北京 02 上海 03 天津 04 重庆）+0100（总共9位）
			address ="http://www.weather.com.cn/data/cityinfo/"+provinceCode+"0100.html";}
		else{
			//非直辖市借口查询时中间数字是 中国101+省级+市级+001（总共9位）
			address="http://www.weather.com.cn/data/cityinfo/"+provinceCode+cityCode+"01.html";}
//		Log.d("city_code",cityCode);
		queryFromServer(address);
	}
	
	private void queryFromServer(final String address){
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener(){
			@Override
		public void onFinish(final String response){
				Utility.handleWeatherResponse(response, WeatherActivity.this );
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						showWeather();
					}
				});
				
			}
		public void onError(Exception e){
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					publishText.setText("同步失败");
				}
			});
		}
		});
	}
	
	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather(){
		final SharedPreferences perfs = PreferenceManager.getDefaultSharedPreferences(this);
//		Log.d("city_name",perfs.getString("city_name", ""));
//		runOnUiThread(new Runnable(){
//		@Override
//		public void run(){
		cityNameText.setText(perfs.getString("city_name", ""));
		temp1Text.setText(perfs.getString("temp1", ""));
		temp2Text.setText(perfs.getString("temp2", ""));
		weatherDespText.setText(perfs.getString("weather_desp", ""));
		publishText.setText("今天"+perfs.getString("publish_time", "")+"发布");
		currentDateText.setText(perfs.getString("current_date", ""));
		cityNameText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
//		}
//		});
		
	}
}
