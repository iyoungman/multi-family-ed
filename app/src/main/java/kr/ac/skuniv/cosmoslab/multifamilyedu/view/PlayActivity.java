package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.AnalysisWaveFormController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DecodeWaveFileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DrawWaveFormController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.PlayController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.PretreatmentController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WaveFormModel;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;

/**
 * Created by chunso on 2018-12-03.
 */

public class PlayActivity extends AppCompatActivity implements DialogResult.OnCompleteListener {
    private final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiFamily";
    private final int PASS_SCORE = 80;

    TextView textView, highestScoreTV, preScoreTV;
    ImageView imageView;
    Button playBtn, recordBtn, recordPlayBtn, submitBtn;

    String mTag, mWord, mRecordPath, mOriginalPath, mPCMPath, mImagePath;

    private String mUserId;
    private String mDay;
    int mHighestScore, mPreScore, mIndex, mFinalScore = 0;

    List<String> mPassWords = new ArrayList<>();
    List<String> mFailWords = new ArrayList<>();
    boolean isRecording = false;
    boolean isPlaying = false;

    boolean passAllWord = false;
    public AudioRecord mAudioRecord = null;
    public AudioTrack mAudioTrack = null;
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = ENCODING_PCM_16BIT;

    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    DecodeWaveFileController decodeWaveFileController = new DecodeWaveFileController();
    SharedPreferences sharedPreferences;
    UserController userController;
    FileController fileController;
    DrawWaveFormController originalDisplayView;
    DrawWaveFormController recordDisplayView;
    WordInfoDto mWordInfoDto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        sharedPreferences = getSharedPreferences("wordScore", MODE_PRIVATE);
        userController = new UserController(getApplicationContext());
        fileController = new FileController(getApplicationContext());

        highestScoreTV = findViewById(R.id.highestScoreTV);
        preScoreTV = findViewById(R.id.preScore);
        textView = findViewById(R.id.wordTV);
        imageView = findViewById(R.id.displayView);

        Intent intent = getIntent();
        mTag = intent.getStringExtra("tag");
        mUserId = intent.getStringExtra("user_id");
        mDay = intent.getStringExtra("day");
        mWordInfoDto = (WordInfoDto) intent.getSerializableExtra("word_info");

        mFailWords = findFailWords();
        if(mTag.equals("status"))
            mWord = intent.getStringExtra("word");
        else
            mWord = mFailWords.get(mIndex);

        setEnvironment(mWord);

        imageView.setImageBitmap(onDrawOriginalWaveForm());

        playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(v, mOriginalPath);
            }
        });

        recordBtn = findViewById(R.id.recordBtn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, RecordActivity.class);
                intent.putExtra("word", mWord);
                startActivityForResult(intent, 1111);
            }
        });

        recordPlayBtn = findViewById(R.id.recordPlayBtn);
        recordPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(v, mRecordPath);
            }
        });

        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File recodeFile = new File(mRecordPath);
                if (!recodeFile.exists()) {
                    Toast.makeText(getApplicationContext(), "녹음부터 하시기바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogFragment();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        mWordInfoDto.setWordpassinfo(syncData(mPassWords));

        Intent intent = new Intent();
        intent.putExtra("word_info", mWordInfoDto);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111 && resultCode == 2222) {
            if(onDraw() == null){
                imageView.setImageBitmap(onDrawOriginalWaveForm());
            } else {
                imageView.setImageBitmap(onDraw());

            }
        }
    }

    @Override
    public void onReplay(int score) {
        mPreScore = mHighestScore;
        saveScore(score);
        if (mPreScore != mHighestScore)
            highestScoreTV.setText(String.valueOf(mHighestScore));
        mPreScore = score;
        preScoreTV.setText(String.valueOf(mPreScore));
    }

    @Override
    public void onNext(String complete, int score) {
        saveScore(score);
        if(score < PASS_SCORE)
            mFailWords.add(mFailWords.get(mIndex));

        mIndex++;
        if(mIndex < mFailWords.size()) {
            mWord = mFailWords.get(mIndex);
            setEnvironment(mWord);
        }
        else
            Toast.makeText(getApplicationContext(), "모든 단어를 합격하셨습니다.", Toast.LENGTH_LONG).show();

        imageView.setImageBitmap(onDrawOriginalWaveForm());
    }

    @Override
    public void startStatusActivity(int score) {
        saveScore(score);
        mWordInfoDto.setWordpassinfo(syncData(mPassWords));

        if(mTag.equals("status")) {
            Intent intent = new Intent();
            intent.putExtra("word_info", mWordInfoDto);
            setResult(RESULT_OK, intent);
        }else if(mTag.equals("select")){
            Intent intent = new Intent(this, DayStatusActivity.class);
            intent.putExtra("tag", "play");
            intent.putExtra("user_id", mUserId);
            intent.putExtra("day", mDay);
            intent.putExtra("word_info", mWordInfoDto);
            startActivity(intent);
        }
        finish();
    }

    private List<String> findFailWords() {
        List<String> wordList = mWordInfoDto.getWordlist();
        Map<String, String> map = mWordInfoDto.getWordpassinfo();
        List<String> failWords = new ArrayList<>();

        for(int i = 0 ; i< wordList.size() ; i++){
            if(map.get(wordList.get(i)).equals("불합격")) {
                failWords.add(wordList.get(i));
            }
        }
        if( failWords.size() == 0)
            failWords = wordList;

        Random random = new Random();
        int leng = failWords.size();
        int[] indexArr = new int[leng];
        for(int i = 0 ; i<leng ; i++){
            indexArr[i] = random.nextInt(leng);
            for(int j = 0; j<i ; j++) {
                if(indexArr[i] == indexArr[j]) {
                    i--;
                }
            }
        }

        List<String> renewalFailWords = new ArrayList<>();
        for(int i = 0; i< leng ; i++)
            renewalFailWords.add(failWords.get(indexArr[i]));


        return renewalFailWords;
    }

    public void onPlay(View view, final String path) {
        if (isPlaying) {
            isPlaying = false;
            playBtn.setText("재생");
        } else {
            isPlaying = true;
            playBtn.setText("멈춤");

            if (mAudioTrack == null) {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] writeData = new byte[mBufferSize];
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }

                    DataInputStream dis = new DataInputStream(fis);
                    mAudioTrack.play();  // write 하기 전에 play 를 먼저 수행해 주어야 함

                    while (isPlaying) {
                        try {
                            int ret = dis.read(writeData, 0, mBufferSize);
                            if (ret <= 0) {
                                (PlayActivity.this).runOnUiThread(new Runnable() { // UI 컨트롤을 위해
                                    @Override
                                    public void run() {
                                        isPlaying = false;
                                        playBtn.setText("재생");
                                    }
                                });
                                break;
                            }
                            mAudioTrack.write(writeData, 0, ret); // AudioTrack 에 write 를 하면 스피커로 송출됨
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mAudioTrack.stop();
                    mAudioTrack.release();
                    mAudioTrack = null;

                    try {
                        dis.close();
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public Bitmap onDrawOriginalWaveForm() {
        PretreatmentController pretreatmentController = new PretreatmentController(getApplicationContext());
        pretreatmentController.setWaveFile(mOriginalPath);
        int[] originalArray = pretreatmentController.getMOriginalDrawModel();
        int maximumValue = pretreatmentController.getWaveMaximumValue(originalArray);

        int bitmapX = originalArray.length;
        int bitmapY = maximumValue;

        Bitmap waveForm = Bitmap.createBitmap(bitmapX, bitmapY, Bitmap.Config.ARGB_8888);
        Canvas originalCanvas = new Canvas(waveForm);

        Paint originalWaveform = new Paint();
        originalWaveform.setColor(Color.BLUE);
        originalWaveform.setAlpha(40);

        for (int i = 0; i < originalArray.length; i++) {
            originalCanvas.drawLine(i, bitmapY - originalArray[i], i, bitmapY, originalWaveform);
        }

        return waveForm;
    }

    public Bitmap onDraw() {
        PretreatmentController pretreatmentController = new PretreatmentController(getApplicationContext());
        if (!pretreatmentController.run(mOriginalPath, mRecordPath)) {
            Toast.makeText(getApplicationContext(), "녹음이 잘못되었습니다. 녹음을 다시 해주십시오...", Toast.LENGTH_LONG).show();
            return null;
        }
        int[] originalArray = pretreatmentController.getMOriginalDrawModel();
        int[] recordArray = pretreatmentController.getMRecordDrawModel();
        int maximumValue = pretreatmentController.getMaximumValue();

        AnalysisWaveFormController analysisWaveform = new AnalysisWaveFormController(getApplicationContext(), pretreatmentController.getMOriginalModel(), pretreatmentController.getMRecordModel());
        mFinalScore = analysisWaveform.getFinalScore();

        if (mFinalScore == 0) {
            Toast.makeText(getApplicationContext(), "점수를 계산하는데 문제가 발생되었습니다. 녹음을 다시 해주십시오...", Toast.LENGTH_SHORT).show();
            return null;
        }

        WaveFormModel originalModel = analysisWaveform.getMOriginalModel();
        WaveFormModel recordModel = analysisWaveform.getMRecodeModel();

        int bitmapX = originalArray.length > recordArray.length ? originalArray.length : recordArray.length;
        int bitmapY = maximumValue;

        Bitmap waveForm = Bitmap.createBitmap(bitmapX, bitmapY, Bitmap.Config.ARGB_8888);
        Canvas originalCanvas = new Canvas(waveForm);
        Canvas recodeCanvas = new Canvas(waveForm);

        Paint originalWaveform = new Paint();
        originalWaveform.setColor(Color.BLUE);
        originalWaveform.setAlpha(40);

        Paint recodeWaveform = new Paint();
        recodeWaveform.setColor(Color.RED);
        recodeWaveform.setAlpha(60);

        for (int i = 0; i < originalArray.length; i++) {
            originalCanvas.drawLine(i, bitmapY - originalArray[i], i, bitmapY, originalWaveform);
        }
        for (int i = 0; i < recordArray.length; i++) {
            recodeCanvas.drawLine(i, bitmapY - recordArray[i], i, bitmapY, recodeWaveform);
        }
        return waveForm;
    }

    public void showDialogFragment() {
        Bundle args = new Bundle();
        args.putString("word", mWord);
        args.putInt("finalScore", mFinalScore);
        DialogFragment newFragment = new DialogResult();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void setEnvironment(String word) {
        if (!fileController.confirmFile(word + ".wav")) {
            Toast.makeText(getApplicationContext(), "파일 없으므로 다운로드", Toast.LENGTH_SHORT).show();
            fileController.downloadFileByFileName(word + ".wav");
        }

        mOriginalPath = FILE_PATH + "/ORIGINAL/" + mWord + ".wav";
        mRecordPath = FILE_PATH + "/RECORD/" + mWord + ".wav";
        mPCMPath = FILE_PATH + "/RECORD/" + mWord + ".pcm";
        mImagePath = FILE_PATH + "/IMAGE/" + mWord + ".png";
        textView.setText(mWord);
        preScoreTV.setText("0");

        mHighestScore = sharedPreferences.getInt(mWord, 0);
        highestScoreTV.setText(String.valueOf(mHighestScore));
    }

    /*public String changeWord() {
        Random random = new Random();
        WaveFileModel[] waveFileModels = WaveFileModel.values();
        boolean findWord = false;
        int rand = 0;
        while (!findWord) {
            rand = random.nextInt(173);
            if (sharedPreferences.getInt(waveFileModels[rand].toString(), 0) == 0)
                findWord = true;
        }
        System.out.println("변경된 단어" + waveFileModels[rand].toString());
        return waveFileModels[rand].toString();
    }*/

    public void saveScore(int score) {
        if (mHighestScore != 0 && score > mHighestScore) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(mWord, score);
            editor.commit();

            mHighestScore = score;
            System.out.println("값이 높아서 저장됨.");
        } else if (mHighestScore == 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(mWord, score);
            editor.commit();

            mHighestScore = score;
            System.out.println("값이 없어서 저장됨");
        }

        if(mHighestScore > PASS_SCORE && mWordInfoDto.getWordpassinfo().get(mWord).equals("불합격")){
            PlayController playController = new PlayController(getApplicationContext());
            mPassWords.add(playController.setWordPassInfo(mUserId, mWord));
        }

        System.out.println("점수: " + score);
        System.out.println(sharedPreferences.getInt(mWord, 0));
    }

    public Map<String, String> syncData(List<String> passWord){
        Map<String, String> map = mWordInfoDto.getWordpassinfo();
        for(int i = 0 ; i< passWord.size() ; i++)
            map.put(passWord.get(i), "합격");
        return map;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                userController.signoutUser();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
