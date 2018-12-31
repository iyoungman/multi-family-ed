package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.FileController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;

public class SigninActivity extends AppCompatActivity {
    private static final String TAG = "SigninActivity";
    private EditText idEditText;
    private EditText pwEditText;
    private Button signinBtn;
    private Button signupBtn;
    private CheckBox autoSigninCheckBox;

    private UserController userController;
    private FileController fileController;
    SharedPreferences auto;
    SharedPreferences.Editor editor;
    String autoId;
    String autoPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionAudio = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED || permissionAudio == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 2);
        }

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        signinBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        autoSigninCheckBox = (CheckBox) findViewById(R.id.autoSigninCheckBox);
        getSupportActionBar().setTitle(R.string.signin_name);

        auto = getSharedPreferences("autoSignin", Activity.MODE_PRIVATE);
        editor = auto.edit();
        autoId = auto.getString("autoId", null);
        autoPw = auto.getString("autoPw", null);

        userController = new UserController(getApplicationContext());
        fileController = new FileController(getApplicationContext());

        fileController.createFilePath();

        if (auto.getBoolean("autoLogin", true)) {
            Intent intent = new Intent(SigninActivity.this, PlayActivity.class);
            startActivity(intent);
            finish();
        }

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Boolean autoCheck = false;
                    if (autoSigninCheckBox.isChecked()) {
                        autoCheck = true;
                    }
                    userController.signinUser(idEditText.getText().toString(), pwEditText.getText().toString(), autoCheck);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoSigninCheckBox.isChecked()) {
            finish();
        } else {
            idEditText.setText("");
            pwEditText.setText("");
        }
    }
}
