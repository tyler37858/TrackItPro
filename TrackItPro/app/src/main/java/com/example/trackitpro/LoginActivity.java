package com.example.trackitpro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignIn;
    private Button btnCreateAccount;

    // sms permission stuff
//    private ActivityResultLauncher<String> smsPermissionLauncher;
//    public static boolean smsPermissionGranted = false;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        //create the repository used for account operations
        userRepository = new UserRepository(this);

        // set the actions for signingn in/ creating an account.
        btnSignIn.setOnClickListener(v -> attemptLogin());
        btnCreateAccount.setOnClickListener(v -> attemptCreateAccount());
    }

//    private void checkSmsPermissionOnLaunch() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
//                == PackageManager.PERMISSION_GRANTED) {
//            smsPermissionGranted = true;
//        } else {
//            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
//        }
//    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Enter username and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = userRepository.authenticateUser(username, password);

        if (userId > 0) {
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
            openEvents(userId, username);
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptCreateAccount() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = userRepository.registerUser(username, password);
        if (userId == -1) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
            openEvents(userId, username);
        }
    }

    private void openEvents(long userId, String username) {
        Intent i = new Intent(LoginActivity.this, EventsActivity.class);
        i.putExtra("userId", userId);
        i.putExtra("username", username);
        startActivity(i);

        finish(); //prevent the login screen from reappearing
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userRepository != null) userRepository.close();
    }
}
