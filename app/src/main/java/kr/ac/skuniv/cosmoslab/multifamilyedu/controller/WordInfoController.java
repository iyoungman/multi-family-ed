package kr.ac.skuniv.cosmoslab.multifamilyedu.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.network.NetRetrofit;
import kr.ac.skuniv.cosmoslab.multifamilyedu.network.NetRetrofitInterface;
import kr.ac.skuniv.cosmoslab.multifamilyedu.view.WordListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MultiFamilyEdu_Android
 * Class: WordInfoController
 * Created by hsl95 on 2019-01-01.
 * <p>
 * Description:
 */
public class WordInfoController {
    private static final String TAG = "WordInfoController";
    private UserModel userModel;
    private WordInfoDto wordInfoDto;
    Context context;

    public WordInfoController(Context context) {
        this.context = context;
    }

    public WordInfoController(UserModel userModel, Context context) {
        this.userModel = userModel;
        this.context = context;
    }

    public WordInfoDto getWordInfoDto() {
        return this.wordInfoDto;
    }

    /*public void getWordListByUserid(String level, String userid) {
        Call<WordInfoDto> res = NetRetrofit.getInstance().getNetRetrofitInterface().getWordListByLevelAndUserid(level, userid);
        Log.i(TAG, "start");

        res.enqueue(new Callback<WordInfoDto>() {
            @Override
            public void onResponse(Call<WordInfoDto> call, Response<WordInfoDto> response) {
                if (response.isSuccessful()) {
                    wordInfoDto = response.body();
                    *//*Intent intent = new Intent(context, WordListActivity.class);
                    intent.putExtra("loginmodel", response.body());
                    context.startActivity(intent);*//*
                } else {
                    Toast.makeText(context.getApplicationContext(), "단어 리스트 받기 실패", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<WordInfoDto> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), "인터넷 연결 실패", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public WordInfoDto getWordListByUserid(final String level, final String userid) {
        final Call<WordInfoDto> res = NetRetrofit.getInstance().getNetRetrofitInterface().getWordListByLevelAndUserid(level, userid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wordInfoDto = res.execute().body();
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "리스트 정보 받기 실패", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wordInfoDto;
    }
}
