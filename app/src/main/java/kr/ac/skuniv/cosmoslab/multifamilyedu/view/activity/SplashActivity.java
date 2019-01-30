package kr.ac.skuniv.cosmoslab.multifamilyedu.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WaveFileModel;

/**
 * Created by chunso on 2019-01-09.
 */

public class SplashActivity extends AppCompatActivity {
    private FileController fileController;
    private ProgressBar progressBar;
    private TextView textView;
    private Timer timer;
    private int percentage;
    private int downCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");

        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionAudio = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED || permissionAudio == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 2);
        }

        fileController = new FileController(getApplicationContext());
        fileController.createFilePath();

        try {
            fileSetting();
            Toast.makeText(getApplicationContext(), "정상적으로 실행됩니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 다운로드 실패.. 인터넷을 연결하고 어플을 다시 시작해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void fileSetting() {
        final List<String> waveFiles = new ArrayList<>();

        for (WaveFileModel waveFileModel : WaveFileModel.values()) {
            waveFiles.add(waveFileModel.toString());
        }

        downCount = waveFiles.size();
        final long period = 100;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (String waveFile : waveFiles) {
                    if (!fileController.confirmFile(waveFile + ".wav")) {
                        fileController.downloadFileByFileName(waveFile + ".wav");
                    }
                    downCount = downCount - 1;
                    percentage = ((waveFiles.size() - downCount) * 100 / waveFiles.size());
                    progressBar.setProgress(percentage);

                    if (downCount > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(String.valueOf(percentage) + "%");
                            }
                        });
                    } else {
                        timer.cancel();
                        Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 0, period);
    }

}