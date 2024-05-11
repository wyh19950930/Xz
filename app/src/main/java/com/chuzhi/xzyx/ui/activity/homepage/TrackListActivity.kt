package com.chuzhi.xzyx.ui.activity.homepage

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.databinding.ActivityTrackListBinding
import com.chuzhi.xzyx.ui.adapter.TrackListAdapter
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity
import com.chuzhi.xzyx.ui.presenter.TrackListPresenter
import com.chuzhi.xzyx.ui.view.TrackListView

/**
 * 轨迹列表activity
 */
class  TrackListActivity : BaseActivity<ActivityTrackListBinding, TrackListPresenter>(),
    TrackListView {

    private var computerSn = ""
    private var computerName = ""
    private var page = 1
    override fun createPresenter(): TrackListPresenter {
        return TrackListPresenter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.includeActTrackList.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActTrackList.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActTrackList.tvIncludeTitleTitle.text = "历史轨迹"
        binding.sltActTrackList.setRefreshHeader(ClassicsHeader(this))
        binding.sltActTrackList.setRefreshFooter(ClassicsFooter(this))
        binding.rlvActTrackList.layoutManager = LinearLayoutManager(this)
        createAdapter()
        computerSn = intent.getStringExtra("computerSn").toString()
        computerName = intent.getStringExtra("computerName").toString()
        if (computerSn!=""){
            presenter.trackInfo(page,computerSn)
        }
        if (computerName!=""){
            binding.includeActTrackList.tvIncludeTitleTitle.text = "$computerName-历史轨迹"
        }

    }

    private var trackListAdapter:TrackListAdapter?=null
    private fun createAdapter() {
        trackListAdapter = TrackListAdapter(this)
        binding.rlvActTrackList.adapter = trackListAdapter
    }

    override fun initData() {
        //下拉刷新上拉加载更多
        binding.sltActTrackList.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.trackInfo(page, computerSn)//请求文章列表
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
        binding.sltActTrackList.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed(Runnable {
                page++
                presenter.trackInfo(page, computerSn)//请求文章列表
                it.finishLoadMore()
            }, 1000)
//            ToastUtil.showShort(activity, "上拉加载")
        }
    }

    //轨迹列表回调
    private var list = ArrayList<TrackInfoListEntity.TrackDictDTO>()
    override fun trackInfo(msg: TrackInfoListEntity?) {
        if (msg!!.track_dict!=null&&msg.track_dict.size>0){
            binding.includeActTrackListNoneData.llIncludeNoneData.visibility = View.GONE
            binding.sltActTrackList.visibility = View.VISIBLE
            if (page == 1){
                list.clear()
            }
            if (msg.track_dict.size < 20) {
                binding.sltActTrackList.finishLoadMoreWithNoMoreData();
            } else {
                binding.sltActTrackList.setNoMoreData(false)
            }
            list.addAll(msg!!.track_dict as ArrayList<TrackInfoListEntity.TrackDictDTO>)
            trackListAdapter!!.setData(list)
            trackListAdapter!!.notifyDataSetChanged()
            trackListAdapter!!.setOnClickListener { view, position ->
                var intent = Intent(this,TrackDetailsActivity::class.java)
                intent.putExtra("trackData",list[position])
                intent.putExtra("trackSn",computerSn)
                intent.putExtra("trackName",computerName)
                startActivity(intent)
            }
        }else{
            binding.includeActTrackListNoneData.llIncludeNoneData.visibility = View.VISIBLE
            binding.sltActTrackList.visibility = View.GONE
        }
    }
}