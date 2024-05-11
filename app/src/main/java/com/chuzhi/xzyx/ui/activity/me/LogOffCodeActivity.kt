package com.chuzhi.xzyx.ui.activity.me

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityLogOffCodeBinding
import com.chuzhi.xzyx.ui.activity.login.LoginActivity
import com.chuzhi.xzyx.ui.presenter.LogOffCodePresenter
import com.chuzhi.xzyx.ui.view.LogOffCodeView
import com.chuzhi.xzyx.utils.ActivityCollectorUtil
import com.chuzhi.xzyx.utils.RegexUtils
import com.chuzhi.xzyx.utils.SpUtils
import com.chuzhi.xzyx.utils.ToastUtil
import org.greenrobot.eventbus.EventBus

/**
 * 账号注销发送验证码页面
 */
class LogOffCodeActivity : BaseActivity<ActivityLogOffCodeBinding,LogOffCodePresenter>(),LogOffCodeView {
    private var timer: CountDownTimer? = null
    override fun createPresenter(): LogOffCodePresenter {
        return LogOffCodePresenter(this)
    }
    override fun initView() {
        binding.includeActLogOffCode.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActLogOffCode.tvIncludeTitleTitle.text = "注销账号"
        binding.includeActLogOffCode.ivIncludeTitleBack.setOnClickListener { finish() }

        val phone = SpUtils.getSharedStringData(this, "Phone")
        binding.etActLogOffCodePhone.setText(phone)

        //获取验证码按钮
        binding.btnActLogOffCodeCode.setOnClickListener {
            if (binding.etActLogOffCodePhone.text.toString() == "") {
                ToastUtil.showShort(this, "请输入手机号码")
                return@setOnClickListener
            } else {
                if (RegexUtils.isMobileExact(binding.etActLogOffCodePhone.text.toString())) {
                    presenter.smsCaptcha(binding.etActLogOffCodePhone.text.toString())
                } else {
                    ToastUtil.showShort(this, "请输入正确的手机号！")
                }
            }
        }
        //注销按钮
        binding.btnActLogOffCodeNext.setOnClickListener {
            when {
                binding.etActLogOffCodePhone.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请输入手机号码")
                    return@setOnClickListener
                }
                binding.etActLogOffCodeCode.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请输入验证码")
                    return@setOnClickListener
                }
                else -> {
                    presenter.logOffPhone(
                        binding.etActLogOffCodePhone.text.toString().trim(),
                        binding.etActLogOffCodeCode.text.toString().trim()
                    )
                }
            }
        }

    }
    override fun initData() {
    }

    override fun codeSuccess(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, "验证码已发送")
        countDownTimer()
    }

    override fun logOffPhone(msg: BaseModel<String>?) {
        ToastUtil.showShort(this,msg!!.msg)
        SpUtils.setSharedStringData(this,"Token","")
        SpUtils.setSharedStringData(this,"Username","")
        SpUtils.setSharedStringData(this,"HomePageBinding","")
        SpUtils.setSharedStringData(this,"Avatar","")
        EventBus.getDefault().postSticky("退出登录")
        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        //跳转登录页面
        var intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }
    //倒计时的方式一
    private fun countDownTimer() {
        var num = 120
        binding.btnActLogOffCodeCode.isEnabled = false
        binding.btnActLogOffCodeCode.setBackgroundResource(R.drawable.btn_eeeeee_round_5_background)
        binding.btnActLogOffCodeCode.setTextColor(Color.parseColor("#999999"))
        timer = object : CountDownTimer(num * 1000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.btnActLogOffCodeCode.text =
                    (millisUntilFinished / 1000).toInt().toString() + "秒后再发送"
            }

            override fun onFinish() {
                timer = null
                binding.btnActLogOffCodeCode.isEnabled = true
                binding.btnActLogOffCodeCode.text = "发送验证码"
                binding.btnActLogOffCodeCode.setTextColor(Color.parseColor("#ffffff"))
                binding.btnActLogOffCodeCode.setBackgroundResource(R.drawable.btn_blue_round_5_background)
            }
        }

        timer?.start()
    }
    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this,msg)
    }

}