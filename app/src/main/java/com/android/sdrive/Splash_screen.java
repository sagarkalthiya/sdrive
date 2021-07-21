package com.android.sdrive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.android.sdrive.Component.Coustom_textview.MyTextView;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.Home.HomeActivity;
import com.android.sdrive.Login_Pages.Login_Activity;

public class Splash_screen  extends AppCompatActivity {

    MyTextView txtAppName;

    public static int splashTimeOut = 4000;  //time in mili seconds
    LinearLayout layout1, l2;
    Animation uptodown, downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        txtAppName = (MyTextView) findViewById(R.id.txt_app_name);

        //Animate text in 360 degree in y axis
        txtAppName.animate().rotationY(360f).setDuration(3000);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        layout1.setAnimation(uptodown);

        // A delaye running at 4 mili seconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_screen.this, HomeActivity.class);
                startActivity(intent);

                finish();
            }
        }, splashTimeOut);

    }
}