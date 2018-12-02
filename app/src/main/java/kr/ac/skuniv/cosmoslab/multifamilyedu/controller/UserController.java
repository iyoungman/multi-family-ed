package kr.ac.skuniv.cosmoslab.multifamilyedu.controller;

import android.content.Context;
import android.util.Log;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.SignupDto;
import kr.ac.skuniv.cosmoslab.multifamilyedu.network.NetRetrofit;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * MultiFamilyEdu_Android
 * Class: UserController
 * Created by youngjun on 2018-11-27.
 * <p>
 * Description:
 */
@Getter
@Setter
public class UserController {
    private static final String TAG = "UserController";
    private UserModel userModel;
    Context context;

    public UserController() {

    }

    public UserController(UserModel userModel, Context context) {
        this.userModel = userModel;
        this.context = context;
    }

    public UserModel getUserModel() {
        return this.userModel;
    }

    //로고인 메소드
    public void signinUser(String userid, String pw) {
        Call<UserModel> res = NetRetrofit.getInstance().getNetRetrofitInterface().signin(userid, pw);
        res.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.body() != null) {
                    UserModel user = response.body();
                    userModel = user;
                    Log.i(TAG, user.getName());
                    Log.i(TAG, user.getId());
                } else {
                    Log.i(TAG, response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    //회원가입 메소드
    public void signupUser(SignupDto signupDto) {
        Call<UserModel> res = NetRetrofit.getInstance().getNetRetrofitInterface().singup(signupDto);
        res.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                if (response.body() != null) {
                    UserModel user = response.body();
                    userModel = user;
                    Log.i(TAG, user.getName());
                    Log.i(TAG, user.getId());
                } else {
                    Log.i(TAG, response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {

            }
        });
    }


}
