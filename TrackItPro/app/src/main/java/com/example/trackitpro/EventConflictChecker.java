package com.example.trackitpro;

import java.util.List;

public class EventConflictChecker {

    //check to see if new event is shceuduled at same time as existing event
    public static boolean hasConflict(long newEventTime, List<Event> existingEvents)
    {
        for (Event event : existingEvents)
        {
            if (event.getTriggerTimeMillis() == newEventTime)
                return true;
        }

        //no conflict found
        return false;
    }
}
