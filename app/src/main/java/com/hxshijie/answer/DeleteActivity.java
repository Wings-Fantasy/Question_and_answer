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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {

    private JSON json;
    private String key;
    private String fileName = "Answer.db";
    private Spinner wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        //获取到所有数据
        this.readDatabases(fileName);
        //获取到所有key
        Iterator<String> keys = json.getKeys();
        //初始化下拉列表
        wordList = findViewById(R.id.wordList);
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

    public void deleteOK_Click(View view) {
        //删除key-value
        json.remove(key);
        //重新写入到数据库
        try {
            FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(json.printJson());
            outputStreamWriter.flush();
            Toast.makeText(getBaseContext(), "删除成功", Toast.LENGTH_SHORT).show();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "数据文件流错误", Toast.LENGTH_SHORT).show();
        }
        //重新获取所有key
        Iterator<String> keys = json.getKeys();
        //更新下拉列表
        this.addWord(wordList, keys);
    }

    private void readDatabases(String fileName) {
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            json = new JSON(jsonString.toString());
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
                String jsonString = "{"+"\"作者\":\"悲剧小白\""+"}";
                outputStreamWriter.write(jsonString);
                outputStreamWriter.flush();
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
