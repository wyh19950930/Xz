package com.chuzhi.xzyx.ui.activity.homepage

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityFenceListBinding
import com.chuzhi.xzyx.ui.adapter.FenceListAdapter
import com.chuzhi.xzyx.ui.bean.mqtt.HomePageMQTTMessage
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity
import com.chuzhi.xzyx.ui.presenter.FenceListPresenter
import com.chuzhi.xzyx.ui.view.FenceListView
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.ToastUtil
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 围栏列表activity
 */
class FenceListActivity : BaseActivity<ActivityFenceListBinding, FenceListPresenter>(),
    FenceListView {
    private var computerData: ComputerListEntity.ComputerListDTO? = null
    private var computerSn = ""
    private var page = 1
    override fun createPresenter(): FenceListPresenter {
        return FenceListPresenter(this)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        binding.includeActFenceList.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActFenceList.tvIncludeTitleTitle.text = "设置围栏"
        binding.includeActFenceList.ivIncludeTitleBack.setOnClickListener { finish() }
        detFenceDialog = AlertDialogIos(this).builder()
        binding.sltActFenceList.setRefreshHeader(ClassicsHeader(this))
        binding.sltActFenceList.setRefreshFooter(ClassicsFooter(this))
        binding.rlvActFenceList.layoutManager = LinearLayoutManager(this)
        binding.rlvActFenceList.isItemViewSwipeEnabled = false//侧滑删除，默认关闭
        createAdapter()
        computerData =
            intent.getSerializableExtra("computerData") as ComputerListEntity.ComputerListDTO
        computerSn = computerData!!.sn
        //新增围栏
        binding.btnActFenceListAdd.setOnClickListener {

            var intent = Intent(this, FenceSettingActivity::class.java)
            intent.putExtra("jumpType", 1)
            intent.putExtra("computerSn", computerSn)
            intent.putExtra("computerCenter", computerData!!.current_coordinate)
            startActivity(intent)
        }
        presenter.geofenceList(computerSn)
        //下拉刷新
        binding.sltActFenceList.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.geofenceList(computerSn)
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
    }

    override fun initData() {
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMqttPoint(msg: HomePageMQTTMessage) {
        Log.e("首页传来的坐标点",msg.toString())
        if (msg!=null){
            if (msg.point!=null&&msg.point.contains(",")){
                if (computerData!=null){
                    computerData!!.current_coordinate = msg.point
                }
            }
        }
    }
    //围栏列表回调
    private var list = ArrayList<GeofenceListEntity.GeofenceListDTO>()
    override fun geofenceList(msg: GeofenceListEntity?) {
        if (msg!!.geofence_list != null && msg.geofence_list.size > 0) {
            AppCache.getInstance().fenceType = msg.geofence_list[0].every
            if (msg!!.geofence_list.size >=3){
                binding.btnActFenceListAdd.isEnabled = false
                binding.btnActFenceListAdd.text = "围栏个数已达上限"
            }else{
                binding.btnActFenceListAdd.isEnabled = true
                binding.btnActFenceListAdd.text = "新建围栏"
            }
            binding.sltActFenceList.visibility = View.VISIBLE
            binding.includeActFenceListNoneData.llIncludeNoneData.visibility = View.GONE
            if (page == 1){
                list.clear()
            }
            list.addAll(msg.geofence_list)
            fenceListAdapter!!.setData(list)
            fenceListAdapter!!.notifyDataSetChanged()
            fenceListAdapter!!.setOnClickListener(object :FenceListAdapter.OnClickListener{
                override fun onClickListener( position: Int) {
                    var intent = Intent(this@FenceListActivity,FenceSettingActivity::class.java)
                    intent.putExtra("jumpType",2)
                    intent.putExtra("computerSn", computerSn)
                    intent.putExtra("computerData",list[position])
                    startActivity(intent)
                }

                override fun onDetClickListener(position: Int, swipeMenuLayout: SwipeMenuLayout?) {
                    detFenceDialog(list[position],swipeMenuLayout)
                }

                override fun onLongClickListener(view: View?, position: Int) {
//                    detFenceDialog(list[position])
                }
            })

        } else {
            binding.sltActFenceList.visibility = View.GONE
            binding.includeActFenceListNoneData.llIncludeNoneData.visibility = View.VISIBLE
        }
    }

    //删除围栏回调
    override fun deleteGeofence(msg: BaseModel<String>?) {
        ToastUtil.showShort(this,msg!!.msg)
        presenter.geofenceList(computerSn)
    }

    private var fenceListAdapter: FenceListAdapter? = null
    private fun createAdapter() {
        fenceListAdapter = FenceListAdapter(this)
        binding.rlvActFenceList.adapter = fenceListAdapter
    }
    //删除围栏弹框
    private var detFenceDialog: AlertDialogIos? = null
    private fun detFenceDialog(data :GeofenceListEntity.GeofenceListDTO, swipeMenuLayout: SwipeMenuLayout?) {
        detFenceDialog!!.setTitle("提示")
            .setMsg("是否对” ${data!!.name} “进行删除？")
            .setNegativeButton("取消", R.color.gray,  View.OnClickListener {
                swipeMenuLayout!!.quickClose()
            })
            .setPositiveButton("确定", R.color.text_default, View.OnClickListener {
                presenter.deleteGeofence(data!!.id,computerSn )
                swipeMenuLayout!!.quickClose()
            })
        detFenceDialog!!.show()
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        if (msg=="未设置围栏!"){
            AppCache.getInstance().fenceType = 0
            binding.sltActFenceList.visibility = View.GONE
            binding.includeActFenceListNoneData.llIncludeNoneData.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        presenter.geofenceList(computerSn)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}