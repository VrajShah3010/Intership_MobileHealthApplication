package com.example.timepass;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FrontCameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private Camera camera;
    private SurfaceView surfaceView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_camera);

        Button openFrontCameraButton = findViewById(R.id.openFrontCameraButton);
        surfaceView = findViewById(R.id.cameraPreview);
        resultTextView = findViewById(R.id.resultTextView);

        openFrontCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openFrontCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });
    }

    private boolean checkCameraPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFrontCamera();
            } else {
                resultTextView.setText("Front camera access denied");
            }
        }
    }

    private void openFrontCamera() {
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            setCameraDisplayOrientation();
            SurfaceHolder holder = surfaceView.getHolder();
            camera.setPreviewDisplay(holder);
            camera.startPreview();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        resultTextView.setText("Passed");
                        saveTestResult(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(FrontCameraActivity.this, MicrophoneActivity.class);
                                startActivity(intent);
                            }
                        }, 3000);
                    }
                }
            }, 5000);
        } catch (Exception e) {
            resultTextView.setText("Failed");
            saveTestResult(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(FrontCameraActivity.this, MicrophoneActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        }
    }

    private void saveTestResult(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FrontCameraTest", isPassed);
        editor.apply();
    }
    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
