package com.hxshijie.answer;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hxshijie.util.ViewPagerAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements OnPageChangeListener {

    private ViewPager viewPager;
    private List<View> views = new ArrayList<>();
    private int[] guideImages = {R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4,R.drawable.guide5};
    private ViewPagerAdapter adapter;
    private ImageView[] points = new ImageView[guideImages.length];
    private LinearLayout pointsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //用建立文件的方法判断用户是否为第一次打开应用
        String fileName = "isOneOpen.dat";
        try {
            //如果文件打开正常，则表示用户已经阅读过欢迎界面
            FileInputStream fileInputStream = openFileInput(fileName);
            fileInputStream.close();
            this.openMainActivity();
        } catch (FileNotFoundException e) {
            //出现文件不存在的错误，则表示用户为第一次打开
            try {
                //写入文件，用于下次打开时判断
                FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                fileOutputStream.write("false".getBytes());
                fileOutputStream.close();
            } catch (IOException e1) {
                Toast.makeText(getBaseContext(), "标记文件流异常", Toast.LENGTH_SHORT).show();
                this.openMainActivity();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "标记文件流异常", Toast.LENGTH_SHORT).show();
            this.openMainActivity();
            return;
        }
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        this.initView();
        this.initData();
    }

    //打开主窗体
    private void openMainActivity () {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView () {
        viewPager = findViewById(R.id.viewPager);
        pointsLayout = findViewById(R.id.pointsLayout);
        adapter = new ViewPagerAdapter(views);
    }

    private void initData () {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        for (int guideImage:guideImages) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(guideImage);
            imageView.setLayoutParams(layoutParams);
            views.add(imageView);
        }
        this.initPoint();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initPoint () {
        for (int i = 0; i<pointsLayout.getChildCount(); i++) {
            points[i] = (ImageView) pointsLayout.getChildAt(i);
            points[i].setImageResource(R.drawable.point_normal);
        }
        points[0].setImageResource(R.drawable.point_select);
    }

    @Override
    public void onPageScrolled(int i, float v, int position) {}

    @Override
    public void onPageScrollStateChanged(int position) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            points[position + 1].setImageResource(R.drawable.point_normal);
        } else if (position == points.length - 2) {
            points[position - 1].setImageResource(R.drawable.point_normal);
        } else if (position == points.length - 1) {
            this.openMainActivity();
            return;
        } else {
            points[position + 1].setImageResource(R.drawable.point_normal);
            points[position - 1].setImageResource(R.drawable.point_normal);
        }
        points[position].setImageResource(R.drawable.point_select);
    }
}
