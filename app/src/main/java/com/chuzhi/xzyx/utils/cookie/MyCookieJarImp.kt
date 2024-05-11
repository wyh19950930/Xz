package com.chuzhi.xzyx.utils.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class MyCookieJarImp( val cookieStore: ICookieStore) : CookieJar {

    override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
        cookieStore.saveCookie(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl?): List<Cookie> {
        return if (url != null) {
            cookieStore.loadCookie(url)
        } else {
            emptyList()
        }
    }

}