package com.chuzhi.xzyx.ui.activity.me

import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import com.chuzhi.xzyx.app.MyApplication.Companion.getInstance
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityChangePwdBinding
import com.chuzhi.xzyx.ui.presenter.ChangePwdPresenter
import com.chuzhi.xzyx.ui.view.ChangePwdView
import com.chuzhi.xzyx.utils.SpUtils
import com.chuzhi.xzyx.utils.ToastUtil


//修改密码页面
class ChangePwdActivity : BaseActivity<ActivityChangePwdBinding, ChangePwdPresenter>(),
    ChangePwdView {

    override fun createPresenter(): ChangePwdPresenter {
        return ChangePwdPresenter(this)
    }

    override fun initView() {
        binding.includeActChangePwd.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActChangePwd.tvIncludeTitleTitle.text = "修改密码"
        binding.includeActChangePwd.ivIncludeTitleBack.setOnClickListener { finish() }

        // 不允许输入汉字
        val digitsKeyListener: DigitsKeyListener = object : DigitsKeyListener() {
            override fun getInputType(): Int {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            override fun getAcceptedChars(): CharArray {
                val a =
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"//!”#$%&’()*+,-./:;<=>?@[\]^_`{|}~
                return a.toCharArray()
            }
        }
        // 设置允许的字符setKeyListener
        binding.etActChangeOldPwd.keyListener = digitsKeyListener
        binding.etActChangeNewPwd.keyListener = digitsKeyListener
        binding.etActChangeAgainNewPwd.keyListener = digitsKeyListener

        binding.cbActChangeOldPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActChangeOldPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActChangeOldPwd.setSelection(binding.etActChangeOldPwd.text.length)
            binding.etActChangeOldPwd.keyListener = digitsKeyListener
        }
        binding.cbActChangeNewPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActChangeNewPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActChangeNewPwd.setSelection(binding.etActChangeNewPwd.text.length)
            binding.etActChangeNewPwd.keyListener = digitsKeyListener
        }
        binding.cbActChangeAgainNewPwd.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.etActChangeAgainNewPwd.inputType = InputType.TYPE_CLASS_TEXT or
                    if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etActChangeAgainNewPwd.setSelection(binding.etActChangeAgainNewPwd.text.length)
            binding.etActChangeAgainNewPwd.keyListener = digitsKeyListener
        }

        binding.btnActChangeLogin.setOnClickListener {
            if (binding.etActChangeOldPwd.text.toString().trim() == "") {
                ToastUtil.showShort(this, "请输入原始密码")
                return@setOnClickListener
            }
            if (binding.etActChangeNewPwd.text.toString().trim() == "") {
                ToastUtil.showShort(this, "请输入新密码")
                return@setOnClickListener
            }
            if (binding.etActChangeAgainNewPwd.text.toString().trim() == "") {
                ToastUtil.showShort(this, "请再次输入新密码")
                return@setOnClickListener
            }
            if (binding.etActChangeOldPwd.text.toString().trim() == binding.etActChangeAgainNewPwd.text.toString().trim()){
                ToastUtil.showShort(this, "新密码不能和原始密码一样！")
                return@setOnClickListener
            }
            presenter.oauthAlterPassword(
                binding.etActChangeOldPwd.text.toString().trim(),
                binding.etActChangeNewPwd.text.toString().trim(),
                binding.etActChangeAgainNewPwd.text.toString().trim()
            )
        }


    }

    override fun initData() {
    }

    override fun oauthAlterPassword(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, msg!!.msg)
        SpUtils.setSharedStringData(
            getInstance(),
            "Password",
            binding.etActChangeAgainNewPwd.text.toString().trim()
        )
        finish()
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this, msg)
    }
}