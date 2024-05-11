package com.chuzhi.xzyx.ui.activity.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import com.luck.picture.lib.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityHpaddDeviceBinding
import com.chuzhi.xzyx.ui.bean.rc.ComputerInfoEntity
import com.chuzhi.xzyx.ui.presenter.HPAddDevicePresenter
import com.chuzhi.xzyx.ui.view.HPAddDeviceView
import com.chuzhi.xzyx.utils.*

/**
 * 首页添加设备activity（绑定电脑）
 */
class HPAddDeviceActivity : BaseActivity<ActivityHpaddDeviceBinding, HPAddDevicePresenter>(),
    HPAddDeviceView {
    var addDevicePop: CommenPop? = null
    private var rxPermissions: RxPermissions? = null
    private var permissionPop: AlertDialogIos? = null
    private var tipPop: CommenPop? = null
    override fun createPresenter(): HPAddDevicePresenter {
        return HPAddDevicePresenter(this)
    }

    override fun initView() {
        binding.includeActHpAddDevice.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActHpAddDevice.tvIncludeTitleTitle.text = "添加设备"
        binding.includeActHpAddDevice.ivIncludeTitleBack.setOnClickListener { finish() }
        rxPermissions = RxPermissions(this)
        addDevicePop =
            CommenPop.getNormalPopu(this, R.layout.pop_add_device, binding.llActHpAddDeviceTop)
        addDevicePop!!.isFocusable = true
        permissionPop()
    }

    override fun initData() {

        //扫描二维码
        binding.btnActHpAddDeviceScan.setOnClickListener {
            if (SingleOnClickUtil.isFastClick()) {
                permissionData()
            }
        }
        //显示输入sn号
        binding.llActHpAddDeviceSn.setOnClickListener {
            binding.llActHpAddDeviceSn.visibility = View.GONE
            binding.llActHpAddDeviceEtSn.visibility = View.VISIBLE
        }
        //搜索sn号
        binding.btnActHpAddDeviceSnSearch.setOnClickListener {
            val sn1 = binding.etActHpAddDeviceSn.text.toString().trim()
            val sn2 = binding.etActHpAddDeviceSn2.text.toString().trim()
            if (sn1 == "") {
                ToastUtils.showToast(this, "请输入sn1号")
            } else if (sn2 == "") {
                ToastUtils.showToast(this, "请输入sn2号")
            } else {
                sn1Flag = sn1
                sn2Flag = sn2
                presenter.computerInfo(sn1, sn2)
            }
        }
    }

    private fun permissionPop() {
        permissionPop = AlertDialogIos(this).builder()
            .setTitle("无法访问相机")
            .setMsg("小志云享需要访问相机以开启“扫一扫\"功能\n请在设置中开启权限")
//            .setNegativeButton("取消", R.color.gray,null)
            .setPositiveButton("设置", R.color.text_default, View.OnClickListener {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                //跳转应用消息，间接打开应用权限设置-效率高
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            })
    }

    @SuppressLint("CheckResult")
    private fun permissionData() {
        tipPopup()
        rxPermissions!!.request(
            Manifest.permission.CAMERA
        ).subscribe { granted ->

            if (granted) {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                // 创建IntentIntegrator对象
                var intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                intentIntegrator.setBarcodeImageEnabled(false)//设置是否保存图片
                intentIntegrator.setBeepEnabled(true)//设置扫码成功后的提示音是否显示
                intentIntegrator.setOrientationLocked(false)//该方法用于设置方向锁
                intentIntegrator.setPrompt("将二维码/条码放入框内，即可自动扫描")//写那句提示的话
                intentIntegrator.captureActivity = ScanQRCodeActivity::class.java // 设置自定义的activity
                intentIntegrator.initiateScan() // 开始扫描
            } else {
                permissionPop!!.show()
            }
        }
    }
    private fun tipPopup(){
        tipPop = CommenPop.getNormalPopu(this,R.layout.pop_tip,binding.llActHpAddDeviceTop)
        val contentView = tipPop!!.contentView
        val tvTip = contentView.findViewById<TextView>(R.id.tv_pop_tip)
        tvTip.text = "相机权限使用说明：\n用于扫描二维码等场景"
        tipPop!!.isOutsideTouchable = true
        tipPop!!.isFocusable = true
        CommenPop.backgroundAlpha(0.5f, this)
        tipPop!!.showAtLocation(
            binding.llActHpAddDeviceTop,
            Gravity.TOP,
            100,
            0
        )
    }
    //二维码扫描回调
    var sn1Flag = ""
    var sn2Flag = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 获取解析结果  二维码扫描结果
        var result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)//IntentResult
        if (result != null) {
            if (result.contents == null) {
                ToastUtils.showToast(this, "取消扫描")
//                addDevicePop()
            } else {
                if (result.contents != null) {
                    val split = result.contents.split("_")
                    if (split.size > 1) {
                        sn1Flag = split[0]
                        sn2Flag = split[1]
                        Log.e("split1 split2===>", sn1Flag+sn2Flag)
                        presenter.computerInfo(sn1Flag, sn2Flag)
                    } else {
                        sn1Flag = result.contents
                        Log.e("split1===>", sn1Flag)
                        presenter.computerInfo(sn1Flag, sn2Flag)
                    }
                } else {
                    ToastUtils.showToast(this, "扫描二维码错误")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //设备添加框（二维码直接弹出）
    private fun addDevicePop(msg: ComputerInfoEntity) {//type 1 手动输入 2 扫码输入
        var etSn1 = addDevicePop!!.contentView.findViewById<TextView>(R.id.et_pop_add_device_sn)
        var etSn2 = addDevicePop!!.contentView.findViewById<TextView>(R.id.et_pop_add_device_sn2)
        var tvModel =
            addDevicePop!!.contentView.findViewById<TextView>(R.id.tv_pop_add_device_model)
        var ivImg = addDevicePop!!.contentView.findViewById<ImageView>(R.id.iv_pop_add_device_img)
        var etName = addDevicePop!!.contentView.findViewById<EditText>(R.id.et_pop_add_device_name)
        var btnUpload =
            addDevicePop!!.contentView.findViewById<Button>(R.id.btn_pop_add_device_upload)
        CommenPop.backgroundAlpha(0.5f, this)
        addDevicePop!!.showAtLocation(binding.llActHpAddDeviceTop, Gravity.CENTER, 20, 0)
        etSn1.text = sn1Flag
        etSn2.text = sn2Flag
        tvModel.text = msg.code

        etName.filters = arrayOf<InputFilter>(SpaceFilter(), InputFilter.LengthFilter(10))

        btnUpload.setOnClickListener {
            if (SingleOnClickUtil.isFastClick()) {
                val str = etName.text.toString().trim()
                if (str == "") {
                    ToastUtils.showToast(this, "请输入设备名称")
                } else {
                    presenter.bindComputer(sn1Flag,sn2Flag, str)
                }
            }
        }
    }

    //查询设备信息回调
    override fun computerInfo(msg: ComputerInfoEntity?) {
        addDevicePop(msg!!)
    }

    //设备绑定成功回调
    override fun bindComputer(msg: BaseModel<String>?) {
        if (addDevicePop != null) {
            addDevicePop!!.dismiss()
        }
        SpUtils.setSharedStringData(this, "HomePageBinding", "已绑定")
        ToastUtils.showToast(this, msg!!.msg)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tipPop!=null){
            if (tipPop!!.isShowing){
                tipPop!!.dismiss()
            }
        }
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtils.showToast(this, msg)
    }
}