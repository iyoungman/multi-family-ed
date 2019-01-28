package kr.ac.skuniv.cosmoslab.multifamilyedu.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WaveFileModel;

/**
 * Created by chunso on 2019-01-09.
 */

public class SplashActivity extends AppCompatActivity {
    private FileController fileController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileController = new FileController(getApplicationContext());
        fileController.createFilePath();

        if (fileSetting()) {
            Toast.makeText(getApplicationContext(), "정상적으로 실행됩니다.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "파일 다운로드 실패.. 인터넷을 연결하고 어플을 다시 시작해주세요.", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean fileSetting() {
        boolean check;
        List<String> waveFiles = new ArrayList<>();

        for (WaveFileModel waveFileModel : WaveFileModel.values()) {
            waveFiles.add(waveFileModel.toString());
        }

        try {
            for (String waveFile : waveFiles) {
                if (!fileController.confirmFile(waveFile + ".wav")) {
                    fileController.downloadFileByFileName(waveFile + ".wav");
                }
            }
            check = true;
        } catch (Exception e) {
            check = false;
        }

        return check;
    }
}
