package kr.ac.skuniv.cosmoslab.multifamilyedu.network;


import java.util.List;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.SignupDto;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * MultiFamilyEdu_Android
 * Class: NetRetrofitInterface
 * Created by youngjun on 2018-11-27.
 * Description:
 * @Query : 서버에 보낼값  -> localhost?email=eml&password=pwd
 * @Path : 서버에 보낼값  -> localhost/{eml}/{pwd}
 * Call <Object> : 서버로 부터 받을 자료형
 */
public interface NetRetrofitInterface {

    @FormUrlEncoded
    @POST("users/signin")
    Call<UserModel> signin(@Field("userid") String userid, @Field("pw") String pw);

    @POST("users/signup")
    Call<UserModel> singup(@Body SignupDto signupDto);

    @GET("downloads/{level}")
    Call<ResponseBody> downloadFileByLevel(@Path("level") String level);

    @GET("downloads/level/{level}/filename/{filename}")
    Call<ResponseBody> downloadFileByFileName(@Path("level") String level, @Path("filename") String fileName);

    @GET("downloads")
    Call<List<ResponseBody>> downloadFileLIst();


}
