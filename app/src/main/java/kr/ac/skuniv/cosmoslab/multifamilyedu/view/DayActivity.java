package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.adapter.DayPageAdapter;
import kr.ac.skuniv.cosmoslab.multifamilyedu.adapter.WordPageAdapter;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WordPassModel;
import lombok.Getter;

/**
 * Created by chunso on 2019-01-02.
 */

public class DayActivity extends AppCompatActivity {
    final int PASS_NEXT_DAY_COUNT = 7;

    private String mDay;
    private String mUserId;
    private String mUserPw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_select);
        Intent intent = getIntent();
        UserModel userModel = (UserModel)intent.getSerializableExtra("login_model");
        ArrayList<WordPassModel> wordPassModels = new ArrayList<>();

        if(userModel != null){
            mUserId = userModel.getId();
            mUserPw = userModel.getPw();
            mDay = userModel.getLevel();
            wordPassModels = setEnviroment(Integer.parseInt(mDay));
        }else {
            Toast.makeText(getApplicationContext(), "인터넷 연결이 안되었습니다.", Toast.LENGTH_LONG).show();
        }

        ListView listView = findViewById(R.id.listView);
        DayPageAdapter adapter = new DayPageAdapter(wordPassModels, userModel.getId(), getApplicationContext());

        listView.setAdapter(adapter);
    }

/*    @Override
    protected void onStart() {
        super.onStart();
        UserController userController = new UserController(getApplicationContext());
        userController.signinUser(mUserId, mUserPw);
        UserModel userModel = userController.getUserModel();
        String day;
        if(userModel != null) {
            day = userModel.getLevel();
            ArrayList<WordPassModel> wordPassModels = setEnviroment(Integer.parseInt(day));
            ListView listView = findViewById(R.id.listView);
            DayPageAdapter adapter = new DayPageAdapter(wordPassModels, userModel.getId(), getApplicationContext());

            listView.setAdapter(adapter);
        }else{
            Toast.makeText(getApplicationContext(), "유저 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG);
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            int nowDay = Integer.parseInt(mDay);
            int reciveDay = Integer.parseInt(data.getStringExtra("day"));
            if(reciveDay == nowDay && data.getStringExtra("pass").equals("합격")){
                /**
                 * 서버와 통신하는 부분
                 */
                ArrayList<WordPassModel> wordPassModels = setEnviroment(++reciveDay);
                DayPageAdapter adapter = new DayPageAdapter(wordPassModels, mUserId, getApplicationContext());
                ListView listview = findViewById(R.id.listView);
                listview.setAdapter(adapter);
                mDay = String.valueOf(reciveDay);
            }
        }
    }

    public ArrayList<WordPassModel> setEnviroment(int userDay){
        ArrayList<WordPassModel> wordPassModels = new ArrayList<>();

        for(int i =1 ;i<= userDay ; i++){
            WordPassModel listViewItem = WordPassModel.builder()
                    .day("day" + i)
                    .pass(true)
                    .build();
            wordPassModels.add(listViewItem);
        }
        for(int i =userDay+1 ;i<= 20 ; i++){
            WordPassModel listViewItem = WordPassModel.builder()
                    .day("day" + i)
                    .pass(false)
                    .build();
            wordPassModels.add(listViewItem);
        }
        return wordPassModels;
    }
}
