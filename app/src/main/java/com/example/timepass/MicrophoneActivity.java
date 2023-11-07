package com.example.timepass;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MicrophoneActivity extends AppCompatActivity {

    private static final int AUDIO_PERMISSION_REQUEST = 1;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private Button recordButton;
    private Button playButton;
    private Button passedButton;
    private TextView resultTextView;
    private Button failedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        passedButton = findViewById(R.id.passedButton);
        failedButton = findViewById(R.id.failedButton);
        resultTextView = findViewById(R.id.resultTextView);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAudioPermission()) {
                    recordAudio();
                } else {
                    requestAudioPermission();
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        passedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText("Passed");
                saveTestResult(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MicrophoneActivity.this, TestsResultsActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }
        });

        failedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText("Failed");
                saveTestResult(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MicrophoneActivity.this, TestsResultsActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }
        });

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
    }

    private void saveTestResult(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("MicrophoneTest", isPassed);
        editor.apply();
    }
    private boolean checkAudioPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordAudio();
            } else {
                Toast.makeText(this, "Audio recording permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recordAudio() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getOutputMediaFile().toString());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            recordButton.setEnabled(false);
            playButton.setVisibility(View.GONE);
            passedButton.setVisibility(View.GONE);
            failedButton.setVisibility(View.GONE);

            Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                }
            }, 5000);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Audio recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Recordings");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + "recording.3gp");
    }


    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordButton.setEnabled(false);
            playButton.setVisibility(View.VISIBLE);
            passedButton.setVisibility(View.VISIBLE);
            failedButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Audio recording completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            File audioFile = new File(getOutputMediaFile().toString());
            if (audioFile.exists()) {
                mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Playing recorded audio...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Audio file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Playback failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


