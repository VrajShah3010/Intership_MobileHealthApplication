package com.example.timepass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.scottyab.rootbeer.RootBeer;

public class RootedStatusActivity extends AppCompatActivity {

    private Button checkRootButton;
    private TextView rootStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooted_status);

        checkRootButton = findViewById(R.id.checkRootButton);
        rootStatusTextView = findViewById(R.id.rootStatusTextView);

        checkRootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRootStatus();
            }
        });
    }

    private void checkRootStatus() {
        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            rootStatusTextView.setText("Rooted");
        } else {
            rootStatusTextView.setText("Not Rooted");
            saveTestResult(true);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RootedStatusActivity.this, SensorCheckActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }
    private void saveTestResult(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rootTest", isPassed);
        editor.apply();
    }
}
