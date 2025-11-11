package com.example.android_practice.http.utils

import android.os.Handler
import android.os.Looper
import com.example.android_practice.http.data.WeatherData
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception

object NetUtil {
  const val BASE_URL = "http://v1.yiketianqi.com/free/day"
  const val APP_ID = "97413444"
  const val APP_SECRET = "v9TSIPws"

  // 创建 OkHttpClient 单例
  private val client = OkHttpClient()
  private val mainHandler = Handler(Looper.getMainLooper())

  fun getWeatherOkhttp(city: String, callback: (WeatherData?) -> Unit){
    // 构建URL
    val url = BASE_URL.toHttpUrlOrNull()?.newBuilder()
      ?.addQueryParameter("appid", APP_ID)
      ?.addQueryParameter("appsecret", APP_SECRET)
      ?.addQueryParameter("city", city)
      ?.addQueryParameter("unescape", "1")
      ?.build()

    if(url == null){
      callback(null)
      return
    }

    val request = Request.Builder()
      .url(url)
      .get()  // GET请求
      .build()

    client.newCall(request).enqueue(object : Callback{
      override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        callback(null)
      }

      override fun onResponse(call: Call, response: Response) {
        if(response.isSuccessful){
          val json = response.body?.string()
          if(json != null){
            val data = decodeJson(json)
            mainHandler.post {
              callback(data)
            }
          } else {
            mainHandler.post {
              callback(null)
            }
          }
        } else {
          mainHandler.post {
            callback(null)
          }
        }
      }
    })
  }



  fun decodeJson(json: String): WeatherData? {
    return try{
      val gson = Gson()
      gson.fromJson<WeatherData>(json, WeatherData::class.java)
    } catch (e: Exception){
      e.printStackTrace()
      null
    }
  }

  fun setResult(data: WeatherData?) : String{
    if(data == null) return "无数据"
    val result = buildString {
      appendLine("城市: ${data.city}")
      appendLine("城市ID: ${data.cityid}")
      appendLine("日期: ${data.date} ${data.week}")
      appendLine("更新时间: ${data.update_time}")
      appendLine("")
      appendLine("天气: ${data.wea}")
      appendLine("天气标识: ${data.wea_img}")
      appendLine("")
      appendLine("温度信息:")
      appendLine("实况温度: ${data.tem}°C")
      appendLine("白天温度: ${data.tem_day}°C")
      appendLine("夜间温度: ${data.tem_night}°C")
      appendLine("")
      appendLine("风力信息:")
      appendLine("风向: ${data.win}")
      appendLine("风力: ${data.win_speed}")
      appendLine("风速: ${data.win_meter}")
      appendLine("")
      appendLine("其他信息:")
      appendLine("空气质量: ${data.air}")
      appendLine("气压: ${data.pressure}hPa")
      appendLine("湿度: ${data.humidity}")
      appendLine("")
      appendLine("今日实时请求次数: ${data.nums}")
    }
    return result
  }
}