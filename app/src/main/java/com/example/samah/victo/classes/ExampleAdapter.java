package com.example.samah.victo.classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.samah.victo.R;
import com.example.samah.victo.Raw.Raw;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter {
    private ArrayList<Raw> list;

    public ExampleAdapter(ArrayList<Raw> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflator_items, viewGroup, false);
        ExampleViewHolder exampleViewHolder = new ExampleViewHolder(v);
        return exampleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ExampleViewHolder exampleViewHolder = (ExampleViewHolder) viewHolder;
        Raw raw = list.get(i);
        if(i == 0){
            View v = (View) exampleViewHolder.priceView.getParent();
            v.setPadding(0, 300, 0, 0);

        }
        exampleViewHolder.nameButton.setText(raw.getName().toUpperCase());
        exampleViewHolder.priceView.setText(raw.getPrice() + "Dz/" + raw.getUnit());
        Button editButton = exampleViewHolder.editButton;
        editButton.setOnClickListener(v -> {
            list.remove(i);
            this.notifyDataSetChanged();


        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public Button nameButton;
        public TextView priceView;
        public Button deleteButton;
        public Button editButton;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameButton = itemView.findViewById(R.id.itemNameButton);
            priceView = itemView.findViewById(R.id.infl2PriceText);
            deleteButton = itemView.findViewById(R.id.itemDeleteButton);
            editButton = itemView.findViewById(R.id.itemEditButton);
        }


    }
}
