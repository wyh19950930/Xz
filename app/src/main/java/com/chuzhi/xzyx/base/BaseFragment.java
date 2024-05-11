package com.chuzhi.xzyx.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;


import com.chuzhi.xzyx.widget.LoadingDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<T extends ViewBinding, P extends BasePresenter> extends Fragment implements BaseView{
    //获取TAG的fragment名称
    protected final String TAG = this.getClass().getSimpleName();
    public Context mContext;
//    private ProgressDialog dialog;
    private LoadingDialog dialog;
    /**
     * 封装toast对象
     */
    private Toast toast;
    protected P presenter;
    protected T binding;
    protected abstract P createPresenter();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            binding = (T) method.invoke(null, getLayoutInflater(), container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return binding.getRoot();

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = createPresenter();
        initView();
        initData();
    }


    protected abstract void initView();

    protected abstract void initData();

    /**
     * 保证同一按钮在1秒内只响应一次点击事件
     */
    public abstract class OnSingleClickListener implements View.OnClickListener {
        //两次点击按钮的最小间隔，目前为1000
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime;

        public abstract void onSingleClick(View view);

        @Override
        public void onClick(View v) {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime;
                onSingleClick(v);
            }
        }
    }

    /**
     * 同一按钮在短时间内可重复响应点击事件
     */
    public abstract class OnMultiClickListener implements View.OnClickListener {
        public abstract void onMultiClick(View view);

        @Override
        public void onClick(View v) {
            onMultiClick(v);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        if (presenter != null) {
            presenter.detachView();
        }
    }

    /**
     * @param s
     */
    public void showtoast(String s) {
        if (toast == null) {
            toast = Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG);
        }
        toast.show();
    }

    private void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
//        LoadingDialog1.getInstance(mContext).dismiss();//隐藏
    }

    private void showLoadingDialog() {

        if (dialog == null) {
            dialog = new LoadingDialog(mContext);
        }
        dialog.setTitle("加载中，请稍后！");
        dialog.setCancelable(false);
        dialog.show();
//        LoadingDialog1.getInstance(mContext).show();//显示
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
        showtoast(msg);
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
