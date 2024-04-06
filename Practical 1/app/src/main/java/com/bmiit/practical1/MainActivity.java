package com.bmiit.practical1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bmiit.practical1.modules.SQLHandler;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameField, passwordField;
    SQLHandler sqlHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        sqlHandler = new SQLHandler(this, "MyDB", null, 1);
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String userData = sharedPreferences.getString("user", "none");
        if(userData.equals("admin")) {
            Intent adminIntent = new Intent(MainActivity.this, admin.class);
            startActivity(adminIntent);
            finish();
        } else if(userData.equals("none")) {
            loginButton.setOnClickListener(v -> {
                if(usernameField.getText().toString().isEmpty() && passwordField.getText().toString().isEmpty()) {
                    return;
                }

                if(usernameField.getText().toString().equals("Admin") && passwordField.getText().toString().equals("12345")) {
                    Intent adminIntent = new Intent(MainActivity.this, admin.class);
                    preferenceEditor.putString("user", "admin");
                    preferenceEditor.apply();
                    startActivity(adminIntent);
                    finish();
                    return;
                }

                String result = sqlHandler.login(usernameField.getText().toString(), passwordField.getText().toString());
                if(result.equals("Login Failed")) {
                    Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                } else {
                    Intent employeeIntent = new Intent(MainActivity.this, employee.class);
                    preferenceEditor.putString("user", result);
                    preferenceEditor.apply();
                    startActivity(employeeIntent);
                    finish();
                }
            });
        } else {
            Intent employeeIntent = new Intent(MainActivity.this, employee.class);
            startActivity(employeeIntent);
            finish();
        }
    }
}