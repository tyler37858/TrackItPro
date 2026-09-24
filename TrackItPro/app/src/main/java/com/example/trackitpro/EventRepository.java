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

    //check if there is a conflicting event
    public boolean hasEventConflict(long userId, long triggerTimeMillis)
    {
        List<Event> existingEvents = getEventsForUser(userId);

        return EventConflictChecker.hasConflict(triggerTimeMillis, existingEvents);
    }

    //add a new event to the db for selected user
    public long addEvent(long userId, String title, String date, long triggerTimeMillis)
    {
        return dbHelper.addEvent(userId,title,date,triggerTimeMillis);
    }

    //update selected photo uri
    public boolean updateEventImageUri(long eventId, String imageUri)
    {
        return dbHelper.updateEventImageUri(eventId, imageUri);
    }

    //get saved uri for image for event
    public String getEventImageUir(long eventId)
    {
        return dbHelper.getEventImageUri(eventId);
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
