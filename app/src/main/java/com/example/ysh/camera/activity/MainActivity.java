package com.example.ysh.camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ysh.camera.R;
import com.example.ysh.camera.adapter.MainRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.OnChildClickerListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        List<String> data = new ArrayList<>();
        data.add("Camera");
        data.add("Camera2");

        MainRecyclerViewAdapter adapter = new MainRecyclerViewAdapter(this, data);
        adapter.setOnChildClickerListener(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onChildClick(RecyclerView parent, View view, int position, String data) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, Camera2Activity.class));
                break;
            default:
                break;
        }
    }
}
