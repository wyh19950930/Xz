package com.chuzhi.xzyx.utils.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

interface ICookieStore {

    /**保存对应url 的cookie*/
    fun saveCookie(url: HttpUrl?, cookies: MutableList<Cookie>?)

    /**获取对应url的所有cookie*/
    fun loadCookie(httpUrl: HttpUrl): List<Cookie>

    /**加载所有cookie*/
    fun loadAllCookie(): List<Cookie>

    /**移除多有cookie*/
    fun removeAllCookie(): Boolean

    /**移除对应url的cookie*/
    fun removeCookie(httpUrl: HttpUrl): Boolean
}