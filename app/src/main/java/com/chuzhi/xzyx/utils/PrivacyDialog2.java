package com.chuzhi.xzyx.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.activity.me.PrivacyPolicyActivity;
import com.chuzhi.xzyx.ui.activity.me.UserAgreementActivity;

/***
 * 首页隐私弹框提示2
 */
public class PrivacyDialog2 {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

    public PrivacyDialog2(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public PrivacyDialog2 builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_privacy2_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.privacy2_lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.privacy2_txt_title);
        txt_msg = (TextView) view.findViewById(R.id.privacy2_txt_msg);
        btn_neg = (Button) view.findViewById(R.id.privacy2_btn_neg);
        btn_pos = (Button) view.findViewById(R.id.privacy2_btn_pos);
        img_line = (ImageView) view.findViewById(R.id.privacy2_img_line);
        setGone();
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.70), LayoutParams.WRAP_CONTENT));
        txt_title.setVisibility(View.GONE);
        return this;
    }

    /**
     * 恢复初始
     *
     * @return
     */
    public PrivacyDialog2 setGone() {
        if (lLayout_bg != null) {
            txt_title.setVisibility(View.GONE);
            txt_msg.setVisibility(View.GONE);
            btn_neg.setVisibility(View.GONE);
            btn_pos.setVisibility(View.GONE);
            img_line.setVisibility(View.GONE);

        }
        showTitle = false;
        showMsg = false;
        showPosBtn = false;
        showNegBtn = false;
        return this;
    }

    /**
     * 设置title
     *
     * @param title
     * @return
     */
    public PrivacyDialog2 setTitle(String title) {
        showTitle = true;
        if (TextUtils.isEmpty(title)) {
            txt_title.setText("提示");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    /**
     * 设置Message
     *
     * @param msg
     * @return
     */
    public PrivacyDialog2 setMsg(String msg) {
        showMsg = true;
        if (TextUtils.isEmpty(msg)) {
            txt_msg.setText("");
        } else {
            setSpannableStringBuilder(msg);
            txt_msg.setText(spannableStringBuilder);
            txt_msg.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return this;
    }

    /**
     *
     */
    public void setSpannableStringBuilder(String msg) {
        spannableStringBuilder.append(msg);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(@NonNull View view) {
                context.startActivity(new Intent(context, UserAgreementActivity.class));
            }
        };
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(@NonNull View view) {
                context.startActivity(new Intent(context, PrivacyPolicyActivity.class));
            }
        };
        spannableStringBuilder.setSpan(clickableSpan,5,12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(clickableSpan1,14,19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan colorSpan =new ForegroundColorSpan(Color.parseColor("#0066ff"));
        ForegroundColorSpan colorSpan1 =new ForegroundColorSpan(Color.parseColor("#0066ff"));
        spannableStringBuilder.setSpan(colorSpan,5,13,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(colorSpan1,14,20,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    /**
     * 设置点击外部是否消失
     *
     * @param cancel
     * @return
     */
    public PrivacyDialog2 setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    /**
     * 右侧按钮
     *
     * @param text
     * @param listener
     * @return
     */
    public PrivacyDialog2 setPositiveButton(String text,
                                            final OnClickListener listener) {
        return setPositiveButton(text, -1, listener);
    }

    public PrivacyDialog2 setPositiveButton(String text, int color,
                                            final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("");
        } else {
            btn_pos.setText(text);
        }
        if (color == -1) {
            color = R.color.text_default;
        }
        btn_pos.setTextColor(ContextCompat.getColor(context, color));
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    public PrivacyDialog2 setPositiveButtonNoDismiss(String text, int color,
                                                     final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("");
        } else {
            btn_pos.setText(text);
        }
        if (color == -1) {
            color = R.color.text_default;
        }
        btn_pos.setTextColor(ContextCompat.getColor(context, color));
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
            }
        });
        return this;
    }

    /**
     * 左侧按钮
     *
     * @param text
     * @param listener
     * @return
     */

    public PrivacyDialog2 setNegativeButton(String text,
                                            final OnClickListener listener) {

        return setNegativeButton(text, -1, listener);
    }

    public PrivacyDialog2 setNegativeButton(String text, int color,
                                            final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("");
        } else {
            btn_neg.setText(text);
        }
        if (color == -1) {
            color = R.color.text_default;
        }
        btn_neg.setTextColor(ContextCompat.getColor(context, color));

        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    /**
     * 设置显示
     */
    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alert_dialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alert_dialog_selector);
        }
    }

    public void show() {
        setLayout();
        if (!isShowing()) {
            dialog.show();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            if (dialog.isShowing())
                return true;
            else
                return false;
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }
}


