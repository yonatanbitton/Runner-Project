package com.example.yonatanbitton.runnerproject;

/**
 * Created by Yonatan Bitton on 26/07/2017.
 */

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;

public class SplashScreen extends Activity{

    private static int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);

                // Kills this Activity
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}