package com.example.bloyal;

import androidx.appcompat.app.AppCompatActivity;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.EventListener;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    ImageView ivBack;
    TextView tvCount;
    Button btnStopCounting;
    ProgressBar progressBar;

    SensorManager sensorManager;
    boolean running = false;

    ManagedChannel myChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        ivBack = findViewById(R.id.ivBack);
        tvCount = findViewById(R.id.tvCount);
        btnStopCounting = findViewById(R.id.btnStopCounting);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StepCounterActivity.this, AccountActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        btnStopCounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int steps = Integer.parseInt(String.valueOf(tvCount.getText()));
                tvCount.setText("0");

                int pointsToAdd = Math.round(steps / 100);

                Toast.makeText(StepCounterActivity.this, "You earned: " + pointsToAdd + " points!", Toast.LENGTH_SHORT).show();

                myChannel = ManagedChannelBuilder.forAddress("10.0.2.2", 8080).usePlaintext().build();
                BlockchainAndroidGrpc.BlockchainAndroidBlockingStub blockingStub = BlockchainAndroidGrpc.newBlockingStub(myChannel);
                Points points = Points.newBuilder().setPoints(pointsToAdd).build();
                Success success = blockingStub.addPoints(points);
                boolean reply = success.getSuccess();
                if(!reply) {
                    Log.d("Error", "Not able to add points");
                }


                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        //sensorManager.unregisterListener(this); this will stop detecting steps
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(running) {
            int steps = Math.round(sensorEvent.values[0]);
            tvCount.setText(String.valueOf(steps));
            progressBar.setProgress(steps/1000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}