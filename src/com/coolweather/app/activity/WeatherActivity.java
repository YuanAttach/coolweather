package com.coolweather.app.activity;

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

public class WeatherActivity extends Activity{
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
//	    switchCity=(Button) findViewById(R.id.switch_city);
//	    refreshWeather =(Button) findViewById(R.id.refresh_weather);
	    String cityCode=getIntent().getStringExtra("city_code");
	    String provinceCode=getIntent().getStringExtra("province_code");
 	    if(!TextUtils.isEmpty(cityCode)){
	    	//���м������ȥ��ѯ����
	    	publishText.setText("ͬ���С�����");
	    	weatherInfoLayout.setVisibility(View.INVISIBLE);
	    	cityNameText.setVisibility(View.INVISIBLE);
	    	queryWeatherInfo(cityCode,provinceCode);
	    }else{
	    	//û���ؼ������ʱ���ֱ����ʾ��������
	    	showWeather();
	    }
//	    switchCity.setOnClickListener(this);
//	    refreshWeather.setOnClickListener(this);
	    
	}
	
/**	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity",true);
			startActivity(intent);
			finish();
			break;
		case R.id.refreshWeather:
			publishText.setText("ͬ���С�����������");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
			default:
				break;}
		}
		*/
	/**
	 * ��ѯ�м���������Ӧ��������Ϣ��
	 */
	private void queryWeatherInfo(String cityCode,String provinceCode){
		int i=Integer.parseInt(provinceCode);
		String address;
		if( i<10105)
		{ //��ΪֱϽ�еĽӿڲ�ѯʱ�м�������� �й�101+ʡ����01 ���� 02 �Ϻ� 03 ��� 04 ���죩+0100���ܹ�9λ��
			address ="http://www.weather.com.cn/data/cityinfo/"+provinceCode+"0100.html";}
		else{
			//��ֱϽ�н�ڲ�ѯʱ�м������� �й�101+ʡ��+�м�+001���ܹ�9λ��
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
					publishText.setText("ͬ��ʧ��");
				}
			});
		}
		});
	}
	
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather(){
		final SharedPreferences perfs = PreferenceManager.getDefaultSharedPreferences(this);
//		Log.d("city_name",perfs.getString("city_name", ""));
		runOnUiThread(new Runnable(){
		@Override
		public void run(){
		cityNameText.setText(perfs.getString("city_name", ""));
		temp1Text.setText(perfs.getString("temp1", ""));
		temp2Text.setText(perfs.getString("temp2", ""));
		weatherDespText.setText(perfs.getString("weather_desp", ""));
		publishText.setText("����"+perfs.getString("publish_time", "")+"����");
		currentDateText.setText(perfs.getString("current_date", ""));
		cityNameText.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.VISIBLE);}
		});
		
	}
}
