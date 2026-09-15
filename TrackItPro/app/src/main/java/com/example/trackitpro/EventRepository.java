package com.example.trackitpro;
import android.content.Context;
import java.util.List;

public class EventRepository {
    private final DatabaseHelper dbHelper;

    //create the repository using the application context
    public EventRepository(Context context)
    {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    //get all events that belong to user
    public List<Event> getEventsForUser(long userId)
    {
        return dbHelper.getEventsForUser(userId);
    }

    //add a new event to the db for selected user
    public long addEvent(long userId, String title, String date, long triggerTimeMillis)
    {
        return dbHelper.addEvent(userId,title,date,triggerTimeMillis);
    }

    //delete an event
    public boolean deleteEvent(long eventId)
    {
        return dbHelper.deleteEvent(eventId);
    }

    //get saved notes for a selected event
    public String getEventNotes(long eventId)
    {
        return dbHelper.getEventNotes(eventId);
    }

    //update the notes for preexisting event
    public boolean updateEventNotes(long eventId, String notes)
    {
        return dbHelper.updateEventNotes(eventId, notes);
    }

    // close the dbhelper whne repository is finished
    public void close()
    {
        dbHelper.close();
    }
}
