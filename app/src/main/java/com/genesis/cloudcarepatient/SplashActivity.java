package com.genesis.cloudcarepatient;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fade();

        final ImageView logo  = (ImageView) findViewById(R.id.imgLogo);
        final TextView text  = (TextView) findViewById(R.id.textspace);

        int SPLASH_TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                logo.setAlpha(0);
                text.setAlpha(0);
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void fade(){
        ImageView logo  = (ImageView) findViewById(R.id.imgLogo);
        TextView text  = (TextView) findViewById(R.id.textspace);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        logo.startAnimation(animation1);
        text.startAnimation(animation1);
    }

}