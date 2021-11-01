package com.example.samah.victo.Raw;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.samah.victo.R;
import com.example.samah.victo.classes.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class RawActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    Map<Integer, Raw> map = new LinkedHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw);
        databaseHelper = new DatabaseHelper(this);
        setData();
        findViewById(R.id.rawHeaderLayout).post(new Runnable() {
            @Override
            public void run() {
                double height = findViewById(R.id.rawHeaderLayout).getHeight();
                findViewById(R.id.rawScrollLayout).setPadding(2, (int) height + 15, 2, 10);

            }
        });
        EditText search_edit = findViewById(R.id.rawSearchView);
        search_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LinearLayout layout = findViewById(R.id.rawScrollLayout);
                EditText searchView = (EditText) layout.getChildAt(0);
                layout.removeAllViews();
                layout.addView(searchView);
                searchView.requestFocus();
                if(map.isEmpty()) return;
                for (int j: map.keySet()){
                    if(map.get(j).getName().toUpperCase().contains(s.toString().toUpperCase())){
                        setRowRaw(map.get(j));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK && data.getIntExtra("ok", 5) == 1) {
            setData();

        }
    }

    private void setData() {
        LinearLayout layout = findViewById(R.id.rawScrollLayout);
        EditText searchView = (EditText) layout.getChildAt(0);
        layout.removeAllViews();
        layout.addView(searchView);
        Cursor cursor = databaseHelper.getAllData("RAW");
        int j = 1;
        if (cursor != null && cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String unit = cursor.getString(2);
                float price = cursor.getFloat(3);
                Raw raw = new Raw(id, name, price, unit);
                setRowRaw(raw);
                map.put(j, raw);
                j++;
            }

        }
        cursor.close();

    }


    private void setRowRaw(Raw raw) {
        ConstraintLayout v = (ConstraintLayout) getLayoutInflater().inflate(R.layout.inflator_items, null);

        Button button = v.findViewById(R.id.itemNameButton);
        button.setText(raw.getName());
        Button buttonEdit = v.findViewById(R.id.itemEditButton);
        buttonEdit.setOnClickListener(v1 -> {
            YoYo.with(Techniques.Tada).duration(150).playOn(buttonEdit);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, AddNewRaw.class);
                intent.putExtra("Edit", 1);
                intent.putExtra("id", raw.getId());
                intent.putExtra("name", raw.getName());
                intent.putExtra("price", raw.getPrice());

                startActivityForResult(intent, 123);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 150);
        });

        Button buttonDelete = v.findViewById(R.id.itemDeleteButton);
        buttonDelete.setOnClickListener(v1 -> {
            YoYo.with(Techniques.Tada).duration(150).playOn(buttonDelete);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Raw")
                    .setMessage("Are you sure you want to delete " + raw.getName().toUpperCase())
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null)
                    .show();
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v2 -> {
                if (databaseHelper.deleteRaw(raw.getId())) {
                    Toast.makeText(RawActivity.this , raw.getName().toUpperCase() + " waw deleted", Toast.LENGTH_LONG).show();
                    setData();
                    alertDialog.dismiss();
                }
            });


        });

        TextView textView = v.findViewById(R.id.infl2PriceText);
        textView.setText(raw.getPrice() + "Dz/" + raw.getUnit());
        LinearLayout linearLayout = findViewById(R.id.rawScrollLayout);
        linearLayout.addView(v);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        layoutParams.setMargins(0, 5, 0, 0);
        v.setLayoutParams(layoutParams);


    }

    @Override
    public void onBackPressed() {
        RawActivity.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void OnclickBack(View view) {
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.rawBackButton));
        onBackPressed();
    }

    public void OnclickAdd2(View view) {
        YoYo.with(Techniques.Pulse).duration(150).playOn(view);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, AddNewRaw.class);
            startActivityForResult(intent, 123);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, 150);
    }

}
