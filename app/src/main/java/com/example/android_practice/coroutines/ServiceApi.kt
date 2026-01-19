package com.example.android_practice.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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

  suspend fun getInfos(userIds: List<String>) : List<UserInfo> {
    val list = userIds.distinct().filter { it.isNotBlank() }  // 去重
    if(list.isEmpty()) return emptyList()

    // 并发控制
    val semaphore = Semaphore(permits = SEMAPHORE_SIZE)

    return coroutineScope {
      val taskList = list.map { userId ->
        async(Dispatchers.IO) {
          semaphore.withPermit {
            getUserInfo(userId)
          }
        }
      }
      taskList.awaitAll().filterNotNull()
    }
  }
}