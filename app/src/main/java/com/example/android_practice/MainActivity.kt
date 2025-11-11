package com.example.android_practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.http.HttpActivity

class MainActivity : AppCompatActivity() {

  private val httpBtn: Button by lazy { findViewById(R.id.main_http) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_layout)

    initListener()
  }

  private fun initListener(){
    httpBtn.setOnClickListener { startActivity(Intent(this, HttpActivity::class.java )) }
  }

}