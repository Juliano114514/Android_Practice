package com.example.android_practice.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.android_practice.databinding.CoroutineLayoutBinding

class CoroutineActivity : AppCompatActivity() {

  private lateinit var binding : CoroutineLayoutBinding
  private lateinit var viewModel: CoroutineViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = CoroutineLayoutBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initViewModel()
    initListener()
  }

  private fun initViewModel(){
    viewModel = ViewModelProvider(this)[CoroutineViewModel::class.java]
    viewModel.queryResult.observe(this) { result ->
      binding.tvQueryResult.text = result
    }

    viewModel.isQuerying.observe(this) { isQuerying ->
      binding.btnStartQuery.isEnabled = !isQuerying
      binding.etQueryCount.isEnabled = !isQuerying
      if (isQuerying) {
        binding.btnStartQuery.text = "查询中..."
      } else {
        binding.btnStartQuery.text = "开始查询"
      }
    }

    viewModel.progress.observe(this) { (total, completed) ->
      binding.progress.max = total
      binding.progress.progress = completed

      val progressText = if (total > 0) {
        "进度$completed/$total"
      } else {
        "进度0/0"
      }
      binding.progressText.text = progressText
    }
  }

  private fun initListener(){
    binding.btnStartQuery.setOnClickListener {
      handleStartQuery()
    }
  }

  private fun handleStartQuery() {
    // 1. 获取输入框内容
    val inputText = binding.etQueryCount.text.toString().trim()

    // 2. 输入校验
    if (inputText.isEmpty()) {
      binding.tvQueryResult.text = "请输入有效的查询数量！"
      return
    }

    // 3. 转换为数字并校验
    val count = try {
      inputText.toInt()
    } catch (e: NumberFormatException) {
      binding.tvQueryResult.text = "请输入有效的数字！"
      return
    }

//    // 4. 校验数量范围（防止输入过大导致性能问题）
//    if (count <= 0 || count > 1000) {
//      binding.tvQueryResult.text = "查询数量需在1-1000之间！"
//      return
//    }

    // 5. 清空输入框（可选），触发查询
    binding.etQueryCount.setText("")
    viewModel.doQuery(count)

  }
}