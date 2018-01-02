package com.example.vertika.smartvib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import java.lang.*;
import android.os.Handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.zone.ZoneRulesException;

public class Receiver extends AppCompatActivity implements SensorEventListener {

    Sensor mySensor;
    SensorManager SM;

    double XReading;
    double YReading;
    double ZReading;

    private int i = 0;
    private int NoOfBitsRecvd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        // Keep the screen on forever.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView btnBack, btnClear;

        final TextView tvXValue, tvYValue, tvZValue;
        final TextView tvNx, tvNy, tvNz, tvNValue;
        final TextView tvMean, tvSD, tvOverallVibration;
        final TextView tvBitsRecvdVal, tvIsBeaconValue;
        final TextView tvDecodedData;

        btnBack = (Button)findViewById(R.id.btnBack);
        btnClear = (Button)findViewById(R.id.btnClear);

        tvXValue = (TextView)findViewById(R.id.tvXValue);
        tvYValue = (TextView)findViewById(R.id.tvYValue);
        tvZValue = (TextView)findViewById(R.id.tvZValue);

        tvNx = (TextView)findViewById(R.id.tvNx);
        tvNy = (TextView)findViewById(R.id.tvNy);
        tvNz = (TextView)findViewById(R.id.tvNz);
        tvNValue = (TextView)findViewById(R.id.tvNValue);

        tvMean = (TextView)findViewById(R.id.tvMean);
        tvSD = (TextView)findViewById(R.id.tvSD);
        tvOverallVibration = (TextView)findViewById(R.id.tvOverallVibration);

        tvBitsRecvdVal = (TextView)findViewById(R.id.tvBitsRecvdVal);
        tvIsBeaconValue = (TextView)findViewById(R.id.tvIsBeaconValue);

        tvDecodedData = (TextView)findViewById(R.id.tvDecodedData);

