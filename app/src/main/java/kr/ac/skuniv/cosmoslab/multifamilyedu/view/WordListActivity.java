package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.UserController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.WordInfoController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;

public class WordListActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private ListView listview;
    private WordInfoController wordInfoController;
    private UserController userController;
    List<String> words = new ArrayList<>();
    Map<String, Boolean> wordsPassinfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");
        String level = intent.getStringExtra("day");
        Toast.makeText(getApplicationContext(), userId + level, Toast.LENGTH_LONG).show();

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        wordInfoController = new WordInfoController(getApplicationContext());
        userController = new UserController(getApplicationContext());
        WordInfoDto wordInfoDto = wordInfoController.getWordListByUserid(level, userId);

        //리스트 생성
        if (wordInfoDto != null) {
            words = wordInfoDto.getWordlist();
            wordsPassinfo = wordInfoDto.getWordpassinfo();
            for (String word : words) {
                items.add(word + wordsPassinfo.get(word));
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "리스트 생성 실패", Toast.LENGTH_LONG).show();
        }

        //해당 단어(Play Activity)로 이동
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class); // 다음넘어갈 화면
                String word = "";
                // intent 객체에 데이터를 실어서 보내기
                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다
                if (items.get(position).contains("true")) {
                    word = items.get(position).replace("true", "");
                } else if (items.get(position).contains("false")) {
                    word = items.get(position).replace("false", "");
                }
                intent.putExtra("word", word);
                startActivity(intent);
            }
        });
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
