package com.chuzhi.xzyx.ui.activity.me

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityMeInformationBinding
import com.chuzhi.xzyx.ui.adapter.PhotoAdapter
import com.chuzhi.xzyx.ui.bean.bbs.CityListEntity
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo
import com.chuzhi.xzyx.ui.bean.bbs.ProvinceListEntity
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity
import com.chuzhi.xzyx.ui.presenter.MeInformationPresenter
import com.chuzhi.xzyx.ui.view.MeInformationView
import com.chuzhi.xzyx.utils.*
import com.chuzhi.xzyx.utils.city.GetJsonDataUtil
import com.chuzhi.xzyx.utils.city.JsonBean
import com.tbruyelle.rxpermissions2.RxPermissions
import me.iwf.photopicker.PhotoPicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * 完善信息页面（修改）
 */
class MeInformationActivity : BaseActivity<ActivityMeInformationBinding, MeInformationPresenter>(),MeInformationView {

    private var pvTime: TimePickerView? = null//时间选择器对象
    private var pvAddress :OptionsPickerView<*>? = null
    var cityPop: CommenPop? = null
    //  省
    private var options1Items: List<JsonBean> = ArrayList<JsonBean>()
    //  市
    private val options2Items: ArrayList<ArrayList<String>> = ArrayList()
    //  区
    private val options3Items: ArrayList<ArrayList<ArrayList<String>>> = ArrayList()
    private var selectedPhotos = ArrayList<String>()
    private var photoAdapter: PhotoAdapter? = null
    private var uploadFlag = 0
    private var rxPermissions: RxPermissions?=null
    private var permissionPop : AlertDialogIos? = null
    private var tipPop: CommenPop? = null

    override fun createPresenter(): MeInformationPresenter {
        return MeInformationPresenter(this)
    }

    private var rlvProvince:RecyclerView?=null
    private var rlvCity:RecyclerView?=null
    private var sexFlag = 0
    override fun initView() {
        binding.includeActInformation.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActInformation.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActInformation.tvIncludeTitleTitle.text = "完善信息"

        rxPermissions = RxPermissions(this)
        permissionPop()
        cityPop = CommenPop.getNormalPopu(this, R.layout.pop_select_city, binding.llActMeInfoTop)
        val contentView = cityPop!!.contentView
        rlvProvince = contentView.findViewById<RecyclerView>(R.id.rlv_pop_select_c_province)
        rlvCity = contentView.findViewById<RecyclerView>(R.id.rlv_pop_select_c_city)
        var tvCancel = contentView.findViewById<TextView>(R.id.tv_pop_select_c_cancel)
        var tvCommit = contentView.findViewById<TextView>(R.id.tv_pop_select_c_commit)
        rlvProvince!!.layoutManager = LinearLayoutManager(this)
        rlvCity!!.layoutManager = LinearLayoutManager(this)
        tvCancel.setOnClickListener {
            cityPop!!.dismiss()
        }
        tvCommit.setOnClickListener {
            binding.btnActMeInfoAddress.text = provinceName+cityName
            cityPop!!.dismiss()
        }
//        parseData()//作废
//        initAddressPicker()//作废
        initTimePicker()
        presenter.oauthSetUserInfo()
        presenter.provinceList()
    }

