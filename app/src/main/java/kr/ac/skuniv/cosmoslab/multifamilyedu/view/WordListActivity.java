package kr.ac.skuniv.cosmoslab.multifamilyedu.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.controller.WordInfoController;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.dto.WordInfoDto;

public class WordListActivity extends AppCompatActivity {
    private ArrayList<String> items;// 빈 데이터 리스트 생성.
    private ArrayAdapter adapter; // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
    private ListView listview;// listview 생성 및 adapter 지정.
    private WordInfoController wordInfoController;
    List<String> words = new ArrayList<>();
    Map<String,Boolean> wordsPassinfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.listView) ;
        listview.setAdapter(adapter) ;

        wordInfoController = new WordInfoController(getApplicationContext());
        WordInfoDto wordInfoDto = wordInfoController.getWordListByUserid("1","testid");

        if(wordInfoDto != null) {
            words = wordInfoDto.getWordlist();
            wordsPassinfo = wordInfoDto.getWordpassinfo();
            for(String word : words) {
                items.add(word + wordsPassinfo.get(word));
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "리스트 생성 실패", Toast.LENGTH_LONG).show();
        }
    }

}
