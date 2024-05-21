package cn.li.feature.login.state

internal fun validateUsername(username: String): String? {
    if (username.isEmpty()) {
        return "账号/手机号不能为空!"
    }
    if (username.length >= 20) {
        return "账号/手机号过长!"
    }
    return null
}

internal fun validatePassword(password: String): String? {
    if (password.isEmpty()) {
        return "密码不能为空!"
    }
    if (password.length >= 30) {
        return "密码过长!"
    }
    return null
}

internal fun validateSecondPassword(password: String, secondPassword: String): String? {
    if (secondPassword.isEmpty()) {
        return "重复密码不能为空!"
    }
    if (secondPassword.length >= 30) {
        return "密码过长!"
    }
    if (password != secondPassword) {
        return "两次密码不相同!"
    }
    return null
}