package com.chuzhi.xzyx.ui.activity.find

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.app.MyApplication
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityFindDetailsBinding
import com.chuzhi.xzyx.ui.adapter.FindDetailsCommentAdapter
import com.chuzhi.xzyx.ui.adapter.FindDetailsImgAdapter
import com.chuzhi.xzyx.ui.bean.bbs.ArticleAnswersEntity
import com.chuzhi.xzyx.ui.bean.bbs.ArticleDetailsEntity
import com.chuzhi.xzyx.ui.presenter.FindDetailsPresenter
import com.chuzhi.xzyx.ui.view.FindDetailsView
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.SingleOnClickUtil
import com.chuzhi.xzyx.utils.ToastUtil
import me.iwf.photopicker.PhotoPreview


/**
 * 发现详情页
 */
class FindDetailsActivity : BaseActivity<ActivityFindDetailsBinding, FindDetailsPresenter>(),
    FindDetailsView {
    var findItemId = 0
    private var delFollowerPop: AlertDialogIos? = null
    private var page = 1
    private var likeType = -1
    private var commentCount = 0
    override fun createPresenter(): FindDetailsPresenter {
        return FindDetailsPresenter(this)
    }

    override fun initView() {
        binding.includeActFindDetails.tvIncludeTitleTitle.text = "详情"
        binding.includeActFindDetails.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.sltActFindDetailsComment.setEnableRefresh(false)
        binding.rlvActFindDetailsComment.layoutManager = LinearLayoutManager(this)
        binding.rlvActFindDetailsApp.setHasFixedSize(true)
        binding.rlvActFindDetailsApp.isNestedScrollingEnabled = false
//        binding.rlvActFindDetailsComment.setHasFixedSize(true)
        binding.rlvActFindDetailsComment.isNestedScrollingEnabled = false

        createCommentAdapter()
        binding.includeActFindDetails.ivIncludeTitleBack.setOnClickListener {
            finish()
        }

        delFollowerPop = AlertDialogIos(this).builder()
        val settings: WebSettings = binding.wvActFindDetails.settings
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setAppCacheMaxSize((1024 * 1024 * 8).toLong())
        val appCachePath = MyApplication.getInstance().cacheDir.absolutePath
        settings.setAppCachePath(appCachePath)
        settings.allowFileAccess = true
        settings.setAppCacheEnabled(true)
        settings.textZoom = 200
    }

    override fun initData() {
        findItemId = intent.getIntExtra("findItemId", 0)
        if (findItemId != 0) {
            presenter.articleDetails(findItemId.toString())
        } else {
            ToastUtil.showShort(this, "该文章暂无数据")
        }

        //关注 取消关注
        binding.btnActFindDetailsFollower.setOnClickListener {
            if (findItemId == 0) {
                ToastUtil.showShort(this, "操作失败！")
                return@setOnClickListener
            }
            val isFollower = binding.btnActFindDetailsFollower.text.toString()
            if (isFollower == "关注") {
                presenter.addFollow(findItemId)
            } else {
                delFollower()
            }
        }
        //点赞 取消点赞
        binding.tvActFindDetailsArticlePraiseCount.setOnClickListener {
            if (SingleOnClickUtil.isFastClick()) {
                presenter.articlePraise(findItemId)
            }
        }
        //评论列表接口
        presenter.articleAnswers(page, findItemId.toString())

        //上拉加载更多
        binding.sltActFindDetailsComment.setOnLoadMoreListener { it ->
            it.finishLoadMore(1000)
            it.layout.postDelayed( {
                page++
                presenter.articleAnswers(page, findItemId.toString())//请求评论列表
                it.finishLoadMore()
            }, 1000)
        }

        //键盘弹出
        binding.etActFindDetailsBottom.setOnClickListener {
            binding.llActFindDetailsBottom.visibility = View.GONE
            binding.llActFindDetailsBottom1.visibility = View.VISIBLE
            showSoftKeyboard(binding.etActFindDetailsBottom1)
        }
        //键盘弹出
        binding.tvActFindDetailsAnswersCount.setOnClickListener {
            binding.llActFindDetailsBottom.visibility = View.GONE
            binding.llActFindDetailsBottom1.visibility = View.VISIBLE
            showSoftKeyboard(binding.etActFindDetailsBottom1)
        }
        //发送评论
        binding.tvActFindDetailsSend.setOnClickListener {
            if (SingleOnClickUtil.isFastClick()) {
                if (binding.etActFindDetailsBottom1.text.toString().trim().isEmpty()){
                    ToastUtil.showShort(this,"评论内容不能为空！")
                }else{
                    presenter.addAnswer(findItemId,binding.etActFindDetailsBottom1.text.toString())
                }
            }
        }
    }

    //取消关注弹框
    private fun delFollower() {
        delFollowerPop!!.setTitle("提示")
            .setMsg("是否取消关注？")
            .setNegativeButton("取消", R.color.gray, null)
            .setPositiveButton("确定", R.color.text_default, View.OnClickListener {
                presenter.deleteFollow(findItemId)
            })
        delFollowerPop!!.show()
    }

    //详情回调
    @SuppressLint("SetTextI18n")
    override fun articleDetails(msg: ArticleDetailsEntity?) {
        Glide.with(this).load(msg!!.avatar).circleCrop()
            .placeholder(R.drawable.my_img_portrait_default)
            .error(R.drawable.my_img_portrait_default).into(binding.ivActFindDetailsHead)
        binding.tvActFindDetailsName.text = msg.user_name
        binding.tvActFindDetailsTime.text = msg.create_time
        binding.tvActFindDetailsTitle.text = msg.title
        binding.tvActFindDetailsArticlePraiseCount.text = msg.praise_count.toString()
        binding.tvActFindDetailsAnswersCount.text = msg.answers_count.toString()
        binding.tvActFindDetailsReadCount.text = msg.read_count.toString()
        commentCount = msg.answers_count
        binding.tvActFindDetailsCommentCount.text = "${commentCount}条评论"


        binding.btnActFindDetailsFollower.setBackgroundResource(
            if (msg.isFollow_status) R.drawable.btn_eeeeee_round_30_background
            else R.drawable.btn_blue_ring_30_background
        )
        binding.btnActFindDetailsFollower.setTextColor(
            if (msg.isFollow_status) Color.parseColor("#999999")
            else Color.parseColor("#0066ff")
        )
        binding.btnActFindDetailsFollower.text = if (msg.isFollow_status) "已关注" else "关注"

        isLike(!msg.isPraise_status)
        when(msg.sourced){
            0->{//富文本
                binding.wvActFindDetails.visibility = View.VISIBLE
                binding.rltActFindDetailsApp.visibility = View.GONE

                val replace =
                    msg.contents.replace("<img", "<img style=\"display:        ;max-width:100%;\"")
                binding.wvActFindDetails.loadDataWithBaseURL(
                    null,
                    replace,
                    "text/html",
                    "utf-8",
                    null
                )
            }
            1->{//app
                binding.wvActFindDetails.visibility = View.GONE
                binding.rltActFindDetailsApp.visibility = View.VISIBLE
                binding.tvActFindDetailsContents.text = msg.contents
                if (msg.image_name!=null && msg.image_name.size>0) {//app

                    binding.rlvActFindDetailsApp.layoutManager = LinearLayoutManager(this)
                    val adapter = FindDetailsImgAdapter(this)
                    binding.rlvActFindDetailsApp.adapter = adapter
                    adapter.setData(msg.image_name)
                    val imgNewList = ArrayList<String>()
                    for (i in msg.image_name.indices){
                        imgNewList.add(msg.image_name[i].trim())
                    }
                    adapter.setOnClickFindDetailsImgListener { position ->
                        PhotoPreview.builder()
                            .setPhotos(imgNewList as java.util.ArrayList<String>?)
                            .setCurrentItem(position)
                            .setShowDeleteButton(false)
                            .start(this@FindDetailsActivity)
                    }
                }
            }
        }



    }

    //关注 取消关注回调
    override fun articleFollower(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, msg!!.msg)
        if (msg.msg.contains("取消")) {
            binding.btnActFindDetailsFollower.text = "关注"
            binding.btnActFindDetailsFollower.setTextColor(Color.parseColor("#0066ff"))
            binding.btnActFindDetailsFollower.setBackgroundResource(R.drawable.btn_blue_ring_30_background)
        } else {
            binding.btnActFindDetailsFollower.text = "已关注"
            binding.btnActFindDetailsFollower.setTextColor(Color.parseColor("#999999"))
            binding.btnActFindDetailsFollower.setBackgroundResource(R.drawable.btn_eeeeee_round_30_background)
        }
    }

    //文章点赞 取消点赞回调
    override fun articlePraise(msg: BaseModel<String>?) {
        isLike(msg!!.msg.contains("取消"))
        if (msg.msg.contains("取消")) {
            binding.tvActFindDetailsArticlePraiseCount.text =
                (binding.tvActFindDetailsArticlePraiseCount.text.toString().toInt() - 1).toString()
        } else {
            binding.tvActFindDetailsArticlePraiseCount.text =
                (binding.tvActFindDetailsArticlePraiseCount.text.toString().toInt() + 1).toString()
        }
    }

    //评论列表回调
    var articleAnswersList = ArrayList<ArticleAnswersEntity.AnswerListDTO>()
    override fun articleAnswers(msg: ArticleAnswersEntity?) {
        if (msg!!.answer_list != null && msg.answer_list.size > 0) {
            binding.sltActFindDetailsComment.visibility = View.VISIBLE
            binding.includeActFindDetailsNoneData.llIncludeNoneData.visibility = View.GONE
            if (page == 1) {
                articleAnswersList.clear()
            }
            if (msg.answer_list.size < 20) {
                binding.sltActFindDetailsComment.finishLoadMoreWithNoMoreData()
            } else {
                binding.sltActFindDetailsComment.setNoMoreData(false)
            }
            articleAnswersList.addAll(msg.answer_list)
            commentAdapter!!.setData(articleAnswersList)
            commentAdapter!!.notifyDataSetChanged()
            commentAdapter!!.setOnClickFindDetailsCommentListener { data, position ->
                likeType = position
                presenter.answerPraise(data!!.id)
            }
        } else {
            binding.sltActFindDetailsComment.visibility = View.GONE
            binding.includeActFindDetailsNoneData.llIncludeNoneData.visibility = View.VISIBLE
        }
    }

    //评论点赞 取消点赞回调
    override fun answerPraise(msg: BaseModel<String>?) {
        if (likeType != -1) {
            if (commentAdapter != null) {
                commentAdapter!!.notifyItemChanged(likeType, "like" + msg!!.msg)
            }
        }
    }

    //发送评论回调
    @SuppressLint("SetTextI18n")
    override fun addAnswer(msg: BaseModel<String>?) {
        ToastUtil.showShort(this,msg!!.msg)
        hideSoftKeyboard(binding.etActFindDetailsBottom1)
        presenter.articleAnswers(1,findItemId.toString())
        commentCount ++
        binding.tvActFindDetailsCommentCount.text = "${commentCount}条评论"
        binding.etActFindDetailsBottom1.setText("")
    }

    var commentAdapter: FindDetailsCommentAdapter? = null
    private fun createCommentAdapter() {
        commentAdapter = FindDetailsCommentAdapter(this)
        binding.rlvActFindDetailsComment.adapter = commentAdapter
    }

    private fun isLike(isLike: Boolean) {
        if (isLike) {
            val drawableLeft: Drawable =
                resources.getDrawable(R.mipmap.detail_icon_praise_default)
            drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
            binding.tvActFindDetailsArticlePraiseCount.setCompoundDrawables(
                null,
                drawableLeft,
                null,
                null
            )

        } else {
            val drawableLeft: Drawable =
                resources.getDrawable(R.mipmap.detail_icon_praise_highlight)
            drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
            binding.tvActFindDetailsArticlePraiseCount.setCompoundDrawables(
                null,
                drawableLeft,
                null,
                null
            )
        }
    }

    //显示键盘
    private fun showSoftKeyboard(view: EditText) {
        if (view != null) {
            val handler = Handler()
            handler.post(Runnable {
                view.requestFocus()
                val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            })

        }
    }

    //隐藏键盘
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        binding.llActFindDetailsBottom.visibility = View.VISIBLE
        binding.llActFindDetailsBottom1.visibility = View.GONE
    }

    //重写 点击edittext以外区域隐藏软键盘
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                //如果现在取得焦点的View为EditText则进入判断
                currentFocus?.let { view ->
                    if (view is EditText) {
                        if (!isInSide(view, ev) && isSoftInPutDisplayed()) {
                            hideSoftKeyboard(view)
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //判断点击坐标是否在EditText内部
    private fun isInSide(currentFocus: View, ev: MotionEvent): Boolean {
        val location = intArrayOf(0, 0)
        //获取当前EditText坐标
        currentFocus.getLocationInWindow(location)
        //上下左右
        val left = location[0]
        val top = location[1]
        val right = left + currentFocus.width + binding.tvActFindDetailsSend.width
        val bottom = top + currentFocus.height + binding.tvActFindDetailsSend.height
        //点击坐标是否在其内部
        return (ev.x >= left && ev.x <= right && ev.y > top && ev.y < bottom)
    }

    private fun isSoftInPutDisplayed(): Boolean {
        return ViewCompat.getRootWindowInsets(window.decorView)
            ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    }


    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this, msg)
    }

}