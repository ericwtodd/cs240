package edu.byu.erictodd.familymapclient.userinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;

/**
 * An activity that allows the user to turn on and off filter settings corresponding to each eventType,
 * as well as events from father's side, the mother's side, male events, and female events.
 */
public class FilterActivity extends AppCompatActivity {

    /**
     * Switch to control the filter of the father's side events
     */
    private Switch mFilterFatherSwitch;
    /**
     * Switch to control the filter of the mother's side events
     */
    private Switch mFilterMotherSwitch;
    /**
     * Switch to control the filter of male events
     */
    private Switch mFilterMaleSwitch;
    /**
     * Switch to control the filter of female events
     */
    private Switch mFilterFemaleSwitch;
    /**
     * A Recycler view to display each of the eventType filter options
     */
    private RecyclerView mFilterRecyclerView;
    /**
     * An adapter to communicate between FilterViewHolders and the recycler view.
     */
    private FilterRecyclerAdapter mFilterRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //Wire up all the switches
        mFilterFatherSwitch = (Switch) findViewById(R.id.filterFatherSwitch);
        mFilterMotherSwitch = (Switch) findViewById(R.id.filterMotherSwitch);
        mFilterMaleSwitch = (Switch) findViewById(R.id.filterMaleSwitch);
        mFilterFemaleSwitch = (Switch) findViewById(R.id.filterFemaleSwitch);
        //Wire up the RecyclerView
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filterRecyclerView);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Wire up the adapter with all the eventTypes
        mFilterRecyclerAdapter = new FilterRecyclerAdapter(Model.getInstance().getEventTypes());
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter);
        //set the father's side switch to the current filter settings & set a clickListener to handle changes
        mFilterFatherSwitch.setChecked(Model.getInstance().getFilter().isFatherSideEnabled());
        mFilterFatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Model.getInstance().getFilter().setFatherSideEnabled(true);
                } else {
                    Model.getInstance().getFilter().setFatherSideEnabled(false);
                }
            }
        });
        //set the mother's side switch to the current filter settings & set a clickListener to handle changes
        mFilterMotherSwitch.setChecked(Model.getInstance().getFilter().isMotherSideEnabled());
        mFilterMotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Model.getInstance().getFilter().setMotherSideEnabled(true);
                } else {
                    Model.getInstance().getFilter().setMotherSideEnabled(false);
                }
            }
        });
        //set the male events switch to the current filter settings & set a clickListener to handle changes
        mFilterMaleSwitch.setChecked(Model.getInstance().getFilter().isMaleEnabled());
        mFilterMaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Model.getInstance().getFilter().setMaleEnabled(true);
                } else {
                    Model.getInstance().getFilter().setMaleEnabled(false);
                }
            }
        });
        //set the female events switch to the current filter settings & set a clickListener to handle changes
        mFilterFemaleSwitch.setChecked(Model.getInstance().getFilter().isFemaleEnabled());
        mFilterFemaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Model.getInstance().getFilter().setFemaleEnabled(true);
                } else {
                    Model.getInstance().getFilter().setFemaleEnabled(false);
                }
            }
        });
    }

}
