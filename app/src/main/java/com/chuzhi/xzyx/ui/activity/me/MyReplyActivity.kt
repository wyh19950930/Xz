package com.chuzhi.xzyx.ui.activity.me

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.databinding.ActivityMyReplyBinding
import com.chuzhi.xzyx.ui.activity.find.FindDetailsActivity
import com.chuzhi.xzyx.ui.adapter.MyReplyAdapter
import com.chuzhi.xzyx.ui.bean.bbs.AnswerListEntity
import com.chuzhi.xzyx.ui.presenter.MyReplyPresenter
import com.chuzhi.xzyx.ui.view.MyReplyView

/**
 * 我的回复
 */
class MyReplyActivity : BaseActivity<ActivityMyReplyBinding, MyReplyPresenter>(),MyReplyView {

    private var page = 1
    override fun createPresenter(): MyReplyPresenter {
        return MyReplyPresenter(this)
    }

    override fun initView() {
        binding.includeActMyReply.tvIncludeTitleTitle.text = "我的回复"
        binding.includeActMyReply.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActMyReply.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.sltActMyReply.setRefreshHeader(ClassicsHeader(this))
        binding.sltActMyReply.setRefreshFooter(ClassicsFooter(this))
        binding.rlvActMyReply.layoutManager = LinearLayoutManager(this)
        presenter.answerList(page)
        createAdapter()
    }

    override fun initData() {
        //下拉刷新上拉加载更多
        binding.sltActMyReply.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.answerList(page)
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
        binding.sltActMyReply.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed(Runnable {
                page++
                presenter.answerList(page)
                it.finishLoadMore()
            }, 1000)
//            ToastUtil.showShort(activity, "上拉加载")
        }
    }
    //我的回复列表
    private var list = ArrayList<AnswerListEntity.AnswerListDTO>()
    var myReplyAdapter: MyReplyAdapter? = null
    override fun answerList(msg: AnswerListEntity?) {
        if (msg!!.answer_list!=null&&msg.answer_list.size>0){
            binding.includeActMyReplyNoneData.llIncludeNoneData.visibility = View.GONE
            binding.sltActMyReply.visibility = View.VISIBLE
            if (page == 1){
                list.clear()
            }
            if (msg.answer_list.size < 20) {
                binding.sltActMyReply.finishLoadMoreWithNoMoreData();
            } else {
                binding.sltActMyReply.setNoMoreData(false)
            }
            list.addAll(msg!!.answer_list as ArrayList<AnswerListEntity.AnswerListDTO>)
            myReplyAdapter!!.setData(list)
            myReplyAdapter!!.notifyDataSetChanged()
            myReplyAdapter!!.setOnClickListener { view, position ->
                var intent = Intent(this, FindDetailsActivity::class.java)
                intent.putExtra("findItemId", list[position].article_id)
                startActivity(intent)
            }
        }else{
            binding.includeActMyReplyNoneData.llIncludeNoneData.visibility = View.VISIBLE
            binding.sltActMyReply.visibility = View.GONE
        }
    }

    private fun createAdapter(){
        myReplyAdapter = MyReplyAdapter(this)
        binding.rlvActMyReply.adapter = myReplyAdapter
    }
}