package com.chuzhi.xzyx.ui.fragment.homepage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.chuzhi.xzyx.base.BaseFragment
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.FragmentHomePageBindingBinding
import com.chuzhi.xzyx.ui.activity.homepage.HPAddDeviceActivity
import pl.droidsonroids.gif.GifDrawable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageBindingFragment.newInstance] factory method to
 * create an instance of this fragment. 首页绑定设备fragment
 */
class HomePageBindingFragment : BaseFragment<FragmentHomePageBindingBinding, BasePresenter<*>>(),
    BaseView {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomePageBindingFragment()
    }

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.btnFragBindingBd.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    HPAddDeviceActivity::class.java
                )
            )
        }
    }

    private var drawable:GifDrawable?=null
    override fun initData() {
//        Glide.with(this).asGif()
//            .load(R.drawable.bd_gif)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(binding.ivFragHpBdGif)
        drawable = binding.ivFragHpBdGif.drawable as GifDrawable
        drawable!!.start()
    }

    override fun onResume() {
        super.onResume()
        if (drawable!=null){
            drawable!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (drawable!=null){
            drawable!!.stop()
        }
    }

}