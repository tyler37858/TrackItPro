package com.example.trackitpro;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText etEventTitle;
    private TextView tvDate;
    private TextView tvTime;

    // stores what user picked
    private String selectedDate = null;
    private String selectedTime = null;

    // store actual numbers so we can make millis later
    private Integer pickedYear = null;
    private Integer pickedMonth = null;
    private Integer pickedDay = null;
    private Integer pickedHour24 = null;
    private Integer pickedMinute = null;

    // runs when add event screen opens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Event");
        }

        // connect views from xml
        etEventTitle = findViewById(R.id.etEventTitle);
        Button btnPickDate = findViewById(R.id.btnPickDate);
        Button btnPickTime = findViewById(R.id.btnPickTime);
        Button btnSaveEvent = findViewById(R.id.btnSaveEvent);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        // open date picker when clicked
        btnPickDate.setOnClickListener(v -> openDatePicker());

        // open time picker when clicked
        btnPickTime.setOnClickListener(v -> openTimePicker());

        // save event and go back
        btnSaveEvent.setOnClickListener(v -> saveAndReturn());
    }

    // shows date picker dialog
    private void openDatePicker() {
        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    // month starts at 0 so its weird
                    pickedYear = y;
                    pickedMonth = m;
                    pickedDay = d;

                    selectedDate = String.format(Locale.US, "%02d/%02d/%04d", (m + 1), d, y);
                    tvDate.setText(selectedDate);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    // shows time picker dialog
    private void openTimePicker() {
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, h, m) -> {
                    pickedHour24 = h;
                    pickedMinute = m;

                    // convert to 12 hour time for display
                    int hour12 = h % 12;
                    if (hour12 == 0) hour12 = 12;
                    String ampm = (h < 12) ? "AM" : "PM";

                    selectedTime = String.format(Locale.US, "%02d:%02d %s", hour12, m, ampm);
                    tvTime.setText(selectedTime);
                },
                hour,
                minute,
                false
        );

        dialog.show();
    }

    // validates input then sends data back to events screen
    private void saveAndReturn() {
        String title = etEventTitle.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate == null || pickedYear == null || pickedMonth == null || pickedDay == null) {
            Toast.makeText(this, "Pick a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTime == null || pickedHour24 == null || pickedMinute == null) {
            Toast.makeText(this, "Pick a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // make a calendar from what user picked
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, pickedYear);
        cal.set(Calendar.MONTH, pickedMonth);
        cal.set(Calendar.DAY_OF_MONTH, pickedDay);
        cal.set(Calendar.HOUR_OF_DAY, pickedHour24);
        cal.set(Calendar.MINUTE, pickedMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long triggerTimeMillis = cal.getTimeInMillis();

        // put date and time together for display/storage
        String dateTime = selectedDate + " " + selectedTime;

        // send data back to events activity
        Intent result = new Intent();
        result.putExtra("title", title);
        result.putExtra("date", dateTime);
        result.putExtra("triggerTimeMillis", triggerTimeMillis);
        setResult(RESULT_OK, result);

        // close this screen
        finish();
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


