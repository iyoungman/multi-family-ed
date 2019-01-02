package kr.ac.skuniv.cosmoslab.multifamilyedu.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

import kr.ac.skuniv.cosmoslab.multifamilyedu.R;
import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.WordPassModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.view.WordListActivity;

/**
 * Created by chunso on 2019-01-02.
 */

public class DayPageAdapter extends BaseAdapter {
    private final String userId;
    private ArrayList<WordPassModel> wordPassModels = new ArrayList<>();
    private ArrayList<Boolean> buttonStatus = new ArrayList<Boolean>();
    private boolean[] enableBtn;
    String TAG = "TAG";


    public DayPageAdapter(ArrayList<WordPassModel> wordPassModels, String userId) {
        this.wordPassModels = wordPassModels;
        this.userId = userId;
        enableBtn = new boolean[wordPassModels.size()];
        for (int i = 0; i < wordPassModels.size(); i++) {
            buttonStatus.add(false);
            enableBtn[i] = wordPassModels.get(i).isPass();
        }
    }

    @Override
    public int getCount() {
        return wordPassModels.size();
    }

    @Override
    public Object getItem(int position) {
        return wordPassModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }
        Button button = convertView.findViewById(R.id.dayBtn);

        if(buttonStatus.get(position)){
            button.setEnabled(enableBtn[position]);
            Log.d(TAG, "다시 생성된 position: "+ position);
        }else{
            buttonStatus.set(position, true);
            Log.d(TAG, "새로 생성된 position: "+ position);

        }
        final WordPassModel wordPassModel = wordPassModels.get(position);

        button.setText(wordPassModels.get(position).getDay());
        if(!enableBtn[position])
            button.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WordListActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("day", wordPassModel.getDay());
                parent.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

}
