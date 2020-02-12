package edu.byu.erictodd.familymapclient.userinterface;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * An Adapter that handles EventViewHolders in order to display them in a RecyclerView
 */
public class EventRecyclerAdapter extends RecyclerView.Adapter<EventViewHolder> {

    /**
     * A list of events that the RecyclerView is able to display
     */
    private List<Event> mEventList;
    /**
     * The person with which the list of events is associated
     */
    private Person mBasePerson;

    /**
     * Constructor that uses a list of events related to a specific person and provides information
     * about the person to which these events are related.
     *
     * @param eventList      the list of events about a particular person
     * @param selectedPerson the person to which the events are related
     */
    public EventRecyclerAdapter(List<Event> eventList, Person selectedPerson) {
        mEventList = eventList;
        Collections.sort(mEventList);
        mBasePerson = selectedPerson;
    }

    /**
     * Constructor that takes a list of events to display information about them
     *
     * @param eventList the list of events to be displayed
     */
    public EventRecyclerAdapter(List<Event> eventList) {
        mEventList = eventList;
        Collections.sort(mEventList);
        mBasePerson = null;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new EventViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        //If there is not a base person (host is a search activity)
        if (mBasePerson == null) {
            Event event = mEventList.get(position);
            String fullName = Model.getInstance().findPerson(event.getPersonID()).getFirstName()
                    + " " + Model.getInstance().findPerson(event.getPersonID()).getLastName();
            holder.bind(event, fullName);
        } else { //Host is a person activity
            Event event = mEventList.get(position);
            holder.bind(event, mBasePerson.getFirstName() + " " + mBasePerson.getLastName());
        }
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
