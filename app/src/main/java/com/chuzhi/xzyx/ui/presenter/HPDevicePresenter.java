package com.chuzhi.xzyx.ui.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.bean.rc.DynamicPasswordEntity;
import com.chuzhi.xzyx.ui.view.HPDeviceView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

import static android.os.FileUtils.copy;

public class HPDevicePresenter extends BasePresenter<HPDeviceView> {
    public HPDevicePresenter(HPDeviceView baseView) {
        super(baseView);
    }

    public void userComputerList() {
        addDisposable(apiServer.userComputerList(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ComputerListEntity> body = (BaseModel<ComputerListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getMsg().contains("未绑定")) {
                            ComputerListEntity entity = new ComputerListEntity();
                            baseView.userComputerList(entity);
                        } else {
                            if (body.getData() != null)
                                baseView.userComputerList(body.getData());
                        }
                    } else {
                        if (!body.getMsg().equals("账号已在其他设备登录，请重新登录")&&
                                !body.getMsg().equals("你还未登录/登录超时!")){
                            baseView.showError(body.getMsg());
                        }
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if (!msg.equals("账号已在其他设备登录，请重新登录")){
                    baseView.showError(msg);
                }
            }
        });
    }
    //设备信息展示
    public void showHomeInformation() {
        addDisposable(apiServer.showHomeInformation(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ComputerListEntity> body = (BaseModel<ComputerListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getMsg().contains("未绑定")) {
                            ComputerListEntity entity = new ComputerListEntity();
                            baseView.showHomeInformation(entity);
                        } else {
                            if (body.getData() != null)
                                baseView.showHomeInformation(body.getData());
                        }
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
    //动态口令
    public void getTotp(String id,int position) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id", id);
        addDisposable(apiServer.getTotp(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<DynamicPasswordEntity> body = (BaseModel<DynamicPasswordEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null){
                            baseView.getTotp(body.getData(),position);
                        }
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
    public void versionUpdate() {
        addDisposable(apiServer.getVersionUpdate(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                VersionUpdateNewEntity body = (VersionUpdateNewEntity) o;
                if (body != null) {
                            baseView.versionUpdate(body);
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
    //更新轨迹
    public void updateTrack(String sn,String coordinate) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        fieldMap.put("coordinate", coordinate);
        addDisposable(apiServer.updateTrack(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                            baseView.updateTrack(body);
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }

    //高德静态地图
    public void getMapImage(String location, String zoom,
                            String size, String markers,
                            String key) {

        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("location", location);
        fieldMap.put("zoom", zoom);
        fieldMap.put("size", size);
        fieldMap.put("markers", markers);
        fieldMap.put("key", key);
//        fieldMap.put("scale", scale);

        addDisposable(apiServer.getMapImage(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                ResponseBody body = (ResponseBody) o;
                if (body != null) {
                    try {
                        Bitmap bitmap1 = netToLoacalBitmap(body.string());
                        baseView.getMapImage(bitmap1);
//                        writeResponseBodyToDisk("测试图片",body);
                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
    /**
     * todo 将网络资源图片转换为Bitmap
     * @param imgUrl 网络资源图片路径
     * @return Bitmap
     * 该方法调用时要放在子线程中
     */
    public Bitmap netToLoacalBitmap(String imgUrl){
        Bitmap bitmap = null;
        InputStream in=null;
        BufferedOutputStream out = null;
        try{
            in = new BufferedInputStream(new URL(imgUrl).openStream(),1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream,1024);
            copy(in,out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            data = null;
            return bitmap;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
    /**
     * 保存下载的图片流写入SD卡文件
     * @param imageName  xxx.jpg
     * @param body  image stream
     */
    public static void writeResponseBodyToDisk(String imageName, ResponseBody body) {
        String galleryPath = Environment.getExternalStorageDirectory()

                + File.separator + Environment.DIRECTORY_DCIM

                + File.separator +"Camera" + File.separator;
        if(body==null){
            Log.e("bitmap===","图片源错误");
            return;
        }
        try {
            InputStream is = body.byteStream();
            File fileDr = new File(galleryPath);
            if (!fileDr.exists()) {
                fileDr.mkdir();
            }
            File file = new File(galleryPath, imageName);
            if (file.exists()) {
                file.delete();
                file = new File(galleryPath, imageName );
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
