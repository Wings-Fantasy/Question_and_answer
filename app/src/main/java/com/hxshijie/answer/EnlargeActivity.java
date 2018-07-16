package com.hxshijie.answer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EnlargeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("answer");
        String value = bundle.getString("value");
        EditText answer = findViewById(R.id.answer);
        answer.setText(value);
    }

}
