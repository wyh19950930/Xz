package com.chuzhi.xzyx.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.tbruyelle.rxpermissions2.RxPermissions
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.app.AppManager
import com.chuzhi.xzyx.app.MyApplication.Companion.getInstance
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.databinding.ActivityWelcomeBinding
import com.chuzhi.xzyx.ui.activity.login.LoginActivity
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.presenter.WelcomePresenter
import com.chuzhi.xzyx.ui.view.WelcomeView
import com.chuzhi.xzyx.utils.*
import com.chuzhi.xzyx.utils.thread.AppExecutors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding, WelcomePresenter>(), WelcomeView {

    private var rxPermissions: RxPermissions? = null
    private var permissionPop: AlertDialogIos? = null
    private var animatorSet: AnimatorSet? = null
    private var schedule: ScheduledFuture<*>? = null
    private var jumpType = 0
    override fun createPresenter(): WelcomePresenter {
        return WelcomePresenter(this)
    }

    override fun initView() {
        AppCache.getInstance().cardPath = GetFileUtil.getSDCardPath();
        /*//状态栏透明
        setStatusBarTransparent();
        //状态栏字体颜色 true浅色 false深色
        setStatusBarTextColor(false)*/
        rxPermissions = RxPermissions(this)
        permissionPop()
        schedule = AppExecutors.getInstance().scheduledExecutor().schedule({
//            jump()
        }, 1510, TimeUnit.MILLISECONDS)
        val alpha = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1.0f)
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.3f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.3f, 1.0f)
        val objectAnimator1 =
            ObjectAnimator.ofPropertyValuesHolder(binding.llWelcomeScale, alpha, scaleX, scaleY)
//        val objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(iv_logo, alpha, scaleX, scaleY)
//        countDownTimer()
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(objectAnimator1)//, objectAnimator2
        animatorSet!!.interpolator = AccelerateInterpolator()
        animatorSet!!.duration = 1500
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                /**
                 * 动画执行完毕回调
                 */
                if (jumpType == 0) {
                    jump()
                }
            }

            override fun onAnimationCancel(animator: Animator) {

            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        animatorSet!!.start()
        binding.llWelcomeStart.setOnClickListener {
            jumpType = 1
            jump()
        }

    }

    override fun initData() {
    }

    //倒计时的方式一
    private var timer: CountDownTimer? = null
    private fun countDownTimer() {
        var num = 2
        timer = object : CountDownTimer(num * 1000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.btnWelcomeSecond.text =
                    (millisUntilFinished / 1000).toInt().toString() + "s"
                if ((millisUntilFinished / 1000 - 1).toInt() == 0) {

//                    jump()
                }
            }

            override fun onFinish() {
                //有一到两秒延迟，所以把逻辑放到上面方法里
//                jump()
            }
        }

        timer?.start()
    }

    @SuppressLint("CheckResult")
    private fun jump() {

        val token = SpUtils.getSharedStringData(getInstance(), "Token")
        if (token.isEmpty()) {
            LoginActivity.startAction(this@WelcomeActivity)
            AppManager.getAppManager().finishActivity(this@WelcomeActivity)
        } else {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                MainActivity.startAction(this@WelcomeActivity)
                AppManager.getAppManager().finishActivity(this@WelcomeActivity)
            } else {
                presenter.userComputerList()
            }
        }


        /*rxPermissions!!.request(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            *//*Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION*//*
        ).subscribe { granted ->

                if (granted) {

                        val token = SpUtils.getSharedStringData(getInstance(), "Token")
                        if (token.isEmpty()) {
                            LoginActivity.startAction(this@WelcomeActivity)
                            AppManager.getAppManager().finishActivity(this@WelcomeActivity)
                        } else {
                            MainActivity.startAction(this@WelcomeActivity)
                            AppManager.getAppManager().finishActivity(this@WelcomeActivity)
                        }
                } else {
                    permissionPop!!.show()
                }
            }*/

    }

    private fun permissionPop() {
        permissionPop = AlertDialogIos(this).builder()
            .setTitle("开启权限")
            .setMsg("小志云享需要访问存储、电话等相关权限以开启“设备定位等\"功能\n请在设置中开启权限")
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

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer!!.onFinish()
            timer!!.cancel()
        }
        if (schedule != null) {
            schedule!!.cancel(false)
        }

    }

    //提前调用一下接口检测是否已在其他设备登录
    override fun userComputerList(msg: ComputerListEntity?) {
        MainActivity.startAction(this@WelcomeActivity)
        AppManager.getAppManager().finishActivity(this@WelcomeActivity)
    }

}