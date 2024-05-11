package com.chuzhi.xzyx.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityMainBinding
import com.chuzhi.xzyx.ui.adapter.BottomAdapter
import com.chuzhi.xzyx.ui.fragment.find.FindFragment
import com.chuzhi.xzyx.ui.fragment.homepage.HPDeviceFragment
import com.chuzhi.xzyx.ui.fragment.me.MeFragment
import com.chuzhi.xzyx.utils.AndroidBarUtils
import com.chuzhi.xzyx.utils.NetworkUtils
import com.chuzhi.xzyx.utils.ToastUtil
import com.chuzhi.xzyx.widget.NoScrollViewPager


class MainActivity : BaseActivity<ActivityMainBinding, BasePresenter<*>>(), BaseView {
    private var isBackPressed: Boolean = false//是否退出软件

    companion object {
        /**
         * 入口
         * @param activity
         */

        fun startAction(activity: Context) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            /*activity.overridePendingTransition(R.anim.fade_in,
                    R.anim.fade_out)*/
        }
    }


    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
//        AndroidBarUtils.setBarPaddingTop(this,binding.vpActMain)
        binding.vpActMain.offscreenPageLimit = 3
        binding.vpActMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.bntActMain.menu.getItem(position).isChecked = true
            }
        })
        setupViewPager(binding.vpActMain)
        binding.bntActMain.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_tab1 -> {
                    binding.vpActMain.currentItem = 0
                }
                R.id.item_tab2 -> {
                    if (NetworkUtils.isNetworkAvailable(this)){
                        binding.vpActMain.currentItem = 1
                        FindFragment.newInstance("q", "q")
                    }else{
                        ToastUtil.showLong(this,"请检查您的网络")
                    }
                }
                R.id.item_tab3 -> {
                    if (NetworkUtils.isNetworkAvailable(this)){
                        binding.vpActMain.currentItem = 2
                        FindFragment.newInstance("q", "q")
                    }else{
                        ToastUtil.showLong(this,"请检查您的网络")
                    }
                }

            }
            false
        }

    }

    private fun setupViewPager(vpActMain: NoScrollViewPager) {
        var adapter = BottomAdapter(supportFragmentManager)
        adapter.addFragment(HPDeviceFragment())
        adapter.addFragment(FindFragment())
        adapter.addFragment(MeFragment())
        vpActMain.adapter = adapter

    }

    override fun initData() {
//        val service = Intent(applicationContext, KeepAliveService::class.java)
//        startService(service)
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

    /**
     * 设置一个占位高度，避免内容整体上移
     * @param actionBarHight
     */
    private fun setHight(actionBarHight: Int) {

        val params = binding.vpActMain.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0,actionBarHight,0,0)
        binding.vpActMain.layoutParams = params
    }

    /**
     * 获取沉浸式状态栏高度
     * @return
     */
    private fun getActionBarHight(): Int {
        var result = 0
        val identifier = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (identifier > 0) {
            result = resources.getDimensionPixelSize(identifier)
        }
        return result
    }

    /**
     * 设置沉浸式状态栏透明 兼容4.4版本以上以及5.0以上
     */
    private fun addActionBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView: View = window.decorView
            val option: Int =  //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.setSystemUiVisibility(option)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= 19) {
            val decorView: View = window.decorView
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    //判断设备fragment页面连接中的pop框还在显示的时候拦截其他控件的点击事件
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (HPDeviceFragment.mqttSuccessPop!=null && HPDeviceFragment.mqttSuccessPop!!.isShowing){
            return false
        }
        return super.dispatchTouchEvent(ev)
    }
}

