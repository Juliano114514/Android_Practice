package com.example.android_practice.listener

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android_practice.databinding.DragListenerViewBinding

class ListenerActivity : AppCompatActivity() {

  private lateinit var binding: DragListenerViewBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DragListenerViewBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }

}