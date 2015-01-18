package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
/**
 * �����ʹ�����������ص�ʡ������
 */
	public synchronized static boolean handleProvinceResponse(String response,CoolWeatherDB coolWeatherDB){
		if(!TextUtils.isEmpty(response)){
			//��������ʽ���������ݿ�
			String[] allProvinces= response.substring(1, response.length()-1).split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p : allProvinces){
					String[] array=p.split(":");
					Province province= new Province();
					String provinceName=new String(array[1]);
					province.setProvinceName(provinceName.substring(1,provinceName.length()-1));
					String provinceCode=new String(array[0]);
					province.setProvinceCode(provinceCode.substring(1, provinceCode.length()-1));
					coolWeatherDB.saveProvince(province);
					
				}
				return true;
			}
		}
		return false;
	}

/**
 * �����ʹ�����������ص��м�����
 */
	public synchronized static boolean handleCityResponse(String response,CoolWeatherDB coolWeatherDB,int provinceId){
		if(!TextUtils.isEmpty(response)){
			//��������ʽ���������ݿ�
			String[] allCities= response.substring(1, response.length()-1).split(",");
			if(allCities!=null && allCities.length>0){
				for(String p : allCities){
					String[] array=p.split(":");
					City city= new City();
					String cityName=new String(array[1]);
					city.setCityName(cityName.substring(1,cityName.length()-1));
					String cityCode=new String(array[0]);
					city.setCityCode(cityCode.substring(1,cityCode.length()-1));
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		return false;
	}

/**
 * �����ʹ�����������ص��ؼ�����
 */
	public synchronized static boolean handleCountyResponse(String response,CoolWeatherDB coolWeatherDB,int cityId){
		if(!TextUtils.isEmpty(response)){
			//��������ʽ���������ݿ�
			String[] allCounties= response.substring(1,response.length()-1).split(",");
			if(allCounties!=null && allCounties.length>0){
				for(String p : allCounties){
					String[] array=p.split(":");
					County county= new County();
				    String countyName=new String(array[1]);
					county.setCountyName(countyName.substring(1,countyName.length()-1));
					String countyCode=new String(array[0]);
					county.setCountyCode(countyCode.substring(1,countyCode.length()-1));
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
	public static void handleWeatherResponse(String response,Context context){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo= jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected",true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2",temp2);
		editor.putString("weather_desp",weatherDesp);
		editor.putString("publish_time",publishTime);
		editor.putString("current_date",sdf.format(new Date()));
		editor.commit();
		Log.d("cityName",cityName);
	}
}