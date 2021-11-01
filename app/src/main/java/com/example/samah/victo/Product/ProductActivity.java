package com.example.samah.victo.Product;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.samah.victo.CalculatorActivity;
import com.example.samah.victo.R;
import com.example.samah.victo.classes.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    Map<Integer, Product> map = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        databaseHelper = new DatabaseHelper(this);
        setData();
        findViewById(R.id.productHeaderLayout).post(new Runnable() {
            @Override
            public void run() {
                double height = findViewById(R.id.productHeaderLayout).getHeight();
                findViewById(R.id.productScrollLayout).setPadding(2, (int) height + 15, 2, 10);
            }
        });
        EditText search_edit = findViewById(R.id.search_view);

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LinearLayout layout = findViewById(R.id.productScrollLayout);
                EditText searchView = (EditText) layout.getChildAt(0);
                layout.removeAllViews();
                layout.addView(searchView);
                searchView.requestFocus();
                if(map.isEmpty()) return;
                for (int j: map.keySet()){
                    if(map.get(j).getName().toUpperCase().contains(s.toString().toUpperCase())) setRowProduct(map.get(j));
                     else if(map.get(j).getComp().toUpperCase().contains(s.toString().toUpperCase())) setRowProduct(map.get(j));


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
        LinearLayout layout = findViewById(R.id.productScrollLayout);
        EditText searchView = (EditText) layout.getChildAt(0);
        layout.removeAllViews();
        layout.addView(searchView);
        Cursor cursor = databaseHelper.getAllData("PRODUCTS");
        int j = 1;
        if (cursor != null && cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                float maxPrice = cursor.getFloat(2);
                float weight = cursor.getFloat(3);
                String comp ="";
                Cursor cursor2 = databaseHelper.getComposition(id);
                if (cursor2 != null && cursor.getCount() != 0) {
                    comp ="mmm";
                    while ((cursor2.moveToNext())) {
                        String item= cursor2.getString(2);
                        comp = comp +", "+ item ;
                    }
                    if(comp.equals("mmm")) comp = "";
                     else comp = comp.replace("mmm,", "");
                }
                Product product = new Product(id, name, comp, maxPrice, weight);
                setRowProduct(product);
                map.put(j, product);
                j++;
                cursor2.close();
            }

        }
        cursor.close();
    }


    private void setRowProduct(Product product) {
        ConstraintLayout v = (ConstraintLayout) getLayoutInflater().inflate(R.layout.inflator_items, null);
        Button button = v.findViewById(R.id.itemNameButton);
        button.setOnClickListener(v1 ->{
            YoYo.with(Techniques.Pulse).duration(150).playOn(button);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, CalculatorActivity.class);
                intent.putExtra("CAL", 1);
                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("maxPrice", product.getMaxPrice());
                intent.putExtra("weight", product.getWeight());
                startActivityForResult(intent, 123);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 150);

        } );
        button.setText(product.getName());
        Button buttonEdit = v.findViewById(R.id.itemEditButton);
        buttonEdit.setOnClickListener(v1 -> {
            YoYo.with(Techniques.Tada).duration(150).playOn(buttonEdit);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, EditProduct.class);
                intent.putExtra("Edit", 1);
                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("maxPrice", product.getMaxPrice());
                intent.putExtra("weight", product.getWeight());
                startActivityForResult(intent, 123);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 150);
        });

        Button buttonDelete = v.findViewById(R.id.itemDeleteButton);
        buttonDelete.setOnClickListener(v1 -> {
            YoYo.with(Techniques.Tada).duration(150).playOn(buttonDelete);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete " + product.getName().toUpperCase())
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null)
                    .show();
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v2 -> {
                if (databaseHelper.deleteProduct(product.getId())) {
                    setData();
                    Toast.makeText(this, product.getName().toUpperCase() + " was deleted", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                }
            });


        });

        TextView textView = v.findViewById(R.id.infl2PriceText);
        textView.setText(product.getComp());
        LinearLayout linearLayout = findViewById(R.id.productScrollLayout);
        linearLayout.addView(v);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        layoutParams.setMargins(0, 5, 0, 0);
        v.setLayoutParams(layoutParams);


    }

    @Override
    public void onBackPressed() {
        ProductActivity.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void OnclickBack(View view) {
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.productBackButton));
        onBackPressed();
    }

    public void OnclickAdd(View view) {
        YoYo.with(Techniques.Pulse).duration(150).playOn(view);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, AddNewProduct.class);
            startActivityForResult(intent, 123);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, 150);
    }

}
