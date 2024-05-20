package cn.li.feature.login.state

internal data class ErrorTips(
    val hasError: Boolean,
    val tips: String
)

internal fun validateUsername(username: String): ErrorTips? {
    if (username.isEmpty()) {
        return ErrorTips(true, "账号/手机号不能为空!")
    }
    if (username.length >= 20) {
        return ErrorTips(true, "账号/手机号过长!")
    }
    return null
}

internal fun validatePassword(password: String): ErrorTips? {
    if (password.isEmpty()) {
        return ErrorTips(true, "密码不能为空!")
    }
    if (password.length >= 30) {
        return ErrorTips(true, "密码过长!")
    }
    return null
}

internal fun validateSecondPassword(password: String, secondPassword: String): ErrorTips? {
    if (secondPassword.isEmpty()) {
        return ErrorTips(true, "重复密码不能为空!")
    }
    if (secondPassword.length >= 30) {
        return ErrorTips(true, "密码过长!")
    }
    if (password != secondPassword) {
        return ErrorTips(true, "两次密码不相同!")
    }
    return null
}