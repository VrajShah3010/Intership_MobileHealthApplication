package com.example.timepass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorCheckActivity extends AppCompatActivity implements SensorEventListener {

    private Vibrator vibrator;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor proximitySensor;

    private Button vibrationCheckButton;
    private Button accelerometerCheckButton;
    private Button gyroscopeCheckButton;
    private Button proximityCheckButton;

    private TextView vibrationResultTextView;
    private TextView accelerometerResultTextView;
    private TextView gyroscopeResultTextView;
    private TextView proximityResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_check);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        vibrationCheckButton = findViewById(R.id.vibrationCheckButton);
        accelerometerCheckButton = findViewById(R.id.accelerometerCheckButton);
        gyroscopeCheckButton = findViewById(R.id.gyroscopeCheckButton);
        proximityCheckButton = findViewById(R.id.proximityCheckButton);

        vibrationResultTextView = findViewById(R.id.vibrationResultTextView);
        accelerometerResultTextView = findViewById(R.id.accelerometerResultTextView);
        gyroscopeResultTextView = findViewById(R.id.gyroscopeResultTextView);
        proximityResultTextView = findViewById(R.id.proximityResultTextView);

        vibrationCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVibration();
            }
        });

        accelerometerCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAccelerometer();
            }
        });

        gyroscopeCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGyroscope();
            }
        });

        proximityCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkProximity();
            }
        });
    }

    private void checkVibration() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(1000); // Vibrate for 1 second
            vibrationResultTextView.setText("Passed");
            saveTestResultVibration(true);
        } else {
            vibrationResultTextView.setText("Failed");
        }
    }
    private void checkAccelerometer() {
        if (accelerometerSensor != null) {
            accelerometerResultTextView.setText("Passed");
            saveTestResultAccelerometer(true);
        } else {
            accelerometerResultTextView.setText("Failed");
        }
    }
    private void checkGyroscope() {
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            gyroscopeResultTextView.setText("Passed");
            saveTestResultGyroscope(true);
        } else {
            gyroscopeResultTextView.setText("Failed");
        }
    }
    private void checkProximity() {
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            proximityResultTextView.setText("Passed");
            saveTestResultProximity(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start MicrophoneCheckActivity
                    Intent intent = new Intent(SensorCheckActivity.this, SpeakerCheckActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        } else {
            proximityResultTextView.setText("Failed");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start MicrophoneCheckActivity
                    Intent intent = new Intent(SensorCheckActivity.this, SpeakerCheckActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        }
    }

    private void saveTestResultVibration(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("vibrationTest", isPassed);
        editor.apply();
    }
    private void saveTestResultAccelerometer(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("accelerometerTest", isPassed);
        editor.apply();
    }
    private void saveTestResultGyroscope(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("gyroscopeTest", isPassed);
        editor.apply();
    }
    private void saveTestResultProximity(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("proximityTest", isPassed);
        editor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
