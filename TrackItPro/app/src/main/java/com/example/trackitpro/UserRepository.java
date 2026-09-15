package com.example.trackitpro;

import android.content.Context;

public class UserRepository {

    //handle the database operations within this repository
    private final DatabaseHelper dbHelper;

    //create the repository
    public UserRepository(Context context)
    {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    //create a new user account in the dtaabase
    public long registerUser(String username, String password)
    {
        return dbHelper.registerUser(username, password);
    }

    //check username and password and return userId
    public long authenticateUser(String username, String password)
    {
        return dbHelper.authenticateUser(username, password);
    }

    //close the dbHelper when the repository finishes
    public void close()
    {
        dbHelper.close();
    }
}
