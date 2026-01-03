package com.example.unconvert;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner fromUnit, toUnit;
    Button convertBtn;
    TextView resultText;

    String[] units = {"Meter", "Kilometer", "Centimeter", "Feet"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        fromUnit = findViewById(R.id.fromUnit);
        toUnit = findViewById(R.id.toUnit);
        convertBtn = findViewById(R.id.convertBtn);
        resultText = findViewById(R.id.resultText);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                units
        );

        fromUnit.setAdapter(adapter);
        toUnit.setAdapter(adapter);

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputValue.getText().toString().isEmpty()) {
                    resultText.setText("Please enter a value");
                    return;
                }

                double value = Double.parseDouble(inputValue.getText().toString());
                String from = fromUnit.getSelectedItem().toString();
                String to = toUnit.getSelectedItem().toString();

                double result = convertUnits(value, from, to);
                resultText.setText("Result: " + result);
            }
        });
    }

    private double convertUnits(double value, String from, String to) {

        // Convert everything to meters first
        double meterValue = 0;

        switch (from) {
            case "Meter":
                meterValue = value;
                break;
            case "Kilometer":
                meterValue = value * 1000;
                break;
            case "Centimeter":
                meterValue = value / 100;
                break;
            case "Feet":
                meterValue = value * 0.3048;
                break;
        }
        // Convert from meters to target unit
        switch (to) {
            case "Meter":
                return meterValue;
            case "Kilometer":
                return meterValue / 1000;
            case "Centimeter":
                return meterValue * 100;
            case "Feet":
                return meterValue / 0.3048;
        }
        return 0;
    }
}
