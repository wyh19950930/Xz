package com.chuzhi.xzyx.ui.mqtt;

/**
 * @Author : wyh
 * @Time : On 2023/6/28 19:24
 * @Description : MyMqttService
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.api.ApiRetrofit;
import com.chuzhi.xzyx.app.MyApplication;
import com.chuzhi.xzyx.ui.bean.mqtt.MQTTMessage;
import com.chuzhi.xzyx.utils.DeviceIdUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MQTTService extends Service {

    public static final String TAG = "MQTTService";

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;

    //    private String host = "tcp://testmqtt.chuzhi.cn";
    private String host = "tcp://mqtt.chuzhi.cn:1883";
    private String userName = "emqx_u";
    private String passWord = "public";
    private static String myTopic = "/coordinate_data";      //设备坐标主题
    private static String csqTopic = "/csq_data";      //信号强度主题
    private static String batteryTopic = "/battery";      //设备电量主题
    private static String powerStatusTopic = "/power_status";      //power键状态主题
    private static String backlightStatusTopic = "/backlight_status";      //背光状态主题
    private static String clockStatusTopic = "/clock_status";      //锁机状态主题
    private static String stolenStatusTopic = "/stolen_status";      //被盗状态主题
    private static String computerStatusTopic = "/computer_status";      //设备开机状态主题
    private static String ssdCommandTopic = "/ssd_command";      //硬盘锁定和解锁状态查询
    private static String usbStatusTopic = "/usb_status";      //USB端口状态
    private static String typeStatusTopic = "/type_status";      //Type-C端口状态
    private static String wifiStatusTopic = "/wifi_status";      //wifi端口状态
    private static String bluetoothStatusTopic = "/bluetooth_status";      //蓝牙端口状态
    private String clientId = "androidId" + DeviceIdUtils.getDeviceId(MyApplication.Companion.getInstance());//客户端标识
    private IGetMessageCallBack IGetMessageCallBack;
    private static String snType = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getName(), "onCreate");

//        init();
    }

    public static int position = 0;

    public static void publish(String sn, int pos, String type) {
//        Log.e("mqtt坐标请求==>",sn);
        position = pos;
//        String topic =msg+"myTopic";
        String topic = sn + "/command";
        Integer qos = 0;
        Boolean retained = false;
        try {
            if (client != null) {
                if (successType != 200) {
                    return;
                }
//                client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
                if (type.equals("设备坐标")) {
                    client.publish(topic, "{\"get_coordinate\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求设备坐标==>", sn);
                } else if (type.equals("信号强度")) {
                    client.publish(topic, "{\"get_csq\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求信号强度==>", sn);
                } else if (type.equals("设备电量")) {
                    client.publish(topic, "{\"battery\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求设备电量==>", sn);
                } else if (type.equals("启用power键")) {
                    client.publish(topic, "{\"open_power\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求启用power键==>", sn);
                } else if (type.equals("禁用power键")) {
                    client.publish(topic, "{\"close_power\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求禁用power键==>", sn);
                } else if (type.equals("power键状态")) {
                    client.publish(topic, "{\"power_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求power键状态==>", sn);
                } else if (type.equals("开启背光")) {
                    client.publish(topic, "{\"open_backlight\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求开启背光==>", sn);
                } else if (type.equals("关闭背光")) {
                    client.publish(topic, "{\"close_backlight\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求关闭背光==>", sn);
                } else if (type.equals("背光状态")) {
                    client.publish(topic, "{\"backlight_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求背光状态==>", sn);
                } else if (type.equals("锁机")) {
                    client.publish(topic, "{\"clock\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求锁机==>", sn);
                } else if (type.equals("解锁")) {
                    client.publish(topic, "{\"unlock\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求解锁==>", sn);
                } else if (type.equals("锁机状态")) {
                    client.publish(topic, "{\"clock_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求锁机状态==>", sn);
                } else if (type.equals("开启被盗模式")) {
                    client.publish(topic, "{\"stolen\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求开启被盗模式==>", sn);
                } else if (type.equals("解除被盗模式")) {
                    client.publish(topic, "{\"unsteal\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求解除被盗模式==>", sn);
                } else if (type.equals("被盗状态")) {
                    client.publish(topic, "{\"stolen_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求被盗状态==>", sn);
                } else if (type.equals("设备开机状态")) {
                    client.publish(topic, "{\"computer_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求开机状态==>", sn);
                } else if (type.equals("开机")) {
                    client.publish(topic, "{\"bootup\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求开机==>", sn);
                } else if (type.equals("关机")) {
                    client.publish(topic, "{\"shutdown\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求关机==>", sn);
                } else if (type.equals("销毁")) {
                    client.publish(topic, "{\"ssd_command\":\"[0xAB,0xBA,0x04,0x00,0xA6,0x83,0x2D,0x01]\"}".getBytes(), qos, false);
                    Log.e("mqtt请求销毁==>", sn);
                } else if (type.equals("锁定硬盘")) {
                    client.publish(topic, "{\"ssd_command\":\"[0xAB,0xBA,0x04,0x00,0xAA,0xF0,0x9E,0x01]\"}".getBytes(), qos, false);
                    Log.e("mqtt请求锁定硬盘==>", sn);
                } else if (type.equals("解锁硬盘")) {
                    client.publish(topic, "{\"ssd_command\":\"[0xAB,0xBA,0x04,0x00,0xAA,0x00,0xAE,0x00]\"}".getBytes(), qos, false);
                    Log.e("mqtt请求解锁硬盘==>", sn);
                } else if (type.equals("硬盘状态")) {
                    client.publish(topic, "{\"ssd_command\":\"[0xAB,0xBA,0x04,0x00,0xAC,0x01,0xB1,0x00]\"}".getBytes(), qos, false);
                    Log.e("mqtt请求硬盘状态==>", sn);
                } else if (type.equals("硬盘SN号")) {
                    client.publish(topic, "{\"ssd_command\":\"[0xAB,0xBA,0x04,0x00,0xA3,0x03,0xAA,0x00]\"}".getBytes(), qos, false);
                    Log.e("mqtt请求硬盘SN号==>", sn);
                } else if (type.equals("启用USB端口")) {
                    client.publish(topic, "{\"open_usb\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求启用USB端口==>", sn);
                } else if (type.equals("禁用USB端口")) {
                    client.publish(topic, "{\"close_usb\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求禁用USB端口==>", sn);
                } else if (type.equals("USB端口状态")) {
                    client.publish(topic, "{\"usb_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求USB端口状态==>", sn);
                } else if (type.equals("启用Type-C端口")) {
                    client.publish(topic, "{\"open_type\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求启用Type-C端口==>", sn);
                } else if (type.equals("禁用Type-C端口")) {
                    client.publish(topic, "{\"close_type\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求禁用Type-C端口==>", sn);
                } else if (type.equals("Type-C端口状态")) {
                    client.publish(topic, "{\"type_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求Type-C端口状态==>", sn);
                } else if (type.equals("启用wifi")) {
                    client.publish(topic, "{\"open_wifi\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求启用wifi==>", sn);
                } else if (type.equals("禁用wifi")) {
                    client.publish(topic, "{\"close_wifi\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求禁用wifi==>", sn);
                } else if (type.equals("wifi端口状态")) {
                    client.publish(topic, "{\"wifi_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求wifi端口状态==>", sn);
                } else if (type.equals("启用蓝牙")) {
                    client.publish(topic, "{\"open_bluetooth\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求启用蓝牙==>", sn);
                } else if (type.equals("禁用蓝牙")) {
                    client.publish(topic, "{\"close_bluetooth\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求禁用蓝牙==>", sn);
                } else if (type.equals("蓝牙端口状态")) {
                    client.publish(topic, "{\"bluetooth_status\":\"true\"}".getBytes(), qos, false);
                    Log.e("mqtt请求蓝牙端口状态==>", sn);
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void init(String sn,String snPwd) {
        snType = sn;
        // 服务器地址（协议+地址+端口号）
        String uri = ApiRetrofit.host;
        client = new MqttAndroidClient(MyApplication.Companion.getInstance(), uri, "androidId" + sn + System.currentTimeMillis());//clientId
//        client = new MqttAndroidClient(MyApplication.Companion.getInstance(), uri, "androidId"+sn+DeviceIdUtils.getDeviceId(MyApplication.Companion.getInstance()));//clientId
        Log.e("clientId", "androidId" + sn);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(10);
        conOpt.setAutomaticReconnect(true);//自动重连 自己加
        // 用户名
        conOpt.setUserName(snType);
//        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(snPwd.toCharArray());     //将字符串转换为字符串数组
//        conOpt.setPassword(passWord.toCharArray());     //将字符串转换为字符串数组

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + "androidId" + sn + "\"}";//clientId
        Log.e(getClass().getName(), "message是:" + message);
        String topic = sn + myTopic;
        String topicComputerStatus = sn + computerStatusTopic;
        String topicCsq = sn + csqTopic;
        String topicBattery = sn + batteryTopic;
        String topicPowerStatus = sn + powerStatusTopic;
        String topicBacklightStatus = sn + backlightStatusTopic;
        String topicClockStatus = sn + clockStatusTopic;
        String topicStolenStatus = sn + stolenStatusTopic;
        String topicSsdCommand = sn + ssdCommandTopic;
        String topicUsbStatus = sn + usbStatusTopic;
        String topicTypeStatus = sn + typeStatusTopic;
        String topicWifiStatus = sn + wifiStatusTopic;
        String topicBluetoothStatus = sn + bluetoothStatusTopic;
        Integer qos = 0;
        Boolean retained = false;
        // 最后的遗嘱
        // MQTT本身就是为信号不稳定的网络设计的，所以难免一些客户端会无故的和Broker断开连接。
        //当客户端连接到Broker时，可以指定LWT，Broker会定期检测客户端是否有异常。
        //当客户端异常掉线时，Broker就往连接时指定的topic里推送当时指定的LWT消息。

        try {
            conOpt.setWill(sn+"/command", message.getBytes(), qos, false);
//            conOpt.setWill(topicComputerStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicCsq, message.getBytes(), qos, false);
//            conOpt.setWill(topicBattery, message.getBytes(), qos, false);
//            conOpt.setWill(topicPowerStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicBacklightStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicClockStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicStolenStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicSsdCommand, message.getBytes(), qos, false);
//            conOpt.setWill(topicUsbStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicTypeStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicWifiStatus, message.getBytes(), qos, false);
//            conOpt.setWill(topicBluetoothStatus, message.getBytes(), qos, false);
        } catch (Exception e) {
            Log.i(TAG, "Exception Occured", e);
            doConnect = false;
            iMqttActionListener.onFailure(null, e);
        }

        if (doConnect) {
            doClientConnection();
        }

    }


    @Override
    public void onDestroy() {
        successType = 0;
        stopSelf();
        try {
            client.unregisterResources();
            client = null;
            Thread.sleep(50);
            if (client != null) {
                client.disconnect();
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
//        disconnect();
        super.onDestroy();
    }
    // 取消订阅
    public static void unSubscribe() {
        try {
            Thread.sleep(30);
            if (client != null) {
                client.unsubscribe(snType+"/#");
//                client.disconnect();

            }

        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
//        disconnect();
    }
    // 退出登录
    public static void exitLogin() {
        try {
            Thread.sleep(30);
            if (client != null) {
                if (client.isConnected()){
                    client.disconnect();
                }
            }

        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect() {
        try {
            if (client != null) {
                if(client.isConnected())
                    client.unsubscribe(snType+"/#");
                client.close();
                client.unregisterResources();
                client = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (client!=null){
            if (!client.isConnected() && isConnectIsNormal()) {
                try {
                    client.connect(conOpt, null, iMqttActionListener);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    // MQTT是否连接成功
    private static int successType = 0;
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            successType = 200;
            Log.i(TAG, "连接成功 ");
            try {
                // 订阅myTopic话题
                if (client!=null&& !snType.equals("")){
                    client.subscribe(snType + "/#", 1);
                }
//                client.subscribe(snType + myTopic, 1);
//                client.subscribe(snType + computerStatusTopic, 1);
//                client.subscribe(snType + csqTopic, 1);
//                client.subscribe(snType + batteryTopic, 1);
//                client.subscribe(snType + powerStatusTopic, 1);
//                client.subscribe(snType + backlightStatusTopic, 1);
//                client.subscribe(snType + clockStatusTopic, 1);
//                client.subscribe(snType + stolenStatusTopic, 1);
//                client.subscribe(snType + ssdCommandTopic, 1);
//                client.subscribe(snType + usbStatusTopic, 1);
//                client.subscribe(snType + typeStatusTopic, 1);
//                client.subscribe(snType + wifiStatusTopic, 1);
//                client.subscribe(snType + bluetoothStatusTopic, 1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.d("MQTTService, ", "失去连接，重连0");
            // 连接失败，重连
//            doClientConnection();//20230906
            MQTTMessage entity = new MQTTMessage();
            entity.setType("连接失败");
            entity.setPosition(position);
            EventBus.getDefault().postSticky(entity);
            if (client!=null){
                try {
                    client.subscribe(snType+"/#",1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };



    private ExecutorService pool = Executors.newFixedThreadPool(10);

    class MessageHandler implements Runnable {
        MqttMessage message;
        String topic;

        public MessageHandler(String topic, MqttMessage message) {
            this.message = message;
            this.topic = topic;
        }

        public void run() {
            String str1 = new String(message.getPayload());
//            Log.e("mqtt坐标数据外==>",str1);

            if (IGetMessageCallBack != null) {
//                Log.e("topic==>",topic);
                MQTTMessage entity = new MQTTMessage();
                if (topic.contains("/coordinate_data")) {
                    Log.e("mqtt设备坐标==>", str1);
//                    IGetMessageCallBack.setMessage(str1, position, topic);
                    entity.setType("设备坐标");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/csq_data")) {//信号状态
                    Log.e("mqtt信号强度==>", str1);
//                    IGetMessageCallBack.setCsq(str1, position);
                    entity.setType("信号强度");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/battery")) {//电量状态
                    Log.e("mqtt设备电量==>", str1);
//                    IGetMessageCallBack.setBattery(str1, position);
                    entity.setType("设备电量");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/destory_info")) {//销毁

                } else if (topic.contains("/power_status")) {//power键状态
                    Log.e("mqttPower键状态==>", str1);
//                    IGetMessageCallBack.setPowerStatus(str1, position);
                    entity.setType("Power键状态");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/backlight_status")) {//背光状态
                    Log.e("mqtt背光状态==>", str1);
//                    IGetMessageCallBack.setBacklightStatus(str1, position);
                    entity.setType("背光状态");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/clock_status")) {//锁机状态
                    Log.e("mqtt锁机状态==>", str1);
//                    IGetMessageCallBack.setClockStatus(str1, position);
                    entity.setType("锁机状态");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/stolen_status")) {//被盗状态
                    Log.e("mqtt被盗状态==>", str1);
//                    IGetMessageCallBack.setStolenStatus(str1, position);
                    entity.setType("被盗状态");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/computer_status")) {//设备开机状态
                    Log.e("mqtt设备开机状态==>", str1);
//                    IGetMessageCallBack.setComputerStatus(str1, position);
                    entity.setType("开机状态");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/ssd_command")) {//硬盘状态
                    if (str1.contains("-")) {
                        Log.e("mqtt设备硬盘SN号==>", str1);
//                        IGetMessageCallBack.setSsdCommandSN(str1, position);
                        entity.setType("设备硬盘SN号");
                        entity.setMessage(str1);
                        entity.setPosition(position);
                        EventBus.getDefault().postSticky(entity);
                    } else {
//                        Log.e("mqtt设备硬盘状态==>", str1);
//                        IGetMessageCallBack.setSsdCommand(str1, position);
                        entity.setType("设备硬盘状态");
                        entity.setMessage(str1);
                        entity.setPosition(position);
                        EventBus.getDefault().postSticky(entity);
                    }
                } else if (topic.contains("/usb_status")) {//USB端口状态
                    Log.e("mqtt设备USB端口状态==>", str1);
//                    IGetMessageCallBack.setUsbStatus(str1, position);
                    entity.setType("usb");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/type_status")) {//Type-C端口状态
                    Log.e("mqtt设备Type-C端口状态==>", str1);
//                    IGetMessageCallBack.setTypeStatus(str1, position);
                    entity.setType("type-c");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/wifi_status")) {//wifi端口状态
                    Log.e("mqtt设备wifi端口状态==>", str1);
//                    IGetMessageCallBack.setWifiStatus(str1, position);
                    entity.setType("wifi");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                } else if (topic.contains("/bluetooth_status")) {//蓝牙端口状态
                    Log.e("mqtt设备蓝牙端口状态==>", str1);
//                    IGetMessageCallBack.setBlueToothStatus(str1, position);
                    entity.setType("蓝牙");
                    entity.setMessage(str1);
                    entity.setPosition(position);
                    EventBus.getDefault().postSticky(entity);
                }

            }
            String str2 = snType + topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.i(TAG, "messageArrived:" + str1);
            Log.i(TAG, str2);
        }
    }

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            pool.execute(new MessageHandler(topic, message));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
            try {
                Log.d("IMqttDeliveryToken",
                        deliveryToken.getMessage().toString());
            } catch (MqttException ex) {
                Log.d("Exception, ", ex.toString());
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.d("MQTTService, ", "失去连接，重连1");
            doClientConnection();
//            if (client!=null){
//                try {
//                    client.subscribe(snType+"/#",1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }


        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        String snType = intent.getStringExtra("snType");
        String snPwd = intent.getStringExtra("snPwd");
        Log.e(getClass().getName(), "onBind");
        Log.e(getClass().getName() + "onBind", snType+snPwd);
        init(snType,snPwd);
        return new CustomBinder();
    }

    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack) {
        this.IGetMessageCallBack = IGetMessageCallBack;
    }

    public class CustomBinder extends Binder {
        public MQTTService getService() {
            return MQTTService.this;
        }
    }

    public void toCreateNotification(String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MQTTService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);//3、创建一个通知，属性太多，使用构造器模式

        Notification notification = builder
                .setTicker("mqtt测试标题")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText(message)
                .setContentInfo("")
                .setContentIntent(pendingIntent)//点击后才触发的意图，“挂起的”意图
                .setAutoCancel(true)        //设置点击之后notification消失
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(0, notification);
        notificationManager.notify(0, notification);

    }
}

