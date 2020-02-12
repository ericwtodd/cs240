package edu.byu.erictodd.familymapclient.network;

import android.os.AsyncTask;

import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.model.UserLoginRequest;
import edu.byu.erictodd.familymapclient.model.UserResult;
import edu.byu.erictodd.familymapclient.network.ServerProxy;

/**
 * A simple AsyncTask that attempts to log the user in
 */
public class LoginTask extends AsyncTask<UserLoginRequest, Void, UserResult> {

    /**
     * The LoginInterface requires that the parent Activity/class implement
     * functionality for when a login both succeeds and fails
     */
    public interface LoginInterface {
        void onLoginSuccess(UserResult loginResult);

        void onLoginFailure(UserResult loginResult);
    }

    /**
     * A pointer to the parent Activity/class that implements a LoginTask
     */
    private LoginInterface parent;

    /**
     * Default Constructor that creates a pointer to the parent activity or fragment that
     * implements this ASyncTask
     *
     * @param parent a pointer to the parent activity/fragment
     */
    public LoginTask(LoginInterface parent) {
        this.parent = parent;
    }

    @Override
    protected UserResult doInBackground(UserLoginRequest... loginRequests) {
        //Perform Login Functionality
        return ServerProxy.getInstance().login(loginRequests[0]);
    }

    @Override
    protected void onPostExecute(UserResult loginResult) {
        //If login succeeded
        if (loginResult.getMessage() == null) {
            parent.onLoginSuccess(loginResult);
        }
        //If the login failed
        else {
            parent.onLoginFailure(loginResult);
        }
    }
}
