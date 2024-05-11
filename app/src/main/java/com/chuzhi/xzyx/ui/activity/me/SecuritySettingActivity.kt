package com.chuzhi.xzyx.ui.activity.me

import android.view.View
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivitySecuritySettingBinding

/**
 * 安全设置
 */
class SecuritySettingActivity : BaseActivity<ActivitySecuritySettingBinding,BasePresenter<*>>(),BaseView {

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.includeActSecuritySet.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActSecuritySet.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActSecuritySet.tvIncludeTitleTitle.text = "安全设置"
        binding.onOffActSecuritySetBd.setDefOff(true)
    }

    override fun initData() {
    }
}