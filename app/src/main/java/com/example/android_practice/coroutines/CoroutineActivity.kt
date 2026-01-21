package com.example.android_practice.coroutines

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.databinding.CoroutineLayoutBinding

class CoroutineActivity : AppCompatActivity() {

  private lateinit var binding : CoroutineLayoutBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = CoroutineLayoutBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }



}