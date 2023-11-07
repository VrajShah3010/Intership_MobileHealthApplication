package com.example.timepass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class SpeakerCheckActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private int randomNumber;
    private EditText userInputEditText;
    private TextView resultTextView;
    private Button checkButton;
    private boolean hasRetried = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_check);

        TextView randomNumberText = findViewById(R.id.randomNumberText);
        userInputEditText = findViewById(R.id.userInputEditText);
        resultTextView = findViewById(R.id.resultTextView);
        checkButton = findViewById(R.id.checkButton);

        Random random = new Random();
        randomNumber = random.nextInt(10) + 1;
        randomNumberText.setText("Type the number: ");

        textToSpeech = new TextToSpeech(this, this);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyInput();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                textToSpeech.speak(String.valueOf(randomNumber), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }
    private void verifyInput() {
        String userInput = userInputEditText.getText().toString().trim();
        if (!userInput.isEmpty() && Integer.parseInt(userInput) == randomNumber) {
            resultTextView.setText("Passed");
            saveTestResult(true);
            navigateToNextActivity();
        } else {
            if (!hasRetried) {
                resultTextView.setText("Failed. Try again.");

                userInputEditText.setText("");

                hasRetried = true;
            } else {
                resultTextView.setText("Failed");
                saveTestResult(false);
                navigateToNextActivity();
            }
        }
    }

    private void saveTestResult(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("SpeakerTest", isPassed);
        editor.apply();
    }

    private void navigateToNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SpeakerCheckActivity.this, RearCameraActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}