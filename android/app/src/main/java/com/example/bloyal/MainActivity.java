package com.example.bloyal;

import androidx.appcompat.app.AppCompatActivity;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnSignUp;
    TextView btnRedirectLogin;
    private ManagedChannel myChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnRedirectLogin = findViewById(R.id.btnRedirectLogin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                //String password = etPassword.getText().toString();
                /*if((!email.equals("")) & (!password.equals(""))){
                    //we create gRPC channel for our stub
                    myChannel = ManagedChannelBuilder.forAddress("10.0.2.2", 8080).usePlaintext().build();
                    BlockchainAndroidGrpc.BlockchainAndroidBlockingStub blockingStub = BlockchainAndroidGrpc.newBlockingStub(myChannel);
                    User user = User.newBuilder().setEmail(email).setPassword(password).build();
                    Success success = blockingStub.logInUser(user);
                    boolean reply = success.getSuccess();

                    if (reply) {
                        Toast.makeText(MainActivity.this, "Mozeeee", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }else Toast.makeText(MainActivity.this, "Your email and/or password are not correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Email or password are empty", Toast.LENGTH_SHORT).show();
                }*/

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();

            }
        });

        btnRedirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
