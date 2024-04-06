package com.bmiit.practical1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bmiit.practical1.modules.Leave;
import com.bmiit.practical1.modules.SQLHandler;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class employee extends AppCompatActivity {

    Button applyLeave, logoutButton;
    ListView listView;
    TextView nameView;
    BaseAdapter baseAdapter;
    ArrayList<Leave> leaveList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;
    SQLHandler sqlHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutButton = findViewById(R.id.logout_button);
        listView = findViewById(R.id.leave_list);
        nameView = findViewById(R.id.name);
        applyLeave = findViewById(R.id.leave_picker);
        leaveList = new ArrayList<Leave>();
        sqlHandler = new SQLHandler(this, "MyDB", null, 1);
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return leaveList.size();
            }

            @Override
            public Object getItem(int position) {
                return leaveList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.employee_leave, null);
                TextView dateView = view.findViewById(R.id.date);
                TextView statusView = view.findViewById(R.id.status);
                dateView.setText(leaveList.get(position).date);
                statusView.setText(leaveList.get(position).status);

                return view;
            }
        };
        listView.setAdapter(baseAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameView.setText(nameView.getText() + sharedPreferences.getString("user", "none"));
        sqlHandler.getLeaves(leaveList, sharedPreferences.getString("user", "none"));
        baseAdapter.notifyDataSetChanged();

        logoutButton.setOnClickListener(v -> {
            preferenceEditor.remove("user");
            preferenceEditor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        applyLeave.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                sqlHandler.applyLeave(sharedPreferences.getString("user", "none"), dayOfMonth + "-" + (month + 1) + "-" + year);
                leaveList.clear();

                sqlHandler.getLeaves(leaveList, sharedPreferences.getString("user", "none"));
                baseAdapter.notifyDataSetChanged();
            });
            datePickerDialog.show();
        });
    }
}