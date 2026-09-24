package com.example.trackitpro;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle;
    private TextView tvDetailDate;
    private EditText etNotes;
    private ImageView ivDetailImage;
    private Button btnSelectPhoto;
    private Button btnSaveNotes;

    private EventRepository eventRepository;
    private long eventId = -1;

    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;

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
        ivDetailImage = findViewById(R.id.ivDetailImage);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnSaveNotes = findViewById(R.id.btnSaveNotes);

        eventRepository = new EventRepository(this);

        //setup photo picker
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null)
                {
                    saveSelectedPhoto(uri);
                }
            });

        // get values passed in
        eventId = getIntent().getLongExtra("eventId", -1);
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");

        if (title != null) tvDetailTitle.setText(title);
        if (date != null) tvDetailDate.setText(date);

        // load saved notes
        if (eventId != -1) {
            etNotes.setText(eventRepository.getEventNotes(eventId));
            loadSavedPhoto();
        }

        //select photo button
        btnSelectPhoto.setOnClickListener(v -> openPhotoPicker());

        // save notes button
        btnSaveNotes.setOnClickListener(v -> saveNotes());
    }

    //open the photopicker for selecting images
    private void openPhotoPicker()
    {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder().setMediaType(
                ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build();

        photoPickerLauncher.launch(request);
    }

    //display the selected photo
    private void saveSelectedPhoto(Uri uri)
    {
        if (eventId == -1)
        {
            Toast.makeText(this, "Event missing Id", Toast.LENGTH_LONG).show();

            return;
        }

        boolean updated = eventRepository.updateEventImageUri(eventId, uri.toString());

        if (updated)
        {
            ivDetailImage.setImageURI(uri);

            Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Unable to save photo", Toast.LENGTH_SHORT).show();
        }
    }

    //load saved photo for event
    private void loadSavedPhoto()
    {
        String imageUri = eventRepository.getEventImageUir(eventId);

        if (imageUri != null && !imageUri.isEmpty())
        {
            Uri uri = Uri.parse(imageUri);
            ivDetailImage.setImageURI(uri);
        }
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


