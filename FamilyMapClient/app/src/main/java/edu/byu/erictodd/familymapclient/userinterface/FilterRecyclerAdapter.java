package edu.byu.erictodd.familymapclient.userinterface;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * An Adapter that handles FilterViewHolders in order to display them in a RecyclerView
 */
public class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterViewHolder> {

    /**
     * A list of the eventTypes that are to be displayed in the filterRecyclerView
     */
    private List<String> eventTypes;

    /**
     * Constructor that initializes the current list of event types to display
     *
     * @param eventTypes the list of eventTypes to be displayed
     */
    public FilterRecyclerAdapter(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        return new FilterViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String currentEventType = eventTypes.get(position);
        holder.bind(currentEventType);
    }

    @Override
    public int getItemCount() {
        return eventTypes.size();
    }
}
