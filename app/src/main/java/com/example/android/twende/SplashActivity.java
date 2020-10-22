package com.example.android.twende;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    ImageView car, twende;
            //ImageView bike;
    Animation fromLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        car = findViewById(R.id.taxi);
        twende = findViewById(R.id.twende);
       // bike= findViewById(R.id.bike);

        fromLeft = AnimationUtils.loadAnimation(this,R.anim.fromleft);
        car.setAnimation(fromLeft);
        twende.setAnimation(fromLeft);
        //bike.setAnimation(fromLeft);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeintent  = new Intent(SplashActivity.this, SignupActivity.class);
                startActivity(homeintent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    }
