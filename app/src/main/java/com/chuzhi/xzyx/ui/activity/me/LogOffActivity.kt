package com.chuzhi.xzyx.ui.activity.me

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.databinding.ActivityLogOffBinding
import com.chuzhi.xzyx.utils.SpUtils

/**
 * 账号注销首页
 */
class LogOffActivity : BaseActivity<ActivityLogOffBinding,BasePresenter<*>>() {

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.includeActLogOff.tvIncludeTitleTitle.text = "注销账号"
        binding.includeActLogOff.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActLogOff.ivIncludeTitleBack.setOnClickListener { finish() }
        val phone = SpUtils.getSharedStringData(this, "Phone")
        binding.tvActLogOffUser.text = "将"+phone+"所绑定的账号注销"
        binding.tvActLogOffXz.setOnClickListener {
            startActivity(Intent(this,LogOffXzActivity::class.java))
        }
        binding.btnActLogOffNext.setOnClickListener {
            startActivity(Intent(this,LogOffCodeActivity::class.java))
        }
    }

    override fun initData() {
    }
}