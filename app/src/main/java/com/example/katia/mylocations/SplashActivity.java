package com.example.katia.mylocations;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        passScreen();
    }

    private void passScreen() {

        new CountDownTimer(1000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                Intent intent = getClearCacheIntent();
                intent.setClass(SplashActivity.this, LocationsActivity.class);
                startActivity(intent);
            }
        }.start();
    }

    public static Intent getClearCacheIntent() {

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public void onBackPressed() {
    }
}
