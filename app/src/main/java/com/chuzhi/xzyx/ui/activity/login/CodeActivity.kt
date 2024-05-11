package com.chuzhi.xzyx.ui.activity.login

import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityCodeBinding
import com.chuzhi.xzyx.ui.activity.MainActivity
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity
import com.chuzhi.xzyx.ui.presenter.CodePresenter
import com.chuzhi.xzyx.ui.view.CodeView
import com.chuzhi.xzyx.utils.ActivityCollectorUtil
import com.chuzhi.xzyx.utils.SpUtils
import com.chuzhi.xzyx.utils.ToastUtil
import com.chuzhi.xzyx.widget.VerifyCodeView

/**
 * 获取验证码页面
 */
class CodeActivity : BaseActivity<ActivityCodeBinding, CodePresenter>(), CodeView {

    private var phoneNumber: String? = null
    private var timer: CountDownTimer? = null
    override fun createPresenter(): CodePresenter {
        return CodePresenter(this)
    }

    override fun initView() {
        binding.includeActCode.tvIncludeTitleTitle.visibility = View.GONE
        binding.includeActCode.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActCode.ivIncludeTitleBack.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        phoneNumber = intent.getStringExtra("phone")
        if (phoneNumber!!.length == 11) {
            val sub1 = phoneNumber!!.substring(0, 3)
            val sub2 = phoneNumber!!.substring(3, 7)
            val sub3 = phoneNumber!!.substring(7, 11)
            binding.tvActCodePhone.text = "$sub1 $sub2 $sub3"
        }
        presenter.smsCaptcha(phoneNumber)
        //重新发送验证码
        binding.tvActCodeAgain.setOnClickListener {
            presenter.smsCaptcha(phoneNumber)
        }

        //监听验证码输入状态
        binding.vcvActCode.setInputCompleteListener(object : VerifyCodeView.InputCompleteListener {
            override fun inputComplete() {//已输入6位验证码
                binding.btnActCodeLogin.visibility = View.VISIBLE
            }

            override fun invalidContent() {//未输入6位验证码
                binding.btnActCodeLogin.visibility = View.GONE
            }
        })

        //登录按钮逻辑：
        //获取验证码后不管是新用户还是老用户，统一调用调用登录接口，再调用根据用户是否注册，未注册再调用注册接口
        binding.btnActCodeLogin.setOnClickListener {
            presenter.codeLogin(phoneNumber, binding.vcvActCode.editContent)
        }

    }


    //验证码获取成功回调
    override fun codeSuccess(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, "验证码已发送")
        countDownTimer()
    }

    //注册成功回调
    override fun registerUserSuccess(msg: BbsUserEntity?) {
        ToastUtil.showLong(this,"注册成功")
        //注册成功后把本地绑定设备状态清空，否则登录成功不会跳转到绑定设备页面
        SpUtils.setSharedStringData(this,"HomePageBinding","")
        AppCache.getInstance().logInAgain = 1
        SpUtils.setSharedStringData(this, "Token", msg!!.token)
        SpUtils.setSharedStringData(this, "Username", msg.nickname)
        SpUtils.setSharedStringData(this, "Avatar", msg.avatar)

        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun loginSuccess(msg: BbsUserEntity?) {
        AppCache.getInstance().logInAgain = 1
        SpUtils.setSharedStringData(this, "Token", msg!!.token)
        SpUtils.setSharedStringData(this, "Username", msg.nickname)
        SpUtils.setSharedStringData(this, "Avatar", msg.avatar)

        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        startActivity(Intent(this, MainActivity::class.java))

    }

    override fun showError(msg: String?) {
        ToastUtil.showShort(this, msg)
//        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        binding.llActCodeS.visibility = View.VISIBLE
        binding.tvActCodeS.visibility = View.GONE
        binding.tvActCodeAgain.text = msg
    }


    //倒计时的方式一
    private fun countDownTimer() {
        var num = 120
        binding.llActCodeS.visibility = View.VISIBLE
        binding.tvActCodeS.visibility = View.VISIBLE
        binding.tvActCodeAgain.isEnabled = false
        binding.tvActCodeAgain.text = "秒后重新发送验证码"
        binding.tvActCodeAgain.setTextColor(Color.parseColor("#999999"))
        timer = object : CountDownTimer(num * 1000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.tvActCodeS.text = (millisUntilFinished / 1000).toInt().toString()
            }

            override fun onFinish() {
                timer = null
                binding.tvActCodeS.visibility = View.GONE
                binding.tvActCodeAgain.isEnabled = true
                binding.tvActCodeAgain.text = "重新发送验证码"
                binding.tvActCodeAgain.setTextColor(Color.parseColor("#0066ff"))
            }
        }

        timer?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

}