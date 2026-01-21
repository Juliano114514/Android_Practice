package com.example.android_practice.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * @Author: JULIANO
 * @CreateDate: 2026/1/19 21:14
 * @Description:
 */
class CoroutineViewModel : ViewModel() {
  private val _queryResult = MutableLiveData("")
  val queryResult: LiveData<String> = _queryResult

  private val _isQuerying = MutableLiveData(false)
  val isQuerying: LiveData<Boolean> = _isQuerying

  private val _progress = MutableLiveData(ProgressData(0,0))
  val progress: LiveData<ProgressData> = _progress

  fun doQuery(count : Int){
    if (_isQuerying.value == true) return
    val userIdList = List(count) { ServiceApi.getRadomId() }
    viewModelScope.launch {
      _isQuerying.postValue(true)
      _queryResult.postValue("正在查询...")
      try{
        val userInfoList = ServiceApi.getInfos(userIdList) { completed, total ->
          _progress.postValue(ProgressData(total = total, completed = completed))
        }
        updateResult(userInfoList)
      } catch (e: Exception) {
        // 异常处理：捕获并展示错误信息
        _queryResult.postValue("查询失败：${e.message ?: "未知错误"}")
      } finally {
        _isQuerying.postValue(false)
      }
    }
  }

  private fun updateResult(infoList : List<UserInfo>){
    if (infoList.isEmpty()) {
      _queryResult.postValue("查询完成：无有效用户信息")
      return
    }

    val result = buildString {
      appendLine("查询完成！共${infoList.size}条结果：")
      appendLine("------------------------")
      infoList.forEach { info ->
        append(ServiceApi.displayInfo(info))
        appendLine("------------------------")
      }
    }
    _queryResult.postValue(result)
  }

  fun clearResult() {
    _queryResult.value = ""
  }
}

data class ProgressData(
  var total: Int = 0,
  var completed: Int = 0,
)