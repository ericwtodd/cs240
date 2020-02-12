package edu.byu.erictodd.familymapclient.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that keeps track of the app's event filter preferences, including which eventTypes are enabled,
 * which sides of the family are enabled, which gender events are enabled in the map fragment, event activity,
 * search activity, and person activity.
 */
public class Filter {

    /**
     * A map of eventType to whether or not each is enabled.
     * This list is created dynamically when the data is loaded from the server based
     * on the number of unique event types. The default is that all are enabled.
     */
    Map<String, Boolean> mEventTypeEnabled;
    /**
     * A flag determining whether or not the father's side events are enabled. Default is true
     */
    boolean mFatherSideEnabled;
    /**
     * A flag determining whether or not the mother's side events are enabled. Default is true
     */
    boolean mMotherSideEnabled;
    /**
     * A flag determining whether or not the male events are enabled. Default is true.
     */
    boolean mMaleEnabled;
    /**
     * A flag determining whether or not the female events are enabled. Default is true.
     */
    boolean mFemaleEnabled;

    /**
     * Default constructor that initializes the default values and map
     */
    public Filter() {
        mEventTypeEnabled = new HashMap<>();
        mFatherSideEnabled = true;
        mMotherSideEnabled = true;
        mMaleEnabled = true;
        mFemaleEnabled = true;
    }


    public Map<String, Boolean> getEventTypeEnabled() {
        return mEventTypeEnabled;
    }

    public void setEventTypeEnabled(Map<String, Boolean> eventTypeEnabled) {
        mEventTypeEnabled = eventTypeEnabled;
    }

    public boolean isFatherSideEnabled() {
        return mFatherSideEnabled;
    }

    public void setFatherSideEnabled(boolean fatherSideEnabled) {
        mFatherSideEnabled = fatherSideEnabled;
    }

    public boolean isMotherSideEnabled() {
        return mMotherSideEnabled;
    }

    public void setMotherSideEnabled(boolean motherSideEnabled) {
        mMotherSideEnabled = motherSideEnabled;
    }

    public boolean isMaleEnabled() {
        return mMaleEnabled;
    }

    public void setMaleEnabled(boolean maleEnabled) {
        mMaleEnabled = maleEnabled;
    }

    public boolean isFemaleEnabled() {
        return mFemaleEnabled;
    }

    public void setFemaleEnabled(boolean femaleEnabled) {
        mFemaleEnabled = femaleEnabled;
    }
}
