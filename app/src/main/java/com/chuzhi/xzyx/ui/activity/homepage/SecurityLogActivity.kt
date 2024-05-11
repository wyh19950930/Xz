package com.chuzhi.xzyx.ui.activity.homepage

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.databinding.ActivitySecurityLogBinding
import com.chuzhi.xzyx.ui.adapter.SecurityLogListAdapter
import com.chuzhi.xzyx.ui.bean.rc.GeofenceRecordEntity
import com.chuzhi.xzyx.ui.presenter.SecurityLogPresenter
import com.chuzhi.xzyx.ui.view.SecurityLogView

/**
 * 安全日志activity
 */
class SecurityLogActivity : BaseActivity<ActivitySecurityLogBinding,SecurityLogPresenter>(),SecurityLogView {

    private var page = 1
    private var portSn = ""
    override fun createPresenter(): SecurityLogPresenter {
        return SecurityLogPresenter(this)
    }

    override fun initView() {
        binding.includeActSecLog.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActSecLog.tvIncludeTitleTitle.text = "安全日志"
        binding.includeActSecLog.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.sltActSecLog.setRefreshHeader(ClassicsHeader(this))
        binding.sltActSecLog.setRefreshFooter(ClassicsFooter(this))
        binding.rlvActSecLog.layoutManager = LinearLayoutManager(this)
        createAdapter()
        portSn = intent.getStringExtra("portSn").toString()
        presenter.getGeofenceRecord(page, portSn)
    }

    override fun initData() {
        //下拉刷新上拉加载更多
        binding.sltActSecLog.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.getGeofenceRecord(page, portSn)
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
        binding.sltActSecLog.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed(Runnable {
                page++
                presenter.getGeofenceRecord(page, portSn)
                it.finishLoadMore()
            }, 1000)
//            ToastUtil.showShort(activity, "上拉加载")
        }
    }

    //日志列表
    private var list = ArrayList<GeofenceRecordEntity.RecordListDTO>()
    override fun getGeofenceRecord(msg: GeofenceRecordEntity?) {
        if (msg!!.record_list!=null&&msg.record_list.size>0){
            binding.sltActSecLog.visibility = View.VISIBLE
            binding.includeActSecLogNoneData.llIncludeNoneData.visibility = View.GONE
            if (page == 1) {
                list.clear()
            }
            if (msg.record_list.size < 20) {
                binding.sltActSecLog.finishLoadMoreWithNoMoreData();
            } else {
                binding.sltActSecLog.setNoMoreData(false)
            }
            list.addAll(msg!!.record_list as ArrayList<GeofenceRecordEntity.RecordListDTO>)
            securityLogListAdapter!!.setData(list)
            securityLogListAdapter!!.notifyDataSetChanged()
        }else{
            binding.sltActSecLog.visibility = View.GONE
            binding.includeActSecLogNoneData.llIncludeNoneData.visibility = View.VISIBLE
        }
    }
    var securityLogListAdapter:SecurityLogListAdapter?=null
    fun createAdapter(){
        securityLogListAdapter = SecurityLogListAdapter(this)
        binding.rlvActSecLog.adapter = securityLogListAdapter
    }
}