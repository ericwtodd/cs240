package edu.byu.erictodd.familymapclient.network;

import android.os.AsyncTask;

import edu.byu.erictodd.familymapclient.model.UserRegisterRequest;
import edu.byu.erictodd.familymapclient.model.UserResult;
import edu.byu.erictodd.familymapclient.network.ServerProxy;

/**
 * A simple Async Task that attempts to register a new user
 */
public class RegisterTask extends AsyncTask<UserRegisterRequest, Void, UserResult> {

    /**
     * The RegisterInterface requires that the parent Activity/class implement
     * functionality for when register both succeeds and fails
     */
    public interface RegisterInterface {
        void onRegisterSuccess(UserResult result);

        void onRegisterFailure(UserResult result);
    }

    /**
     * A pointer to the parent Activity/class that implements a RegisterTask
     */
    private RegisterInterface parent;

    /**
     * Default Constructor that creates a pointer to the parent Activity/Fragment that
     * implements this ASyncTask
     *
     * @param parent pointer to the task's parent activity/fragment
     */
    public RegisterTask(RegisterInterface parent) {
        this.parent = parent;
    }

    @Override
    protected UserResult doInBackground(UserRegisterRequest... registerRequests) {
        return ServerProxy.getInstance().register(registerRequests[0]);
    }

    @Override
    protected void onPostExecute(UserResult registerResult) {
        //Register was successful
        if (registerResult.getMessage() == null) {
            onRegisterSuccess(registerResult);
        }
        //Register was unsuccessful
        else {
            onRegisterFailure(registerResult);
        }
    }

    private void onRegisterSuccess(UserResult registerResult) {
        parent.onRegisterSuccess(registerResult);
    }

    private void onRegisterFailure(UserResult registerResult) {
        parent.onRegisterFailure(registerResult);
    }
}

