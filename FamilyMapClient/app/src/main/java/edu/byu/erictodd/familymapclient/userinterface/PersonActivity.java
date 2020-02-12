package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * An Activity that displays the details about a person, including their name, gender, events (that aren't being filtered),
 * as well as their immediate family members
 */
public class PersonActivity extends AppCompatActivity {
    /**
     * A string that references the person that is to be displayed in the personActivity
     */
    public static final String EXTRA_PERSON_ID = "person_id";
    /**
     * The person that is currently being displayed in the personActivity
     */
    private Person mSelectedPerson;
    /**
     * A view that displays the first name of the selected person
     */
    private TextView mFirstNameTextView;
    /**
     * A view that displays the last name of the selected person
     */
    private TextView mLastNameTextView;
    /**
     * A view that displays the gender of the person in text form
     */
    private TextView mGenderTextView;
    /**
     * A recyclerView that displays all the family members of the currently selected person
     */
    private RecyclerView mPersonRecyclerView;
    /**
     * An adapter to help the person recycler display each family member
     */
    private PersonRecyclerAdapter mPersonRecyclerAdapter;
    /**
     * A recyclerView that displays all the associated events of the currently selected person
     */
    private RecyclerView mEventRecyclerView;
    /**
     * An adapter to help the event recycler display each associated event that isn't being filtered.
     */
    private EventRecyclerAdapter mEventRecyclerAdapter;
    /**
     * A view that allows the user to toggle whether or not the associated persons are viewable
     */
    private TextView mPersonRecyclerViewHeader;
    /**
     * A view that allows the user to toggle whether or not the associated events are viewable
     */
    private TextView mEventRecyclerViewHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //Use the passed in ID to get the person associated with the selected event
        //Wire up all the widgets in the activity
        String personID = (String) getIntent().getSerializableExtra(EXTRA_PERSON_ID);
        mSelectedPerson = Model.getInstance().findPerson(personID);

        mFirstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
        mLastNameTextView = (TextView) findViewById(R.id.lastNameTextView);
        mGenderTextView = (TextView) findViewById(R.id.genderTextView);

        mFirstNameTextView.setText(mSelectedPerson.getFirstName());
        mLastNameTextView.setText(mSelectedPerson.getLastName());
        if (mSelectedPerson.getGender().equals("m")) {
            mGenderTextView.setText(R.string.personActivityMale);
        } else {
            mGenderTextView.setText(R.string.personActivityFemale);
        }

        mPersonRecyclerViewHeader = (TextView) findViewById(R.id.personRecyclerViewHeader);
        mPersonRecyclerViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPersonRecyclerView.getVisibility() == View.INVISIBLE || mPersonRecyclerView.getVisibility() == View.GONE) {
                    mPersonRecyclerView.setVisibility(View.VISIBLE);
                } else if (mPersonRecyclerView.getVisibility() == View.VISIBLE) {
                    mPersonRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        mPersonRecyclerView = (RecyclerView) findViewById(R.id.personRecyclerView);
        mPersonRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEventRecyclerViewHeader = (TextView) findViewById(R.id.eventRecyclerViewHeader);
        mEventRecyclerViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEventRecyclerView.getVisibility() == View.INVISIBLE || mEventRecyclerView.getVisibility() == View.GONE) {
                    mEventRecyclerView.setVisibility(View.VISIBLE);
                } else if (mEventRecyclerView.getVisibility() == View.VISIBLE) {
                    mEventRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        mEventRecyclerView = (RecyclerView) findViewById(R.id.eventRecyclerView);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    /**
     * Creates an intent object with an extra storing the personID of the person
     * that is to be displayed in the personActivity
     *
     * @param packageContext the context in which the event activity is being called from
     * @param personID       the personID to be centered on
     * @return an intent with the extra personID
     */
    public static Intent newIntent(Context packageContext, String personID) {
        Intent intent = new Intent(packageContext, PersonActivity.class);
        intent.putExtra(EXTRA_PERSON_ID, personID);
        return intent;
    }

    /**
     * A function that retrieves the list of people and events that are associated with the currently
     * selected person and passes those to the adapters to display them in their respective recyclerViews.
     */
    private void updateUI() {
        //Add in the relatives that should be shown
        mPersonRecyclerAdapter = new PersonRecyclerAdapter(
                Model.getInstance().getPersonRelatives().get(mSelectedPerson.getPersonID()), mSelectedPerson);
        mPersonRecyclerView.setAdapter(mPersonRecyclerAdapter);

        mEventRecyclerAdapter = new EventRecyclerAdapter(
                Model.getInstance().getFilteredPersonEvents(mSelectedPerson.getPersonID()), mSelectedPerson);
        mEventRecyclerView.setAdapter(mEventRecyclerAdapter);
    }
}
