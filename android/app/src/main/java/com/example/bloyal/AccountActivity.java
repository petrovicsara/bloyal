package com.example.bloyal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    ImageView ivBack;
    Button btnCounter;

    private int COUNTER_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ivBack = findViewById(R.id.ivBack);
        btnCounter = findViewById(R.id.btnCounter);

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        btnCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCounter.setEnabled(false);
                requestActivityRecognitionPermission();
                /*
                if(ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(AccountActivity.this, StepCounterActivity.class));
                } else {
                    requestActivityRecognitionPermission();
                }
                 */
            }
        });
    }

    private void requestActivityRecognitionPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This action needs your permission to use your phone's counter sensor")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AccountActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, COUNTER_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, COUNTER_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == COUNTER_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(AccountActivity.this, StepCounterActivity.class);
                Intent currentIntent = getIntent();
                String email = currentIntent.getStringExtra("email");
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}