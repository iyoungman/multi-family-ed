package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.adapter.DayPageAdapter;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WordPassModel;

/**
 * Created by chunso on 2019-01-02.
 */

public class DayActivity extends AppCompatActivity {
    private UserController userController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_select);
        Intent intent = getIntent();
        UserModel userModel = (UserModel)intent.getSerializableExtra("login_model");
        userController = new UserController(getApplicationContext());
        ArrayList<WordPassModel> wordPassModels = new ArrayList<>();
        if(userModel != null){
            int userLevel = Integer.parseInt(userModel.getLevel());
            for(int i =1 ;i<= userLevel ; i++){
                WordPassModel listViewItem = WordPassModel.builder()
                        .day("day" + i)
                        .pass(true)
                        .build();
                wordPassModels.add(listViewItem);
            }
            for(int i =userLevel+1 ;i<= 17 ; i++){
                WordPassModel listViewItem = WordPassModel.builder()
                        .day("day" + i)
                        .pass(false)
                        .build();
                wordPassModels.add(listViewItem);
            }
        }else {
            Toast.makeText(getApplicationContext(), "인터넷 연결이 안되었습니다.", Toast.LENGTH_LONG).show();
        }

        ListView listView = findViewById(R.id.listView);
        DayPageAdapter adapter = new DayPageAdapter(wordPassModels, userModel.getId());

        listView.setAdapter(adapter);
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
