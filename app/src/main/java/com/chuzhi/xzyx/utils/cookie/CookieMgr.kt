package com.chuzhi.xzyx.utils.cookie

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by HeXiangyuan on 18-3-21.
 * Description:这个cookie进行本地化保存　保存在SP opt_cookie_sp中
 *
 * sp中储存方式 是 key :url.host   value :,opr_cookie_cookie1,opt_cookie_cookie2,opt_cookie_cookie3
 */
object CookieMgr {
    const val COOKIE_SP = "OPT_COOKIE_SP"
    private const val COOKIE_PREFIX = ",opt_cookie_"

    fun mapCookiesToString(httpUrl: HttpUrl, cookies: List<Cookie>): String {
        return if (cookies.isEmpty()) {
            ""
        } else {
            val sb = StringBuilder()
            cookies.forEach {
                sb.append(CookieMgr.COOKIE_PREFIX)
                sb.append(SerializableCookie.encodeCookie(httpUrl.host(), it))
            }
            sb.toString()
        }
    }

    fun mapStringToCookies(cookieString: String): MutableList<Cookie> {
        return if (cookieString.isEmpty()) {
            mutableListOf()
        } else {
            val cookies = arrayListOf<Cookie>()
            cookieString
                .split(CookieMgr.COOKIE_PREFIX)
                .forEach {
                    if (!it.isEmpty()) {
                        val cookie = SerializableCookie.decodeCookie(it)
                        Log.e("cookieLog", "name == ${cookie.name()}  domain == ${cookie.domain()}  cookieString == $cookie ")
                        cookies.add(cookie)
                    }
                }
            return cookies
        }
    }

    /**以Cookie的name 和domain 作为判断依据，
     * 如果是 一致的则通过map 的key 唯一性进行替换
     * 也就是说如果两个cookie　对比name 和domain
     * */
    fun addToCookies(originCookies: MutableList<Cookie>, cookies: MutableList<Cookie>?): MutableList<Cookie> {
        if (cookies == null || cookies.size == 0) {
            return originCookies
        } else {
            val cookieMap = ConcurrentHashMap<String, Cookie>()
            originCookies.forEach {
                cookieMap.put("${it.name()}@${it.domain()}", it)
            }
            cookies.forEach {
                cookieMap.put("${it.name()}@${it.domain()}", it)
            }
            val currentCookie = mutableListOf<Cookie>()
            cookieMap.forEach {
                currentCookie.add(it.value)
            }
            return currentCookie
        }
    }
}

class LocalCookieStore(val context: Application) : ICookieStore {
    private val cookieSp: SharedPreferences = context.getSharedPreferences(CookieMgr.COOKIE_SP, MODE_PRIVATE)
    private val cookiesMap: ConcurrentHashMap<String, MutableList<Cookie>> = ConcurrentHashMap()

    init {
        //初始化之前先将Cookie全部从Sp中读取到内存中
        cookieSp.all.forEach {
            if (it.value != null) {
                cookiesMap[it.key] = CookieMgr.mapStringToCookies(it.value.toString())
            }
        }
    }

    @Synchronized
    override fun saveCookie(url: HttpUrl?, cookies: MutableList<Cookie>?) {
        if (cookies != null && url != null) {
            //先存到内存在缓存
            val newCookie = CookieMgr.addToCookies(cookiesMap[url.host()] ?: arrayListOf(), cookies)
            cookiesMap[url.host()] = newCookie

            val prefsWriter = cookieSp.edit()
            prefsWriter.putString(url.host(), CookieMgr.mapCookiesToString(url, newCookie))
            prefsWriter.apply()
        }
    }

    override fun loadCookie(httpUrl: HttpUrl): List<Cookie> {
        if (cookiesMap.containsKey(httpUrl.host())) {
            return cookiesMap[httpUrl.host()] ?: emptyList()
        }
        return emptyList()
    }

    override fun loadAllCookie(): List<Cookie> {
        val cookies = arrayListOf<Cookie>()
        cookiesMap.forEach {
            it.value.forEach { cookie ->
                cookies.add(cookie)
            }
        }
        return cookies
    }

    override fun removeAllCookie(): Boolean {
        cookiesMap.clear()
        cookieSp.edit().clear().apply()
        return true
    }

    override fun removeCookie(httpUrl: HttpUrl): Boolean {
        cookiesMap.remove(httpUrl.host())
        cookieSp.edit().remove(httpUrl.host()).apply()
        return true
    }

}