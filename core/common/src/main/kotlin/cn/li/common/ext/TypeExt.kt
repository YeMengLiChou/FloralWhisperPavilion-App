package cn.li.common.ext


inline fun <reified R> Any?.cast(): R? {
    return this?.let { this as R }
}

inline fun <reified R> Any?.safeCast(): R? {
    return this?.let {
        this as? R
    }
}