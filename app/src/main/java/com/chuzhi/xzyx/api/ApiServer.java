package com.chuzhi.xzyx.api;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.ui.bean.UserIpEntity;
import com.chuzhi.xzyx.ui.bean.bbs.AnswerListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleAnswersEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleDetailsEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;
import com.chuzhi.xzyx.ui.bean.bbs.CarouselArticleEntity;
import com.chuzhi.xzyx.ui.bean.bbs.CityListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo;
import com.chuzhi.xzyx.ui.bean.bbs.PraiseCountEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ProvinceListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity;
import com.chuzhi.xzyx.ui.bean.bbs.UserArticleListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity;
import com.chuzhi.xzyx.ui.bean.rc.AllCordinateDataEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerInfoEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.bean.rc.DynamicPasswordEntity;
import com.chuzhi.xzyx.ui.bean.rc.GeoFenceCountEntity;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceRecordEntity;
import com.chuzhi.xzyx.ui.bean.rc.PortStatusEntity;
import com.chuzhi.xzyx.ui.bean.rc.ShowPortListEntity;
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.chuzhi.xzyx.api.ApiRetrofit.BASE_GET_VERSION_URL;

public interface ApiServer {

    ///**************************远程管控接口****************************///
    /**
     *登录模块
     */
    //远程管控登录
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")//静态替换
    @POST("app/getToken/")
    Observable<BaseModel> rcGetToken(@FieldMap Map<String, Object> params);
    //根据sn号搜索设备信息
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/computerInfo/")
    Observable<BaseModel<ComputerInfoEntity>> computerInfo(@FieldMap Map<String, Object> params);
    //绑定设备
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/bindComputer/")
    Observable<BaseModel> bindComputer(@FieldMap Map<String, Object> params);
    //解绑设备
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/unbindComputer/")
    Observable<BaseModel> unbindComputer(@FieldMap Map<String, Object> params);
    //修改设备名称
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/alterBindInfo/{id}/")
    Observable<BaseModel> alterBindInfo(@Path ("id") int id,@FieldMap Map<String,Object> params);
    //设备信息列表
    @Headers("BaseUrlName:BASE_RC")
    @GET("app/userComputerList/")
    Observable<BaseModel<ComputerListEntity>> userComputerList();
    //设备首页信息查询(下发命令)
    @Headers("BaseUrlName:BASE_RC")
    @GET("app/inquireHomeInformation/")
    Observable<BaseModel> inquireHomeInformation();
    //设备首页信息展示
    @Headers("BaseUrlName:BASE_RC")
    @GET("app/showHomeInformation/")
    Observable<BaseModel<ComputerListEntity>> showHomeInformation();
    //查询设备位置①
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/checkCoordinate/")
    Observable<BaseModel> checkCoordinate(@FieldMap Map<String, Object> params);

    //锁机和解锁①
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/clockComputer/")
    Observable<BaseModel> clockComputer(@FieldMap Map<String, Object> params);
    //查询锁机状态②
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/checkClockStatus/")
    Observable<BaseModel> checkClockStatus(@FieldMap Map<String, Object> params);
    //获取锁机状态信息③
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getClockStatus/")
    Observable<BaseModel> getClockStatus(@FieldMap Map<String, Object> params);
    //查询首页全部设备位置①
    @Headers("BaseUrlName:BASE_RC")
    @GET("app/checkUserCoordinate/")
    Observable<BaseModel> checkUserCoordinate();
    //展示首页全部设备位置②
    @Headers("BaseUrlName:BASE_RC")
    @GET("app/getUserCoordinateData/")
    Observable<BaseModel<AllCordinateDataEntity>> getUserCoordinateData();
    //围栏信息列表
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/geofenceList/")
    Observable<BaseModel<GeofenceListEntity>> geofenceList(@FieldMap Map<String, Object> params);
    //添加围栏
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/addGeofence/")
    Observable<BaseModel> addGeofence(@FieldMap Map<String, Object> params);
    //修改围栏
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("/api/app/alterGeofence/{id}/")// 值为follower,查询的是关注者发布的文章列表
    Observable<BaseModel> alterGeofence(@Path ("id") int id,@FieldMap Map<String,Object> params);
    //删除围栏
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/deleteGeofence/")
    Observable<BaseModel> deleteGeofence(@FieldMap Map<String, Object> params);
    //历史轨迹列表
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/trackInfo/{page}/")
    Observable<BaseModel<TrackInfoListEntity>> trackInfo(@Path ("page") int page,@FieldMap Map<String, Object> params);
    //越过围栏总次数
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getGeofenceCount/")
    Observable<BaseModel<GeoFenceCountEntity>> getGeofenceCount(@FieldMap Map<String, Object> params);
    //更新轨迹
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/updateTrack/")
    Observable<BaseModel> updateTrack(@FieldMap Map<String, Object> params);

