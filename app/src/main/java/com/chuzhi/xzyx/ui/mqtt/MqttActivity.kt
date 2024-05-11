package com.chuzhi.xzyx.ui.mqtt

import android.content.Context
import android.content.Intent
import android.util.Log
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityMqttBinding
import java.util.*


/**
 * mqtt测试页面
 */
class MqttActivity : BaseActivity<ActivityMqttBinding, BasePresenter<*>>(), BaseView,
    IGetMessageCallBack {
    private var serviceConnection: MyServiceConnection? = null
    private var mqttService: MQTTService? = null
    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {

        serviceConnection = MyServiceConnection()
        serviceConnection!!.setIGetMessageCallBack(this@MqttActivity)

        val intent = Intent(this, MQTTService::class.java)
        intent.putExtra("snType", "868038065406739")
        bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)

        binding.btMqttPoint.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "信号强度")
            MQTTService.publish("868038065406739", 0, "设备电量")
            MQTTService.publish("868038065406739", 0, "设备开机状态")
            val timer = Timer() //实例化Timer类
            timer.schedule(object : TimerTask() {
                override fun run() {
                    MQTTService.publish("868038065406739", 0, "设备坐标")
                    this.cancel()
                }
            }, 1000)
        }
        binding.btMqttCsq.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "信号强度")
        }
        binding.btMqttBattery.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "设备电量")
        }
        binding.btMqttOpenPower.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "启用power键")
        }
        binding.btMqttClosePower.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "禁用power键")
        }
        binding.btMqttPowerStatus.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "power键状态")
        }
        binding.btMqttOpenBacklight.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "开启背光")
        }
        binding.btMqttCloseBacklight.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "关闭背光")
        }
        binding.btMqttBacklightStatus.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "背光状态")
        }
        binding.btMqttClock.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "锁机")
        }
        binding.btMqttUnlock.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "解锁")
        }
        binding.btMqttClockStatus.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "锁机状态")
        }
        binding.btMqttStolen.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "开启被盗模式")
        }
        binding.btMqttUnsteal.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "解除被盗模式")
        }
        binding.btMqttStolenStatus.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "被盗状态")
        }
        binding.btMqttStatus.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "设备开机状态")
        }
        binding.btMqttKj.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "开机")
        }
        binding.btMqttGj.setOnClickListener {
            MQTTService.publish("868038065406739", 0, "关机")
        }

    }

    override fun initData() {
    }

    override fun setMessage(message: String?, pos: Int, type: String) {
        if (message != null) {
            Log.e("mqtt=>>", message)
        }
        runOnUiThread {
            binding.tvMqtt.text = message
        }
        mqttService = serviceConnection!!.mqttService;
//        mqttService!!.toCreateNotification(message);
    }

    //信号强度
    override fun setCsq(message: String?, pos: Int) {
        runOnUiThread {
            binding.btMqttCsq.text = "信号强度$message"
        }

    }

    //设备电量
    override fun setBattery(message: String?, pos: Int) {
        runOnUiThread {
            binding.btMqttBattery.text = "设备电量$message"
        }
    }

    override fun setPowerStatus(message: String?, pos: Int) {
    }

    override fun setBacklightStatus(message: String?, pos: Int) {
    }

    override fun setClockStatus(message: String?, pos: Int) {
    }

    override fun setStolenStatus(message: String?, pos: Int) {
    }

    override fun setComputerStatus(message: String?, pos: Int) {
        runOnUiThread {
            binding.btMqttStatus.text = "开关机状态$message"
        }

    }

    override fun setSsdCommand(message: String?, pos: Int) {

    }

    override fun setSsdCommandSN(message: String?, pos: Int) {

    }

    //mqttUsb端口状态回调
    override fun setUsbStatus(message: String?, pos: Int) {

    }
    //mqttType-c端口状态回调
    override fun setTypeStatus(message: String?, pos: Int) {
    }
    //mqttWifi端口状态回调
    override fun setWifiStatus(message: String?, pos: Int) {
    }
    //mqtt蓝牙端口状态回调
    override fun setBlueToothStatus(message: String?, pos: Int) {
    }
    override fun onDestroy() {
        unbindService(serviceConnection!!)
        super.onDestroy()
    }
}