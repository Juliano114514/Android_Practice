package com.example.android_practice.http.api

import com.example.android_practice.http.data.WeatherData
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
  @GET("free/day")
  fun getWeather(
    @Query("appid") appid: String,
    @Query("appsecret") appsecret: String,
    @Query("city") city: String,
    @Query("unescape") unescape: String = "1"
  ): Call<WeatherData>


  // 新增Rxjava订阅者功能
  @GET("free/day")
  fun getWeatherObservable(
    @Query("appid") appid: String,
    @Query("appsecret") appsecret: String,
    @Query("city") city: String,
    @Query("unescape") unescape: String = "1"
  ): Observable<WeatherData>
}