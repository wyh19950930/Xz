package com.chuzhi.xzyx.ui.fragment.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseFragment
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.FragmentFindBinding
import com.chuzhi.xzyx.ui.activity.find.FindDetailsActivity
import com.chuzhi.xzyx.ui.activity.find.ReleaseMessageActivity
import com.chuzhi.xzyx.ui.adapter.FindBannerVPAdapter
import com.chuzhi.xzyx.ui.adapter.FindListAdapter
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1
import com.chuzhi.xzyx.ui.bean.bbs.ArticleListEntity
import com.chuzhi.xzyx.ui.bean.bbs.CarouselArticleEntity
import com.chuzhi.xzyx.ui.presenter.FindFragPresenter
import com.chuzhi.xzyx.ui.view.FindFragView
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.AndroidBarUtils
import com.chuzhi.xzyx.utils.NetworkUtils
import com.chuzhi.xzyx.utils.ToastUtil
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FindFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindFragment : BaseFragment<FragmentFindBinding, FindFragPresenter>(), FindFragView {
    private var param1: String? = null
    private var param2: String? = null
    private var page = 1
    private var tabName = "推荐"
    private var tabChildName = "问题反馈"
    private var tabId = "3"
    private var delFollowerPop: AlertDialogIos? = null
    private var followerType = -1
    private var firstFinish = 1
    private var netWorkRefresh = 0
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
            FindFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    Log.e("发现==", param1 + param2)
                }
            }
    }

    override fun createPresenter(): FindFragPresenter {
        return FindFragPresenter(this)
    }

    override fun initView() {
        binding.includeFindTitle.tvIncludeTitleTitle.text = "发现"
        binding.includeFindTitle.ivIncludeTitleAdd.visibility = View.VISIBLE
        binding.sltFragFind.setRefreshHeader(ClassicsHeader(activity))
        binding.sltFragFind.setRefreshFooter(ClassicsFooter(activity))
        binding.rlvFragFind.layoutManager = GridLayoutManager(activity, 2)
        val statusBarHeight = AndroidBarUtils.getStatusBarHeight(requireActivity())
        binding.llFragFindTop.setPadding(0,statusBarHeight,0,0)
//        createBannerVpAdapter()
        createAdapter()
        presenter.articleCategoryList()//请求文章tab

        delFollowerPop = AlertDialogIos(activity).builder()

        binding.includeFindTitle.ivIncludeTitleAdd.setOnClickListener {
            startActivity(Intent(activity, ReleaseMessageActivity::class.java))
        }
    }

    override fun initData() {
        //一级tabLayout
        binding.tabFragFind.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabName = tab!!.text.toString()
                page = 1

                if (tabLists != null && tabLists.size > 0) {
                    if ("心声" != tabName) {
                        binding.tabFragFindChild.visibility = View.GONE
                        for (i in 0 until tabLists.size) {
                            if (tabLists[i].id == 3) {
                                tabId = tabLists[i].id.toString()
                                presenter.articleList(page, tabId)
                            }

                        }
                    } else if ("心声" == tabName) {//子tabLayout显示{
                        binding.tabFragFindChild.visibility = View.VISIBLE
                        if (firstFinish == 1) {
                            tabId = "4"
                            binding.tabFragFindChild.getTabAt(0)!!.select()
                            presenter.articleList(page, tabId)
                            firstFinish = 2
                        } else {
                            for (i in 0 until tabLists.size) {
                                if (tabChildName == tabLists[i].name) {
                                    tabId = tabLists[i].id.toString()
                                    presenter.articleList(page, tabId)//请求文章列表
                                }
                            }
                        }


                        //二级tabLayout
                        binding.tabFragFindChild.addOnTabSelectedListener(object :
                            TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                tabChildName = tab!!.text.toString()
                                page = 1

                                if (tabLists != null && tabLists.size > 0) {
                                    for (i in 0 until tabLists.size) {
                                        if (tabChildName == tabLists[i].name) {
                                            tabId = tabLists[i].id.toString()
                                            presenter.articleList(page, tabId)//请求文章列表
                                        }
                                    }
                                }

                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {
                            }

                            override fun onTabReselected(tab: TabLayout.Tab?) {
                            }
                        })
                    }
                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        val linearLayout = binding.tabFragFindChild.getChildAt(0) as LinearLayout
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE;
        linearLayout.dividerDrawable = ContextCompat.getDrawable(
            this.mContext,
            R.drawable.layout_divider_vertical
        );
        linearLayout.dividerPadding = 30;
        //下拉刷新上拉加载更多
        binding.sltFragFind.setOnRefreshListener { it ->
            it.layout.postDelayed(Runnable {
                page = 1
                presenter.articleList(page, tabId)//请求文章列表
                it.finishRefresh()
                    .resetNoMoreData()
            }, 1000)
//            ToastUtil.showShort(activity, "下拉刷新")
        }
        binding.sltFragFind.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed(Runnable {
                page++
                presenter.articleList(page, tabId)//请求文章列表
                it.finishLoadMore()
            }, 1000)
//            ToastUtil.showShort(activity, "上拉加载")
        }
    }

    private var bannerVpAdapter: FindBannerVPAdapter? = null