        btnBack.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(getApplicationContext(), ActionSelect.class);
                        startActivity(intent);
                    }
                }
        );

        btnClear.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        tvDecodedData.setText("");
                    }
                }
        );

        // Initial values for calculation.
        i = 0;
        java.util.Arrays.fill(Utils.NormAccValues, 0);
        Utils.BeaconRecvd = false;
        tvIsBeaconValue.setText("IsBeacon: False");
        NoOfBitsRecvd = 0;

        final Handler handler;
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                handler.postDelayed(this, 1000 / Utils.N);
                //tvDecodedData.setText(tvDecodedData.getText().toString() + (char)i++);

                // As of now I have declared the below two variables globally.
                // This is done because, the sensor is unregistered in onSensorChanged() event.
                // Sensor mySensor;
                // SensorManager SM;

                // Create a SensorManager.
                SM = (SensorManager)getSystemService(SENSOR_SERVICE);

                // Accelerometer sensor.
                mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                SM.registerListener(Receiver.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

                // I dont need to unregister the accelerometer listener for now.
                // It seems to work without unregistering.
                // SM.unregisterListener(Receiver.this);

                double XValue = XReading;
                double YValue = YReading;
                double ZValue = ZReading;

                XValue = Math.floor(XValue * 10000) / 10000d;
                YValue = Math.floor(YValue * 10000) / 10000d;
                ZValue = Math.floor(ZValue * 10000) / 10000d;

                tvXValue.setText("X: " + Double.toString(XValue));
                tvYValue.setText("Y: " + Double.toString(YValue));
                tvZValue.setText("Z: " + Double.toString(ZValue));

                double Nx = Utils.Kx * XValue + Utils.Bx;
                double Ny = Utils.Ky * YValue + Utils.By;
                double Nz = Utils.Kz * ZValue + Utils.Bz;

                Nx = Math.floor(Nx * 10000) / 10000d;
                Ny = Math.floor(Ny * 10000) / 10000d;
                Nz = Math.floor(Nz * 10000) / 10000d;

                tvNx.setText("Nx: " + Double.toString(Nx));
                tvNy.setText("Ny: " + Double.toString(Ny));
                tvNz.setText("Nz: " + Double.toString(Nz));

                Utils.NormAccValues[i] = Math.sqrt(Math.pow(Nx, 2) + Math.pow(Ny, 2) + Math.pow(Nz, 2));
                Utils.NormAccValues[i] = Math.floor(Utils.NormAccValues[i] * 10000) / 10000d;
                tvNValue.setText("N: " + Double.toString(Utils.NormAccValues[i]));

                i++;

                if (i >= Utils.N) {
                    // 16 samples are received in 1 second.

                    // Calculate Mean.
                    int j;
                    double Mean = 0;
                    for (j = 0; j < Utils.N; j++) {
                        Mean += Utils.NormAccValues[j];
                    }
                    Mean = Mean / Utils.N;

                    Mean = Math.floor(Mean * 10000) / 10000d;
                    tvMean.setText("Mean:" + Double.toString(Mean));

                    // Calculate SD.
                    double SD = 0;
                    for (j = 0; j < Utils.N; j++) {
                        SD += Math.pow(Utils.NormAccValues[j] - Mean, 2);
                    }
                    SD = Math.sqrt(SD / Utils.N);

                    SD = Math.floor(SD * 10000) / 10000d;
                    tvSD.setText("SD:" + Double.toString(SD));

                    // Calculate OverallVibration.
                    double OverallVibration = Mean * Utils.Alpha;

                    OverallVibration = Math.floor(OverallVibration * 10000 ) / 10000d;
                    tvOverallVibration.setText("OV: " + Double.toString(OverallVibration));

                    // Calculate the BitRecvd.
                    int BitRecvd = 0;
                    if (SD > 0.006) {
                        // A vibration is received in the previous 1 second.
                        BitRecvd = 1;
                    }

                    // The below two lines of code are the working code that was used for initial testing.
                    // Utils.CharRecvd = (Utils.CharRecvd << 1) + BitRecvd;
                    // tvBitsRecvdVal.setText(Integer.toBinaryString(Utils.CharRecvd));

                    // Make a decision based on BitRecvd and Utils.BeaconRecvd.
                    if (!Utils.BeaconRecvd) {
                        // Beacon was not received until now. Beacon is now received.
                        if (BitRecvd == 1) {
                            Utils.BeaconRecvd = true;
                            NoOfBitsRecvd = 0;
                            tvIsBeaconValue.setText("IsBeacon: True");
                        }
                    } else {
                        // Beacon was already received. Vibration for one of the char is received.
                        NoOfBitsRecvd++;
                        Utils.CharRecvd = (Utils.CharRecvd << 1) + BitRecvd;
                        tvBitsRecvdVal.setText(String.format("%7s", Integer.toBinaryString(Utils.CharRecvd).replace(' ', '0')) + "Decimal: " + Utils.CharRecvd);

                        if (NoOfBitsRecvd == 7) {
                            // All the bits in the char was received.
                            // Get the character retrieved here.
                            tvDecodedData.setText(tvDecodedData.getText().toString() + (char)Utils.CharRecvd);

                            // Sleep here for 3 seconds. Analyze whether this has to be done or not.
                            // Sleeping for 3 seconds need not be done.

                            // Reset the variables to their initial values.
                            Utils.CharRecvd = 0;
                            NoOfBitsRecvd = 0;
                            Utils.BeaconRecvd = false;
                            tvIsBeaconValue.setText("IsBeacon: False");
                            tvBitsRecvdVal.setText("");
                        }
                    }

                    // Reset the value of i = 0.
                    i = 0;
                    java.util.Arrays.fill(Utils.NormAccValues, 0);

                }
            }
        };

        handler.postDelayed(r, 1000 / Utils.N);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        XReading = sensorEvent.values[0];
        YReading = sensorEvent.values[1];
        ZReading = sensorEvent.values[2];

        SM.unregisterListener(Receiver.this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Will not be using this.
    }
}
