package com.example.android_practice.http.api

import com.example.android_practice.http.data.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
  @GET("free/day")
  fun getWeather(
    @Query("appid") appId: String,
    @Query("appsecret") appSecret: String,
    @Query("city") city: String
  ): Call<WeatherData>
}