    override fun initData() {

        binding.etActMeInfoNickname.filters = arrayOf<InputFilter>(SpaceFilter(), InputFilter.LengthFilter(10))

        //选择头像
        binding.rltActMeInfoHead.setOnClickListener {
            permissionData()

        }
        //选择生日
        binding.btnActMeInfoBirthday.setOnClickListener {
            pvTime!!.show()
        }
        //选择地址
        binding.btnActMeInfoAddress.setOnClickListener {
//            pvAddress!!.show()
            CommenPop.backgroundAlpha(0.5f, this)
            cityPop!!.showAtLocation(binding.llActMeInfoTop, Gravity.BOTTOM, 0, 0)
        }
        //选择性别
        binding.rgpActMeInfoSex.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbt_act_me_info_man->{
                    sexFlag = 0
                }
                R.id.rbt_act_me_info_woman->{
                    sexFlag = 1
                }
            }
        }
        //修改信息
        binding.btnActMeInfoSave.setOnClickListener {
            val nickName = binding.etActMeInfoNickname.text.toString().trim()
            val birthday = binding.btnActMeInfoBirthday.text.toString().trim()
            val address = binding.btnActMeInfoAddress.text.toString().trim()
            if (nickName == ""){
                ToastUtil.showShort(this,"请输入昵称")
            } else if (birthday == ""){
                ToastUtil.showShort(this,"请选择生日")
            }else if (address == ""){
                ToastUtil.showShort(this,"请选择地区")
            }else{
                presenter.updateUserInfo(nickName,sexFlag,birthday,cityId)
            }
        }

    }
    //查看信息回调
    override fun oauthSetUserInfo(msg: OauthSetUserInfo?) {
        if (msg!!.avatar!=""){
            Glide.with(this).load(msg.avatar)
                .placeholder(R.drawable.my_img_portrait_default)
                .error(R.drawable.my_img_portrait_default)
                .fallback(R.drawable.my_img_portrait_default)
                .transform(CenterCrop(), CircleCrop()
            ).into(binding.ivActMeInfoHead)
        }
        cityId = msg!!.city_id
        binding.etActMeInfoNickname.setText(msg!!.nickname)
        binding.btnActMeInfoBirthday.text = msg.birthday
        if (msg.province!=""&&msg.city!=""){
            binding.btnActMeInfoAddress.text = msg.province+msg.city
        }
        if (msg.gender!=-1){
            when(msg.gender){
                0->{
                    binding.rbtActMeInfoMan.isChecked = true
                }
                1->{
                    binding.rbtActMeInfoWoman.isChecked = true
                }
            }
        }
    }

    //修改成功回调
    override fun updateUserInfo(msg: BaseModel<String>?) {
        ToastUtil.showShort(this,msg!!.msg)
        SpUtils.setSharedStringData(this,"Username",binding.etActMeInfoNickname.text.toString().trim())
        finish()
    }

    //上传头像回调
    override fun uploadAvatar(msg: UploadFileImgEntity?) {
            Glide.with(this).load(msg!!.file_path)
                .circleCrop()
                .into(binding.ivActMeInfoHead)
        SpUtils.setSharedStringData(this,"Avatar",msg.file_path)
    }

    private fun tipPopup(){
        tipPop = CommenPop.getNormalPopu(this,R.layout.pop_tip,binding.llActMeInfoTop)
        val contentView = tipPop!!.contentView
        val tvTip = contentView.findViewById<TextView>(R.id.tv_pop_tip)
        tvTip.text = "相机权限使用说明：\n用于拍照上传头像\n读取文件权限说明：\n获取相册文件上传头像"
        tipPop!!.isOutsideTouchable = true
        tipPop!!.isFocusable = true
        CommenPop.backgroundAlpha(0.5f, this)
        tipPop!!.showAtLocation(
            binding.llActMeInfoTop,
            Gravity.TOP,
            100,
            0
        )
    }
    private fun permissionPop(){
        permissionPop = AlertDialogIos(this).builder()
            .setTitle("无法访问相机或读取文件")
            .setMsg("小志云享需要访问相机和读取文件以开启“拍照\"“相册\"功能\n请在设置中开启权限")
//            .setNegativeButton("取消", R.color.gray,null)
            .setPositiveButton("设置", R.color.text_default,View.OnClickListener {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                //跳转应用消息，间接打开应用权限设置-效率高
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            })
    }
    @SuppressLint("CheckResult")
    private fun permissionData(){
        tipPopup()
        rxPermissions!!.request(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe { granted ->

            if (granted) {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setPreviewEnabled(false)
                    .setSelected(selectedPhotos)
                    .start(this!!, 234)
            } else {
                permissionPop!!.show()
            }
        }
    }

    //省份列表回调
    private var provinceSelect = 0
    private var provinceId= 0
    private var provinceName= ""

    private var provinceItems:  ArrayList<ProvinceListEntity.ProvenceListDTO>?=null
    override fun provinceList(msg: ProvinceListEntity?) {
        provinceItems = ArrayList<ProvinceListEntity.ProvenceListDTO>()
            if (msg!!.provence_list!=null&&msg.provence_list.size>0){
                presenter.cityList(msg!!.provence_list[0].id)
                val provenceList = msg!!.provence_list
                for (i in 0 until provenceList.size){
                    provinceItems!!.add(provenceList[i])
                }

                rlvProvince!!.adapter = object :BaseQuickAdapter<ProvinceListEntity.ProvenceListDTO,
                        BaseViewHolder>(R.layout.item_select_province,provinceItems){
                    override fun convert(
                        helper: BaseViewHolder?,
                        item: ProvinceListEntity.ProvenceListDTO?
                    ) {
                        helper!!.setText(R.id.tv_item_select_province,item!!.name)
                        val tvProvince = helper.getView<TextView>(R.id.tv_item_select_province)
                        if (provinceSelect == helper!!.adapterPosition){
                            provinceId = item.id
                            provinceName = item.name
                            tvProvince.setTextColor(Color.parseColor("#000000"))
                        }else{
                            tvProvince.setTextColor(Color.parseColor("#999999"))
                        }

                        helper.itemView.setOnClickListener {
                            provinceSelect = helper.adapterPosition
                            citySelect = 0
                            notifyDataSetChanged()
                            presenter.cityList(item.id)
                        }
                    }

                }

            }
    }

    //城市列表回调
    private var citySelect = 0
    private var cityId = 0
    private var cityName = ""
    private var cityItems : ArrayList<CityListEntity.CityListDTO>?=null
    override fun cityList(msg: CityListEntity?) {
        cityItems = ArrayList()
        if (msg!!.city_list!=null&&msg.city_list.size>0){
            val cityList = msg!!.city_list
            for (i in 0 until cityList.size){
                cityItems!!.add(cityList[i])
            }
            rlvCity!!.adapter = object :BaseQuickAdapter<CityListEntity.CityListDTO,
                    BaseViewHolder>(R.layout.item_select_province,cityItems){
                override fun convert(
                    helper: BaseViewHolder?,
                    item: CityListEntity.CityListDTO?
                ) {
                    helper!!.setText(R.id.tv_item_select_province,item!!.name)
                    val tvProvince = helper.getView<TextView>(R.id.tv_item_select_province)
                    if (citySelect == helper!!.adapterPosition){
                        cityId = item.id
                        cityName = item.name
                        tvProvince.setTextColor(Color.parseColor("#000000"))
                    }else{
                        tvProvince.setTextColor(Color.parseColor("#999999"))
                    }

                    helper.itemView.setOnClickListener {
                        citySelect = helper.adapterPosition
                        notifyDataSetChanged()
//                        presenter.cityList(item.id)
                    }
                }

            }
        }

    }


    /**
     * 解析数据并组装成自己想要的list
     */
    private fun parseData() {
        var jsonStr = GetJsonDataUtil().getJson(this, "province.json") //获取assets目录下的json文件数据
        //     数据解析
        var gson = Gson()
        var type = object : TypeToken<List<JsonBean?>?>() {}.type
        var shengList: List<JsonBean> = gson.fromJson<List<JsonBean>>(jsonStr, type)
        //     把解析后的数据组装成想要的list
        options1Items = shengList
        //     遍历省
        for (i in shengList.indices) {
//         存放城市
            var cityList: ArrayList<String> = ArrayList()
            //         存放区
            var provinceAreaList: ArrayList<ArrayList<String>> = ArrayList()
            //         遍历市
            for (c in 0 until shengList[i].city.size) {
//        拿到城市名称
                val cityName: String = shengList[i].city[c].name
                cityList.add(cityName)
                val cityAreaList: ArrayList<String> = ArrayList() //该城市的所有地区列表
                if (shengList[i].city.get(c).area == null || shengList[i].city[c].area.size === 0) {
                    cityAreaList.add("")
                } else {
                    cityAreaList.addAll(shengList[i].city[c].area)
                }
                provinceAreaList.add(cityAreaList)
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList)
            /**
             * 添加地区数据
             */
            options3Items.add(provinceAreaList)
        }
    }

    /**
     * 地址选择器
     */
    private fun initAddressPicker() { // 弹出选择器
        pvAddress = OptionsPickerBuilder(
            this
        ) { options1, options2, options3, v -> //返回的分别是三个级别的选中位置
            var tx = options1Items[options1].name +
                    options2Items[options1][options2] +
                    options3Items[options1][options2][options3]
//            Toast.makeText(this, tx, Toast.LENGTH_SHORT).show()
            binding.btnActMeInfoAddress.text = tx
        }
            .setTitleText("城市选择")
            .setDividerColor(Color.BLACK)
            .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
            .setContentTextSize(20)
            .build<Any>()

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvAddress!!.setPicker(options1Items as List<Nothing>?,
            options2Items as List<Nothing>?, options3Items as List<Nothing>?
        ) //三级选择器
    }

    //初始化时间选择器
    private fun initTimePicker() {
        val selectedDate: Calendar = Calendar.getInstance()
        val startDate: Calendar = Calendar.getInstance()
        startDate.set(1900, 1, 1) //起始时间
        val endDate: Calendar = Calendar.getInstance()
        endDate.set(2099, 12, 31) //结束时间
        pvTime = TimePickerBuilder(this) { date, v -> //选中事件回调
            //mTvMyBirthday 这个组件就是个TextView用来显示日期 如2020-09-08
            binding.btnActMeInfoBirthday.text = getTimes(date)
        } //年月日时分秒 的显示与否，不设置则默认全部显示
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .setLabel("年", "月", "日", "", "", "")
            .isCenterLabel(true)
            .setDividerColor(Color.DKGRAY)
            .setTitleSize(21)
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setDecorView(null)
            .build()
    }

    //格式化时间
    private fun getTimes(date: Date): String? {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    //图片选择回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {//&&requestCode != 66
            if (requestCode == 234) {
                var photos: ArrayList<String>? = null
                selectedPhotos.clear()

                if (data != null) {
                    photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                }
                if (photos != null) {
                    for (i in photos) {
                        val file = File(i)
                        val name = file.name
                        val bitmap = IOHelper.loadBitmap(file.path, true)
                        val file1: File = compressImages(bitmap, name)
                         upFile(file1);
                    }
                    selectedPhotos.addAll(photos)
                }
                if (photoAdapter != null) photoAdapter!!.notifyDataSetChanged()
            }
        }
    }
    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    fun compressImages(bitmap: Bitmap, fileName: String): File {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 1024) { //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset() //重置baos即清空baos
            options -= 10 //每次都减少10
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) //这里压缩options%，把压缩后的数据存放到baos中
            val length = baos.toByteArray().size.toLong()
        }
        //        File file = new File(Environment.getExternalStorageDirectory(), fileName + ".png");
        val file1 = File(AppCache.getInstance().cardPath + "czkj/xzyx/pic/")
        if (!file1.isDirectory) {
            file1.mkdirs()
        }
        val file = File(AppCache.getInstance().cardPath + "czkj/xzyx/pic/1" + fileName)
