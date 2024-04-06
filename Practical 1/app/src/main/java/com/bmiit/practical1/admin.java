package com.bmiit.practical1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bmiit.practical1.modules.Leave;
import com.bmiit.practical1.modules.SQLHandler;

import java.util.ArrayList;

public class admin extends AppCompatActivity {

    Button logoutButton;
    ListView listView;
    SQLHandler sqlHandler;
    ArrayList<Leave> leaveList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;
    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();
        sqlHandler = new SQLHandler(this, "MyDB", null, 1);
        leaveList = new ArrayList<Leave>();
        listView = findViewById(R.id.leave_list);
        logoutButton = findViewById(R.id.logout_button);

        adapter = new BaseAdapter() {
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
                View view = layoutInflater.inflate(R.layout.admin_leave, null);
                TextView dateView = view.findViewById(R.id.date);
                TextView nameView = view.findViewById(R.id.name);
                dateView.setText(leaveList.get(position).date);
                nameView.setText(leaveList.get(position).employee);
                Button approveButton = view.findViewById(R.id.approve_button);
                Button rejectButton = view.findViewById(R.id.reject_button);

                approveButton.setOnClickListener(v -> {
                    sqlHandler.changeLeaveStatus(leaveList.get(position).employee, leaveList.get(position).date, "approve");
                    leaveList.clear();
                    sqlHandler.getAllLeaves(leaveList);
                    adapter.notifyDataSetChanged();
                });

                rejectButton.setOnClickListener(v -> {
                    sqlHandler.changeLeaveStatus(leaveList.get(position).employee, leaveList.get(position).date, "reject");
                    leaveList.clear();
                    sqlHandler.getAllLeaves(leaveList);
                    adapter.notifyDataSetChanged();
                });
                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sqlHandler.getAllLeaves(leaveList);
        adapter.notifyDataSetChanged();

        logoutButton.setOnClickListener(v -> {
            preferenceEditor.remove("user");
            preferenceEditor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}