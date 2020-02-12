package edu.byu.erictodd.familymapclient.network;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;
import java.util.HashMap;

import edu.byu.erictodd.familymapclient.model.AuthToken;
import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.network.ServerProxy;

/**
 * A simple AsyncTask that attempts to retrieve person data and populate the Model data structures
 * to serve as a local cache
 */
public class PersonTask extends AsyncTask<AuthToken, Void, PersonResult> {

    public static final String TAG = "PersonTask";

    /**
     * The PersonInterface requires that the parent Activity/class implement
     * functionality for when retrieval from the server both succeeds and fails
     */
    public interface PersonInterface {
        void onPersonsRetrievalFailure(PersonResult personResult);

        void onPersonsRetrievalSuccess(PersonResult personResult);
    }

    /**
     * A pointer to the parent Activity/class that implements a PersonTask
     */
    private PersonInterface parent;

    /**
     * Default Constructor that creates a pointer to the parent Activity/Fragment
     * that implements this AsyncTask
     *
     * @param parent a pointer to the parent Activity/Fragment
     */
    public PersonTask(PersonInterface parent) {
        this.parent = parent;
    }

    @Override
    public PersonResult doInBackground(AuthToken... currentUserToken) {
        PersonResult personResult = ServerProxy.getInstance().getPersons(currentUserToken[0]);
        if (personResult.getPersons() == null) {
            Log.e(TAG, "PERSON RESULT FROM SERVER PROXY WAS NULL");
        }
        if (personResult.getMessage() == null && personResult.getPerson() == null
                && personResult.getPersons() != null) {
            Model.getInstance().populatePersonData(personResult);
        }
        return personResult;
    }

    @Override
    protected void onPostExecute(PersonResult personResult) {
        //If the login succeeded
        if (personResult.getMessage() == null && personResult.getPerson() == null
                && personResult.getPersons() != null) {
            parent.onPersonsRetrievalSuccess(personResult);
        }
        //If the login failed
        else {
            parent.onPersonsRetrievalFailure(personResult);
        }
    }
}
