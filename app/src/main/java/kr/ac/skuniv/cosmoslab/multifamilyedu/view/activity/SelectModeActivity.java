package kr.ac.skuniv.cosmoslab.multifamilyedu.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;

/**
 * Created by chunso on 2019-01-06.
 */

public class SelectModeActivity extends AppCompatActivity {
    String mUserId;
    String mDay;
    WordInfoDto mWordInfoDto;

    Button startBtn;
    Button statusBtn;
    TextView textView;

    private UserController userController;
    private SharedPreferences achievementSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        achievementSharedPreferences = getSharedPreferences("achievement", MODE_PRIVATE);

        startBtn = findViewById(R.id.start_button);
        statusBtn = findViewById(R.id.day_status_button);
        textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra("user_id");
        mDay = intent.getStringExtra("day");
        mWordInfoDto = (WordInfoDto) intent.getSerializableExtra("word_info");
        userController = new UserController(getApplicationContext());

        String tmp = mDay.substring(3);
        textView.setText("Day " + tmp);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("tag", "help_select");
                intent.putExtra("user_id", mUserId);
                intent.putExtra("day", mDay);
                intent.putExtra("word_info", mWordInfoDto);
                startActivityForResult(intent, 1111);
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DayStatusActivity.class);
                intent.putExtra("tag", "help_select");
                intent.putExtra("user_id", mUserId);
                intent.putExtra("day", mDay);
                intent.putExtra("word_info", mWordInfoDto);
                startActivityForResult(intent, 3000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("day", mDay.substring(3));
        intent.putExtra("pass", checkPassDay(mWordInfoDto));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            mWordInfoDto = (WordInfoDto) data.getSerializableExtra("word_info");
            setAchievementSharedPreferences(mWordInfoDto.getWordlist(), mWordInfoDto.getWordpassinfo());
        } else if(requestCode == 1111 && resultCode == 1111) {
            mWordInfoDto = (WordInfoDto) data.getSerializableExtra("word_info");
            setAchievementSharedPreferences(mWordInfoDto.getWordlist(), mWordInfoDto.getWordpassinfo());

            Intent intent = new Intent();
            intent.putExtra("day", mDay.substring(3));
            intent.putExtra("pass", checkPassDay(mWordInfoDto));
            setResult(RESULT_OK, intent);
            finish();
        } else if(requestCode == 3000 && resultCode == 3000) {
           /* mWordInfoDto = (WordInfoDto) data.getSerializableExtra("word_info");
            setAchievementSharedPreferences(mWordInfoDto.getWordlist(), mWordInfoDto.getWordpassinfo());

            Intent intent = new Intent();
            intent.putExtra("day", mDay.substring(3));
            intent.putExtra("pass", checkPassDay(mWordInfoDto));
            setResult(RESULT_OK, intent);*/
            finish();
        }
    }

    private String checkPassDay(WordInfoDto wordInfoDto){
        int cnt = 0;

        List<String> wordList = wordInfoDto.getWordlist();
        Map<String, String> passList = wordInfoDto.getWordpassinfo();
        for(int i = 0 ; i<wordInfoDto.getWordlist().size() ; i++){
            if(passList.get(wordList.get(i)).equals("합격")) {
                cnt++;
            }
        }

        if(cnt > wordList.size() * 0.7) {
            return "합격";
        }
        else
            return "불합격";
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setAchievementSharedPreferences(List<String> wordList, Map<String, String> passInfo){
        int wordCnt = wordList.size();
        int passCnt = 0;
        for(int i = 0 ; i<wordCnt ; i++){
            if(passInfo.get(wordList.get(i)).equals("합격"))
                passCnt++;
        }

        SharedPreferences.Editor editor = achievementSharedPreferences.edit();
        editor.putString(mDay, (int)(((double)passCnt/(double)wordCnt)*100) + "%");
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(SelectModeActivity.this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_signout:
                userController.signoutUser();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
