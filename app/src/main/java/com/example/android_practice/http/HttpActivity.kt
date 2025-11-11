package com.example.android_practice.http

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.R
import com.example.android_practice.http.utils.NetUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

class HttpActivity : AppCompatActivity() {

  val mOkhttpBtn: Button by lazy { findViewById(R.id.http_okhttp) }
  val mRetrofitBtn: Button by lazy { findViewById(R.id.http_retrofit) }
  val mRxJavaBtn: Button by lazy { findViewById(R.id.http_observable) }
  val mReqInput: EditText by lazy { findViewById(R.id.http_input) }
  val mResult: TextView by lazy { findViewById(R.id.http_result) }

  // CompositeDisposable 用于管理所有订阅，防止内存泄漏
  private val compositeDisposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.http_layout)
    initListener()
  }

  private fun initListener(){
    mOkhttpBtn.setOnClickListener {
      val city = getCity()
      if(city.isEmpty())return@setOnClickListener

      NetUtil.getWeatherOkhttp(city) { weatherData ->
        if (weatherData != null) {
          mResult.text = NetUtil.setResult(weatherData)
        } else {
          mResult.text = "请求失败，请检查网络连接或城市名称"
        }
      }
    }

    mRetrofitBtn.setOnClickListener {
      val city = getCity()
      if(city.isEmpty())return@setOnClickListener

      // 调用 Retrofit API（通过 NetUtil）
      NetUtil.getWeatherRetrofit(city) { weatherData ->
        if (weatherData != null) {
          mResult.text = NetUtil.setResult(weatherData)
        } else {
          mResult.text = "请求失败，请检查网络连接或城市名称"
        }
      }
    }

    mRxJavaBtn.setOnClickListener {
      val city = getCity()
      if(city.isEmpty())return@setOnClickListener

      val disposable = NetUtil.getWeatherObservable(city) { data ->
        if (data != null) {
          mResult.text = NetUtil.setResult(data)
        } else {
          mResult.text = "请求失败，请检查网络连接或城市名称"
        }
      }
      compositeDisposable.add(disposable)
    }
  }

  private fun getCity() : String{
    val city = mReqInput.text.toString().trim()
    // 验证输入
    if (city.isEmpty()) {
      Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show()
      return ""
    }
    mResult.text = "正在请求数据..."
    return city
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }

}