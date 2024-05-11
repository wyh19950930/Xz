package com.chuzhi.xzyx.ui.fragment.me

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseFragment
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.FragmentMeBinding
import com.chuzhi.xzyx.ui.activity.homepage.SecurityLogActivity
import com.chuzhi.xzyx.ui.activity.login.LoginActivity
import com.chuzhi.xzyx.ui.activity.me.*
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo
import com.chuzhi.xzyx.ui.bean.bbs.PraiseCountEntity
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.presenter.MeFragPresenter
import com.chuzhi.xzyx.ui.view.MeFragView
import com.chuzhi.xzyx.utils.*
import org.greenrobot.eventbus.EventBus

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeFragment : BaseFragment<FragmentMeBinding, MeFragPresenter>(), MeFragView {
    private var param1: String? = null
    private var param2: String? = null
    private var outLoginPop : AlertDialogIos? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }

    override fun createPresenter(): MeFragPresenter {
        return MeFragPresenter(this)
    }
    private var pendingIntent: PendingIntent? = null

    private val CHANNEL_ID = "MY_CHANNEL_ID"
    private val CHANNEL_NAME = "MY_CHANNEL_NAME"
    override fun initView() {
        //把状态栏的高度添加到背景中
        val statusBarHeight = AndroidBarUtils.getStatusBarHeight(requireActivity())
        val layoutParams = binding.llFragMeTitle.layoutParams
        layoutParams.height=layoutParams.height+statusBarHeight
        binding.llFragMeTitle.layoutParams = layoutParams
        //顶部《我的》距顶部一个状态栏的高度
        val tvTop = binding.tvFragMeTop.layoutParams as ViewGroup.MarginLayoutParams
        tvTop.setMargins(0,statusBarHeight,0,0)
        binding.tvFragMeTop.layoutParams = tvTop
        //获赞数linearlayout同样距离顶部加上状态栏的高度
        val llPraise = binding.llFragMePraise.layoutParams as ViewGroup.MarginLayoutParams
        val llPraiseTop= DpPxUtils.dpToPx(activity, 30)
        llPraise.setMargins(DpPxUtils.dpToPx(activity,20),
            layoutParams.height-llPraiseTop,DpPxUtils.dpToPx(activity,20),0)
        binding.llFragMePraise.layoutParams = llPraise

        outLogin()
        binding.llFragMeEdit.setOnClickListener {
            startActivity(Intent(activity,MeInformationActivity::class.java))
        }
        //账号与安全
        binding.llFragMeZhAq.setOnClickListener {
            startActivity(Intent(activity,AccountSecurityActivity::class.java))
        }
        //退出登录
        binding.btnFragMeOutLogin.setOnClickListener {
            outLoginPop!!.show()
        }
        //设备管理
        binding.btnFragMeDeviceManage.setOnClickListener {
            startActivity(Intent(activity,DeviceManageActivity::class.java))
        }
        //我的发布
        binding.llFragMeRelease.setOnClickListener {
            startActivity(Intent(activity,MyReleaseActivity::class.java))
        }
        //我的回复
        binding.llFragMeMyReply.setOnClickListener {
            startActivity(Intent(activity,MyReplyActivity::class.java))
        }
        //安全设置
        binding.llFraMeSecuritySet.setOnClickListener {
            startActivity(Intent(activity,SecuritySettingActivity::class.java))
        }
        //隐私政策
        binding.llFragPrivacyPolicy.setOnClickListener {
            startActivity(Intent(activity,PrivacyPolicyActivity::class.java))
        }
        //关于我们
        binding.llFragMeAbout.setOnClickListener {
            startActivity(Intent(activity,AboutActivity::class.java))
        }
        //测试mqtt
        binding.llFragMeFollow.setOnClickListener {
//            startActivity(Intent(activity,MqttActivity::class.java))
//            startActivity(Intent(activity,GoogleMapActivity::class.java))
//            startActivity(Intent(activity,NetWorkSignalActivity::class.java))
//            startActivity(Intent(activity,DynamicActivity::class.java))
//            startActivity(Intent(activity,SMBActivity::class.java))
        }

//        binding.llFragMeFans.setOnClickListener {
//            /**
//             * 创建通知管理类NotificationManager的实例，用来管理通知
//             */
//            val manager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
//
//            /**
//             * 创建通知类Notification实例(用来存储通知所需的信息)； 一共三个参数：
//             * 1)、指定通知使用的图标，如：R.drawable.ic_launcher ；
//             * 2)、指定通知的ticker内容，通知被创建的时候，在状态栏一闪而过，属于瞬时提示信息。
//             * 3)、指定通知被创建的时间，以毫秒为单位，下拉状态栏时，这个时间会显示在相应的通知上。
//             */
//
//            var notificationIntent: Intent? = Intent(activity, LocationTrackingActivity::class.java)
//            val computerListDTO = ComputerListEntity.ComputerListDTO()
//
//            notificationIntent!!.putExtra("deviceData", computerListDTO)
//            notificationIntent!!.putExtra("deviceType", 0)
//            pendingIntent = PendingIntent.getActivity(activity, 1001, notificationIntent, 0)
//
//            var notification: Notification? = null
//
//            notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_LOW
//                )
//                manager!!.createNotificationChannel(channel)
//                Notification.Builder(activity, CHANNEL_ID)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(getString(R.string.text_keep_alive))
//                    .setSmallIcon(R.mipmap.app_icon_foreground)
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(false)
//                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                    .build()
//            } else {
//                NotificationCompat.Builder(requireActivity())
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(getString(R.string.text_keep_alive))
//                    .setSmallIcon(R.mipmap.app_icon_foreground)
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(false)
//                    .setPriority(NotificationCompat.PRIORITY_MIN)
//                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                    .build()
//            }
//            /**
//             * 手机设置的默认提示音
//             */
//            val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            /**
//             * sound属性是一个 Uri 对象。 可以在通知发出的时候播放一段音频，这样就能够更好地告知用户有通知到来.
//             * 如：手机的/system/media/audio/ringtones 目录下有一个 Basic_tone.ogg音频文件，
//             * 可以写成： Uri soundUri = Uri.fromFile(new
//             * File("/system/media/audio/ringtones/Basic_tone.ogg"));
//             * notification.sound = soundUri; 我这里为了省事，就去了手机默认设置的铃声
//             */
//            notification.sound = uri
//            /**
//             * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
//             * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
//             */
//            val vibrates = longArrayOf(0, 1000, 1000, 1000)
//            notification.vibrate = vibrates
//            /**
//             * 手机处于锁屏状态时， LED灯就会不停地闪烁， 提醒用户去查看手机,下面是绿色的灯光一 闪一闪的效果
//             */
//            notification.ledARGB = Color.GREEN // 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
//
//            notification.ledOnMS = 1000 // 指定 LED 灯亮起的时长，以毫秒为单位
//
//            notification.ledOffMS = 1000 // 指定 LED 灯暗去的时长，也是以毫秒为单位
//
//            notification.flags = Notification.FLAG_SHOW_LIGHTS // 指定通知的一些行为，其中就包括显示
//            /**
//             * 启动通知. 两个参数： 1)、id，保证每个通知的id唯一； 2)、Notification对象
//             */
//            if (manager!=null){
//                manager!!.notify(1001, notification)
//            }
//        }
//        presenter.praiseCount()
//        presenter.oauthSetUserInfo()
    }

    override fun initData() {
        binding.tvFragMeUsername.text = SpUtils.getSharedStringData(activity,"Username")
        var avatar = SpUtils.getSharedStringData(activity,"Avatar")
        //头像
        Glide.with(mContext).load(avatar)
            .placeholder(R.drawable.my_img_portrait_default)
            .error(R.drawable.my_img_portrait_default)
            .fallback(R.drawable.my_img_portrait_default)
            .transform(
                CenterCrop(),
                CircleCrop()
            )
            .into(binding.ivActMeHead)
    }

    private fun outLogin(){
        val token = SpUtils.getSharedStringData(activity, "Token")
        outLoginPop = AlertDialogIos(activity).builder()
            .setTitle("提示")
            .setMsg("是否退出登录?")
            .setNegativeButton("取消", R.color.gray,null)
            .setPositiveButton("确定", R.color.text_default,View.OnClickListener {
                if (token.isNotEmpty()){
                    presenter.bbsOutToken(token)
                }
            })
    }

    override fun bbsOutToken(msg: BaseModel<String>?) {
        ToastUtil.showShort(activity,msg!!.msg)
        SpUtils.setSharedStringData(activity,"Token","")
        SpUtils.setSharedStringData(activity,"Username","")
        SpUtils.setSharedStringData(activity,"HomePageBinding","")
        SpUtils.setSharedStringData(activity,"Avatar","")
        SpUtils.setSharedList(activity,"userComputerList",ArrayList<ComputerListEntity.ComputerListDTO>())
        EventBus.getDefault().postSticky("退出登录")
        //清空栈内的Activity
        ActivityCollectorUtil.finishAllActivity()
        //跳转登录页面
        var intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    //获赞/点赞/粉丝数统计
    override fun praiseCount(msg: PraiseCountEntity?) {
        binding.tvFragMePraise.text = msg!!.praise_count.toString()
        binding.tvFragMeFollow.text = msg.follow_count.toString()
        binding.tvFragMeFan.text = msg.fan_count.toString()
        binding.tvFragMeArtcle.text = msg.artcle_count.toString()
        binding.tvFragMeAnswer.text = msg.answer_count.toString()

    }

    //个人信息
    override fun oauthSetUserInfo(msg: OauthSetUserInfo?) {
        if (msg!!.avatar!=""){
            //头像
            Glide.with(mContext).load(msg!!.avatar)
                .placeholder(R.drawable.my_img_portrait_default)
                .error(R.drawable.my_img_portrait_default)
                .fallback(R.drawable.my_img_portrait_default)
                .transform(
                    CenterCrop(),
                    CircleCrop()
                )
                .into(binding.ivActMeHead)
        }
        binding.tvFragMeUsername.text = msg.nickname
    }

    override fun onResume() {
        super.onResume()
        val avatar = SpUtils.getSharedStringData(activity, "Avatar")
        binding.tvFragMeUsername.text = SpUtils.getSharedStringData(activity,"Username")
        //头像
        Glide.with(mContext).load(avatar)
            .placeholder(R.drawable.my_img_portrait_default)
            .error(R.drawable.my_img_portrait_default)
            .fallback(R.drawable.my_img_portrait_default)
            .transform(
                CenterCrop(),
                CircleCrop()
            )
            .into(binding.ivActMeHead)

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (view != null && isVisibleToUser) {
            presenter.praiseCount()
            presenter.oauthSetUserInfo()
            Log.e("FindFragmentHint","isVisibleToUser=$isVisibleToUser")
        }else{
            Log.e("FindFragmentHint","isVisibleToUser=$isVisibleToUser")
        }
    }
    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(activity,msg)
    }

}
