package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.Model;

/**
 * An Activity that displays information about a selected Event.
 * Contains a Map Fragment that displays and interactive map and a display
 * that shows information about the currently-selected Event.
 */
public class EventActivity extends AppCompatActivity {

    /**
     * A String that references the eventID that is to be centered on in the map fragment
     */
    public static final String EXTRA_EVENT_ID = "event_id";

    /**
     * The MapFragment that is being displayed in the event activity
     */
    private MapFragment mMapFragment;
    /**
     * The currently selected Event to be displayed and centered on in the map.
     */
    private Event mCurrentEvent;

    /**
     * Creates an intent object with an extra storing the eventID that is to be the center of the event Activity
     * @param packageContext the context in which the event activity is being called from
     * @param eventID the eventID to be centered on
     * @return an intent with the extra eventID
     */
    public static Intent newIntent(Context packageContext, String eventID) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Set the current Event from the EventActivity Intent
        String eventID = (String) getIntent().getSerializableExtra(EXTRA_EVENT_ID);
        mCurrentEvent = Model.getInstance().findEvent(eventID);
        //Start the Map Fragment Display inside the Event Activity
        FragmentManager fm = this.getSupportFragmentManager();
        mMapFragment = (MapFragment) fm.findFragmentById(R.id.eventMapFragment);
        mMapFragment = MapFragment.newInstance(mCurrentEvent.getEventID());

        fm.beginTransaction()
                .add(R.id.eventMapFragment, mMapFragment)
                .commit();
    }
}
