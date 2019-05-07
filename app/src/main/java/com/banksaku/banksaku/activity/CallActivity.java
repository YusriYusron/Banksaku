package com.banksaku.banksaku.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.banksaku.banksaku.MainActivity;
import com.banksaku.banksaku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CallActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 30000;
    MediaPlayer mediaPlayer;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String email = user.getEmail();

    String [] name = email.split("@");

    private TextView textViewName;
    private TextView textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewName = findViewById(R.id.textViewName);
        textViewName.setText(name[0].toUpperCase());

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewEmail.setText(email);

        mediaPlayer = MediaPlayer.create(CallActivity.this,R.raw.call);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CallActivity.this,LoginActivity.class);
                CallActivity.this.startActivity(intent);
                CallActivity.this.finish();
                mediaPlayer.stop();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