//    private fun createBannerVpAdapter(){
//        bannerVpAdapter = FindBannerVPAdapter(activity)
//        binding.bannerVpFragFind.setLifecycleRegistry(lifecycle)
//            .setAdapter(bannerVpAdapter)
//            .create()
//        binding.bannerVpFragFind.setCanLoop(true).setIndicatorVisibility(View.GONE)
//    }

    var tabLists = ArrayList<ArticleCategoryEntity1.ArtcleCategoryListDTO>()

    //文章tab列表
    override fun articleCategoryList(msg: ArticleCategoryEntity1?) {
        if (msg != null && msg.artcle_category_list.size > 0) {
            tabLists.clear()
            binding.tabFragFind.removeAllTabs()
            for (i in 0 until msg.artcle_category_list.size) {
                tabLists.add(msg.artcle_category_list[i])
                if (msg.artcle_category_list[i].id == 3){
                    binding.tabFragFind.addTab(binding.tabFragFind.newTab().setText(msg.artcle_category_list[i].name))
                }
            }

//            binding.tabFragFind.addTab(binding.tabFragFind.newTab().setText("推荐"))
            binding.tabFragFind.addTab(binding.tabFragFind.newTab().setText("心声"))
            binding.tabFragFindChild.removeAllTabs()
            for (i in 0 until tabLists.size) {
                if (tabLists[i].id >3) {
                    binding.tabFragFindChild.addTab(
                        binding.tabFragFindChild.newTab().setText(tabLists[i].name)
                    )
                }
            }
        }
    }

    //文章列表
    private var list = ArrayList<ArticleListEntity.ArticleListDTO>()
    var findListAdapter: FindListAdapter? = null
    override fun articleList(msg: ArticleListEntity?) {

//        if (msg!!.article_list != null && msg.article_list.size > 0) {
            binding.sltFragFind.visibility = View.VISIBLE
            binding.includeFragFindNoneData.llIncludeNoneData.visibility = View.GONE
            if (page == 1) {
                list.clear()
            }
            if (msg!!.article_list.size < 20) {
                binding.sltFragFind.finishLoadMoreWithNoMoreData();
            } else {
                binding.sltFragFind.setNoMoreData(false)
            }

            list.addAll(msg!!.article_list as ArrayList<ArticleListEntity.ArticleListDTO>)
            findListAdapter!!.setData(list)
            findListAdapter!!.notifyDataSetChanged()
            if (list.size > 0){
                binding.sltFragFind.visibility = View.VISIBLE
                binding.includeFragFindNoneData.llIncludeNoneData.visibility = View.GONE
            }else{
                binding.sltFragFind.visibility = View.GONE
                binding.includeFragFindNoneData.llIncludeNoneData.visibility = View.VISIBLE
            }


            findListAdapter!!.setOnClickFindListListener(object :
                FindListAdapter.OnClickFindListListener {
                override fun onClickListener(data: ArticleListEntity.ArticleListDTO?) {//跳转详情
                    var intent = Intent(activity, FindDetailsActivity::class.java)
                    intent.putExtra("findItemId", data!!.id)
                    startActivity(intent)
                }

                //关注取消关注
                override fun onClickFollowerListener(
                    data: ArticleListEntity.ArticleListDTO?,
                    position: Int,
                    view: Button?
                ) {//关注 取消关注
                    val isFollower = view!!.text.toString();
                    if (isFollower == "关注") {
                        presenter.addFollow(data!!.id)
                    } else {
                        delFollower(data)
                    }
                    followerType = position
                }

                //点赞取消点赞
                override fun onClickLikeListener(
                    data: ArticleListEntity.ArticleListDTO?,
                    position: Int
                ) {
                    presenter.articlePraise(data!!.id)
                    followerType = position
                }
            })
//        } else {
//            binding.sltFragFind.visibility = View.GONE
//            binding.includeFragFindNoneData.llIncludeNoneData.visibility = View.VISIBLE
//        }
    }

    //取消关注弹框
    private fun delFollower(data: ArticleListEntity.ArticleListDTO?) {
        delFollowerPop!!.setTitle("提示")
            .setMsg("是否取消对” ${data!!.author_name} “的关注？")
            .setNegativeButton("取消", R.color.gray, null)
            .setPositiveButton("确定", R.color.text_default, View.OnClickListener {
                presenter.deleteFollow(data!!.id)
            })
        delFollowerPop!!.show()
    }

    //关注和取消关注
    override fun articleFollower(msg: BaseModel<String>?) {
        ToastUtil.showShort(activity, msg!!.msg)
        if (followerType != -1) {
            if (findListAdapter != null) {
                findListAdapter!!.notifyItemChanged(followerType, "follower")
            }
        }
    }

    //文章点赞
    override fun articlePraise(msg: BaseModel<String>?) {
        if (followerType != -1) {
            if (findListAdapter != null) {
                findListAdapter!!.notifyItemChanged(followerType, "like" + msg!!.msg)
            }
        }
    }

    private fun createAdapter() {
        findListAdapter = FindListAdapter(activity)
        binding.rlvFragFind.adapter = findListAdapter
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(activity, msg)
    }

    var article_lists =ArrayList<CarouselArticleEntity.ArticleListDTO>()
    //轮播图回调
    override fun carouselArticle(msg: CarouselArticleEntity?) {
        if (msg!!.article_list != null && msg.article_list.size > 0) {
            article_lists!!.clear()
            for (i in 0 until msg!!.article_list.size){
                if (msg!!.article_list[i].article_img!=null){
                    article_lists!!.add(msg!!.article_list[i])
                }
            }
            binding.bannerFragFind.setAdapter(object :
                BannerImageAdapter<CarouselArticleEntity.ArticleListDTO>(article_lists) {
                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: CarouselArticleEntity.ArticleListDTO?,
                    position: Int,
                    size: Int
                ) {
                    holder!!.itemView.setOnClickListener {
                        var intent = Intent(activity, FindDetailsActivity::class.java)
                        intent.putExtra("findItemId", msg.article_list[position].id)
                        startActivity(intent)
                    }
                    Glide.with(this@FindFragment).load(data!!.article_img)
                        .placeholder(R.drawable.banner_zwt_img)
                        .error(R.drawable.banner_zwt_img)
                        .fallback(R.drawable.banner_zwt_img).transform(
                            CenterCrop(),
                            RoundedCorners(15)
                        ).into(holder!!.imageView)
                }
            }).addBannerLifecycleObserver(this)
            binding.bannerFragFind.setBannerGalleryEffect(20, 20)
            binding.bannerIndicatorFragFind.initIndicatorCount(article_lists.size)
            binding.bannerFragFind.viewPager2.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bannerIndicatorFragFind.changeIndicator(position - 1)
//                Log.e("发现轮播下标==>",position.toString())
                }
            })
//            binding.bannerVpFragFind.refreshData(msg.article_list)
//            binding.bannerIndicatorFragFind.initIndicatorCount(msg.article_list.size)

//            binding.bannerVpFragFind.registerOnPageChangeCallback(object :
//                ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    binding.bannerIndicatorFragFind.changeIndicator(position)
//                }
//            })
//            bannerVpAdapter!!.setOnItemClickListener(FindBannerVPAdapter.OnItemClickListener { v, position ->
//                var intent = Intent(activity, FindDetailsActivity::class.java)
//                intent.putExtra("findItemId", msg.article_list[position].id)
//                startActivity(intent)
//            })
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (view != null && isVisibleToUser) {
            presenter.carouselArticle()//轮播图
            if (NetworkUtils.isNetworkAvailable(activity)){
                if (netWorkRefresh == 0){
                    presenter.articleCategoryList()//请求文章tab
                    netWorkRefresh = 1
                }
            }else{
                netWorkRefresh = 0
            }
            Log.e("FindFragmentHint","isVisibleToUser=$isVisibleToUser")
        }else{
            Log.e("FindFragmentHint","isVisibleToUser=$isVisibleToUser")
        }
    }

}