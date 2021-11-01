package com.example.samah.victo;

import android.database.Cursor;
import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.samah.victo.classes.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CalculatorActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    LinkedHashMap<String, Integer> AllProduct;
    LinkedHashMap<Integer, Float> AllProductWeight;
    LinkedHashMap<Integer, Float> AllProductMaxPrice;
    LinkedHashMap<String, Float> cProductQuantity;
    private LinkedList<String> list;

    private boolean editMode;
    private int proID;
    private Map<Integer, Float> quantityMap;
    private Map<Integer, Float> priceMap;
    private Map<Integer, Button> validButtonsMap;
    private Map<Integer, Boolean> validMap;
    private Button validButton;
    private boolean same;
    private float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        databaseHelper = new DatabaseHelper(this);
        AllProduct = new LinkedHashMap<>();
        quantityMap = new HashMap<>();
        priceMap = new HashMap<>();
        validButtonsMap = new HashMap<>();
        validMap = new HashMap<>();
        cProductQuantity = new LinkedHashMap<>();
        AllProductWeight= new LinkedHashMap<>();
        AllProductMaxPrice= new LinkedHashMap<>();

        list = new LinkedList<>();
        list.add("Choose a Product");
        validButton = findViewById(R.id.calValidButton);
        validButton.setEnabled(false);

        findViewById(R.id.calHeaderLayout).post(new Runnable() {
            @Override
            public void run() {
                double height = findViewById(R.id.calHeaderLayout).getHeight();
                double height2 = findViewById(R.id.resultPricePerKgLabel).getHeight();
                findViewById(R.id.calLinearLayout).setPadding(10, (int) height + 15, 10, 5);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.calScrollView).getLayoutParams();
                layoutParams.setMargins(0, 0, 0, (int) (height2 + 25));
                findViewById(R.id.calScrollView).setLayoutParams(layoutParams);
            }
        });

        Cursor cursor = databaseHelper.getAllData("PRODUCTS");
        if (cursor != null && cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                float maxPrice = cursor.getFloat(2);
                float weight = cursor.getFloat(3);
                AllProduct.put(name+"("+maxPrice+"Dz - " + weight +"Kg)", id);
                AllProductWeight.put(id, weight);
                AllProductMaxPrice.put(id, maxPrice);
                list.add(name+"("+maxPrice+"Dz - " + weight +"Kg)");
            }
        }
        cursor.close();


        androidx.appcompat.widget.AppCompatSpinner spinner = findViewById(R.id.calSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                ((TextView) v).setTextSize(16);
//                v.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.style_spinner));
                return v;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    validButton.setEnabled(false);
                    return;
                }
                validButton.setEnabled(true);
                proID = AllProduct.get(spinner.getSelectedItem().toString());
                InitiateAddMethods(proID);
                Calculate();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        if (getIntent().hasExtra("CAL")) {
            editMode = true;
            Bundle bundle = getIntent().getExtras();
            proID = bundle.getInt("id");
            float maxPrice = bundle.getFloat("maxPrice");
            float weight = bundle.getFloat("weight");
            proID = bundle.getInt("id");
            String proName = bundle.getString("name");
            spinner.setSelection(spinnerAdapter.getPosition(proName +"("+maxPrice+"Dz - " + weight +"Kg)"));
        }
    }

    private void InitiateAddMethods(int proID) {
        AppCompatSpinner spinner = findViewById(R.id.calSpinner);
        LinearLayout linearLayout = findViewById(R.id.calNamesLayout);
        LinearLayout layout = findViewById(R.id.calLinearLayout);
        layout.removeAllViews();
        layout.addView(spinner);
        layout.addView(linearLayout);

        // COMPOSITION.rawID,  rawQuantity, RAW.rawName, RAW.rawUnit
        quantityMap.clear();
        priceMap.clear();
        Cursor cursor = databaseHelper.getComposition(proID);
        same = true;
        if (cursor != null && cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                int rawID = cursor.getInt(0);
                float rawQuantity = cursor.getFloat(1);
                String rawName = cursor.getString(2);
                String rawUnit = cursor.getString(3);
                float rawPrice = cursor.getFloat(4);
                quantityMap.put(rawID, rawQuantity);
                priceMap.put(rawID, rawPrice);
                if(!rawUnit.equals("Kg")) same = false;
                ConstraintLayout v = (ConstraintLayout) getLayoutInflater().inflate(R.layout.inflator_cal, null);
                layout.addView(v);
                InitiateAddViewMethods(v, proID,  rawID, rawName, rawUnit);
                setParam(v);
            }
            cursor.close();
        }
        cursor.close();


    }

    private void InitiateAddViewMethods(ConstraintLayout v, int proID, int rawID, String rawName, String rawUnit) {
        final boolean[] green = {true, true};
        final float[] rawQuantity = {quantityMap.get(rawID)};
        final float[] rawPrice = {priceMap.get(rawID)};
        validMap.put(rawID, true);
        validButton.setBackground(getDrawable(R.drawable.style_all_done_green_button));
        TextView calItemName = v.findViewById(R.id.calInflItemName);
        calItemName.setText(rawName + "(" + rawUnit + ")");

        EditText calInflPrice = v.findViewById(R.id.calInflPrice);
        calInflPrice.setText(rawPrice[0] + "");
        EditText calInflQuantity = v.findViewById(R.id.calInflQuantity);
        calInflQuantity.setText(rawQuantity[0] + "");
        Button calInflvalidButton = v.findViewById(R.id.calInflvalidButton);
        validButtonsMap.put(rawID, calInflvalidButton);
        calInflQuantity.addTextChangedListener(new TextWatcher() {
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
                        float x = Float.valueOf(calInflQuantity.getText().toString());
                        if (x != rawQuantity[0]) {
                            calInflvalidButton.setBackground(getDrawable(R.drawable.style_done_gray_button));
                            green[0] = false;
                            validMap.put(rawID, false);
                            validButton.setBackground(getDrawable(R.drawable.style_all_done_gray_button));
                        } else {
                            green[0] = true;
                            if (green[1]) {
                                validMap.put(rawID, true);
                                calInflvalidButton.setBackground(getDrawable(R.drawable.style_done_green_button));
                                validButton.setBackground(getDrawable(R.drawable.style_all_done_green_button));
                                for (int id : validMap.keySet()) {
                                    if(!validMap.get(id)) {
                                        validButton.setBackground(getDrawable(R.drawable.style_all_done_gray_button));
                                        break;
                                    }
                                }
                            }
                        }
                        quantityMap.put(rawID, x);

                        Calculate();
                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        calInflPrice.addTextChangedListener(new TextWatcher() {
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
                        float x = Float.valueOf(calInflPrice.getText().toString());

                        if (x != rawPrice[0]) {
                            calInflvalidButton.setBackground(getDrawable(R.drawable.style_done_gray_button));
                            green[1] = false;
                            validMap.put(rawID, false);
                            validButton.setBackground(getDrawable(R.drawable.style_all_done_gray_button));
                        } else {
                            green[1] = true;
                            if (green[0]) {
                                validMap.put(rawID, true);
                                calInflvalidButton.setBackground(getDrawable(R.drawable.style_done_green_button));
                                validButton.setBackground(getDrawable(R.drawable.style_all_done_green_button));
                                for (int id : validMap.keySet()) {
                                    if(!validMap.get(id)) {
                                        validButton.setBackground(getDrawable(R.drawable.style_all_done_gray_button));
                                        break;
                                    }
                                }
                            }
                        }
                        priceMap.put(rawID, x);
                        Calculate();

                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        calInflvalidButton.setOnClickListener(v1 -> {
            if(calInflPrice.getText().length()<1 || calInflPrice.getText().toString().equals(".")){
                calInflPrice.setText(rawPrice[0]+"");
            }
            if(calInflQuantity.getText().length()<1 || calInflQuantity.getText().toString().equals(".")) {
                calInflQuantity.setText(rawQuantity[0]+"");
            }
            if(validMap.get(rawID)) { return; }

            YoYo.with(Techniques.Tada).duration(150).playOn(calInflvalidButton);

            calInflvalidButton.setBackground(getDrawable(R.drawable.style_done_green_button));
            rawQuantity[0] = quantityMap.get(rawID);
            validMap.put(rawID, true);
            rawPrice[0] = priceMap.get(rawID);
            green[0] = true;
            green[1] = true;
            validButton.setBackground(getDrawable(R.drawable.style_all_done_green_button));
            for (boolean va : validMap.values()) {
                if(!va) {
                    validButton.setBackground(getDrawable(R.drawable.style_all_done_gray_button));
                    break;
                }
            }
            databaseHelper.editPriceRaw(rawID,  rawPrice[0] );
            databaseHelper.updateComposition(proID, rawID, rawQuantity[0]);

        });


    }

    public void Calculate() {
        float price = 0;
        float weight = 0;
        float proPrice = 0;
        for(int j: priceMap.keySet()){
            price += priceMap.get(j)*quantityMap.get(j);
            weight += quantityMap.get(j);
        }
        TextView resultTPriceLabel = findViewById(R.id.resultTPriceLabel);
        TextView resultPricePerKgLabel = findViewById(R.id.resultPricePerKgLabel);
        TextView resultPriceLabel = findViewById(R.id.resultPriceLabel);
            TextView resultTWeightLabel = findViewById(R.id.resultTWeightLabel);
        resultTPriceLabel.setText(new DecimalFormat("##.#").format(price)+"Dz");

        if(same){
            float kgPrice = price/weight;
            resultPricePerKgLabel.setText(new DecimalFormat("##.#").format(kgPrice) +"Dz");
            resultTWeightLabel.setText(weight+"Kg");
            proPrice = AllProductWeight.get(proID)*kgPrice;
            resultPriceLabel.setText(new DecimalFormat("##.#").format(proPrice)+"Dz");
            if(proPrice > AllProductMaxPrice.get(proID)){
                resultPriceLabel.setTextColor(Color.RED);
            }else{
                resultPriceLabel.setTextColor(Color.parseColor("#00904A"));
            }

        }else{
            resultPricePerKgLabel.setText("-");
            resultTWeightLabel.setText("-");
        }




    }






    @Override
    public void onBackPressed() {
        CalculatorActivity.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    public void OnclickBack(View view) {
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.calBackButton));
        onBackPressed();
    }


    private void setParam(View v) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        layoutParams.setMargins(0, 7, 0, 0);
        v.setLayoutParams(layoutParams);

    }

    public void validOnClick(View view) {
        for(Button button: validButtonsMap.values()){
            button.performClick();
        }
    }
}
