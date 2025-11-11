package com.example.android_practice.http.utils

import android.os.Handler
import android.os.Looper
import com.example.android_practice.http.api.WeatherApi
import com.example.android_practice.http.data.WeatherData
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception

object NetUtil {
  const val OKHTTP_URL = "http://v1.yiketianqi.com/free/day"
  const val BASE_URL = "http://v1.yiketianqi.com/"
  const val APP_ID = "97413444"
  const val APP_SECRET = "v9TSIPws"

  // 创建 OkHttpClient 单例
  private val client = OkHttpClient()
  private val mainHandler = Handler(Looper.getMainLooper())

  // retrofit 相关
  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 新增Rxjava支持
    .build()
  private val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)

  fun getWeatherOkhttp(city: String, callback: (WeatherData?) -> Unit){
    // 构建URL
    val url = OKHTTP_URL.toHttpUrlOrNull()?.newBuilder()
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

  fun getWeatherRetrofit(city: String, callback: (WeatherData?) -> Unit) {
    val call = weatherApi.getWeather(
      appid = APP_ID,
      appsecret = APP_SECRET,
      city = city,
      unescape = "1"
    )

    call.enqueue(object : retrofit2.Callback<WeatherData> {
      override fun onResponse(
        call: retrofit2.Call<WeatherData>,
        response: retrofit2.Response<WeatherData>
      ) {
        if (response.isSuccessful) {
          val weatherData = response.body()
          mainHandler.post {
            callback(weatherData)
          }
        } else {
          mainHandler.post {
            callback(null)
          }
        }
      }

      override fun onFailure(
        call: retrofit2.Call<WeatherData>,
        t: Throwable
      ) {
        t.printStackTrace()
        mainHandler.post {
          callback(null)
        }
      }
    })
  }

  fun getWeatherObservable(city: String, callback: (WeatherData?) -> Unit): Disposable{
    return weatherApi.getWeatherObservable(
      appid = APP_ID,
      appsecret = APP_SECRET,
      city = city,
      unescape = "1"
    ).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread()) // 主线程中处理结果（涉及到UI更新）
      .subscribe(
        { weatherData ->
          // onNext: 请求成功，返回数据
          callback(weatherData)
        },
        { throwable ->
          // onError: 请求失败，打印错误并回调 null
          throwable.printStackTrace()
          callback(null)
        }
      )
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