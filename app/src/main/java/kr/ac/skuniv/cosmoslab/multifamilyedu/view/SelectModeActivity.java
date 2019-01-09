package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DayStatusController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;

/**
 * Created by chunso on 2019-01-06.
 */

public class SelectModeActivity extends AppCompatActivity {
    String userId;
    String day;
    WordInfoDto wordInfoDto;

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
        userId = intent.getStringExtra("user_id");
        day = intent.getStringExtra("day");
        wordInfoDto = (WordInfoDto) intent.getSerializableExtra("word_info");

        textView.setText(day);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("tag", "select");
                intent.putExtra("user_id", userId);
                intent.putExtra("day", day);
                intent.putExtra("word_info", wordInfoDto);
                startActivity(intent);
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DayStatusActivity.class);
                intent.putExtra("tag", "select");
                intent.putExtra("user_id", userId);
                intent.putExtra("day", day);
                intent.putExtra("word_info", wordInfoDto);
                startActivityForResult(intent, 3000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("word_info", wordInfoDto);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
            wordInfoDto = (WordInfoDto) data.getSerializableExtra("word_info");
    }
}