//        val file = File(Environment.getExternalStorageDirectory(), fileName)
        try {
            val fos = FileOutputStream(file)
            try {
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        recycleBitmap(bitmap)
        return file
    }

    //释放
    fun recycleBitmap(vararg bitmaps: Bitmap?) {
        if (bitmaps == null) {
            return
        }
        for (bm in bitmaps) {
            if (null != bm && !bm.isRecycled) {
                bm.recycle()
            }
        }
    }

    private fun upFile(file1: File) {
        val name = SpUtils.getSharedStringData(this, "Username")
        //1.创建MultipartBody.Builder对象
        var builder =  MultipartBody.Builder().setType(MultipartBody.FORM)
        //2.获取图片，创建请求体
        val body: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file1)
        //3.调用MultipartBody.Builder的addFormDataPart()方法添加表单数据
        builder.addFormDataPart("username", name) //传入服务器需要的key，和相应value值
        builder.addFormDataPart("img_file", file1.name, body) //添加图片数据，body创建的请求体
        //4.创建List<MultipartBody.Part> 集合，
        //  调用MultipartBody.Builder的build()方法会返回一个新创建的MultipartBody
        //  再调用MultipartBody的parts()方法返回MultipartBody.Part集合
        val parts: List<MultipartBody.Part> = builder.build().parts()
        presenter.uploadAvatar(parts)
    }
    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this,msg)
    }
}