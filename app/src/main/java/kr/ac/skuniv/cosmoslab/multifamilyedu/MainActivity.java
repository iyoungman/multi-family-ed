package kr.ac.skuniv.cosmoslab.multifamilyedu;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.AnalysisWaveFormController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.SettingForAnalysisController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WaveFormModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.view.PlayActivity;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class MainActivity extends AppCompatActivity {
    private final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiFamily";
    private final String ORIGINAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OriginalWav/구름.wav";
    private final String RECODE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecodeWav/구름.wav";

    public static boolean isRecording = false;
    public static boolean isPlaying = false;

    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    public AudioRecord mAudioRecord = null;
    public AudioTrack mAudioTrack = null;
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    private UserModel userModel;
    private FileController fileController;
   // private UserController userController = new UserController(getApplicationContext());
    private filename fn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionAudio = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED || permissionAudio == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 2);
        } 

//        Toolbar toolbar = (Toolbar)f
        userController = new UserController();
        fileController = new FileController();
        fileController.createFilePath();



        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingForAnalysisController settingForAnalysisController = new SettingForAnalysisController(ORIGINAL_PATH, RECODE_PATH);
                settingForAnalysisController.controller();
                WaveFormModel originalModel = settingForAnalysisController.getmOriginalData();
                WaveFormModel recodeModel = settingForAnalysisController.getmRecodeData();

                AnalysisWaveFormController analysisWaveFormController = new AnalysisWaveFormController(originalModel, recodeModel);
                int finalScore = analysisWaveFormController.getFinalScore();

                System.out.println(finalScore);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //랜덤 추출 -> enum
                String testFileName = fn.haha.toString();
                testFileName = testFileName + ".wav";

                //핸드폰 저장소에 없는 파일이면 다운로드
                /*if (!fileController.confirmFile("1","test.wav")) {
                    Toast.makeText(context.getApplicationContext(), "파일 다운로드 실패", Toast.LENGTH_LONG).show();*/
                    fileController.downloadFileByFileName(testFileName);
                //}

                //Play
            }
        });

        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            //로그인 test
            @Override
            public void onClick(View v) {
                UserController userController = new UserController(getApplicationContext());
                userController.signoutUser();
                finish();
            }
        });
        button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            //로그인 test
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("wordScore" ,MODE_PRIVATE);
                Toast.makeText(getApplicationContext(), "확인: "+sp.getInt("소", 0), Toast.LENGTH_SHORT).show();
            }
        });
        button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            //로그인 test
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재화면의 제어권자
                        PlayActivity.class); // 다음넘어갈 화면

                // intent 객체에 데이터를 실어서 보내기
                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다
                intent.putExtra("name", "박춘소");
                intent.putExtra("word", "가방");
                intent.putExtra("highScore", 78);

                startActivity(intent);
            }
        });

    }


    enum filename {
        haha,test
    }

}
