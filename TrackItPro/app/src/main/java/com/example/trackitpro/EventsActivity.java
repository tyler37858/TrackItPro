package com.example.trackitpro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private FloatingActionButton fabAddEvent;
    private EventAdapter adapter;

    private final List<Event> events = new ArrayList<>();
    private EventRepository eventRepository;

    private long userId = -1;

    private ActivityResultLauncher<Intent> addEventLauncher;
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    // runs when the screen first opens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // create the repository
        eventRepository = new EventRepository(this);

        // get the logged in users id from login screen
        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            // if we dont have a user then go back
            Toast.makeText(
                    this,
                    "No user found. Go back to login.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // connect views from xml
        rvEvents = findViewById(R.id.rvEvents);
        fabAddEvent = findViewById(R.id.fabAddEvent);

        //handle the notification permission result
        notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted)
                {
                    Toast.makeText(
                            this,
                            "Notifications are disabled",
                            Toast.LENGTH_SHORT).show();
                }
            }
        );

        //check notifications permissions when the event screen opens
        checkNotificationPermission();

        // load events for this user from database
        events.clear();
        events.addAll(eventRepository.getEventsForUser(userId));

        // setup recycler adapter and click listeners
        adapter = new EventAdapter(events, new EventAdapter.OnEventClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteEventAt(position);
            }

            @Override
            public void onItemClick(int position) {
                openEventDetailAt(position);
            }
        });

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);

        // listens for result coming back from add event screen
        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;

                    String title = result.getData().getStringExtra("title");
                    String date = result.getData().getStringExtra("date");
                    long triggerTimeMillis =
                            result.getData().getLongExtra("triggerTimeMillis", -1);

                    // make sure we got values back
                    if (title == null || date == null || triggerTimeMillis == -1) return;

                    // save new event into database
                    long newEventId =
                            eventRepository.addEvent(userId, title, date, triggerTimeMillis);

                    if (newEventId != -1) {
                        // add event to list and update ui
                        Event newEvent = new Event(
                                newEventId, userId, title, date, triggerTimeMillis,"");
                        events.add(0, newEvent);
                        adapter.notifyItemInserted(0);
                        rvEvents.scrollToPosition(0);

                        // schedule the alarm so notification pops later
                        AlarmScheduler.scheduleEventAlarm(
                                this, newEventId, userId, title, date, triggerTimeMillis);
                    } else {
                        Toast.makeText(
                                this,
                                "Could not save event",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // plus button opens add event screen
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            addEventLauncher.launch(intent);
        });
    }

    //check notification permission on version 13 and newer
    private void checkNotificationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
            {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // deletes event from database and list
    private void deleteEventAt(int position) {
        if (position < 0 || position >= events.size())
            return;

        Event event = events.get(position);
        boolean ok = eventRepository.deleteEvent(event.getId());
        if (ok) {
            // cancel alarm too or it will still notify
            AlarmScheduler.cancelEventAlarm(this, event.getId());

            events.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    // opens detail screen for selected event
    private void openEventDetailAt(int position) {
        if (position < 0 || position >= events.size())
            return;

        Event event = events.get(position);
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("date", event.getDate());
        startActivity(intent);
    }

    // close repository when screen is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventRepository != null)
            eventRepository.close();
    }
}



