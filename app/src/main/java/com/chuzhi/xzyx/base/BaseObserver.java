package com.chuzhi.xzyx.base;

import android.content.Intent;
import android.util.Log;

import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.utils.SpUtils;
import com.google.gson.JsonParseException;
import com.chuzhi.xzyx.api.AppCache;
import com.chuzhi.xzyx.app.MyApplication;
import com.chuzhi.xzyx.ui.activity.login.LoginActivity;
import com.chuzhi.xzyx.utils.ActivityCollectorUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class BaseObserver<T> extends DisposableObserver<T> {

    protected BaseView view;

    private boolean isShowDialog;


    public BaseObserver(BaseView view) {
        this.view = view;
    }

    public BaseObserver(BaseView view, boolean isShowDialog) {
        this.view = view;
        this.isShowDialog = isShowDialog;
    }

    @Override
    protected void onStart() {
        if (view != null && isShowDialog) {
            view.showLoading();
        }
    }

    @Override
    public void onNext(T o) {
        BaseModel<String> body = (BaseModel<String>) o;
        if (body.getMsg().equals("账号已在其他设备登录，请重新登录")||body.getMsg().equals("你还未登录/登录超时!")){
            if (body.getMsg().equals("账号已在其他设备登录，请重新登录")){
                AppCache.getInstance().setLogInAgain(0);
            }else if (body.getMsg().equals("你还未登录/登录超时!")){
                AppCache.getInstance().setLogInAgain(-1);
            }

            SpUtils.setSharedList(MyApplication.Companion.getInstance(),
                    "userComputerList",new ArrayList<ComputerListEntity.ComputerListDTO>());
            EventBus.getDefault().postSticky("退出登录");
            //清空栈内的Activity
            ActivityCollectorUtil.finishAllActivity();
            //跳转登录页面
            Intent intent = new Intent(MyApplication.Companion.getInstance(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.Companion.getInstance().startActivity(intent);
        }else {
            onSuccess(o);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (view != null && isShowDialog) {
            view.hideLoading();
        }
        BaseException be = null;

        if (e != null) {

            if (e instanceof BaseException) {
                be = (BaseException) e;

                //回调到view层 处理 或者根据项目情况处理
                if (view != null) {
                    view.onErrorCode(new BaseModel(be.getErrorCode(), be.getErrorMsg()));
                    Log.e("BaseObserver:1",be.getErrorMsg());
                } else {
                    onError(be.getErrorMsg());
                    Log.e("BaseObserver:2",be.getErrorMsg());
                }

            } else {
                if (e instanceof HttpException) {
                    //   HTTP错误
                    be = new BaseException(BaseException.BAD_NETWORK_MSG, e, BaseException.BAD_NETWORK);
                } else if (e instanceof ConnectException
                        || e instanceof UnknownHostException) {
                    //   连接错误
                    be = new BaseException(BaseException.CONNECT_ERROR_MSG, e, BaseException.CONNECT_ERROR);
                } else if (e instanceof InterruptedIOException) {
                    //  连接超时
                    be = new BaseException(BaseException.CONNECT_TIMEOUT_MSG, e, BaseException.CONNECT_TIMEOUT);
                } else if (e instanceof JsonParseException
                        || e instanceof JSONException
                        || e instanceof ParseException) {
                    //  解析错误
                    be = new BaseException(BaseException.PARSE_ERROR_MSG, e, BaseException.PARSE_ERROR);
                    Log.e("BaseObserver:3",BaseException.PARSE_ERROR_MSG);
                } else {
                    be = new BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER);
                }
            }
        } else {
            be = new BaseException(BaseException.OTHER_MSG, e, BaseException.OTHER);
        }

        onError(be.getErrorMsg());
        Log.e("BaseObserver:4",be.getErrorMsg());
    }

    @Override
    public void onComplete() {
        if (view != null && isShowDialog) {
            view.hideLoading();
        }

    }


    public abstract void onSuccess(T o);

    public abstract void onError(String msg);


}


