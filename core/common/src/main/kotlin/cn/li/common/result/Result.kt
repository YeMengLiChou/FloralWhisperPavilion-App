package cn.li.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking


/**
 * 网络请求的自定义结果类，分为三种结果：
 * 1. [Success] 成功响应
 * 2. [Error] 失败响应
 * 3. [Loading] 等待响应
 * */
sealed interface Result<out T> {

    /**
     * 成功响应的网络请求结果类
     * */
    data class Success<T>(val data: T) : Result<T>

    /**
     * 失败响应的网络请求结果类
     * */
    data class Error(val exception: Throwable) : Result<Nothing>

    /**
     * 等待响应的网络请求结果类
     * */
    data object Loading : Result<Nothing>
}

/**
 * 网络请求 Flow
 * 1. 在开始时 [onStart] 返回 [Result.Loading]，表示等待
 * 2. 在有异常时 [catch] 返回 [Result.Error]，表示请求失败
 * 3. 在获得响应时进行转换 [map]，返回 [Result.Success]，表示请求成功
 *
 * @sample sampleFlowUsage
 * */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> {
    Result.Success(it)
}.onStart {
    Result.Loading
}.catch {
    Result.Error(it)
}


/**
 * 例子代码
 * */
internal suspend fun sampleFlowUsage() = runBlocking {
    flow {
        // 模拟网络请求的返回结果
        emit(10)
        // 模拟请求出错
        throw Exception()
    }
        .asResult()
        .collect {
            when(it) {
                is Result.Success -> {
                    // 处理成功响应的结果
                    it.data
                }
                is Result.Loading -> {
                    // 加载中....
                }
                is Result.Error -> {
                    // 处理失败响应的异常
                    it.exception
                }
            }
        }
}