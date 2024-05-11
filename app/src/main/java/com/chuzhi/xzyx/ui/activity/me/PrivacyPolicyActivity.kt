package com.chuzhi.xzyx.ui.activity.me

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebSettings
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityPrivacyPolicyBinding

/**
 * 隐私政策activity
 */
class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding,BasePresenter<*>>(),BaseView {

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }
    override fun initView() {
        binding.includeActPP.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActPP.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActPP.tvIncludeTitleTitle.text = "隐私政策"
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {  
        val settings: WebSettings = binding.webActPP.settings
        settings.javaScriptEnabled = true
        // 设置支持本地存储
        settings.databaseEnabled = true
        //设置存储模式
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCacheMaxSize((20 * 1024 * 1024).toLong())
        settings.setAppCacheEnabled(true)
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.domStorageEnabled = true
        binding.webActPP.loadUrl("http://www.chuzhi.cn/appPrivacyPolicy/")
    }
}