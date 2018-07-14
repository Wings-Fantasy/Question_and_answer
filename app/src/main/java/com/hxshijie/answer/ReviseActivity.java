package com.hxshijie.answer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hxshijie.util.JSON;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReviseActivity extends AppCompatActivity {

    private JSON json;
    private String key;
    private String fileName = "Answer.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        //获取到所有数据
        this.readDatabases(fileName);
        //获取到所有key
        Iterator<String> keys = json.getKeys();
        //初始化下拉列表
        Spinner wordList = findViewById(R.id.wordList);
        this.addWord(wordList, keys);
        //监听下拉列表选择事件
        wordList.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取所选的key
                ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                key = (String) adapter.getItem(position);
                //查询到value
                try {
                    String value = json.getValue(key);
                    EditText answer = findViewById(R.id.answer);
                    answer.setText(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void reviseOK_Click(View view) {
        //获取修改后的value
        EditText answer = findViewById(R.id.answer);
        String value = answer.getText().toString();
        try {
            //覆盖到json中
            json.setValue(key, value);
            //写入数据库
            try {
                FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                fileOutputStream.write(json.printJson().getBytes());
                Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                fileOutputStream.close();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "数据文件流错误", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "数据文件错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void readDatabases(String fileName) {
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            byte temp[] = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder("");
            int len;
            while ((len = fileInputStream.read(temp)) > 0){
                stringBuilder.append(new String(temp, 0, len));
            }
            String jsonString = stringBuilder.toString();
            fileInputStream.close();
            json = new JSON(jsonString);
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                String jsonString = "{"+"\"作者\":\"悲剧小白\""+"}";
                fileOutputStream.write(jsonString.getBytes());
                fileOutputStream.close();
                json = new JSON(jsonString);
            } catch (IOException e1) {
                Toast.makeText(getBaseContext(), "数据文件流异常", Toast.LENGTH_SHORT).show();
            } catch (JSONException e1) {
                Toast.makeText(getBaseContext(), "数据文件错误", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "数据文件流异常", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "数据文件错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void addWord (Spinner wordList, Iterator<String> keys) {
        List<String> data_list = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("作者")) {
                data_list.add(key);
            }
        }
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data_list);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        wordList.setAdapter(arr_adapter);
    }

    public void back_click(View view) {
        finish();
    }
}
