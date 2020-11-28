package com.example.bloyal;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    TextView btnRedirectSignUp;
    Button btnLogin;
    EditText etEmail, etPassword;
    ManagedChannel myChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnRedirectSignUp = findViewById(R.id.btnRedirectSignup);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRedirectSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if((!email.equals("")) & (!password.equals(""))){
                    myChannel = ManagedChannelBuilder.forAddress("10.0.2.2", 8080).usePlaintext().build();
                    BlockchainAndroidGrpc.BlockchainAndroidBlockingStub blockingStub = BlockchainAndroidGrpc.newBlockingStub(myChannel);
                    User user = User.newBuilder().setEmail(email).setPassword(password).build();
                    Success success = blockingStub.logInUser(user);
                    boolean reply = success.getSuccess();

                    if (reply) {
                        Toast.makeText(LoginActivity.this, "Mozeeee", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }else Toast.makeText(LoginActivity.this, "Your email and/or password are not correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Email or password fields must not be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
