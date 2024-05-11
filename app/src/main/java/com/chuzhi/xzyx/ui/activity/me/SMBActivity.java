package com.chuzhi.xzyx.ui.activity.me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.chuzhi.xzyx.R;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import me.jingbin.smb.BySMB;
import me.jingbin.smb.OnReadFileListNameCallback;

public class SMBActivity extends AppCompatActivity {
    String serverUrl = "smb://192.168.2.52";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smbactivity);
//        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(serverUrl, "tset", "0706");
//
//        try {
//            SmbFile smbFile = new SmbFile(serverUrl,auth);
//            InputStream in = new BufferedInputStream(new SmbFileInputStream(smbFile));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // 处理读取的每一行
//                Log.e("文件名",line);
//            }
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    BySMB bySMB =  BySMB.with()
                            .setConfig("192.168.2.52","tset","0706","D")
                            .setReadTimeOut(60)
                            .setSoTimeOut(180)
                            .build();
                    bySMB.listShareFileName(new OnReadFileListNameCallback() {
                        @Override
                        public void onSuccess(@NotNull List<String> list) {
                            Log.e("文件读取成功",list.toString());
                        }

                        @Override
                        public void onFailure(@NotNull String s) {
                            Log.e("文件读取失败：",s);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }
}