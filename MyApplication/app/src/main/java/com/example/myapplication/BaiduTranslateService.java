package com.example.myapplication;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface BaiduTranslateService {
    @POST("translate")
    @FormUrlEncoded
    Call<RespondBean> translate(@Field("q") String q, @Field("from") String from, @Field("to") String to, @Field("appid") String appid, @Field("salt") String salt,
                                @Field("sign") String sign);
}
