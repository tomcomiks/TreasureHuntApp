package com.tommelani.treasurehunt.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.helpers.PermissionHelper;

/**
 * This is the entry point of the application.
 * It displays two buttons: one to start hunting treasures, one to reset the game.
 */
public class MainActivity extends Activity {

    //public final String TAG = "MainActivity";

    public final static String SESSION = "Session";
    public final static String SESSION_MILESTONE_ID = "milestone_id";
    public final static String SESSION_PASSED_MILESTONES = "passed_milestone_list";
    public final static String SESSION_FOUND_TREASURES = "found_treasure_list";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    /**
     * Load the main layout
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Load layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get shared preferences
        preferences = this.getSharedPreferences(SESSION, MODE_PRIVATE);

        //Request permissions
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this);
        }


    }

    /**
     * Launch the actions for Play and Clear buttons
     * @param v the view
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                Intent i = new Intent(this, TrackActivity.class);
                startActivity(i);
                break;
            case R.id.btn_clear:
                editor = preferences.edit();
                editor.clear();
                editor.apply();
                break;
        }
    }


}
