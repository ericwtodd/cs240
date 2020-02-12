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
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * A ViewHolder that displays the information for one Person to be displayed inside a RecyclerView.
 */
public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * A view that stores the icon of the person (male or female icon)
     */
    private IconTextView personIcon;
    /**
     * A view that stores the text to be displayed in the top part of a person list item
     */
    private TextView personTopTextView;
    /**
     * A view that stores the text to be displayed in the bottom part of a person list item
     */
    private TextView personBottomTextView;
    /**
     * The person that is being displayed in a PersonViewHolder
     */
    private Person mPerson;

    /**
     * Constructor that creates pointers to each of the items/widgets inside of itself as well as a click listener
     * on the entire viewHolderRow
     *
     * @param inflater the inflater that inflates the viewHolder
     * @param parent   the parent where the viewHolder is being displayed
     */
    public PersonViewHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item, parent, false));
        personIcon = (IconTextView) itemView.findViewById(R.id.listItemIcon);
        personTopTextView = (TextView) itemView.findViewById(R.id.listItemTopText);
        personBottomTextView = (TextView) itemView.findViewById(R.id.listItemBottomText);
        itemView.setOnClickListener(this);
    }

    /**
     * Called when the Adapter calls onBindViewHolder, it updates the information for a given personViewHolder
     * to bind to a new Person.
     *
     * @param person   the new person information to be displayed
     * @param relation the relation of the person that is being displayed, if empty, the relation textView
     *                 is left blank (in case of a search activity)
     */
    public void bind(Person person, String relation) {
        mPerson = person;
        String fullName = person.getFirstName() + " " + person.getLastName();
        personTopTextView.setText(fullName);
        //For now, set Relation to the empty String until it can be calculated later.
        personBottomTextView.setText(relation);
        //Set the Gender Icon
        if (mPerson.getGender().equals("m")) {
            personIcon.setText(R.string.maleIcon);
        } else {
            personIcon.setText(R.string.femaleIcon);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = PersonActivity.newIntent(itemView.getContext(), mPerson.getPersonID());
        itemView.getContext().startActivity(intent);
    }

}
