package com.chuzhi.xzyx.ui.activity.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.databinding.ActivityLogOffXzBinding

/**
 * APP账号注销须知
 */
class LogOffXzActivity : BaseActivity<ActivityLogOffXzBinding,BasePresenter<*>>() {

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.includeActLogOffXz.tvIncludeTitleTitle.text = "APP账号注销须知"
        binding.includeActLogOffXz.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActLogOffXz.ivIncludeTitleBack.setOnClickListener { finish() }
    }

    override fun initData() {
        val settings: WebSettings = binding.webActLogOffXz.settings
        settings.javaScriptEnabled = true
        // 设置支持本地存储
        settings.databaseEnabled = true
        //设置存储模式
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCacheMaxSize((20 * 1024 * 1024).toLong())
        settings.setAppCacheEnabled(true)
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.domStorageEnabled = true
        binding.webActLogOffXz.loadUrl("http://www.chuzhi.cn/logoutNotice/")
    }
}