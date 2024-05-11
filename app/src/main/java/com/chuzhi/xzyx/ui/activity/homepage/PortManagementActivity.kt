package com.chuzhi.xzyx.ui.activity.homepage

import android.util.Log
import android.view.View
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityPortManagementBinding
import com.chuzhi.xzyx.ui.bean.mqtt.MQTTMessage
import com.chuzhi.xzyx.ui.mqtt.MQTTService
import com.chuzhi.xzyx.utils.thread.ScheduledTask
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 端口管理activity
 */
class PortManagementActivity : BaseActivity<ActivityPortManagementBinding, BasePresenter<*>>(),
    BaseView {
    private var sn = ""
    private var scheduledTask : ScheduledTask?=null
    private var delaytime = 0
    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.includeActPortMgt.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActPortMgt.tvIncludeTitleTitle.text = "端口管理"
        binding.includeActPortMgt.ivIncludeTitleBack.setOnClickListener { finish() }
        EventBus.getDefault().register(this)
        sn = intent.getStringExtra("portSn").toString()
        if (sn != "") {
            scheduledTask = ScheduledTask()
            try {
                scheduledTask!!.startCountdown(100L,100L) {
                    delaytime++
                    if (delaytime<6){
                        when(delaytime){
                            1->{
                                MQTTService.publish(sn, 0, "wifi端口状态")
                            }
                            2->{
                                MQTTService.publish(sn, 0, "蓝牙端口状态")
                            }
                            3->{
                                MQTTService.publish(sn, 0, "USB端口状态")
                            }
                            4->{
                                MQTTService.publish(sn, 0, "Type-C端口状态")
                            }
                            5->{
                                Log.e("delaytime","命令5")
                            }

                        }
                    }else{
                        scheduledTask!!.stop()
                        delaytime = 0
                        Log.e("delaytime","停止")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initData() {
        //wifi端口
        binding.onOffActPortMgtNetwork.setCheckBoxCall { isOff -> //关true 开false
            if (isOff) {
                MQTTService.publish(sn, 0, "禁用wifi")
            } else {
                MQTTService.publish(sn, 0, "启用wifi")
            }
        }
        //bluetooth端口
        binding.onOffActPortMgtBluetooth.setCheckBoxCall { isOff -> //关true 开false
            if (isOff) {
                MQTTService.publish(sn, 0, "禁用蓝牙")
            } else {
                MQTTService.publish(sn, 0, "启用蓝牙")
            }
        }
        //usb端口
        binding.onOffActPortMgtUsb.setCheckBoxCall { isOff -> //关true 开false
            if (isOff) {
                MQTTService.publish(sn, 0, "禁用USB端口")
            } else {
                MQTTService.publish(sn, 0, "启用USB端口")
            }
        }
        //type-c端口
        binding.onOffActPortMgtTypeC.setCheckBoxCall { isOff -> //关true 开false
            if (isOff) {
                MQTTService.publish(sn, 0, "禁用Type-C端口")
            } else {
                MQTTService.publish(sn, 0, "启用Type-C端口")
            }
        }
    }

    override fun showError(msg: String?) {
        super.showError(msg)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMqttMessage(entity: MQTTMessage) {
        when (entity.type) {
            "usb" -> {
                if (entity.message == "0") binding.onOffActPortMgtUsb.setDefOff(false)
                else binding.onOffActPortMgtUsb.setDefOff(true)
            }
            "type-c" -> {
                if (entity.message == "0") binding.onOffActPortMgtTypeC.setDefOff(false)
                else binding.onOffActPortMgtTypeC.setDefOff(true)
            }
            "wifi" -> {
                if (entity.message == "0") binding.onOffActPortMgtNetwork.setDefOff(false)
                else binding.onOffActPortMgtNetwork.setDefOff(true)
            }
            "蓝牙" -> {
                if (entity.message == "0") binding.onOffActPortMgtBluetooth.setDefOff(false)
                else binding.onOffActPortMgtBluetooth.setDefOff(true)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        if (scheduledTask!=null){
            scheduledTask!!.stop()
        }
    }
}