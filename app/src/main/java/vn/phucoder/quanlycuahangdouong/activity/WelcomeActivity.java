package vn.phucoder.quanlycuahangdouong.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import vn.phucoder.quanlycuahangdouong.R;
@SuppressLint("CustomSplashScreen")
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(this::goToFeatureActivity, 2000);
    }

    private void goToFeatureActivity() {
        Intent intent = new Intent(WelcomeActivity.this,FeatureActivity.class);
        startActivity(intent);
    }
}