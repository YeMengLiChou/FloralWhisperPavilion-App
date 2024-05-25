package cn.li.common.ext

@OptIn(ExperimentalStdlibApi::class)
/**
 * 用于显示该实例的唯一标识符（并非实际的内存地址）
 * */
fun Any?.debugId(): String {
    return System.identityHashCode(this).toHexString()
}