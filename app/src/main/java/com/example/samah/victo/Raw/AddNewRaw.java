package com.example.samah.victo.Raw;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.samah.victo.R;
import com.example.samah.victo.classes.DatabaseHelper;


public class AddNewRaw extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    boolean editMode = false;
    int id =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_raw);
        databaseHelper = new DatabaseHelper(this);
        if (getIntent().hasExtra("Edit")) {
            editMode = true;
            Bundle bundle = getIntent().getExtras();
            id = bundle.getInt("id");
            String  name = bundle.getString("name");
            float price = bundle.getFloat("price");
            Button button = findViewById(R.id.rawAddAddButton);
            button.setText("Edit");
            EditText nameView = findViewById(R.id.rawAddNameEditView);
            EditText priceView = findViewById(R.id.rawAddPriceEditView);
            nameView.setText(name);
            priceView.setText(price+"");
        }

    }


    @Override
    public void onBackPressed() {
        AddNewRaw.this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void OnclickBack(View view) {
        //Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.rawAddBackButton));

        onBackPressed();
    }

    public void onClickAddEditRaw(View view) {
        YoYo.with(Techniques.Pulse).duration(150).playOn(findViewById(R.id.rawAddAddButton));
        EditText nameEditView = findViewById(R.id.rawAddNameEditView);
        EditText priceEditView = findViewById(R.id.rawAddPriceEditView);
        androidx.appcompat.widget.AppCompatSpinner spinner = findViewById(R.id.rawAddSpinner);
        String unit = spinner.getSelectedItem().toString();

        if (nameEditView.getText().equals("") || nameEditView.getText().length() < 3 || priceEditView.getText().equals(""))
            return;
        String price = priceEditView.getText().toString();
        Intent data = new Intent();
        if(!editMode) {
            if (databaseHelper.insertRaw(nameEditView.getText().toString(), Float.parseFloat(price), unit)) {
                Toast.makeText(this, nameEditView.getText().toString().toUpperCase() + " was added to database!", Toast.LENGTH_LONG).show();
                data.putExtra("ok", 1);
            } else {
                Toast.makeText(this, "Something went wrong; Check if there is another item with the same name!", Toast.LENGTH_LONG).show();
                data.putExtra("ok", 0);
            }
        }else{
            if (databaseHelper.editRaw(id, nameEditView.getText().toString(), Float.parseFloat(price), unit)) {
                Toast.makeText(this, nameEditView.getText().toString().toUpperCase() + " was edited!", Toast.LENGTH_LONG).show();
                data.putExtra("ok", 1);
            } else {
                Toast.makeText(this, "Something went wrong; Check if there is another item with the same name!", Toast.LENGTH_LONG).show();
                data.putExtra("ok", 0);

            }

        }
        setResult(RESULT_OK, data);
        finish();
    }


}
