package com.chuzhi.xzyx.ui.mqtt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chuzhi.xzyx.app.MyApplication;
import com.chuzhi.xzyx.utils.DeviceIdUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Author : wyh
 * @Time : On 2023/7/17 17:05
 * @Description : MyMqttService
 */
public class MyMqttService extends Service {

    public final static String TAG = MyMqttService.class.getSimpleName();
    public static MqttAndroidClient mqttAndroidClient;
    private static MqttConnectOptions mMqttConnectOptions;
    public static String HOST = "tcp://mqtt.chuzhi.cn";//服务器地址（协议+地址+端口号）
    public String USERNAME = "emqx_u";//用户名
    public String PASSWORD = "public";//密码
    public static String PUBLISH_TOPIC = "/coordinate_data";      //设备坐标主题 发布主题
//    public static String RESPONSE_TOPIC = Config.getMqResponseTopic();//响应主题
    public String CLIENTID = "Android:"+DeviceIdUtils.getDeviceId(MyApplication.Companion.getInstance());//设备唯一标识

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_NOT_STICKY;//非粘性的 service强制杀死后，不会尝试重新启动service
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开启服务
     */
    public static void startService(Context mContext) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(new Intent(mContext, MyMqttService.class));
        } else {
            mContext.startService(new Intent(mContext, MyMqttService.class));
        }
//        mContext.startService(new Intent(mContext, MyMqttService.class));
    }

    /**
     * 发布 （模拟其他客户端发布消息）
     *
     * @param message 消息
     */
    public static void publish(String message) {
        String topic = PUBLISH_TOPIC;
        Integer qos = 1;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     *
     * @param message 消息
     */
    public static void response(String message) {
        String topic = PUBLISH_TOPIC;
        Integer qos = 1;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        Log.i(TAG, "初始化MQ" + serverURI);
        if (mqttAndroidClient == null) {
            mqttAndroidClient = new MqttAndroidClient(this, serverURI, CLIENTID);
            mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        }
        if (mMqttConnectOptions == null) {
            mMqttConnectOptions = new MqttConnectOptions();
            mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
            mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
            mMqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
            mMqttConnectOptions.setUserName(USERNAME); //设置用户名
            mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        }
        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = 1;
        Boolean retained = true;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured");
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private static void doClientConnection() {
        try {
            if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
                Log.i(TAG, "连接MQTT服务器" + HOST);
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断网络是否连接
     */
    private static boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.Companion.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "没有可用网络doClientConnection");
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private static IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 " + HOST);
            try {
                if (mqttAndroidClient != null) {
                    mqttAndroidClient.subscribe(PUBLISH_TOPIC, 1);//订阅主题，参数：主题、服务质量
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage msgStr) throws Exception {

            try {
                String enCodeMsg = new String(msgStr.getPayload());
                Log.i(TAG, "收到消息： " + enCodeMsg);
                //收到消息，这里弹出Toast表示。如果需要更新UI，可以使用广播或者EventBus进行发送
                //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
                response("message arrived");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            doClientConnection();//连接断开，重连
        }
    };

    public static void disconnect(Context context) {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.unsubscribe(PUBLISH_TOPIC);
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient.disconnect(0); //断开连接
                mqttAndroidClient = null;
                context.stopService(new Intent(context, MyMqttService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}