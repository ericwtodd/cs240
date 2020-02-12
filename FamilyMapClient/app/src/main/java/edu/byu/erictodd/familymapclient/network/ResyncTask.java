package edu.byu.erictodd.familymapclient.network;

import android.os.AsyncTask;

import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.PersonResult;

/**
 * A simple Async Task that attempts to resync with the server and store the new data in the local cache.
 */
public class ResyncTask extends AsyncTask<Void, Void, Void> implements PersonTask.PersonInterface, EventTask.EventInterface {

    /**
     * A string containing an error message if the Resync Task failed
     */
    private String mErrorMessage;

    /**
     * The ResyncInterface requires the parent to implement
     * functionality for when resyncing with the server both succeeds and fails
     */
    public interface ResyncInterface {
        void onResyncSuccess();

        void onResyncFailure(String errorMessage);
    }

    /**
     * A pointer to the parent Activity/class that Implements a Resync Interface
     */
    private ResyncInterface parent;

    /**
     * Default Constructor that creates a pointer to the parent Activity/Fragment
     * that implements this AsyncTask
     *
     * @param parent a pointer to the parent Activity/Fragment
     */
    public ResyncTask(ResyncInterface parent) {
        this.parent = parent;
    }

    @Override
    public Void doInBackground(Void... params) {
        if (ServerProxy.getInstance().getCurrentUserToken() != null) {
            new PersonTask(this).execute(ServerProxy.getInstance().getCurrentUserToken());
            new EventTask(this).execute(ServerProxy.getInstance().getCurrentUserToken());
        }
        return null;
    }

    @Override
    public void onPersonsRetrievalSuccess(PersonResult personResult) {
        Model.getInstance().setLoadPersonSuccess(true);
    }

    @Override
    public void onPersonsRetrievalFailure(PersonResult personResult) {
        Model.getInstance().setLoadPersonSuccess(false);
        if (personResult.getMessage() != null) {
            mErrorMessage = personResult.getMessage();
        } else {
            mErrorMessage = "Failed to Re-Sync Persons. Try Again";
        }
    }

    @Override
    public void onEventRetrievalSuccess(EventResult eventResult) {
        Model.getInstance().setLoadEventSuccess(true);
        if (Model.getInstance().isLoadPersonSuccess() && Model.getInstance().isLoadEventSuccess()) {
            parent.onResyncSuccess();
        } else {
            parent.onResyncFailure(mErrorMessage);
        }
    }

    @Override
    public void onEventRetrievalFailure(EventResult eventResult) {
        Model.getInstance().setLoadEventSuccess(false);
        if (mErrorMessage == null) {
            if (eventResult.getMessage() != null) {
                mErrorMessage = eventResult.getMessage();
            } else {
                mErrorMessage = "Failed to Re-Sync Events. Try Again";
            }
        } else {
            mErrorMessage = "Failed to Re-Sync. Try Again";
        }
        parent.onResyncFailure(mErrorMessage);
    }
}
