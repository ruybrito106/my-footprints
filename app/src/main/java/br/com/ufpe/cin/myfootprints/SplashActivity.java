package br.com.ufpe.cin.myfootprints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Context ctx = this;
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ctx, LoginActivity.class);
                startActivity(intent);
            }
        }, 4000);

    }
}
