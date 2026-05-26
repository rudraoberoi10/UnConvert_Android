package com.example.unconvert;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputValue;
    private AutoCompleteTextView fromUnitDropdown, toUnitDropdown;
    private TextView textResult, textGreeting, matrixLogs, historyLogs;
    private MaterialButton btnSwap;

    private final String[] lengthUnits = {"Kilometers", "Miles", "Meters", "Centimeters", "Inches"};
    private final StringBuilder runtimeHistory = new StringBuilder();
    private int historyCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        fromUnitDropdown = findViewById(R.id.fromUnitAutoComplete);
        toUnitDropdown = findViewById(R.id.toUnitAutoComplete);
        textResult = findViewById(R.id.textResult);
        btnSwap = findViewById(R.id.btnSwap);
        textGreeting = findViewById(R.id.textGreeting);
        matrixLogs = findViewById(R.id.matrixLogs);
        historyLogs = findViewById(R.id.historyLogs);

        initializeGreeting();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, lengthUnits);
        fromUnitDropdown.setAdapter(adapter);
        toUnitDropdown.setAdapter(adapter);

        fromUnitDropdown.setText(lengthUnits[2], false); // Meters
        toUnitDropdown.setText(lengthUnits[4], false);   // Inches

        setupListeners();
    }

    private void initializeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = (hour < 12) ? "Good Morning ✨" : (hour < 17) ? "Good Afternoon ☀️" : "Good Evening 🌙";
        textGreeting.setText(greeting);
    }

    private void setupListeners() {
        inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performConversion(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fromUnitDropdown.setOnItemClickListener((parent, view, position, id) -> performConversion(true));
        toUnitDropdown.setOnItemClickListener((parent, view, position, id) -> {
            performConversion(true);
            logHistoryEntry(); // Append tracking data elements when explicitly selected
        });

        btnSwap.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String currentFrom = fromUnitDropdown.getText().toString();
            String currentTo = toUnitDropdown.getText().toString();

            fromUnitDropdown.setText(currentTo, false);
            toUnitDropdown.setText(currentFrom, false);

            performConversion(true);
            logHistoryEntry();
        });
    }

    private void performConversion(boolean runAnimation) {
        String inputStr = inputValue.getText().toString().trim();

        if (inputStr.isEmpty() || inputStr.equals(".")) {
            textResult.setText("0.00");
            matrixLogs.setText("Enter a value above to view parallel matrix calculations...");
            return;
        }

        try {
            double value = Double.parseDouble(inputStr);
            String fromUnit = fromUnitDropdown.getText().toString();
            String toUnit = toUnitDropdown.getText().toString();

            double meters = convertToMeters(value, fromUnit);
            double result = convertFromMeters(meters, toUnit);

            String formattedResult = (result == (long) result) ? String.format(Locale.US, "%d", (long) result) : String.format(Locale.US, "%.4f", result);
            textResult.setText(formattedResult);

            if (runAnimation) {
                AlphaAnimation fadeIn = new AlphaAnimation(0.3f, 1.0f);
                fadeIn.setDuration(250);
                textResult.startAnimation(fadeIn);
            }

            // Populate Feature 1: The Live Matrix Display Data Pipeline
            calculateParallelMatrix(value, fromUnit);

        } catch (NumberFormatException e) {
            textResult.setText("Error");
        }
    }

    private void calculateParallelMatrix(double value, String fromUnit) {
        double baseMeters = convertToMeters(value, fromUnit);
        StringBuilder matrixBuilder = new StringBuilder();

        for (String targetUnit : lengthUnits) {
            if (!targetUnit.equals(fromUnit)) {
                double convertedVal = convertFromMeters(baseMeters, targetUnit);
                String formatted = (convertedVal == (long) convertedVal) ? String.format(Locale.US, "%d", (long) convertedVal) : String.format(Locale.US, "%.3f", convertedVal);
                matrixBuilder.append("• ").append(formatted).append(" ").append(targetUnit).append("\n");
            }
        }
        matrixLogs.setText(matrixBuilder.toString().trim());
    }

    private void logHistoryEntry() {
        String inputStr = inputValue.getText().toString().trim();
        if (inputStr.isEmpty()) return;

        String entry = "• " + inputStr + " " + fromUnitDropdown.getText().toString() + " → " + textResult.getText().toString() + " " + toUnitDropdown.getText().toString() + "\n";

        // Prevent infinite string building blocks accumulation tracking locally
        if (historyCounter >= 4) {
            int firstLineBreak = runtimeHistory.indexOf("\n");
            if (firstLineBreak != -1) {
                runtimeHistory.delete(0, firstLineBreak + 1);
            }
        } else {
            historyCounter++;
        }

        runtimeHistory.append(entry);
        historyLogs.setText(runtimeHistory.toString().trim());
    }

    private double convertToMeters(double value, String unit) {
        switch (unit) {
            case "Kilometers": return value * 1000.0;
            case "Miles":      return value * 1609.344;
            case "Centimeters":return value / 100.0;
            case "Inches":     return value * 0.0254;
            default:           return value;
        }
    }

    private double convertFromMeters(double meters, String unit) {
        switch (unit) {
            case "Kilometers": return meters / 1000.0;
            case "Miles":      return meters / 1609.344;
            case "Centimeters":return meters * 100.0;
            case "Inches":     return meters / 0.0254;
            default:           return meters;
        }
    }
}