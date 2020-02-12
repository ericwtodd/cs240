package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Event;

/**
 * A ViewHolder that displays the information for one Event to be displayed inside a RecyclerView.
 */
public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * A view that stores the Icon to be displayed in an event List Item
     */
    private IconTextView eventIcon;
    /**
     * A view that stores the text to be displayed in the top part of an event list item
     */
    private TextView eventTopTextView;
    /**
     * A view that stores the text to be displayed in the bottom part of an event list item
     */
    private TextView eventBottomTextView;
    /**
     * The event that is to be displayed in the viewHolder
     */
    private Event mEvent;

    /**
     * Constructor that creates pointers to each of the items/widgets inside of itself as well as a click listener
     *
     * @param inflater the inflater that inflates the viewHolder
     * @param parent the parent where the viewHolder is being displayed
     */
    public EventViewHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item, parent, false));
        eventIcon = (IconTextView) itemView.findViewById(R.id.listItemIcon);
        eventTopTextView = (TextView) itemView.findViewById(R.id.listItemTopText);
        eventBottomTextView = (TextView) itemView.findViewById(R.id.listItemBottomText);
        itemView.setOnClickListener(this);
    }

    /**
     * Called when the Adapter calls onBindViewHolder, it updates the information for a given event ViewHolder
     * to bind to a new Event.
     *
     * @param event    the new event information to be displayed
     * @param fullName the full name of the person to which the vent belongs
     */
    public void bind(Event event, String fullName) {
        mEvent = event;
        //Set the Top Text to display event Details
        String eventDetails =
                event.getEventType() + ": " + event.getCity() + " " + event.getCountry() + " (" + event.getYear() + ")";
        eventTopTextView.setText(eventDetails);
        //Set the Bottom TextView to display the full name of the person to which the event belongs
        eventBottomTextView.setText(fullName);
        //Set the Icon to an Event Icon
        eventIcon.setText(R.string.eventIcon);
    }

    @Override
    public void onClick(View view) {
        //Create an intent to an event activity centered on this viewHolder's event
        Intent intent = EventActivity.newIntent(itemView.getContext(), mEvent.getEventID());
        itemView.getContext().startActivity(intent);
    }

}

