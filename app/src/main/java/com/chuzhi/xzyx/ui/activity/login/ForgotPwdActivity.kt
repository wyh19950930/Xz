package com.chuzhi.xzyx.ui.activity.login

import android.graphics.Color
import android.os.CountDownTimer
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityForgotpwdBinding
import com.chuzhi.xzyx.ui.presenter.ForgotPwdPresenter
import com.chuzhi.xzyx.ui.view.ForgotPwdView
import com.chuzhi.xzyx.utils.RegexUtils
import com.chuzhi.xzyx.utils.ToastUtil

/**
 * 忘记密码activity
 */
class ForgotPwdActivity : BaseActivity<ActivityForgotpwdBinding,ForgotPwdPresenter>(),ForgotPwdView {

    private var timer: CountDownTimer? = null
    override fun createPresenter(): ForgotPwdPresenter {
        return ForgotPwdPresenter(this)
    }

    override fun initView() {
        binding.includeActForgotPwd.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActForgotPwd.tvIncludeTitleTitle.text = "忘记密码"
        binding.includeActForgotPwd.ivIncludeTitleBack.setOnClickListener { finish() }

        //获取验证码按钮
        binding.btnActForgotPwdAdminCode.setOnClickListener {
            if (binding.etActForgotPwdAdminPhone.text.toString() == "") {
                ToastUtil.showShort(this, "请输入手机号码")
                return@setOnClickListener
            } else {
                if (RegexUtils.isMobileExact(binding.etActForgotPwdAdminPhone.text.toString())) {
                    presenter.smsCaptcha(binding.etActForgotPwdAdminPhone.text.toString())
                } else {
                    ToastUtil.showShort(this, "请输入正确的手机号！")
                }

            }
        }
        // 不允许输入汉字
        val digitsKeyListener: DigitsKeyListener = object : DigitsKeyListener() {
            override fun getInputType(): Int {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            override fun getAcceptedChars(): CharArray {
                val a =
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"//!”#$%&’()*+,-./:;<=>?@[\]^_`{|}~
                return a.toCharArray()
            }
        }
        // 设置允许的字符setKeyListener
        binding.etActForgotPwdAdminPwd.keyListener = digitsKeyListener
        binding.etActForgotPwdAdminAgainPwd.keyListener = digitsKeyListener
        binding.cbActForgotPwdAdminPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActForgotPwdAdminPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActForgotPwdAdminPwd.setSelection(binding.etActForgotPwdAdminPwd.text.length)
            binding.etActForgotPwdAdminPwd.keyListener = digitsKeyListener
        }
        binding.cbActForgotPwdAdminAgainPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActForgotPwdAdminAgainPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActForgotPwdAdminAgainPwd.setSelection(binding.etActForgotPwdAdminAgainPwd.text.length)
            binding.etActForgotPwdAdminAgainPwd.keyListener = digitsKeyListener
        }
        //注册按钮
        binding.btnActForgotPwdForgotPwd.setOnClickListener {
            when {
                binding.etActForgotPwdAdminPhone.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请输入手机号码")
                    return@setOnClickListener
                }
                binding.etActForgotPwdAdminPwd.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请输入新密码")
                    return@setOnClickListener
                }
                binding.etActForgotPwdAdminAgainPwd.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请再次输入新密码")
                    return@setOnClickListener
                }
                binding.etActForgotPwdAdminCode.text.toString().trim() == "" -> {
                    ToastUtil.showShort(this, "请输入验证码")
                    return@setOnClickListener
                }
                else -> {
                    presenter.oauthRetrievePassword(
                        binding.etActForgotPwdAdminPhone.text.toString().trim(),
                        binding.etActForgotPwdAdminPwd.text.toString().trim(),
                        binding.etActForgotPwdAdminAgainPwd.text.toString().trim(),
                        binding.etActForgotPwdAdminCode.text.toString().trim()
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

    override fun oauthRetrievePassword(msg: BaseModel<String>?) {
        ToastUtil.showShort(this,msg!!.msg)
        finish()
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this,msg)
    }
    //倒计时的方式一
    private fun countDownTimer() {
        var num = 120
        binding.btnActForgotPwdAdminCode.isEnabled = false
        binding.btnActForgotPwdAdminCode.setBackgroundResource(R.drawable.btn_eeeeee_round_5_background)
        binding.btnActForgotPwdAdminCode.setTextColor(Color.parseColor("#999999"))
        timer = object : CountDownTimer(num * 1000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.btnActForgotPwdAdminCode.text =
                    (millisUntilFinished / 1000).toInt().toString() + "秒后再发送"
            }

            override fun onFinish() {
                timer = null
                binding.btnActForgotPwdAdminCode.isEnabled = true
                binding.btnActForgotPwdAdminCode.text = "发送验证码"
                binding.btnActForgotPwdAdminCode.setTextColor(Color.parseColor("#ffffff"))
                binding.btnActForgotPwdAdminCode.setBackgroundResource(R.drawable.btn_blue_round_5_background)
            }
        }

        timer?.start()
    }
}