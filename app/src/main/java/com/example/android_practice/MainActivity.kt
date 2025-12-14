package com.example.android_practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.databinding.MainLayoutBinding
import com.example.android_practice.http.HttpActivity
import com.example.android_practice.listener.ListenerActivity

class MainActivity : AppCompatActivity() {

  private lateinit var binding: MainLayoutBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = MainLayoutBinding.inflate(layoutInflater)
    setContentView(binding.root)

    initListener()
  }

  private fun initListener(){
    binding.mainHttp.setOnClickListener {
      startActivity(Intent(this, HttpActivity::class.java ))
    }

    binding.dragView.setOnClickListener {
      startActivity(Intent(this, ListenerActivity::class.java))
    }
  }


}