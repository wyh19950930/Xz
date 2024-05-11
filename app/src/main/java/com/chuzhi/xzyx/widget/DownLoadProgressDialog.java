package com.chuzhi.xzyx.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.chuzhi.xzyx.R;

/**
 * @Author : wyh
 * @Time : On 2023/7/24 17:45
 * @Description : DownLoadProgress
 */
public class DownLoadProgressDialog extends Dialog {
    public DownLoadProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.pop_download_progress,null);
        setContentView(view);
    }
}
