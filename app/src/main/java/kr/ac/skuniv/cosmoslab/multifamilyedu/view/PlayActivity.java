package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DecodeWaveFileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.DrawWaveFormController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.PlayController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;

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
    int mHighestScore, mPreScore, mIndex = 0;

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
//        imageView = findViewById(R.id.waveformImg);

        Intent intent = getIntent();
        mTag = intent.getStringExtra("tag");
        mUserId = intent.getStringExtra("user_id");
        mDay = intent.getStringExtra("day");
        mWordInfoDto = (WordInfoDto) intent.getSerializableExtra("word_info");

        if(mTag.equals("status"))
            mWord = intent.getStringExtra("word");
        else {
            mFailWords = findFailWords();
            mWord = mFailWords.get(mIndex);
            mIndex++;
        }
        setEnvironment(mWord);

        //원본 그래프
        LinearLayout originalDisplayLayout = (LinearLayout) findViewById(R.id.originalDisplayView);
        originalDisplayView = new DrawWaveFormController(getApplicationContext(), true);
        originalDisplayLayout.addView(originalDisplayView);
        originalDisplayView.setOriginalWaveDisplay(mOriginalPath);

        //실시간 그래프
        LinearLayout recordDisplayLayout = (LinearLayout) findViewById(R.id.recordDisplayView);
        recordDisplayView = new DrawWaveFormController(getApplicationContext(), false);
        recordDisplayLayout.addView(recordDisplayView);

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
                onRecord(v);
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
                    recordDisplayView.clearWaveData();
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

        if(mIndex < mFailWords.size()) {
            mWord = mFailWords.get(mIndex);
            setEnvironment(mWord);
        }
        else
            Toast.makeText(getApplicationContext(), "모든 단어를 합격하셨습니다.", Toast.LENGTH_LONG).show();
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

    public void onRecord(View view) {
        if (isRecording) {
            isRecording = false;
            Toast.makeText(this, "녹음이 중지됩니다...", Toast.LENGTH_SHORT).show();
            recordBtn.setText("녹음");
            decodeWaveFileController.pcmToWav(mPCMPath, mRecordPath);
        } else {
            isRecording = true;
            recordBtn.setText("녹음중");
            //recordDisplayView.clearWaveData();
            Toast.makeText(this, "녹음이 시작됩니다...", Toast.LENGTH_SHORT).show();

            if (mAudioRecord == null) {
                mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                mAudioRecord.startRecording();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] readData = new byte[mBufferSize];
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mPCMPath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    while (isRecording) {
                        int ret = mAudioRecord.read(readData, 0, mBufferSize);  //  AudioRecord의 read 함수를 통해 pcm data 를 읽어옴
                        //int bufferSize = ret;
                        recordDisplayView.addWaveData(readData, 0, ret);///////////////////////////////
                        //addValue(len);///////////////////////

                        //저장
                        try {
                            fos.write(readData, 0, mBufferSize);    //  읽어온 readData 를 파일에 write 함
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;

                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
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

    public void showDialogFragment() {
        Bundle args = new Bundle();
        args.putString("word", mWord);
        DialogFragment newFragment = new DialogResult();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void setEnvironment(String word) {
        mOriginalPath = FILE_PATH + "/ORIGINAL/" + mWord + ".wav";
        mRecordPath = FILE_PATH + "/RECORD/" + mWord + ".wav";
        mPCMPath = FILE_PATH + "/RECORD/" + mWord + ".pcm";
        mImagePath = FILE_PATH + "/IMAGE/" + mWord + ".png";
        textView.setText(mWord);
        preScoreTV.setText("0");

        mHighestScore = sharedPreferences.getInt(mWord, 0);
        highestScoreTV.setText(String.valueOf(mHighestScore));

        /*File imgFile = new File(mImagePath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }*/
    }

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
