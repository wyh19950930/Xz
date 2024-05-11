package com.chuzhi.xzyx.ui.activity.login

import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityRegisterBinding
import com.chuzhi.xzyx.ui.activity.MainActivity
import com.chuzhi.xzyx.ui.activity.me.PrivacyPolicyActivity
import com.chuzhi.xzyx.ui.activity.me.UserAgreementActivity
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity
import com.chuzhi.xzyx.ui.presenter.RegisterPresenter
import com.chuzhi.xzyx.ui.view.RegisterView
import com.chuzhi.xzyx.utils.*

/**
 * 注册activity
 */
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterPresenter>(), RegisterView {

    private var timer: CountDownTimer? = null
    override fun createPresenter(): RegisterPresenter {
        return RegisterPresenter(this)
    }

    override fun initView() {
        binding.includeActRegister.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActRegister.tvIncludeTitleTitle.text = "注册"
        binding.includeActRegister.ivIncludeTitleBack.setOnClickListener { finish() }

        //获取验证码按钮
        binding.btnActRegisterAdminCode.setOnClickListener {
            if (binding.etActRegisterAdminPhone.text.toString() == "") {
                ToastUtil.showShort(this, "请输入手机号码")
                return@setOnClickListener
            } else {
                if (RegexUtils.isMobileExact(binding.etActRegisterAdminPhone.text.toString())) {
                    presenter.smsCaptcha(binding.etActRegisterAdminPhone.text.toString())
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
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                return a.toCharArray()
            }
        }
        // 设置允许的字符setKeyListener
        binding.etActRegisterAdminPwd.keyListener = digitsKeyListener
        binding.etActRegisterAdminAgainPwd.keyListener = digitsKeyListener
        binding.cbActRegisterAdminPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActRegisterAdminPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActRegisterAdminPwd.setSelection(binding.etActRegisterAdminPwd.text.length)
            binding.etActRegisterAdminPwd.keyListener = digitsKeyListener
        }
        binding.cbActRegisterAdminAgainPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActRegisterAdminAgainPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActRegisterAdminAgainPwd.setSelection(binding.etActRegisterAdminAgainPwd.text.length)
            binding.etActRegisterAdminAgainPwd.keyListener = digitsKeyListener
        }
        //注册按钮
        binding.btnActRegisterRegister.setOnClickListener {
            if (SingleOnClickUtil.isFastClick()) {

                if (!binding.cbActRegisterYxZc.isChecked){
                    ToastUtil.showLong(this,"请阅读并同意用户协议和隐私政策")
                    return@setOnClickListener
                }
                when {
                    binding.etActRegisterAdminPhone.text.toString().trim() == "" -> {
                        ToastUtil.showShort(this, "请输入手机号码")
                        return@setOnClickListener
                    }
                    binding.etActRegisterAdminPwd.text.toString().trim() == "" -> {
                        ToastUtil.showShort(this, "请输入密码")
                        return@setOnClickListener
                    }
                    binding.etActRegisterAdminAgainPwd.text.toString().trim() == "" -> {
                        ToastUtil.showShort(this, "请再次输入密码")
                        return@setOnClickListener
                    }
                    binding.etActRegisterAdminCode.text.toString().trim() == "" -> {
                        ToastUtil.showShort(this, "请输入验证码")
                        return@setOnClickListener
                    }
                    else -> {
                        presenter.registerUserCode(
                            binding.etActRegisterAdminPhone.text.toString().trim(),
                            binding.etActRegisterAdminPwd.text.toString().trim(),
                            binding.etActRegisterAdminAgainPwd.text.toString().trim(),
                            binding.etActRegisterAdminCode.text.toString().trim()
                        )
                    }
                }
            }
        }
        //用户协议
        binding.tvActRegisterYhXy.setOnClickListener {
            startActivity(Intent(this, UserAgreementActivity::class.java))
        }
        //隐私政策
        binding.tvActRegisterYsZc.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        //返回登录
        binding.btnActRegisterLogin.setOnClickListener { finish() }
    }

    override fun initData() {
    }

    //注册成功回调
    override fun registerUserSuccess(msg: BbsUserEntity?) {
        ToastUtil.showLong(this,"注册成功")
        AppCache.getInstance().logInAgain = 1
        SpUtils.setSharedStringData(this, "Token", msg!!.token)
        SpUtils.setSharedStringData(this, "Phone", msg.username)
        SpUtils.setSharedStringData(this, "Username", msg.username)
        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this, msg)
    }

    override fun codeSuccess(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, "验证码已发送")
        countDownTimer()
    }

    //倒计时的方式一
    private fun countDownTimer() {
        var num = 120
        binding.btnActRegisterAdminCode.isEnabled = false
        binding.btnActRegisterAdminCode.setBackgroundResource(R.drawable.btn_eeeeee_round_5_background)
        binding.btnActRegisterAdminCode.setTextColor(Color.parseColor("#999999"))
        timer = object : CountDownTimer(num * 1000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.btnActRegisterAdminCode.text =
                    (millisUntilFinished / 1000).toInt().toString() + "秒后再发送"
            }

            override fun onFinish() {
                timer = null
                binding.btnActRegisterAdminCode.isEnabled = true
                binding.btnActRegisterAdminCode.text = "发送验证码"
                binding.btnActRegisterAdminCode.setTextColor(Color.parseColor("#ffffff"))
                binding.btnActRegisterAdminCode.setBackgroundResource(R.drawable.btn_blue_round_5_background)
            }
        }

        timer?.start()
    }
}