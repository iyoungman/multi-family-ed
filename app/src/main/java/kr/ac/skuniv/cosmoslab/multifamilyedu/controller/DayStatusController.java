package kr.ac.skuniv.cosmoslab.multifamilyedu.controller;

import android.content.Context;
import android.widget.Toast;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;
import kr.ac.skuniv.cosmoslab.multifamilyedu.network.NetRetrofit;
import retrofit2.Call;

/**
 * MultiFamilyEdu_Android
 * Class: DayStatusController
 * Created by hsl95 on 2019-01-01.
 * <p>
 * Description:
 */
public class DayStatusController {
    private WordInfoDto wordInfoDto;
    private Context context;

    public DayStatusController(Context context) {
        this.context = context;
    }


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
