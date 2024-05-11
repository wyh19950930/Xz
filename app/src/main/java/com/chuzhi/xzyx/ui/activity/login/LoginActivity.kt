package com.chuzhi.xzyx.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.app.MyApplication
import com.chuzhi.xzyx.app.MyApplication.Companion.getInstance
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityLoginBinding
import com.chuzhi.xzyx.ui.activity.MainActivity
import com.chuzhi.xzyx.ui.activity.me.PrivacyPolicyActivity
import com.chuzhi.xzyx.ui.activity.me.UserAgreementActivity
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity
import com.chuzhi.xzyx.ui.presenter.LoginPresenter
import com.chuzhi.xzyx.ui.view.LoginView
import com.chuzhi.xzyx.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginPresenter>(), LoginView {
    var adminTitle = ""
    private var isBackPressed: Boolean = false//是否退出软件
    private var privacyPop: PrivacyDialog? = null
    private var privacyPop2: PrivacyDialog2? = null
    private var rxPermissions:RxPermissions?=null
    private var permissionPop : AlertDialogIos? = null
    companion object {
        /**
         * 入口
         * @param activity
         */

        fun startAction(activity: Context) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            /*activity.overridePendingTransition(R.anim.fade_in,
                    R.anim.fade_out)*/
        }
    }

    override fun createPresenter(): LoginPresenter {
        return LoginPresenter(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {

        rxPermissions = RxPermissions(this)
        permissionPop()
        val firstLogin = SpUtils.getSharedBooleanData(this, "firstLogin")
        if (!firstLogin){
            privacyPop()
            Log.e("firstLogin",firstLogin.toString())
        }else{
//            setRxPermissions()
            Log.e("firstLogin",firstLogin.toString())
        }

        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS;

        adminTitle = "您好，<br/>欢迎使用<font color='#0066ff'>小志云享</font>"
        binding.tvActLoginTitle.text = Html.fromHtml(adminTitle)

        //验证码登录、密码登录切换
        binding.tvActLoginCodePwd.setOnClickListener {
            val str = binding.tvActLoginCodePwd.text.toString()
            when (str) {
                "验证码登录" -> {
                    adminTitle = "您好，<br/>欢迎使用<font color='#0066ff'>小志云享</font>"
                    binding.tvActLoginTitle.text = Html.fromHtml(adminTitle)
                    binding.tvActLoginCodePwd.text = "密码登录"
                    binding.btActLoginLogin.text = "获取验证码"
                    binding.llActLoginAdmin.visibility = View.GONE
                    binding.llActLoginCode.visibility = View.VISIBLE
                    binding.tvActLoginRegister.visibility = View.GONE
                }
                "密码登录" -> {
                    adminTitle = "使用<font color='#0066ff'>小志云享</font>账号登录"
                    binding.tvActLoginTitle.text = Html.fromHtml(adminTitle)
                    binding.tvActLoginCodePwd.text = "验证码登录"
                    binding.btActLoginLogin.text = "登录"
                    binding.llActLoginAdmin.visibility = View.VISIBLE
                    binding.llActLoginCode.visibility = View.GONE
                    binding.tvActLoginRegister.visibility = View.VISIBLE
                }
            }
        }

        //手机号输入监听(验证码版）
        binding.etActLoginCodePhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (RegexUtils.isMobileExact(binding.etActLoginCodePhone.text.toString())) {
                    binding.btActLoginLogin.isEnabled = true
                    binding.btActLoginLogin.setBackgroundResource(R.drawable.btn_blue_round_30_background)
                } else {
                    binding.btActLoginLogin.isEnabled = false
                    binding.btActLoginLogin.setBackgroundResource(R.drawable.btn_grey_round_background)
                }
            }
        })
        //手机号输入监听(账号密码版）
        binding.etActLoginAdminPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (RegexUtils.isMobileExact(binding.etActLoginAdminPhone.text.toString())) {
                    binding.btActLoginLogin.isEnabled = true
                    binding.btActLoginLogin.setBackgroundResource(R.drawable.btn_blue_round_30_background)
                } else {
                    binding.btActLoginLogin.isEnabled = false
                    binding.btActLoginLogin.setBackgroundResource(R.drawable.btn_grey_round_background)
                }
            }
        })
        //光标在后
        binding.etActLoginCodePhone.setSelection(binding.etActLoginCodePhone.text.length)

        //去注册
        binding.tvActLoginRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        //登录按钮
        binding.btActLoginLogin.setOnClickListener {
            hideSoftKeyboard(binding.btActLoginLogin)
            if (!binding.cbActLoginYxZc.isChecked){
                ToastUtil.showLong(this,"请阅读并同意用户协议和隐私政策")
                return@setOnClickListener
            }
            val toString = binding.btActLoginLogin.text.toString()
            if (toString != "登录") {//去验证码页面
                val intent = Intent(this, CodeActivity::class.java)
                intent.putExtra("phone", binding.etActLoginCodePhone.text.toString().trim())
                startActivity(intent)
            } else {//账号密码登录
                if (binding.etActLoginAdminPwd.text.toString().trim() == "") {
                    ToastUtil.showShort(this, "请输入密码")
                } else {
                    presenter.bbsGetToken(
                        binding.etActLoginAdminPhone.text.toString().trim(),
                        binding.etActLoginAdminPwd.text.toString().trim()
                    )
                }
            }
        }

        //忘记密码
        binding.tvActLoginForgotPwd.setOnClickListener {
            val intent = Intent(this, ForgotPwdActivity::class.java)
            startActivity(intent)
        }
        //用户协议
        binding.tvActLoginYhXy.setOnClickListener {
            startActivity(Intent(this, UserAgreementActivity::class.java))
        }
        //隐私政策
        binding.tvActLoginYsZc.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
    }

    override fun initData() {
        if (AppCache.getInstance().logInAgain == 0){
            tipPop("账号已在其他设备登录，请重新登录")
            ToastUtil.show(MyApplication.getInstance(),"账号已在其他设备登录，请重新登录",4000)
        }else if (AppCache.getInstance().logInAgain == -1){
            tipPop("你还未登录/登录超时!")
            ToastUtil.show(MyApplication.getInstance(),"你还未登录/登录超时!",4000)
        }
    }
    @SuppressLint("CheckResult")
    private fun setRxPermissions(){
        rxPermissions!!.request(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            /*Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION*/
        ).subscribe { granted ->

            if (granted) {
//                ToastUtil.showLong(this,"已授权")
            } else {
                permissionPop!!.show()
            }
        }
    }
    private fun permissionPop(){
        permissionPop = AlertDialogIos(this).builder()
            .setTitle("开启权限")
            .setMsg("小志云享需要访问存储、电话等相关权限以开启“账号单一设备登录等\"功能\n请在设置中开启权限")
            .setCancelable(false)
//            .setNegativeButton("取消", R.color.gray,null)
            .setPositiveButton("设置", R.color.text_default, View.OnClickListener {
                //跳转应用消息，间接打开应用权限设置-效率高
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            })
    }
    private fun privacyPop(){
        privacyPop = PrivacyDialog(this).builder()
            .setTitle("个人信息保护指引")
            .setMsg(getString(R.string.first_privacy))
            .setCancelable(false)
            .setNegativeButton("不同意", R.color.gray,View.OnClickListener {
                privacyPop!!.dismiss()
                privacyPop2()
            })
            .setPositiveButtonNoDismiss("同意", R.color.text_default, View.OnClickListener {
                privacyPop!!.dismiss()
                SpUtils.setSharedBooleanData(this,"firstLogin",true)
//                setRxPermissions()
            })
        privacyPop!!.show()
    }
    private fun privacyPop2(){
        privacyPop2 = PrivacyDialog2(this).builder()
            .setTitle("个人信息保护指引")
            .setMsg(getString(R.string.first_privacy2))
            .setCancelable(false)
            .setNegativeButton("不同意并退出", R.color.gray,View.OnClickListener {
//                privacyPop2!!.dismiss()
                //清空栈内的Activity
                ActivityCollectorUtil.finishAllActivity()
            })
            .setPositiveButtonNoDismiss("同意", R.color.text_default, View.OnClickListener {
                privacyPop2!!.dismiss()
                SpUtils.setSharedBooleanData(this,"firstLogin",true)
//                setRxPermissions()
            })
        privacyPop2!!.show()
    }


    private var tipPop : AlertDialogIos? = null
     fun tipPop(str:String){
        tipPop = AlertDialogIos(this).builder()
            .setTitle("提示")
            .setMsg(str)
            .setPositiveButton("确定", R.color.text_default,View.OnClickListener {
                SpUtils.setSharedStringData(this,"Token","")
                tipPop!!.dismiss()
            })
         tipPop!!.show()
    }


    override fun showError(msg: String?) {
            ToastUtil.showShort(this, msg)
    }

    //登录成功
    override fun loginSuccess(msg: BbsUserEntity?) {
        AppCache.getInstance().logInAgain = 1
        SpUtils.setSharedStringData(this, "Token", msg!!.token)
        SpUtils.setSharedStringData(this, "Phone", msg.username)
        SpUtils.setSharedStringData(this, "Username", msg.nickname)
        SpUtils.setSharedStringData(this, "Avatar", msg.avatar)
        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        startActivity(Intent(this, MainActivity::class.java))
        /*presenter.rcGetToken(
            binding.etActLoginAdminPhone.text.toString().trim(),
            binding.etActLoginAdminPwd.text.toString().trim()
        )*/
    }

    override fun loginRcSuccess(msg: BaseModel<String>?) {

    }
    //隐藏键盘
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    /**
     * 监听手机自带的返回按键
     */
    override fun onBackPressed() {
        if (isBackPressed) {
            isBackPressed = false
            super.onBackPressed()
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show()//Toast.LENGTH_SHORT
            isBackPressed = true
            object : Thread() {
                override fun run() {
                    super.run()
                    try {
                        Thread.sleep(3000)
                        isBackPressed = false
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!permissionPop!!.isShowing){
            val firstLogin = SpUtils.getSharedBooleanData(this, "firstLogin")
            if (firstLogin){
//                setRxPermissions()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tipPop!=null){
            tipPop!!.dismiss()
        }
    }
}