    //设备端口列表信息查询①
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/inquirePortList/")
    Observable<BaseModel> inquirePortList(@FieldMap Map<String, Object> params);
    //设备端口列表信息展示②
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/showPortList/")
    Observable<BaseModel<ShowPortListEntity>> showPortList(@FieldMap Map<String, Object> params);
    //设备端口操作①
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/portOperations/")
    Observable<BaseModel> portOperations(@FieldMap Map<String, Object> params);
    //设备端口查询操作结果②
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getOperationsResult/")
    Observable<BaseModel> getOperationsResult(@FieldMap Map<String, Object> params);
    //设备查询最终状态③
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getOperationsStatus/")
    Observable<BaseModel<PortStatusEntity>> getOperationsStatus(@FieldMap Map<String, Object> params);


    //首页风险操作(开关机、二级菜单)
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/riskOperations/")
    Observable<BaseModel> riskOperations(@FieldMap Map<String, Object> params);
    //首页安全日志
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getGeofenceRecord/{page}/")
    Observable<BaseModel<GeofenceRecordEntity>> getGeofenceRecord(@Path ("page") int page, @FieldMap Map<String, Object> params);

    //动态口令
    @FormUrlEncoded
    @Headers("BaseUrlName:BASE_RC")
    @POST("app/getTotp/")
    Observable<BaseModel<DynamicPasswordEntity>> getTotp(@FieldMap Map<String, Object> params);


    //高德地图静态图片
    @Headers("BaseUrlName:BASE_MAP")
    @GET("v3/staticmap")
    Observable<ResponseBody> getMapImage(@QueryMap Map<String,Object> params);

    //获取ip属地
    @Headers("BaseUrlName:BASE_MAP")
    @GET("ipJson.jsp/")
    Observable<UserIpEntity> getUserIp(@QueryMap Map<String,Object> params);
//    Observable getImage(@Query("location") String location,@Query("zoom") String zoom,
//                        @Query("size") String size,@Query("markers") String markers,
//                        @Query("key") String key,@Query("scale") String scale);


    ///**************************bbs论坛接口****************************///

    /**
     *登录模块
     */
    //bbs获取验证码
    @FormUrlEncoded
    @POST("bbs/smsCaptcha/")
    Observable<BaseModel> smsCaptcha(@FieldMap Map<String, Object> params);
    //bbs验证码注册（账号密码注册）
    @FormUrlEncoded
    @POST("bbs/register/")
    Observable<BaseModel<BbsUserEntity>> registerUserCode(@FieldMap Map<String, Object> params);
    //bbs忘记密码
    @FormUrlEncoded
    @POST("bbs/oauthRetrievePassword/")
    Observable<BaseModel> oauthRetrievePassword(@FieldMap Map<String, Object> params);
    //bbs登录
    @FormUrlEncoded
    @POST("bbs/bbsGetToken/")
    Observable<BaseModel<BbsUserEntity>> bbsGetToken(@FieldMap Map<String, Object> params);

