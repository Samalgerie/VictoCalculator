package com.example.samah.victo.Product;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.samah.victo.R;
import com.example.samah.victo.classes.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.LinkedList;


public class AddNewProduct extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    LinkedHashMap<Integer, String> AllRawUnit;
    LinkedHashMap<String, Integer> AllRaw;
    LinkedHashMap<String, Float> remainRaw;
    private LinkedList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        databaseHelper = new DatabaseHelper(this);
        remainRaw = new LinkedHashMap<>();
        AllRaw = new LinkedHashMap<>();
        AllRawUnit = new LinkedHashMap<>();
        list = new LinkedList<>();
        list.add("Choose an Item");

        Cursor cursor = databaseHelper.getAllData("RAW");
        if (cursor != null && cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String unit = cursor.getString(2);
                AllRaw.put(name, id);
                AllRawUnit.put(id, unit);
                list.add(name);
            }
        }
        cursor.close();

        databaseHelper = new DatabaseHelper(this);
        LinearLayout layout = findViewById(R.id.productAddLinearLayout);
        ConstraintLayout v = (ConstraintLayout) getLayoutInflater().inflate(R.layout.inflator_raw1, null);
        layout.addView(v);
        InitiateAddViewMethods(v);


        findViewById(R.id.productHeaderLayout).post(new Runnable() {
            @Override
            public void run() {
                double height = findViewById(R.id.productHeaderLayout).getHeight();
                double height2 = findViewById(R.id.productAddAddButton).getHeight();
                findViewById(R.id.productAddLinearLayout).setPadding(10, (int) height + 15, 10, 5);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.productAddScrollView).getLayoutParams();
                layoutParams.setMargins(0, 0, 0, (int) (height2 + 25));
                findViewById(R.id.productAddScrollView).setLayoutParams(layoutParams);

            }
        });

    }

    private void InitiateAddViewMethods(ConstraintLayout v) {
        androidx.appcompat.widget.AppCompatSpinner spinner = v.findViewById(R.id.Inf1Spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setBackground(Drawable.createFromPath("R.drawable.item_text_blue"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                ((TextView) v).setTextSize(15);
//                v.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.style_spinner));
                return v;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinnerAdapter.notifyDataSetChanged();
        EditText quantityText = v.findViewById(R.id.infl1PriceText);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    quantityText.setText("");
                    quantityText.setEnabled(false);
                    quantityText.setHint("Quantity");
                    return;
                }
                quantityText.setEnabled(true);
                String item = spinner.getSelectedItem().toString();
                quantityText.setHint("quantity/" + AllRawUnit.get(AllRaw.get(item)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        Button rawAddButton = v.findViewById(R.id.infl1RawAddButton);
        rawAddButton.setOnClickListener(v1 -> {
            //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
            YoYo.with(Techniques.Tada).duration(350).playOn(rawAddButton);
            if (quantityText.getText().length() == 0) return;

            LinearLayout layout = findViewById(R.id.productAddLinearLayout);
            String item = spinner.getSelectedItem().toString();
            layout.removeView(v);
            ConstraintLayout v2 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.inflator_raw2, null);
            layout.addView(v2);

            SetRemoveViewMethods(spinnerAdapter, v2, spinner.getSelectedItem().toString(), quantityText.getText().toString());
            remainRaw.put(item, Float.parseFloat(quantityText.getText().toString()));
            spinnerAdapter.remove(item);
            quantityText.getText().clear();
            spinner.setSelection(0);
            layout.addView(v);

            setParam(v);
            setParam(v2);
            ScrollView scrollView = findViewById(R.id.productAddScrollView);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

        });

    }

    private void SetRemoveViewMethods(ArrayAdapter<String> spinnerAdapter, ConstraintLayout v2, String item, String quantity) {
        TextView rawName = v2.findViewById(R.id.infl2RawName);
        EditText quantityText = v2.findViewById(R.id.infl2PriceText);
        Button rawRemoveButton = v2.findViewById(R.id.infl2RawRemoveButton);

        rawName.setText(item);
        quantityText.setText(quantity);
        quantityText.setHint("quantity/" + AllRawUnit.get(AllRaw.get(item)));
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        float x = Float.valueOf(quantityText.getText().toString());
                        remainRaw.put(item, x);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        });
        rawRemoveButton.setOnClickListener(v1 -> {
            //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
            YoYo.with(Techniques.Tada).duration(350).playOn(rawRemoveButton);
            LinearLayout layout = findViewById(R.id.productAddLinearLayout);
            remainRaw.remove(item);
            layout.removeView(v2);
            spinnerAdapter.add(item);

        });


    }

    @Override
    public void onBackPressed() {
        AddNewProduct.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void OnclickBack(View view) {
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.productAddBackButton));

        onBackPressed();
    }

    public void onClickAddEditProduct(View view) {
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.productAddAddButton));

        EditText nameEditView = findViewById(R.id.nameAddEditView);
        EditText maxPriceAddEditView = findViewById(R.id.maxPriceAddEditView);
        EditText weightAddEditView = findViewById(R.id.weightAddEditView);
        String proName = nameEditView.getText().toString().toUpperCase();
        float maxPrice = Float.parseFloat(maxPriceAddEditView.getText().toString());
        float weight = Float.parseFloat(weightAddEditView.getText().toString());


        if (nameEditView.getText().equals("") || nameEditView.getText().length() < 3) return;
        Intent data = new Intent();
        if (databaseHelper.insertProduct(proName, maxPrice, weight)) {
            Cursor cursor = databaseHelper.getDataFromString("PRODUCTS", "proName", nameEditView.getText().toString().toUpperCase());
            if (cursor != null && cursor.getCount() != 0) {
                if ((cursor.moveToNext())) {
                    int id = cursor.getInt(0);
                    boolean done = true;
                    for (String item : remainRaw.keySet()) {
                        if (!databaseHelper.insertComposition(id, AllRaw.get(item), remainRaw.get(item))) {
                            done = false;
                            break;
                        }
                    }
                    if (done) {
                        Toast.makeText(this, nameEditView.getText().toString().toUpperCase() + " was added to database!", Toast.LENGTH_LONG).show();
                        data.putExtra("ok", 1);
                        setResult(RESULT_OK, data);
                        finish();
                        return;
                    }

                }
            }
            cursor.close();
        }
        Toast.makeText(this, "Something went wrong; Check if there is another product with the same name!", Toast.LENGTH_LONG).show();
        data.putExtra("ok", 0);
        setResult(RESULT_OK, data);
        finish();
    }

    private void setParam(View v) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        layoutParams.setMargins(0, 7, 0, 0);
        v.setLayoutParams(layoutParams);

    }

}
