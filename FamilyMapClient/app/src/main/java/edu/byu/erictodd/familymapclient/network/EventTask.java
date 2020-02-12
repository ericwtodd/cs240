package edu.byu.erictodd.familymapclient.network;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;

import edu.byu.erictodd.familymapclient.model.AuthToken;
import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.network.ServerProxy;

/**
 * A simple AsyncTask that attempts to retrieve event data and populate the Model data structures
 * to serve as a local cache.
 */
public class EventTask extends AsyncTask<AuthToken, Void, EventResult> {

    public static final String TAG = "EventTask";

    /**
     * The Event Interface requires the caller to handle cases where retrieval from the
     * Server both succeeded and failed
     */
    public interface EventInterface {
        void onEventRetrievalFailure(EventResult eventResult);

        void onEventRetrievalSuccess(EventResult eventResult);
    }

    /**
     * A pointer to the parent class/activity that implements an EventTask
     */
    private EventInterface parent;

    /**
     * Default Constructor that creates a pointer to the parent activity or fragment
     * of this ASyncTask
     *
     * @param parent a pointer to the parent class/activity
     */
    public EventTask(EventInterface parent) {
        this.parent = parent;
    }

    @Override
    public EventResult doInBackground(AuthToken... currentUserTokens) {
        EventResult eventResult = ServerProxy.getInstance().getEvents(currentUserTokens[0]);
        //No events were retrieved from the server
        if (eventResult.getEvents() == null) {
            Log.e(TAG, "EVENT RESULT FROM SERVER PROXY WAS NULL");
        }
        if (eventResult.getMessage() == null && eventResult.getEvent() == null
                && eventResult.getEvents() != null) {
            Model.getInstance().populateEventData(eventResult);
        }
        return eventResult;
    }

    @Override
    public void onPostExecute(EventResult eventResult) {
        if (eventResult.getMessage() == null && eventResult.getEvent() == null
                && eventResult.getEvents() != null) {
            parent.onEventRetrievalSuccess(eventResult);
        } else {
            parent.onEventRetrievalFailure(eventResult);
        }
    }
}
