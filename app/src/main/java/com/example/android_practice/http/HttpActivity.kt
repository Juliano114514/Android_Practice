package com.example.android_practice.http

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.R
import com.example.android_practice.http.utils.NetUtil
import com.example.android_practice.http.utils.NetUtil.getWeatherOkhttp

class HttpActivity : AppCompatActivity() {

  val mOkhttpBtn: Button by lazy { findViewById(R.id.http_okhttp) }
  val mRetrofitBtn: Button by lazy { findViewById(R.id.http_retrofit) }
  val mReqInput: EditText by lazy { findViewById(R.id.http_input) }
  val mResult: TextView by lazy { findViewById(R.id.http_result) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.http_layout)

    initListener()
  }

  private fun initListener(){
    mOkhttpBtn.setOnClickListener {
      val city = mReqInput.text.toString().trim()

      // 验证输入
      if (city.isEmpty()) {
        Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      mResult.text = "正在请求数据..."

      getWeatherOkhttp(city) { weatherData ->
        if (weatherData != null) {
          mResult.text = NetUtil.setResult(weatherData)
        } else {
          mResult.text = "请求失败，请检查网络连接或城市名称"
        }
      }
    }

    mRetrofitBtn.setOnClickListener {

    }
  }




}