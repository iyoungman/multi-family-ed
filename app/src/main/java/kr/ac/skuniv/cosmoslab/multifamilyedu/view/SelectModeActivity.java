package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DayStatusController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        startBtn = findViewById(R.id.start_button);
        statusBtn = findViewById(R.id.day_status_button);
        textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra("user_id");
        mDay = intent.getStringExtra("day");
        mWordInfoDto = (WordInfoDto) intent.getSerializableExtra("word_info");

        textView.setText(mDay);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("tag", "select");
                intent.putExtra("user_id", mUserId);
                intent.putExtra("day", mDay);
                intent.putExtra("word_info", mWordInfoDto);
                startActivity(intent);
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DayStatusActivity.class);
                intent.putExtra("tag", "select");
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
        if(resultCode == RESULT_OK)
            mWordInfoDto = (WordInfoDto) data.getSerializableExtra("word_info");
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
}