    /**
     * 发现模块
     */
    //文章详情
    @GET("bbs/articleDetails/")
    Observable<BaseModel<ArticleDetailsEntity>> articleDetails(@QueryMap Map<String,Object> params);
    //文章分类tab
    @GET("bbs/articleCategoryList/")
//    Observable<BaseModel<List<ArticleCategoryEntity>>> articleCategoryList();
    Observable<BaseModel<ArticleCategoryEntity1>> articleCategoryList();
    //文章列表
    @GET("/api/bbs/homepage/{page}/")// 值为follower,查询的是关注者发布的文章列表
    Observable<BaseModel<ArticleListEntity>> articleList(@Path ("page") int page,@QueryMap Map<String,Object> params);
    //关注用户
    @FormUrlEncoded
    @POST("bbs/addFollow/")
    Observable<BaseModel>addFollow(@FieldMap Map<String, Object> params);
    //取消关注用户
    @FormUrlEncoded
    @POST("bbs/deleteFollow/")
    Observable<BaseModel>deleteFollow(@FieldMap Map<String, Object> params);
    //文章点赞
    @FormUrlEncoded
    @POST("bbs/articlePraise/")
    Observable<BaseModel>articlePraise(@FieldMap Map<String, Object> params);
    //评论点赞
    @FormUrlEncoded
    @POST("bbs/answerPraise/")
    Observable<BaseModel>answerPraise(@FieldMap Map<String, Object> params);
    //上传文章图片
    @Multipart
    @POST("extends/upload_file/content_img/")
    Observable<BaseModel<UploadFileImgEntity>>uploadImg(@PartMap Map<String, Object> params, @Part MultipartBody.Part body);
    //发布文章
    @FormUrlEncoded
    @POST("bbs/publishArticle/")
    Observable<BaseModel>publishArticle(@FieldMap Map<String, Object> params);
    //文章详情评论列表
    @GET("/api/bbs/articleAnswers/{page}/")
    Observable<BaseModel<ArticleAnswersEntity>> articleAnswers(@Path ("page") int page, @QueryMap Map<String,Object> params);
    //添加文章评论
    @FormUrlEncoded
    @POST("bbs/addAnswer/")
    Observable<BaseModel>addAnswer(@FieldMap Map<String, Object> params);
    //发现页轮播
    @GET("bbs/CarouselArticle/")
    Observable<BaseModel<CarouselArticleEntity>> CarouselArticle();
    /**
     *我的模块
     */
    //查看个人信息
    @GET("bbs/oauthSetUserInfo/")
    Observable<BaseModel<OauthSetUserInfo>> oauthSetUserInfo();
    //省列表
    @GET("bbs/provinceList/")
    Observable<BaseModel<ProvinceListEntity>> provinceList();
    //城市列表
    @FormUrlEncoded
    @POST("bbs/cityList/")
    Observable<BaseModel<CityListEntity>> cityList(@FieldMap Map<String, Object> params);
    //修改个人信息
    @FormUrlEncoded
    @POST("bbs/oauthSetUserInfo/")
    Observable<BaseModel> updateUserInfo(@FieldMap Map<String, Object> params);
    //修改头像
    @Multipart
    @POST("extends/upload_file/avatar/")
    Observable<BaseModel<UploadFileImgEntity>> updateAvatar(@Part List<MultipartBody.Part> partLis);
    //修改密码
    @FormUrlEncoded
    @POST("bbs/oauthAlterPassword/")
    Observable<BaseModel> oauthAlterPassword(@FieldMap Map<String, Object> params);
    //退出登录
    @FormUrlEncoded
    @POST("bbs/bbsOutToken/")
    Observable<BaseModel> bbsOutToken(@FieldMap Map<String, Object> params);
    //我的发布文章列表
    @GET("/api/bbs/userArticleList/{page}/")
    Observable<BaseModel<UserArticleListEntity>> userArticleList(@Path ("page") int page);
    //获赞/点赞/粉丝数统计
    @GET("bbs/praiseCount/")
    Observable<BaseModel<PraiseCountEntity>> praiseCount();
    //我的评论/回复
    @GET("/api/bbs/answerList/{page}/")
    Observable<BaseModel<AnswerListEntity>> answerList(@Path ("page") int page);
    //检查更新 暂时不用
    @FormUrlEncoded
    @POST("bbs/versionUpdate/")
    Observable<BaseModel<VersionUpdateEntity>> versionUpdate(@FieldMap Map<String, Object> params);
    //检查更新 在用
    @Streaming
    @GET(BASE_GET_VERSION_URL)
    Observable<VersionUpdateNewEntity> getVersionUpdate();
    //下载apk 作废
    @Streaming
    @GET
    Observable<ResponseBody> downloadApk(@Url String fileUrl);
    //注销账号
    @FormUrlEncoded
    @POST("bbs/logOff/")
    Observable<BaseModel> logOff(@FieldMap Map<String, Object> params);

    @POST("shopping_login.htm")
    Observable<String> LoginByRx(@Field("username") String username, @Field("password") String password);

    @GET("/api.php")
    Observable<BaseModel> getWeather(@Query("api") String api, @Query("key") String key, @Query("city") String city);
}


