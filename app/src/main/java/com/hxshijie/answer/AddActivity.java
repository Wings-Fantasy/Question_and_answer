package com.hxshijie.answer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hxshijie.util.JSON;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class AddActivity extends AppCompatActivity {

    private String fileName = "Answer.db";
    private JSON json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //拿到文件中所有数据
        this.readDatabases(fileName);
    }

    public void addOK_Click(View view){
        //获取当前key和value
        EditText word = findViewById(R.id.word);
        EditText answer = findViewById(R.id.addAnswer);
        String key = word.getText().toString();
        String value = answer.getText().toString();
        if (key.length() > 0 && value.length() > 0) {
            try {
                if (key.equals("作者")) {
                    Toast.makeText(getBaseContext(), "修改作者可不是一个好行为哦", Toast.LENGTH_SHORT).show();
                } else {
                    //获取所有key
                    Iterator<String> keys = json.getKeys();
                    boolean keyFlag = true;
                    while (keys.hasNext()) {
                        if (keys.next().equals(key)) {
                            keyFlag = false;
                            break;
                        }
                    }
                    if (keyFlag) {
                        //添加到json中
                        json.setValue(key, value);
                        //复写到文件中
                        FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                        fileOutputStream.write(json.printJson().getBytes());
                        Toast.makeText(getBaseContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        fileOutputStream.close();
                    } else {
                        Toast.makeText(getBaseContext(), "\"" + key + "\"已添加过", Toast.LENGTH_SHORT).show();
                    }
                }
                word.setText("");
                answer.setText("");
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "数据文件流错误", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "数据错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "关键词和答案都必须输入哦", Toast.LENGTH_SHORT).show();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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

    public void back_click(View view) {
        finish();
    }
}
