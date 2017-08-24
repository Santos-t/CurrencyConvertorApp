package com.theo.currencyconvertorapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerl;
    private Spinner spinnerr;
    private EditText box_left;
    private EditText box_right;
    private Button switch_button;
    //Boolean used in order to know if the change of the box was made by the app or by the user
    private boolean app_input;
    private CurrencyRates rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app_input = false;

        spinnerl = (Spinner)findViewById(R.id.spinnerLeft);
        spinnerl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (app_input)
                    app_input = false;
                else
                    convert_left();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spinnerr = (Spinner)findViewById(R.id.spinnerRight);
        spinnerr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (app_input)
                    app_input = false;
                else
                    convert_left();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

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
        CalendarView calendar = (CalendarView)findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String m = month < 10 ? "0" + month : "" + month;
                String d = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                getRates(""+ year + "-" + m + "-" + d);
                convert_left();
            }
        });
        getRates("latest");
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

        double amount = Double.parseDouble(box_left.getText().toString());
        if (left_id == right_id)
        {
            app_input = true;
            box_right.setText(Double.toString(amount));
            return;
        }


        double left_value = left_id == 0 ? 1.0 : rates.rates.get(spinnerl.getSelectedItem().toString());
        double right_value = right_id == 0 ? 1.0 : rates.rates.get(spinnerr.getSelectedItem().toString());

        double result = amount / left_value * right_value;
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

        double amount = Double.parseDouble(box_right.getText().toString());
        if (left_id == right_id)
        {
            app_input = true;
            box_left.setText(Double.toString(amount));
            return;
        }


        double left_value = left_id == 0 ? 1.0 : rates.rates.get(spinnerl.getSelectedItem().toString());
        double right_value = right_id == 0 ? 1.0 : rates.rates.get(spinnerr.getSelectedItem().toString());

        double result = amount / right_value * left_value;
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
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getRates(String date)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.fixer.io/" + date;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Gson g = new Gson();
                        CurrencyRates rates = g.fromJson(response, CurrencyRates.class);
                        setRates(rates);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error);
            }
        });
        queue.add(stringRequest);
    }

    public void setRates(CurrencyRates rates)
    {
        String[] items = new String[rates.rates.keySet().size() + 1];
        int i = 1;
        items[0] = "EUR";
        for (String s : rates.rates.keySet())
        {
            items[i++] = s;
            System.out.println(s);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerl.setAdapter(adapter);
        spinnerr.setAdapter(adapter);
        this.rates = rates;
    }



}
