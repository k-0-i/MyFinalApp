package com.st4rry.myapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.st4rry.myapp.fragments.AbsorbedFragment;
import com.st4rry.myapp.fragments.TaskFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String weatherUrl = "https://yiketianqi.com/api?version=v1&appid=33699742&appsecret=BE6XbbbW";

    public List<Fragment> fragmentList = new ArrayList<>();
    private FragmentManager fragmentManager;
//    private MyHandler myHandler;
    private View absorbedView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化底部导航栏
        InitBottomNavigation();

    }

    // 底部导航栏模块
    public void InitBottomNavigation() {
        // 添加两个fragment实例到fragmentList，以便管理
        fragmentList.add(new AbsorbedFragment());
        fragmentList.add(new TaskFragment());


        //建立fragment管理器
        fragmentManager = getSupportFragmentManager();

        //管理器开启事务，将fragment实例加入管理器
        fragmentManager.beginTransaction()
                .add(R.id.FragmentLayout, fragmentList.get(1), "ABSORBED")
                .add(R.id.FragmentLayout, fragmentList.get(0), "TASK")
                .commit();

        //设置fragment显示初始状态
        fragmentManager.beginTransaction()
                .show(fragmentList.get(0))
                .hide(fragmentList.get(1))
                .commit();

        //设置底部导航栏点击选择监听事件
        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_accounts) {
                    ShowFragment(0);
                    return true;
                } else if (itemId == R.id.menu_task) {
                    ShowFragment(1);
                    return true;
                } else {
                    Log.i(TAG, "onNavigationItemSelected: Error");
                    return false;
                }
            }
        });
    }

    public void ShowFragment(int index) {
        fragmentManager.beginTransaction()
                .show(fragmentList.get(index))
                .hide(fragmentList.get((index + 1) % 2))
                .commit();
    }

}