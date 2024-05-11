package com.chuzhi.xzyx.ui.activity.me

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityMyReleaseBinding
import com.chuzhi.xzyx.ui.activity.find.FindDetailsActivity
import com.chuzhi.xzyx.ui.adapter.MyReleaseListAdapter
import com.chuzhi.xzyx.ui.bean.bbs.UserArticleListEntity
import com.chuzhi.xzyx.ui.presenter.MyReleasePresenter
import com.chuzhi.xzyx.ui.view.MyReleaseView

/**
 * 我的发布activity
 */
class MyReleaseActivity : BaseActivity<ActivityMyReleaseBinding,MyReleasePresenter>(),MyReleaseView{
    private var page = 1
    private var followerType = -1
    override fun createPresenter(): MyReleasePresenter {
        return MyReleasePresenter(this)
    }

    override fun initView() {
        binding.includeActMyRelease.tvIncludeTitleTitle.text = "我的发布"
        binding.includeActMyRelease.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActMyRelease.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.sltActMyRelease.setRefreshHeader(ClassicsHeader(this))
        binding.sltActMyRelease.setRefreshFooter(ClassicsFooter(this))
        binding.rlvActMyRelease.layoutManager = LinearLayoutManager(this)
        createAdapter()
        presenter.userArticleList(1)
    }

    override fun initData() {
        //下拉刷新上拉加载更多
        binding.sltActMyRelease.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.userArticleList(page)//请求文章列表
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
        binding.sltActMyRelease.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed(Runnable {
                page++
                presenter.userArticleList(page)//请求文章列表
                it.finishLoadMore()
            }, 1000)
//            ToastUtil.showShort(activity, "上拉加载")
        }
    }
    private fun createAdapter() {
        releaseListAdapter = MyReleaseListAdapter(this)
        binding.rlvActMyRelease.adapter = releaseListAdapter
    }

    //文章列表
    private var list = ArrayList<UserArticleListEntity.ArticleListDTO>()
    var releaseListAdapter: MyReleaseListAdapter? = null
    override fun userArticleList(msg: UserArticleListEntity?) {
        if (msg!!.article_list != null && msg.article_list.size > 0) {
            binding.sltActMyRelease.visibility = View.VISIBLE
            binding.includeActMyReleaseNoneData.llIncludeNoneData.visibility = View.GONE
            if (page == 1) {
                list.clear()
            }
            if (msg.article_list.size < 20) {
                binding.sltActMyRelease.finishLoadMoreWithNoMoreData();
            } else {
                binding.sltActMyRelease.setNoMoreData(false)
            }

            list.addAll(msg!!.article_list as ArrayList<UserArticleListEntity.ArticleListDTO>)
            releaseListAdapter!!.setData(list)
            releaseListAdapter!!.notifyDataSetChanged()

            releaseListAdapter!!.setOnClickFindListListener(object :
                MyReleaseListAdapter.OnClickFindListListener {
                override fun onClickListener(data: UserArticleListEntity.ArticleListDTO?) {//跳转详情
                    var intent = Intent(this@MyReleaseActivity, FindDetailsActivity::class.java)
                    intent.putExtra("findItemId", data!!.id)
                    startActivity(intent)
                }

                //点赞取消点赞
                override fun onClickLikeListener(
                    data: UserArticleListEntity.ArticleListDTO?,
                    position: Int
                ) {
                    presenter.articlePraise(data!!.id)
                    followerType = position
                }
            })
        } else {
            binding.sltActMyRelease.visibility = View.GONE
            binding.includeActMyReleaseNoneData.llIncludeNoneData.visibility = View.VISIBLE
        }
    }

    override fun articlePraise(msg: BaseModel<String>?) {
        if (followerType != -1) {
            if (releaseListAdapter != null) {
                releaseListAdapter!!.notifyItemChanged(followerType, "like" + msg!!.msg)
            }
        }
    }
}