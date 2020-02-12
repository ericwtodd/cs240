package edu.byu.erictodd.familymapclient.userinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * An activity that allows the user to search persons and events for a given string.
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * The searchView that allows the user to input their search string
     */
    private SearchView mSearchView;
    /**
     * The recyclerView that displays all the person results
     */
    private RecyclerView mPersonRecyclerView;
    /**
     * An adapter to help the person recycler display each family member
     */
    private PersonRecyclerAdapter mPersonRecyclerAdapter;
    /**
     * The recyclerView that displays all the event results
     */
    private RecyclerView mEventRecyclerView;
    /**
     * An adapter to help the event recycler display each associated event that isn't being filtered.
     */
    private EventRecyclerAdapter mEventRecyclerAdapter;
    /**
     * The string corresponding to the user input that is used to search people and events
     */
    private String mQueryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchView = (SearchView) findViewById(R.id.searchActivitySearchView);
        //If the user clicks in the search bar, make the search results disappear
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQueryText = null;
                updateUI();
            }
        });
        //Set a Search Query Listener to perform a search and update the UI
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && query != "") {
                    mQueryText = query;
                    searchAndDisplayResults();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    mQueryText = newText;
                    updateUI();
                }
                return false;
            }
        });
        //Wire up the RecyclerViews
        mPersonRecyclerView = (RecyclerView) findViewById(R.id.searchActivityPersonRecyclerView);
        mPersonRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEventRecyclerView = (RecyclerView) findViewById(R.id.searchActivityEventRecyclerView);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    /**
     * Show or hide the search results depending on whether or not a search
     */
    private void updateUI() {
        if (mQueryText == null || mQueryText.equals("")) {
            mPersonRecyclerView.setVisibility(View.INVISIBLE);
            mEventRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mPersonRecyclerView.setVisibility(View.VISIBLE);
            mEventRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets the set of events and persons that contain the entered query/search string
     * and the adapters take those lists and display them on the screen as person and event ViewHolders
     */
    public void searchAndDisplayResults() {

        List<Event> eventList = Model.getInstance().getFilteredEventSearchResults(mQueryText);
        List<Person> personList = Model.getInstance().getPersonSearchResults(mQueryText);
        if (eventList == null || personList == null) {
            //If the search produced an error because of the string passed in
            mQueryText = null;
            updateUI();
        } else {
            //Pass in the list of filtered persons containing the search string
            mPersonRecyclerAdapter = new PersonRecyclerAdapter(personList);
            mPersonRecyclerView.setAdapter(mPersonRecyclerAdapter);
            mPersonRecyclerView.setNestedScrollingEnabled(false);

            //Pass in the List of filtered Events & their corresponding persons' full names
            //containing the search string
            mEventRecyclerAdapter = new EventRecyclerAdapter(eventList);
            mEventRecyclerView.setAdapter(mEventRecyclerAdapter);
            mEventRecyclerView.setNestedScrollingEnabled(false);
            //Display the results on the screen
            updateUI();
        }
    }
}
