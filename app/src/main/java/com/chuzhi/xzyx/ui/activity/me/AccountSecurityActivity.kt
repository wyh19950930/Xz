 package com.chuzhi.xzyx.ui.activity.me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityAccountSecurityBinding
import com.chuzhi.xzyx.utils.ToastUtil

/**
 * 账号与安全activity
 */
class AccountSecurityActivity : BaseActivity<ActivityAccountSecurityBinding,BasePresenter<*>>(){

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.includeActAccountSecurity.tvIncludeTitleTitle.text = "账号与安全"
        binding.includeActAccountSecurity.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActAccountSecurity.ivIncludeTitleBack.setOnClickListener { finish() }

        binding.llActAccountSecurityPwd.setOnClickListener {
            startActivity(Intent(this,ChangePwdActivity::class.java))
        }
        binding.llActAccountSecurityLogoff.setOnClickListener {
            startActivity(Intent(this,LogOffActivity::class.java))
        }
    }

    override fun initData() {
    }
}