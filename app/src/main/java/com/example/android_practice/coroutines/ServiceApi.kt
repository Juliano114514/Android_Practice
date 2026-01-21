package com.example.android_practice.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object ServiceApi {

  private const val SEMAPHORE_SIZE = 20

  fun getRadomId(): String{
    return Random.nextInt().toString()
  }

  private suspend fun getUserInfo(userId: String): UserInfo {
    val sleepTime = Random.nextLong(2000L)
    delay(sleepTime)
    return UserInfo(
      userId = userId,
      userPoint = Random.nextInt(),
      userName = "user$userId"
    )
  }

  suspend fun getInfos(
    userIds: List<String>,
    onProgress: ((Int, Int) -> Unit)? = null
  ) : List<UserInfo> {
    val list = userIds.distinct().filter { it.isNotBlank() }  // 去重
    if(list.isEmpty()) return emptyList()

    // 并发控制
    val semaphore = Semaphore(permits = SEMAPHORE_SIZE)
    val total = list.size
    val completedCount = AtomicInteger(0)

    return coroutineScope {
      val taskList = list.map { userId ->
        async(Dispatchers.IO) {
          semaphore.withPermit {
            val userInfo = getUserInfo(userId)
            val currentCompleted = completedCount.incrementAndGet()  // 计数 + 1
            onProgress?.invoke(currentCompleted, total)
            userInfo
          }
        }
      }
      taskList.awaitAll().filterNotNull()
    }
  }

  fun displayInfo(info : UserInfo) : String {
    val userId = info.userId ?: "无效ID"
    val userName = info.userName ?: "无效用户"

    return buildString {
      appendLine("用户id = $userId")
      appendLine("用户名 = $userName")
      appendLine("用户积分 = ${info.userPoint}")
    }
  }
}