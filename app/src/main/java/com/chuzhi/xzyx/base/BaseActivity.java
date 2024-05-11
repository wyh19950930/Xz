package com.chuzhi.xzyx.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.app.AppManager;
import com.chuzhi.xzyx.utils.ActivityCollectorUtil;
import com.chuzhi.xzyx.utils.StatusBar;
import com.chuzhi.xzyx.utils.StatusBarCompat;
import com.chuzhi.xzyx.widget.LoadingDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<T extends ViewBinding, P extends BasePresenter> extends AppCompatActivity implements BaseView {
    public Context context;
//    private ProgressDialog dialog;
    private LoadingDialog dialog;
    public Toast toast;
    protected P presenter;
    protected T binding;

    protected abstract P createPresenter();

//    protected abstract int getLayoutId();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        doBeforeSetcontentView();

        //反射获取viewbingding
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            binding = (T) method.invoke(null, getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        context = this;
        ActivityCollectorUtil.addActivity(this);
//        setContentView(getLayoutId());
        presenter = createPresenter();
        initView();
        initData();
    }

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
        //设置昼夜主题
//        initTheme();
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        // 无标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //状态栏半透明
//        setStatusBarTranslucent();
        //状态栏透明
        setStatusBarTransparent();
//        AndroidBarUtils.setTranslucent(this);
        //状态栏字体颜色 true浅色 false深色
        setStatusBarTextColor(false);
    }
    //新状态栏
    //设置颜色为半透明
    protected void setStatusBarTranslucent(){
        StatusBar.setColor(this,R.color.translucent);
    }
    //设置颜色为透明
    protected void setStatusBarTransparent(){
        StatusBar.setColor(this,R.color.transparent);
    }
    //隐藏状态栏
    protected void setStatusBarHide(){
        StatusBar.hide(this);
    }
    //设置字体颜色
    protected void setStatusBarTextColor(boolean isDarkBackground){
        StatusBar.setTextColor(this,isDarkBackground);
    }
    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent));
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor(int color) {
        StatusBarCompat.setStatusBarColor(this, color);
    }

    /**
     * 沉浸状态栏（4.4以上系统有效）
     */
    protected void SetTranslanteBar() {
        StatusBarCompat.translucentStatusBar(this);
    }

    /*
    * 透明状态栏，暗色图标字体
    */
    protected void setStatusBar(int color) {
        StatusBarCompat.setStatusBar(this,color);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
        ActivityCollectorUtil.removeActivity(this);
    }

    /**
     * @param s
     */
    public void showtoast(String s) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        }
        toast.show();
    }

    private void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

//        LoadingDialog1.getInstance(this).dismiss();//隐藏
    }

    private void showLoadingDialog() {

        if (dialog == null) {
            dialog = new LoadingDialog(context);
        }
        dialog.setTitle("加载中，请稍后！");
        dialog.setCancelable(false);
        dialog.show();

//        LoadingDialog1.getInstance(this).show();//显示
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        closeLoadingDialog();
    }

    @Override
    public void showError(String msg) {
//        showtoast(msg);
    }

    @Override
    public void onErrorCode(BaseModel model) {
    }

    /*@Override
    public void showLoadingFileDialog() {
        showFileDialog();
    }

    @Override
    public void hideLoadingFileDialog() {
        hideFileDialog();
    }*/

    @Override
    public void onProgress(long totalSize, long downSize) {
//        if (dialog != null) {
//            dialog.setProgress((int) (downSize * 100 / totalSize));
//        }
    }
}

