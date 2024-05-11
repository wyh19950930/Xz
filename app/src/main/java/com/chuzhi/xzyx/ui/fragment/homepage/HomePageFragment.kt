package com.chuzhi.xzyx.ui.fragment.homepage

import android.os.Bundle
import android.util.Log
import com.chuzhi.xzyx.base.*
import com.chuzhi.xzyx.databinding.FragmentHomePageBinding
import com.chuzhi.xzyx.ui.presenter.HomePagePresenter
import com.chuzhi.xzyx.ui.view.HomePageView
import com.chuzhi.xzyx.utils.SpUtils
import com.chuzhi.xzyx.utils.ToastUtil

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 绑定设备页面（未绑定才会进来）
 */
class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePagePresenter>(), HomePageView {
    // TODO: Rename and change types of parameters
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
        fun newInstance(param1: String, param2: String) =
            HomePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            val homepageBinding = SpUtils.getSharedStringData(activity, "HomePageBinding")
            if (homepageBinding != ""){
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(binding.fltHomePage.id,HPDeviceFragment.newInstance())
                    ?.commit()
                Log.e("首页==》","显示")
            }else{
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(binding.fltHomePage.id,HomePageBindingFragment.newInstance())
                    ?.commit()
            }
        }
    }

    override fun createPresenter(): HomePagePresenter {
        return HomePagePresenter(this)
    }

    override fun initView() {
        val homepageBinding = SpUtils.getSharedStringData(activity, "HomePageBinding")
        if (homepageBinding == ""){
            presenter.userComputerList()
        }else{
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(binding.fltHomePage.id,HPDeviceFragment.newInstance())
                .commit()
        }

    }

    override fun initData() {
    }



    override fun onResume() {
        super.onResume()
        val homepageBinding = SpUtils.getSharedStringData(activity, "HomePageBinding")
        if (homepageBinding != ""){
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(binding.fltHomePage.id,HPDeviceFragment.newInstance())
                .commit()
        }else{
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(binding.fltHomePage.id,HomePageBindingFragment.newInstance())
                .commit()
        }
    }

    //设备信息列表回调
    override fun userComputerList(msg: BaseModel<String>?) {
        if (msg!!.msg.contains("未绑定")){
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(binding.fltHomePage.id,HomePageBindingFragment.newInstance())
                .commit()
        }else{
            SpUtils.setSharedStringData(activity,"HomePageBinding","已绑定")
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(binding.fltHomePage.id,HPDeviceFragment.newInstance())
                .commit()
        }
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(activity,msg)
    }

}