package com.example.batteryusage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private TextView batteryPercentageText, batteryHealthText, batteryStatusText, batteryVoltageText;
    private ProgressBar batteryProgressBar;

    private int previousLevel = -1;
    private long lastCheckTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        batteryPercentageText = findViewById(R.id.batteryPercentage);
        batteryHealthText = findViewById(R.id.batteryHealth);
        batteryStatusText = findViewById(R.id.batteryStatus);
        batteryVoltageText = findViewById(R.id.batteryVoltage);
        batteryProgressBar = findViewById(R.id.batteryProgressBar);


        registerReceiver(batteryStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);


            int batteryPercentage = (int) ((level / (float) scale) * 100);
            batteryProgressBar.setProgress(batteryPercentage);
            batteryPercentageText.setText("Battery: " + batteryPercentage + "%");



            String statusText = (status == BatteryManager.BATTERY_STATUS_CHARGING) ? "Charging" : "Discharging";
            batteryStatusText.setText("Status: " + statusText);


            batteryVoltageText.setText("Voltage: " + voltage / 1000.0f + " V");


            if (previousLevel != -1 && level < previousLevel) {
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - lastCheckTime;
                float batteryDropPerMinute = ((previousLevel - level) / (float) timeDiff) * 60000; // in percentage per minute
                Toast.makeText(context, "Battery dropped by " + batteryDropPerMinute + "% per minute", Toast.LENGTH_LONG).show();
            }

            // Update the previous level and last check time
            previousLevel = level;
            lastCheckTime = System.currentTimeMillis();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryStatusReceiver);
    }

   private String getHealthStatus(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            default:
                return "Undefined";
      }
   }
}
