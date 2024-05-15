package cn.li.common.network

import javax.inject.Qualifier

/**
 * 协程调度器依赖项
 * */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: FwpDispatcher)

/**
 * 协程调度器类型
 * */
enum class FwpDispatcher {
    DEFAULT,
    IO
}