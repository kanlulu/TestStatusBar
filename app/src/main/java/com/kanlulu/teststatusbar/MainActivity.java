package com.kanlulu.teststatusbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kanlulu.teststatusbar.utils.StatusBarUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ChangeStatusBarColor(View view) {
//        StatusBarUtils.setStatusBarBackgroundColor(this, R.color.statusBarColor);
    }

    public void nextActivity(View view) {
//        Toast.makeText(this, "CHANNEL：" + BuildConfig.Channel, Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, SecondActivity.class);
//        startActivity(intent);
    }
}
