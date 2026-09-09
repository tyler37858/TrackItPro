package com.example.trackitpro;

public class Event {

    private final long id;
    private final long userId;
    private final String title;
    private final String date;
    private final long triggerTimeMillis;
    private final String notes;


    public Event(long id, long userId, String title, String date, long triggerTimeMillis, String notes)
    {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.triggerTimeMillis = triggerTimeMillis;
        this.notes = notes;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public long getTriggerTimeMillis() { return triggerTimeMillis; }
    public String getNotes() { return notes; }
}


