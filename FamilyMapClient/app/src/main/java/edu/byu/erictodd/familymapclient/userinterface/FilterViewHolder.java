package edu.byu.erictodd.familymapclient.userinterface;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;

/**
 * A ViewHolder that displays information for one filter row to be displayed. These correspond with
 * the various eventTypes that are loaded in.
 */
public class FilterViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    /**
     * A switch for enabling the EventType
     */
    private Switch mEventTypeSwitch;
    /**
     * A view that stores the text to be displayed in the top part of a filterViewHolder
     */
    private TextView mEventTypeTopTextView;
    /**
     * A view that store the text to be displayed in the bottom part of a filterViewHolder
     */
    private TextView mEventTypeBottomTextView;
    /**
     * The EventType that is being displayed in this filter Row
     */
    private String mEventType;

    /**
     * Constructor that initializes all the viewHolder's widgets
     *
     * @param inflater inflates the view holder inside the recycleView
     * @param parent   the caller where the viewHolder is to be inflated
     */
    public FilterViewHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.filter_list_item, parent, false));
        mEventTypeSwitch = (Switch) itemView.findViewById(R.id.filterEventSwitch);
        mEventTypeSwitch.setOnCheckedChangeListener(this);
        mEventTypeTopTextView = (TextView) itemView.findViewById(R.id.filterEventTopText);
        mEventTypeBottomTextView = (TextView) itemView.findViewById(R.id.filterEventBottomText);
    }

    /**
     * Called when the Adapter calls onBindViewHolder, it updates the information for a given filterViewHolder
     * to bind to a new eventType.
     *
     * @param eventType the eventType to be bound
     */
    public void bind(String eventType) {
        mEventType = eventType;
        String topText = mEventType.substring(0, 1).toUpperCase() + mEventType.substring(1).toLowerCase() + " Events";
        mEventTypeTopTextView.setText(topText);
        String bottomText = "FILTER BY " + mEventType.toUpperCase() + " EVENTS";
        mEventTypeBottomTextView.setText(bottomText);
        mEventTypeSwitch.setChecked(Model.getInstance().getFilter().getEventTypeEnabled().get(mEventType));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            Model.getInstance().getFilter().getEventTypeEnabled().put(mEventType, true);
        } else {
            Model.getInstance().getFilter().getEventTypeEnabled().put(mEventType, false);
        }
    }

}
