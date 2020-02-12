package edu.byu.erictodd.familymapclient.userinterface;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * An adapter that handles PersonViewHolder in order to display them in a recyclerView
 */
public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonViewHolder> {

    /**
     * The list of persons to be displayed by the recyclerView
     */
    private List<Person> mPersonList;
    /**
     * The base person of a person activity whose relatives are to be displayed
     */
    private Person mBasePerson;

    /**
     * Constructor that initializes the personList and the selectedPerson that is being displayed in a personActivity
     *
     * @param personList     the list of persons to be displayed
     * @param selectedPerson the currently selected person in a personActivity whose relatives are to be displayed.
     */
    public PersonRecyclerAdapter(List<Person> personList, Person selectedPerson) {
        mPersonList = personList;
        mBasePerson = selectedPerson;
    }

    /**
     * Constructor that initializes the personList, and sets the selectedPerson to null, since this is utilized by
     * the SearchActivity
     *
     * @param personList the list of persons to be displayed
     */
    public PersonRecyclerAdapter(List<Person> personList) {
        mPersonList = personList;
        mBasePerson = null;
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new PersonViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        //The Adapter is being used by Search
        if (mBasePerson == null) {
            Person person = mPersonList.get(position);
            //We pass in no Relation since we only want the first and last names
            holder.bind(person, "");
        }
        //The Adapter is being used by a Person Activity
        else {
            Person person = mPersonList.get(position);
            String relation = Model.getInstance().getRelation(mBasePerson, person);
            holder.bind(person, relation);
        }
    }

}