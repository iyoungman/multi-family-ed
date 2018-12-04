package kr.ac.skuniv.cosmoslab.multifamilyedu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;

public class SigninActivity extends AppCompatActivity {
    private static final String TAG = "SigninActivity";
    private EditText idEditText;
    private EditText pwEditText;
    private Button signinBtn;
    private Button signupBtn;
    private CheckBox autoSigninCheckBox;

    private UserController userController;
    private UserModel signinModel;
    SharedPreferences auto;
    SharedPreferences.Editor editor;
    String autoId;
    String autoPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        signinBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        autoSigninCheckBox = (CheckBox) findViewById(R.id.autoSigninCheckBox);

        auto = getSharedPreferences("autoSignin", Activity.MODE_PRIVATE);
        editor = auto.edit();
        autoId = auto.getString("autoId", null);
        autoPw = auto.getString("autoPw", null);

        userController = new UserController(getApplicationContext());

        if (auto.getBoolean("autoLogin", true)) {
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
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
        if(autoSigninCheckBox.isChecked()){
            finish();
        } else {
            idEditText.setText("");
            pwEditText.setText("");
        }
    }
}
