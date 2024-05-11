package com.chuzhi.xzyx.ui.activity.find

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.luck.picture.lib.utils.ToastUtils
import com.nanchen.compresshelper.CompressHelper
import com.tbruyelle.rxpermissions2.RxPermissions
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityReleaseMessageBinding
import com.chuzhi.xzyx.ui.adapter.PhotoAdapter
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity
import com.chuzhi.xzyx.ui.fragment.homepage.HPDeviceFragment
import com.chuzhi.xzyx.ui.presenter.ReleaseMessagePresenter
import com.chuzhi.xzyx.ui.view.ReleaseMessageView
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.BitmapUtil
import com.chuzhi.xzyx.utils.CommenPop
import com.chuzhi.xzyx.utils.IOHelper
import com.chuzhi.xzyx.widget.LoadingDialog
import com.chuzhi.xzyx.widget.RecyclerItemClickListener
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


/**
 * 发布文章页面
 */
class ReleaseMessageActivity :
    BaseActivity<ActivityReleaseMessageBinding, ReleaseMessagePresenter>(), ReleaseMessageView {

    private var selectedPhotos = ArrayList<String>()
    private var photoAdapter: PhotoAdapter? = null
    private var uploadFlag = 0
    private var loadingDialog:LoadingDialog?=null
    private var rxPermissions: RxPermissions?=null
    private var permissionPop : AlertDialogIos? = null
    private var tipPop: CommenPop? = null
    override fun createPresenter(): ReleaseMessagePresenter {
        return ReleaseMessagePresenter(this)
    }

    override fun initView() {
        binding.includeActRelMsg.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActRelMsg.btnIncludeTitleAdd.visibility = View.VISIBLE
        binding.includeActRelMsg.tvIncludeTitleTitle.text = "发布作品"
        binding.includeActRelMsg.ivIncludeTitleBack.setOnClickListener { finish() }
        loadingDialog = LoadingDialog(this)
        loadingDialog!!.setTitleMsg("发布中")
        rxPermissions = RxPermissions(this)
        permissionPop()
        binding.rlvActRelMsg.layoutManager = GridLayoutManager(this, 3)
    }

    override fun initData() {
        presenter.articleCategoryList()
        photoAdapter = PhotoAdapter(this, selectedPhotos)
        binding.rlvActRelMsg.adapter = photoAdapter
        binding.rlvActRelMsg.addOnItemTouchListener(RecyclerItemClickListener(
            this
        ) { view, position ->
            if (photoAdapter!!.getItemViewType(position) === PhotoAdapter.TYPE_ADD) {
                permissionData()

            } else {
                PhotoPreview.builder()
                    .setPhotos(selectedPhotos)
                    .setCurrentItem(position)
                    .start(this, 20)
            }
        })
        //选择文章类型
        binding.tvActRelMsgCategory.setOnClickListener {
            pvOptions!!.show()
            hideSoftKeyboard(binding.tvActRelMsgCategory)
        }

        //提交
        binding.includeActRelMsg.btnIncludeTitleAdd.setOnClickListener {
            if (binding.etActRelMsgTitle.text.toString().trim() == "") {
                ToastUtils.showToast(this, "请输入文章标题！")
            } else if (binding.tvActRelMsgCategory.text.toString() == "请选择文章类型") {
                ToastUtils.showToast(this, "请选择文章类型！")
            } else if (binding.etActRelMsgContents.text.toString().trim() == "") {
                ToastUtils.showToast(this, "请输入文章内容！")
            } else {
                loadingDialog!!.show()
                binding.includeActRelMsg.btnIncludeTitleAdd.isEnabled = false
                if (selectedPhotos != null && selectedPhotos.size > 0) {//上传图片

//                    EasyImgCompress.withMultiPics(this, selectedPhotos)
//                        .maxPx(1200)
//                        .maxSize(100)
//                        .enableLog(true)
//                        .cacheDir(AppCache.getInstance().cardPath + "czkj/xzyx/pic/")
//                        .setOnCompressMultiplePicsListener(object :
//                            OnCompressMultiplePicsListener {
//                            override fun onStart() {
//                                Log.i("EasyImgCompress", "onStart")
//                            }
//
//                            override fun onSuccess(successFiles: List<File>) {
//                                for (i in successFiles.indices) {
//                                    Log.i(
//                                        "EasyImgCompress",
//                                        "onSuccess: successFile size = " + GBMBKBUtil.getSize(
//                                            successFiles[i].length()
//                                        ) + "path = " + successFiles[i].absolutePath
//                                    )
//                                    upFile(successFiles[i]);
//                                }
//                            }
//
//                            override fun onHasError(
//                                successFiles: List<File>,
//                                errorImages: List<ErrorBean>
//                            ) {
//                                for (i in successFiles.indices) {
//                                    Log.i(
//                                        "EasyImgCompress",
//                                        "onHasError: successFile  size = " + GBMBKBUtil.getSize(
//                                            successFiles[i].length()
//                                        ) + "path = " + successFiles[i].absolutePath
//                                    )
//
//                                }
//                                for (i in errorImages.indices) {
//                                    Log.e(
//                                        "EasyImgCompress",
//                                        "onHasError: errorImg url = " + errorImages[i].errorImgUrl
//                                    )
//                                    Log.e(
//                                        "EasyImgCompress",
//                                        "onHasError: errorImg msg = " + errorImages[i].errorMsg
//                                    )
//                                }
//                            }
//                        }).start()

                    for (i in selectedPhotos) {
                        val file = File(i)
                        val name = file.name
                        val bitmap = IOHelper.loadBitmap(file.path, true)
//                        val file1: File = compressImages(bitmap, name)
                        val file1: File = BitmapUtil.compressImage(file.path,name)

                        val newFile  = CompressHelper.Builder(this)
                            .setMaxWidth(720F)  // 默认最大宽度为720
                            .setMaxHeight(960F) // 默认最大高度为960
                            .setQuality(80)    // 默认压缩质量为80
                            .setFileName(name) // 设置你需要修改的文件名
                            .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                            .setDestinationDirectoryPath(AppCache.getInstance().cardPath + "czkj/xzyx/pic/")
                            .build()
                            .compressToFile(file);

                        upFile(newFile);
                    }
                } else {//不上传图片
                    for (i in 0 until categoryDataList.size) {
                        if (categoryDataList[i].name == binding.tvActRelMsgCategory.text.toString()) {
                            category = categoryDataList[i].id
                        }
                    }
                    presenter.publishArticle(
                        binding.etActRelMsgTitle.text.toString().trim(),
                        binding.etActRelMsgContents.text.toString().trim(),
                        imgList, category
                    )
                }
            }
        }

        //监听标题输入长度
        binding.etActRelMsgTitle.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                binding.tvActRelMsgLength.text = binding.etActRelMsgTitle.text.length.toString()+"/50"
            }
        })
    }

    //文章类别
    var pvOptions: OptionsPickerView<*>? = null
    private fun msgCategory() {
        pvOptions = OptionsPickerBuilder(
            this
        ) { options1, options2, options3, v ->
            val date: String =
                categoryList[options1]
            //在此获取选择到的内容
            binding.tvActRelMsgCategory.text = date
        }
            .setTitleText("文章类型")
            .setContentTextSize(16)
            .build<Any>()

        pvOptions!!.setPicker(categoryList as List<Nothing>?)
    }
    private fun tipPopup(){
        tipPop = CommenPop.getNormalPopu(this,R.layout.pop_tip,binding.llActRelMsgTop)
        val contentView = tipPop!!.contentView
        val tvTip = contentView.findViewById<TextView>(R.id.tv_pop_tip)
        tvTip.text = "相机权限使用说明：\n用于拍照、录制视频等场景\n读取文件权限说明：\n获取相册文件"
        tipPop!!.isOutsideTouchable = true
        tipPop!!.isFocusable = true
        CommenPop.backgroundAlpha(0.5f, this)
        tipPop!!.showAtLocation(
            binding.llActRelMsgTop,
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
                    .setPhotoCount(PhotoAdapter.MAX)
                    .setShowCamera(true)
                    .setPreviewEnabled(false)
                    .setSelected(selectedPhotos)
                    .start(this!!, 234)
            } else {
                permissionPop!!.show()
            }
        }
    }

    //权限回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("权限回调打印",requestCode.toString()+permissions+grantResults)
    }
    //相机相册回调
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
//                        val file1: File = compressImages(bitmap, name)
//                         upFile(file1);//不在此处上传
                    }
                    selectedPhotos.addAll(photos)
                }
                if (photoAdapter != null) photoAdapter!!.notifyDataSetChanged()
            } else if (requestCode == 20) {
                if (data == null) {
                    return
                }
                var photoLists = data!!.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                if (photoLists != null) {//&& !photoLists.isEmpty()
                    selectedPhotos = photoLists

                    if (photoAdapter != null) {
                        photoAdapter!!.setNewData(selectedPhotos)
//                            photoAdapter!!.notifyDataSetChanged()
                    }
                }
//                    mAddPicture.setPaths(mImagePaths);
            }
        }
    }

    private fun upFile(file1: File) {
        val fileRq = RequestBody.create(MediaType.parse("image/*"), file1)
        val part = MultipartBody.Part.createFormData("img_file", file1.name, fileRq)
        presenter.uploadImg(file1, part)
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
        while (baos.toByteArray().size / 1024 > 1024) { //循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset() //重置baos即清空baos
            options -= 10 //每次都减少10
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            )
            //这里压缩options%，把压缩后的数据存放到baos中
            val length = baos.toByteArray().size.toLong()
        }
        val isBm =
            ByteArrayInputStream(baos.toByteArray()) //把压缩后的数据baos存放到ByteArrayInputStream中

        val bitmaps = BitmapFactory.decodeStream(isBm, null, null) //把ByteArrayInputStream数据生成图片

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
        recycleBitmap(bitmaps)
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

    //文章类型
    var categoryDataList = ArrayList<ArticleCategoryEntity1.ArtcleCategoryListDTO>()
    var categoryList = ArrayList<String>()
    override fun articleCategoryList(msg: ArticleCategoryEntity1?) {
        categoryDataList.clear()
        for (i in 0 until msg!!.artcle_category_list.size) {
            if (msg.artcle_category_list[i].flag == 1) {
                categoryDataList.add(msg.artcle_category_list[i])
                categoryList.add(msg.artcle_category_list[i].name)
            }
        }
        msgCategory()
    }

    //上传图片
    var imgList = ArrayList<String>()
    var category = 0
    override fun uploadImg(msg: UploadFileImgEntity?) {
        imgList.add("\""+msg!!.file_path+"\"")
        uploadFlag++
        if (uploadFlag == selectedPhotos.size) {
            for (i in 0 until categoryDataList.size) {
                if (categoryDataList[i].name == binding.tvActRelMsgCategory.text.toString()) {
                    category = categoryDataList[i].id
                }
            }
            presenter.publishArticle(
                binding.etActRelMsgTitle.text.toString().trim(),
                binding.etActRelMsgContents.text.toString().trim(),
                imgList, category
            )
            Log.e("上传完毕==》", "共" + uploadFlag + "张图片")
            uploadFlag = 0
        } else {
            Log.e("上传中==》", "第" + uploadFlag + "张图片")

        }

    }

    //发布文章回调
    override fun publishArticle(msg: BaseModel<String>?) {
        imgList.clear()
        selectedPhotos.clear()
        category = 0
        if (loadingDialog!=null){
            loadingDialog!!.dismiss()
        }
        ToastUtils.showToast(this,"发布成功")
        finish()
    }

    //隐藏键盘
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tipPop!=null){
            if (tipPop!!.isShowing){
                tipPop!!.dismiss()
            }
        }
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtils.showToast(this,msg)
    }
}