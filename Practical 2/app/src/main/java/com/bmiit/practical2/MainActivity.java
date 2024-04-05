package com.bmiit.practical2;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client;
    List<Person> personList;
    ListView personListView;
    BaseAdapter listAdapter;

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

        client = new OkHttpClient();
        personList = new ArrayList<Person>();
        personListView = findViewById(R.id.person_list_view);
        listAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return personList.size();
            }

            @Override
            public Object getItem(int position) {
                return personList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.person_list, null);

                ImageView imageView = view.findViewById(R.id.person_image);
                Picasso.get().load(Uri.parse(personList.get(position).image)).into(imageView);
                TextView nameView = view.findViewById(R.id.person_name);
                nameView.setText(personList.get(position).name);

                TextView locationView = view.findViewById(R.id.person_location);
                locationView.setText(personList.get(position).city + ", " + personList.get(position).country);

                return view;
            }
        };

        personListView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Request request = new Request.Builder().url("https://demonuts.com/Demonuts/JsonTest/Tennis/json_parsing.php").build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        listData(response.body().string());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void listData(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.optJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentPerson = jsonArray.getJSONObject(i);
            personList.add(new Person(currentPerson.getString("name"), currentPerson.getString("country"), currentPerson.getString("city"), currentPerson.getString("imgURL")));
        }

        runOnUiThread(() -> listAdapter.notifyDataSetChanged());
    }
}