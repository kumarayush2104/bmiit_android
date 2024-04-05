package com.bmiit.practical4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button sendMessageButton, callButton, clearButton;
    EditText contactNumberField, messageField;
    SmsManager smsManager;

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

        clearButton = findViewById(R.id.clear_button);
        sendMessageButton = findViewById(R.id.send_message_button);
        callButton = findViewById(R.id.call_button);
        contactNumberField = findViewById(R.id.contact_number_field);
        messageField = findViewById(R.id.message_field);
        smsManager = SmsManager.getDefault();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendMessageButton.setOnClickListener(v -> {
            if(contactNumberField.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter Contact Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if(messageField.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter Contact Number", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                smsManager.sendTextMessage(contactNumberField.getText().toString(),null, messageField.getText().toString(), null, null);
                Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        callButton.setOnClickListener(v -> {
            if(contactNumberField.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter Contact Number", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contactNumberField.getText().toString()));
            startActivity(callIntent);
        });

        clearButton.setOnClickListener(v -> {
            contactNumberField.setText("");
            messageField.setText("");
        });
    }
}