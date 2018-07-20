package com.hxshijie.answer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hxshijie.util.JSON;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean exitFlag = false;
    private Timer timer = new Timer();
    private JSON json;
    private String fileName = "Answer.db";
    private String[] msg = {"你似乎没有问任何问题(．． )…", "你在想什么啊(o_ _)ﾉ"
            , "没事不要来打扰我！！！(* ￣︿￣)", "你不写问题是来逗我的吗╮(╯▽╰)╭"
            , "你想问什么（＃￣～￣＃）", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void ok_Click(View view) {
        //获取所有数据
        this.readDatabases(fileName);
        //获取提问
        EditText question = findViewById(R.id.question);
        EditText answer = findViewById(R.id.answer);
        ImageView enlarge = findViewById(R.id.enlarge);
        //校验提问是否有效
        if (question.getText().toString().length() > 0) {
            //获取key
            Iterator<String> keys = json.getKeys();
            String key = null;
            while (keys.hasNext()) {
                String tmp = keys.next();
                if (question.getText().toString().contains(tmp)) {
                    key = tmp;
                    break;
                }
            }
            if (key != null) {
                try {
                    if (key.equals("作者")) {
                        answer.setText("悲剧小白");
                    } else {
                        //通过key查找value
                        String value = json.getValue(key);
                        //判断并显示放大按钮
                        if (value.length() >= 100) {
                            enlarge.setVisibility(View.VISIBLE);
                        } else {
                            enlarge.setVisibility(View.INVISIBLE);
                        }
                        //显示在TextView中
                        answer.setText(value);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), "系统出现异常", Toast.LENGTH_SHORT).show();
                }
            } else {
                enlarge.setVisibility(View.INVISIBLE);
                //显示一句话
                answer.setText("我也不知道，请检查是否录入过");
            }
        } else {
            enlarge.setVisibility(View.INVISIBLE);
            Random random = new Random();
            int i = random.nextInt(msg.length);
            answer.setText(msg[i]);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
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

    public void enlarge_Click(View view) {
        EditText answer = findViewById(R.id.answer);
        Intent intent = new Intent(MainActivity.this, EnlargeActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("value", answer.getText().toString());
        intent.putExtra("answer", bundle);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exitFlag) {
                exitFlag = true;
                Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        exitFlag = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return true;
    }

    public void add_Click(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
    }

    public void revise_Click(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, ReviseActivity.class);
        startActivity(intent);
    }

    public void delete_Click(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
        startActivity(intent);
    }

    public void input_Click(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    public void output_Click(MenuItem item) {
        //请求权限
        String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 0);
        } else {
            this.backups();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    this.backups();
                } else {
                    Toast.makeText(getBaseContext(), "权限已被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void backups () {
        //获取所有数据
        this.readDatabases(fileName);
        try {
            //保存到SD卡里
            File file=new File(Environment.getExternalStorageDirectory(), "/"+fileName);
            OutputStream out = new FileOutputStream(file);
            out.write(json.printJson().getBytes());
            out.close();
            EditText answer = findViewById(R.id.answer);
            answer.setText(String.format("文件已保存到%s/%s", Environment.getExternalStorageDirectory(), fileName));
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "数据文件流异常", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                if (uri != null) {
                    File file=new File(uri.getPath());
                    try {
                        InputStream fileInputStream = new FileInputStream(file);
                        byte temp[] = new byte[1024];
                        StringBuilder stringBuilder = new StringBuilder("");
                        int len;
                        while ((len = fileInputStream.read(temp)) > 0){
                            stringBuilder.append(new String(temp, 0, len));
                        }
                        String jsonString = stringBuilder.toString();
                        fileInputStream.close();
                        json = new JSON(jsonString);
                        FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                        fileOutputStream.write(json.printJson().getBytes());
                        Toast.makeText(getBaseContext(), "导入成功", Toast.LENGTH_SHORT).show();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Toast.makeText(getBaseContext(), "数据文件流异常", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(), "数据文件错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
