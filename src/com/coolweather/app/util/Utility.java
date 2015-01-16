package com.coolweather.app.util;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.text.TextUtils;

public class Utility {
/**
 * 解析和处理服务器返回的省级数据
 */
	public synchronized static boolean handleProvinceResponse(String response,CoolWeatherDB coolWeatherDB){
		if(!TextUtils.isEmpty(response)){
			//解析出格式并存入数据库
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
 * 解析和处理服务器返回的市级数据
 */
	public synchronized static boolean handleCityResponse(String response,CoolWeatherDB coolWeatherDB,int provinceId){
		if(!TextUtils.isEmpty(response)){
			//解析出格式并存入数据库
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
 * 解析和处理服务器返回的县级数据
 */
	public synchronized static boolean handleCountyResponse(String response,CoolWeatherDB coolWeatherDB,int cityId){
		if(!TextUtils.isEmpty(response)){
			//解析出格式并存入数据库
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
}