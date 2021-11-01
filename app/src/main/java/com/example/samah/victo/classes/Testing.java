package com.example.samah.victo.classes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samah.victo.R;
import com.example.samah.victo.Raw.Raw;

import java.util.ArrayList;

public class Testing extends AppCompatActivity {
    ArrayList<Raw> list;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        list = new ArrayList<>();
        for(int j = 1; j< 55; j++){
            Raw raw = new Raw(j, "suger00"+j, (float) (566.2f + Math.random()), "kg");
            list.add(raw);
        }
        recyclerView = findViewById(R.id.rec);



        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ExampleAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);




    }
}
