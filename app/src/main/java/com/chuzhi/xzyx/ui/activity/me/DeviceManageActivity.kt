package com.chuzhi.xzyx.ui.activity.me

import android.content.Intent
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityDeviceManageBinding
import com.chuzhi.xzyx.ui.activity.homepage.HPAddDeviceActivity
import com.chuzhi.xzyx.ui.activity.homepage.SecurityLogActivity
import com.chuzhi.xzyx.ui.adapter.DeviceManageListAdapter
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.presenter.DeviceManagePresenter
import com.chuzhi.xzyx.ui.view.DeviceManageView
import com.chuzhi.xzyx.utils.*
import com.mcxtzhang.swipemenulib.SwipeMenuLayout

/**
 * 设备管理activity
 */
class DeviceManageActivity : BaseActivity<ActivityDeviceManageBinding, DeviceManagePresenter>(),
    DeviceManageView {

    var updateNamePop: CommenPop? = null
    override fun createPresenter(): DeviceManagePresenter {
        return DeviceManagePresenter(this)
    }

    override fun initView() {
        unbindDialog = AlertDialogIos(this).builder()
        binding.includeActDeviceManage.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActDeviceManage.tvIncludeTitleTitle.text = "设备管理"
        binding.includeActDeviceManage.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.btnActDeviceManageAdd.setOnClickListener {
            var intent = Intent(this,HPAddDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.rlvActDeviceManage.layoutManager = LinearLayoutManager(this)
        binding.rlvActDeviceManage.isItemViewSwipeEnabled = false//侧滑删除，默认关闭
        updateNamePop = CommenPop.getNormalPopu(
            this,
            R.layout.pop_update_device_name,
            binding.llActDeviceManageTop
        )
        updateNamePop!!.isFocusable = true
        createAdapter()
        presenter.userComputerList()
    }

    override fun initData() {
    }

    //设备列表回调
    private var deviceManageAdapter: DeviceManageListAdapter? = null
    private var list = ArrayList<ComputerListEntity.ComputerListDTO>()
    override fun userComputerList(msg: ComputerListEntity?) {
        if (msg!!.computer_list != null && msg.computer_list.size > 0) {
            //请求数据缓存到本地
            SpUtils.setSharedList(this,"userComputerList",msg!!.computer_list)
            binding.llActDeviceManage.visibility = View.VISIBLE
            binding.includeActDeviceManageNoneData.llIncludeNoneData.visibility = View.GONE
            list.clear()
            list.addAll(msg.computer_list)
            deviceManageAdapter!!.setData(list)
            deviceManageAdapter!!.notifyDataSetChanged()
            deviceManageAdapter!!.setOnClickListener(object :
                DeviceManageListAdapter.OnClickListener {
                override fun onClickListener(view: View?, position: Int) {
                    when (view!!.id) {
                        R.id.btn_item_device_manage -> {//编辑昵称
                            updateName(list[position])
                        }
                        R.id.ll_item_device_manage_aq_rz ->{//安全日志
                            val intent = Intent(this@DeviceManageActivity, SecurityLogActivity::class.java)
                            intent.putExtra("portSn", list[position].sn)
                            startActivity(intent)
                        }
                    }
                }

                override fun onDetClickListener(position: Int,swipeMenuLayout:SwipeMenuLayout?) {
                    if (AppCache.getInstance().riskOperations == 0) {
                            unbindComputerDialog(list[position],swipeMenuLayout)
                    } else {
                        ToastUtil.showLong(this@DeviceManageActivity, "设备正在执行其他操作，请稍后！")
                    }
                }

                override fun onLongClickListener(view: View?, position: Int) {//暂时弃用
                    when (view!!.id) {
                        R.id.ll_item_device_manage -> {//解绑
                            if (AppCache.getInstance().riskOperations == 0) {
//                                unbindComputerDialog(list[position])
                            } else {
                                ToastUtil.showLong(this@DeviceManageActivity, "设备正在执行其他操作，请稍后！")
                            }
                        }

                    }
                }
            })
        } else {
            binding.llActDeviceManage.visibility = View.GONE
            binding.includeActDeviceManageNoneData.llIncludeNoneData.visibility = View.VISIBLE
            SpUtils.setSharedStringData(this, "HomePageBinding", "")
            list.clear()
            SpUtils.setSharedList(this,"userComputerList",list)
        }
    }

    //解绑成功回调
    override fun unbindComputer(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, msg!!.msg)
        presenter.userComputerList()
    }

    //修改设备名称回调
    override fun alterBindInfo(msg: BaseModel<String>?) {
        if (updateNamePop != null) {
            updateNamePop!!.dismiss()
            ToastUtil.showLong(this, msg!!.msg)
            presenter.userComputerList()
        }
    }

    fun updateName(data: ComputerListEntity.ComputerListDTO) {
        var etName =
            updateNamePop!!.contentView.findViewById<EditText>(R.id.et_pop_update_device_name)
        var btnUpload =
            updateNamePop!!.contentView.findViewById<Button>(R.id.btn_pop_update_device_name)
        CommenPop.backgroundAlpha(0.5f, this)
        updateNamePop!!.showAtLocation(binding.llActDeviceManageTop, Gravity.CENTER, 20, 0)
        etName.setText(data.name)
        etName.filters = arrayOf<InputFilter>(SpaceFilter(),InputFilter.LengthFilter(10))
        btnUpload.setOnClickListener {
            presenter.alterBindInfo(data.id, etName.text.toString())
        }
    }

    private fun createAdapter() {
        deviceManageAdapter = DeviceManageListAdapter(this)
        binding.rlvActDeviceManage.adapter = deviceManageAdapter
    }

    //解绑弹框
    private var unbindDialog: AlertDialogIos? = null
    private fun unbindComputerDialog(data: ComputerListEntity.ComputerListDTO,swipeMenuLayout:SwipeMenuLayout?) {
        unbindDialog!!.setTitle("提示")
            .setMsg("是否对"+" '"+data.name+"' "+"进行解绑?")
            .setNegativeButton("取消", R.color.gray, View.OnClickListener {
                swipeMenuLayout!!.quickClose()
            })
            .setPositiveButton("确定", R.color.text_default, View.OnClickListener {
                presenter.unbindComputer(data!!.id.toString())
                swipeMenuLayout!!.quickClose()
            })
        unbindDialog!!.show()
    }

    override fun onResume() {
        super.onResume()
        presenter.userComputerList()
    }
    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this, msg)
    }
}