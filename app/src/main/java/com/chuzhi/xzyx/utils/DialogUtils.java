package com.chuzhi.xzyx.utils;

import android.app.ProgressDialog;
import android.content.Context;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

/**
 * @Author : wyh
 * @Time : On 2023/6/29 11:06
 * @Description : DialogUtils
 */
public class DialogUtils {
    private ProgressDialog progressDialog;
    /**
     * 加载进度弹窗
     *
     * @param title 弹窗标题
     */
    public void showDialog(Context context,String title) {
        runOnUiThread(() -> {
            try {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(context);
                }
                if (progressDialog.isShowing()) {
                    return;
                }
                progressDialog.setTitle(title);
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //关闭进度圈圈
    public void canDialog() {
        if (progressDialog != null) {
            runOnUiThread(() ->
                    progressDialog.cancel()
            );
        }
    }
}
