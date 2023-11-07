package com.example.timepass;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdaptorsResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_adaptors);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        TextView bluetoothResult = findViewById(R.id.bluetoothResult);

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothResult.setText("Bluetooth: Enabled");
            } else {
                bluetoothResult.setText("Bluetooth: Disabled");
            }
            saveTestResultBluetooth(true);
        } else {
            bluetoothResult.setText("Bluetooth is not supported on this device");
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        TextView wifiResult = findViewById(R.id.wifiResult);

        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                wifiResult.setText("Wi-Fi: Enabled");
            } else {
                wifiResult.setText("Wi-Fi: Disabled");
            }
            saveTestResultWifi(true);
        } else {
            wifiResult.setText("Wi-Fi is not supported on this device");
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        TextView gpsResult = findViewById(R.id.gpsResult);

        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsResult.setText("GPS: Enabled");
            } else {
                gpsResult.setText("GPS: Disabled");
            }
            saveTestResultGPS(true);
        } else {
            gpsResult.setText("GPS is not supported on this device");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdaptorsResultActivity.this, RootedStatusActivity.class);
                startActivity(intent);
            }
        }, 3000);

    }
    private void saveTestResultBluetooth(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("bluetoothTest", isPassed);
        editor.apply();
    }
    private void saveTestResultWifi(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("wifiTest", isPassed);
        editor.apply();
    }
    private void saveTestResultGPS(boolean isPassed) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("gpsTest", isPassed);
        editor.apply();
    }
}
