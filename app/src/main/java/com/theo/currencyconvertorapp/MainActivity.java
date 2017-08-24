package com.theo.currencyconvertorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerl;
    private Spinner spinnerr;
    private EditText box_left;
    private EditText box_right;
    private Button switch_button;
    //Boolean used in order to know if the change of the box was made by the app or by the user
    private boolean app_input;
    private boolean about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app_input = false;
        about = false;

        spinnerl = (Spinner)findViewById(R.id.spinnerLeft);
        spinnerr = (Spinner)findViewById(R.id.spinnerRight);

        box_left = (EditText)findViewById(R.id.inputBoxLeft);
        box_left.addTextChangedListener(new TextWatcher() {

            //Do the conversion everytime the text is changed
            public void afterTextChanged(Editable s) {
                if (app_input)
                    app_input = false;
                else
                    convert_left();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        box_right = (EditText)findViewById(R.id.inputBoxRight);
        box_right.addTextChangedListener(new TextWatcher() {

            //Do the conversion everytime the text is changed
            public void afterTextChanged(Editable s) {
                if (app_input)
                    app_input = false;
                else
                    convert_right();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        switch_button = (Button)findViewById(R.id.button);
        switch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_currencies();
                convert_left();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerl.setAdapter(adapter);
        spinnerr.setAdapter(adapter);
    }

    //Convert the amount when the left box is changed
    public void convert_left()
    {
        //If the box is empty then empty the other box
        if (box_left.getText().toString().equals("")) {
            app_input = true;
            box_right.setText("");
            return;
        }

        long left_id = spinnerl.getSelectedItemId();
        long right_id = spinnerr.getSelectedItemId();
        String[] values = getResources().getStringArray(R.array.currencies_value);

        double amount = Double.parseDouble(box_left.getText().toString());
        double left_value = Double.parseDouble(values[(int)left_id]);
        double right_value = Double.parseDouble(values[(int)right_id]);

        double result = amount * left_value / right_value;
        app_input = true;
        box_right.setText(Double.toString(result));
    }

    //Convert the amount when the right box is changed
    public void convert_right()
    {
        //If the box is empty then empty the other box
        if (box_right.getText().toString().equals("")) {
            app_input = true;
            box_left.setText("");
            return;
        }

        long left_id = spinnerl.getSelectedItemId();
        long right_id = spinnerr.getSelectedItemId();
        String[] values = getResources().getStringArray(R.array.currencies_value);

        double amount = Double.parseDouble(box_right.getText().toString());
        double left_value = Double.parseDouble(values[(int)left_id]);
        double right_value = Double.parseDouble(values[(int)right_id]);

        double result = amount * right_value / left_value;
        app_input = true;
        box_left.setText(Double.toString(result));
    }

    public void switch_currencies()
    {
        int left_id = spinnerl.getSelectedItemPosition();
        int right_id = spinnerr.getSelectedItemPosition();
        spinnerl.setSelection(right_id);
        spinnerr.setSelection(left_id);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.about:
                about = true;
                setContentView(R.layout.fragment_about);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (about)
        {
            setContentView(R.layout.activity_main);
            about = false;
        }
        else
            super.onBackPressed();

    }


}
