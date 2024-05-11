package com.chuzhi.xzyx.ui.activity.me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.utils.dynamicpassword.DynamicToken;
import com.chuzhi.xzyx.utils.dynamicpassword.TOTP;
import com.chuzhi.xzyx.utils.dynamicpassword.TotpUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DynamicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        TextView tvDynamic = findViewById(R.id.tv_act_dynamic);
        Button btnDynamic = findViewById(R.id.btn_act_dynamic);
        Button btnDynamic2 = findViewById(R.id.btn_act_dynamic2);


        TotpUtil totpUtil = new TotpUtil();
        // Seed for HMAC-SHA1 - 20 bytes
        String seed = "SS00-3B54-BB2B-0005" +
                "zhangx2023_!070812345678900987654321qazwsxedcrfvtgbyhnujmik,ol.678912344566699944435109637fgvrcdgHHBFHMJHJBFFV";
        // Seed for HMAC-SHA256 - 32 bytes
        String seed32 = "SS00-3B54-BB2B-0005" +
                "zhangx2023_!070812345678900987654321qazwsxedcrfvtgbyhnujmik,ol.678912344566699944435109637fgvrcdgHHBFHMJHJBFFV";
        // Seed for HMAC-SHA512 - 64 bytes
        String seed64 = "SS00-3B54-BB2B-0005" +
                "zhangx2023_!070812345678900987654321qazwsxedcrfvtgbyhnujmik,ol.678912344566699944435109637fgvrcdgHHBFHMJHJBFFV";
        String seed_ = strToHex(seed);
        String seed32_ = strToHex(seed32);
        String seed64_ = strToHex(seed64);

        long T0 = 0;
        long X = 60;
        long testTime[] = {System.currentTimeMillis()};

        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        try {
            System.out.println(
                    "+---------------+-----------------------+" +
                            "------------------+--------+--------+");
            System.out.println(
                    "|  Time(sec)    |   Time (UTC format)   " +
                            "| Value of T(Hex)  |  TOTP  | Mode   |");
            System.out.println(
                    "+---------------+-----------------------+" +
                            "------------------+--------+--------+");

//            for (int i=0; i<testTime.length; i++) {
//                long T = (testTime[i] - T0)/X;
//                steps = Long.toHexString(T).toUpperCase();
//                while (steps.length() < 16) steps = "0" + steps;
//                String fmtTime = String.format("%1$-11s", testTime[i]);
//                String utcTime = df.format(new Date(testTime[i]*1000));
//                System.out.print("|  " + fmtTime + "  |  " + utcTime +
//                        "  | " + steps + " |");
//                System.out.println(totpUtil.generateTOTP(seed_, steps, "6",
//                        "HmacSHA1") + "| SHA1   |");
//                System.out.print("|  " + fmtTime + "  |  " + utcTime +
//                        "  | " + steps + " |");
//                System.out.println(totpUtil.generateTOTP(seed32_, steps, "6",
//                        "HmacSHA256") + "| SHA256 |");
//                System.out.print("|  " + fmtTime + "  |  " + utcTime +
//                        "  | " + steps + " |");
//                System.out.println(totpUtil.generateTOTP512(seed64_, steps, "6") + "| SHA512 |");
//
//                System.out.println(
//                        "+---------------+-----------------------+" +
//                                "------------------+--------+--------+");
//            }

            long T = (System.currentTimeMillis() - T0)/X;
            steps = Long.toHexString(T).toUpperCase();
            while (steps.length() < 16) steps = "0" + steps;
            String fmtTime = String.format("%1$-11s", System.currentTimeMillis());
            String utcTime = df.format(new Date(System.currentTimeMillis()));
            System.out.print("|  " + fmtTime + "  |  " + utcTime +
                    "  | " + steps + " |");
            System.out.println(totpUtil.generateTOTP(seed_, steps, "6") + "| SHA1   |");
            System.out.print("|  " + fmtTime + "  |  " + utcTime +
                    "  | " + steps + " |");
            System.out.println(totpUtil.generateTOTP256(seed32_, steps, "6") + "| SHA256 |");
            System.out.print("|  " + fmtTime + "  |  " + utcTime +
                    "  | " + steps + " |");
            System.out.println(totpUtil.generateTOTP512(seed64_, steps, "6") + "| SHA512 |");

            System.out.println(
                    "+---------------+-----------------------+" +
                            "------------------+--------+--------+");



        }catch (final Exception e){
            System.out.println("Error : " + e);
        }


        btnDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python python = Python.getInstance();
//                python.getModule("totp").callAttr("generateTOTP");
                python.getModule("hello").callAttr("sayHello");
            }
        });

        btnDynamic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python python = Python.getInstance();
                PyObject pyObject = python.getModule("totp").callAttr("generateTOTP", "SS00-3B58-A593-000D");
                Log.e("打印result",pyObject.toString());
            }
        });
    }

    public String strToHex(String str){
        byte[] bytes = str.getBytes();
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        String hexString = hexStringBuilder.toString();
        return hexString;
    }
}