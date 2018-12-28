package com.kanlulu.teststatusbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kanlulu.teststatusbar.utils.StatusBarUtils;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sencond);
//        StatusBarUtils.setStatusBarPaddingTop(this,R.color.statusBarColor);
    }

    public void changeStatus(View view) {

    }
}
