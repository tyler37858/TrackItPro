package com.example.trackitpro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle;
    private TextView tvDetailDate;
    private EditText etNotes;
    private Button btnSaveNotes;

    private EventRepository eventRepository;
    private long eventId = -1;

    // runs when user opens event detail
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // setup toolbar with back arrow
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        etNotes = findViewById(R.id.etNotes);
        btnSaveNotes = findViewById(R.id.btnSaveNotes);

        eventRepository = new EventRepository(this);

        // get values passed in
        eventId = getIntent().getLongExtra("eventId", -1);
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");

        if (title != null) tvDetailTitle.setText(title);
        if (date != null) tvDetailDate.setText(date);

        // load saved notes
        if (eventId != -1) {
            etNotes.setText(eventRepository.getEventNotes(eventId));
        }

        // save notes button
        btnSaveNotes.setOnClickListener(v -> saveNotes());
    }

    // saves notes into database
    private void saveNotes() {
        if (eventId == -1) {
            Toast.makeText(this, "Event id missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = etNotes.getText().toString();
        boolean updated = eventRepository.updateEventNotes(eventId, notes);

        if (updated) {
            Toast.makeText(this, "Notes saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not save notes", Toast.LENGTH_SHORT).show();
        }
    }

    // close db when leaving
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventRepository != null) eventRepository.close();
    }
}


