package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Event;
import edu.byu.erictodd.familymapclient.model.Filter;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;

/**
 * A Fragment Class that manages the view of the map, markers, and lines to be displayed by
 * the main activity and event activities
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    /**
     * A string that is a reference to the eventID argument passed in by an eventActivity call to this fragment
     */
    public static final String ARG_EVENT_ID = "event_id";
    /**
     * A string that is a reference to the boolean whether or not this fragment is being hosted by an event activity
     */
    public static final String ARG_HOSTED_BY_EVENT = "hostedByEventActivity";
    /**
     * The googleMap that contains all the markers and lines that are being drawn
     */
    private GoogleMap mMap;
    /**
     * The mapView that contains the googleMap that is displayed in this fragment
     */
    private MapView mMapView;
    /**
     * The view displaying the whole fragment: map and event information
     */
    private View mView;
    /**
     * The event that is currently selected and its information is being displayed in the fragment
     * If no event is currently selected, this is null
     */
    private Event mCurrentlySelectedEvent;
    /**
     * The person that is related to the currently selected event
     * If no event is currently selected, this is null
     */
    private Person mCurrentlySelectedPerson;
    /**
     * A flag determining whether or not the map fragment is being hosted by an event activity
     * which tells us whether or not to display the menu options
     */
    private boolean mHostedByEventActivity;
    /**
     * The layout containing/displaying the event information at the bottom
     */
    private RelativeLayout displayLayout;
    /**
     * A view displaying the top text of event information
     */
    private TextView displayTopTextView;
    /**
     * A view displaying the bottom text of event information
     */
    private TextView displayBottomTextView;
    /**
     * A view displaying the icon of event information
     */
    private IconTextView displayIconTextView;

    /**
     * Used to create a new instance of the MapFragment for an event activity
     *
     * @param selectedEventID the eventID of the Event that was selected from a search or person activity
     * @return the mapFragment with various arguments to indicate the host of the fragment is an event activity.
     */
    public static MapFragment newInstance(String selectedEventID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, selectedEventID);
        args.putBoolean(ARG_HOSTED_BY_EVENT, true);
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //If we come from settings activity, and user presses back button, then we still want to be able to save the changes
        setMapType();
        updateMap();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_map_menu, menu);
        menu.findItem(R.id.search_Icon).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.menuIcons).actionBarSize());
        menu.findItem(R.id.filter_Icon).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_filter).colorRes(R.color.menuIcons).actionBarSize());
        menu.findItem(R.id.settings_Icon).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.menuIcons).actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.filter_Icon):
                //Go to Filter Activity
                intent = new Intent(getActivity(), FilterActivity.class);
                startActivity(intent);
                return true;

            case (R.id.settings_Icon):
                //Go to Setting Activity
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case (R.id.search_Icon):
                //Go to Search Activity
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        //Get the arguments passed in by the event activity if it is the host.
        if (getArguments() != null) {
            String eventID = (String) getArguments().getSerializable(ARG_EVENT_ID);
            mCurrentlySelectedEvent = Model.getInstance().findEvent(eventID);
            mCurrentlySelectedPerson = Model.getInstance().findPerson(mCurrentlySelectedEvent.getPersonID());
            mHostedByEventActivity = getArguments().getBoolean(ARG_HOSTED_BY_EVENT);
        }

        //If we're hosted by the event activity, we don't want the menu options to appear
        //Otherwise we do.
        setHasOptionsMenu(!mHostedByEventActivity);

        mMapView = (MapView) mView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set the Map Type if the Settings have been changed
        setMapType();

        //When a marker is clicked, we want to update the currently selected event & corresponding person
        //As well as display the descriptive text below.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Update the currentlySelected Event and Person
                mCurrentlySelectedEvent = Model.getInstance().findEvent(marker.getSnippet());
                mCurrentlySelectedPerson = Model.getInstance().findPerson(mCurrentlySelectedEvent.getPersonID());
                //Update the Display Text
                updateDisplayText();
                drawMapLines();
                return false;
            }
        });
        updateMap();
        //If the host is an event activity, then we center on the current event & update the display text
        if (mCurrentlySelectedEvent != null) {
            LatLng selectedEvent = new LatLng(Double.valueOf(mCurrentlySelectedEvent.getLatitude()), Double.valueOf(mCurrentlySelectedEvent.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedEvent));
            updateDisplayText();
        }
    }

    /**
     * Updates the bottom display text when an event has been selected to display a description of the event.
     */
    private void updateDisplayText() {
        displayLayout = mView.findViewById(R.id.fragmentMapTextDisplay);
        displayIconTextView = (IconTextView) mView.findViewById(R.id.fragmentMapDefaultIcon);
        displayTopTextView = (TextView) mView.findViewById(R.id.fragmentMapTextTop);
        displayBottomTextView = (TextView) mView.findViewById(R.id.fragmentMapTextBottom);

        if (mCurrentlySelectedEvent != null && mCurrentlySelectedPerson != null) {
            if (mCurrentlySelectedPerson.getGender().equals("m")) {
                displayIconTextView.setText(R.string.maleIcon);
            } else {
                displayIconTextView.setText(R.string.femaleIcon);
            }
            String topText = mCurrentlySelectedPerson.getFirstName() + " " + mCurrentlySelectedPerson.getLastName();
            displayTopTextView.setText(topText);
            String bottomText = mCurrentlySelectedEvent.getEventType() + ": " +
                    mCurrentlySelectedEvent.getCity() + ", " + mCurrentlySelectedEvent.getCountry()
                    + " (" + mCurrentlySelectedEvent.getYear() + ")";
            displayBottomTextView.setText(bottomText);

            displayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = PersonActivity.newIntent(getActivity(), mCurrentlySelectedPerson.getPersonID());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Sets the Map Type According to the Settings Preferences
     */
    private void setMapType() {
        if (Model.getInstance().getSettings().getMapType() != null && mMap != null) {
            if (Model.getInstance().getSettings().getMapType().equals("Normal")) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (Model.getInstance().getSettings().getMapType().equals("Hybrid")) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (Model.getInstance().getSettings().getMapType().equals("Satellite")) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (Model.getInstance().getSettings().getMapType().equals("Terrain")) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    /**
     * Updates the Markers and Lines on the Map
     */
    private void updateMap() {
        addFilteredMapEvents();
        drawMapLines();
    }

    /**
     * Updates and draws the Lines on the Map according to the settings preferences
     */
    private void drawMapLines() {
        drawSpouseLines();
        drawLifeStoryLines();
        drawFamilyTreeLines();
    }

    /**
     * Adds all the map event markers according to the current filter preferences
     */
    public void addFilteredMapEvents() {
        //Reset the Map
        if (mMap != null) {
            mMap.clear();
            setMapType();
            Map<String, Event> filteredEvents = Model.getInstance().getFilteredEvents();
            for (Map.Entry<String, Event> entry : filteredEvents.entrySet()) {
                LatLng eventMarker = new LatLng(Double.valueOf(entry.getValue().getLatitude()), Double.valueOf(entry.getValue().getLongitude()));
                Marker marker =
                        mMap.addMarker(new MarkerOptions().position(eventMarker)
                                .snippet(entry.getValue().getEventID())
                                .icon(BitmapDescriptorFactory.defaultMarker(Model.getInstance().getEventTypeColors().get(entry.getValue().getEventType().toLowerCase()))));
                //Store the marker in the Model
                Model.getInstance().getMapMarkers().put(entry.getValue().getEventID(), marker);
            }
        }
    }

    /**
     * Draws the Spouse Lines on the Map according to the current settings
     */
    private void drawSpouseLines() {
        Map<String, Marker> mapMarkers = new HashMap<>(Model.getInstance().getMapMarkers());
        //If there is an event Selected & the map exists && the lines are enabled in settings
        if (mCurrentlySelectedEvent != null && mMap != null && Model.getInstance().getSettings().isSpouseLinesEnabled()) {
            for (Marker marker : mapMarkers.values()) {
                //Find the marker that matches the event
                if (mCurrentlySelectedEvent.getEventID().equals(marker.getSnippet())) {
                    //Get the personID associated with the currentlySelectedEvent
                    String personID = mCurrentlySelectedEvent.getPersonID();
                    //If the person has a spouse, we get their associated events
                    if (Model.getInstance().findPerson(personID).getSpouse() != null && !Model.getInstance().findPerson(personID).getSpouse().equals("")) {
                        //Get the list of the spouse's events
                        List<Event> events = Model.getInstance()
                                .getFilteredPersonEvents(Model.getInstance().findPerson(personID).getSpouse());
                        //If there is an event, draw a line to the earliest one.
                        if (!events.isEmpty()) {
                            //Remove the previous line
                            if (Model.getInstance().getSpouseLine() != null) {
                                Model.getInstance().getSpouseLine().remove();
                            }
                            Collections.sort(events);
                            LatLng spouseEarliestEvent = new LatLng(Double.valueOf(events.get(0).getLatitude()), Double.valueOf(events.get(0).getLongitude()));
                            Polyline newSpouseLine =
                                    mMap.addPolyline(new PolylineOptions().add(marker.getPosition(), spouseEarliestEvent)
                                            .color(Model.getInstance().getSettings().colorStringToHue(Model.getInstance().getSettings().getSpouseLinesColor())));
                            //Store the line in the Model
                            Model.getInstance().setSpouseLine(newSpouseLine);
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws the current LifeStory Lines on the Map according to the current settings
     */
    private void drawLifeStoryLines() {
        Map<String, Marker> mapMarkers = new HashMap<>(Model.getInstance().getMapMarkers());
        if (mCurrentlySelectedEvent != null && mMap != null && Model.getInstance().getSettings().isLifeStoryLinesEnabled()) {
            for (Marker marker : mapMarkers.values()) {
                //Find the marker that matches the event
                if (mCurrentlySelectedEvent.getEventID().equals(marker.getSnippet())) {
                    //Get the personID associated with the currentlySelectedEvent
                    String personID = mCurrentlySelectedEvent.getPersonID();
                    //Get the list of the person's events
                    List<Event> events = Model.getInstance()
                            .getFilteredPersonEvents(personID);
                    //If there is an event, draw a line to the earliest one.
                    if (!events.isEmpty()) {
                        //Remove the previous lines from the map and from storage
                        if (Model.getInstance().getLifeStoryLines() != null) {
                            Set<Polyline> storyLines = new HashSet<>(Model.getInstance().getLifeStoryLines());
                            for (Polyline storyLine : storyLines) {
                                storyLine.remove();
                                Model.getInstance().getLifeStoryLines().remove(storyLine);
                            }
                        }
                        Collections.sort(events);
                        for (int i = 0; i < events.size() - 1; i++) {
                            LatLng firstStoryLineLocation =
                                    new LatLng(Double.valueOf(events.get(i).getLatitude()), Double.valueOf(events.get(i).getLongitude()));
                            LatLng secondStoryLineLocation =
                                    new LatLng(Double.valueOf(events.get(i + 1).getLatitude()), Double.valueOf(events.get(i + 1).getLongitude()));
                            Polyline newLifeStoryLine =
                                    mMap.addPolyline(new PolylineOptions().add(firstStoryLineLocation, secondStoryLineLocation)
                                            .color(Model.getInstance().getSettings().colorStringToHue(Model.getInstance().getSettings().getLifeStoryLinesColor())));
                            //Store the current life story lines in the model
                            Model.getInstance().getLifeStoryLines().add(newLifeStoryLine);
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws the current FamilyTree Lines on the Map according to the current settings
     */
    private void drawFamilyTreeLines() {
        Map<String, Marker> mapMarkers = new HashMap<>(Model.getInstance().getMapMarkers());
        //If there is a selected event and FamilyTree Lines are enabled
        if (mCurrentlySelectedEvent != null && mMap != null && Model.getInstance().getSettings().isFamilyTreeLinesEnabled()) {
            for (Marker marker : mapMarkers.values()) {
                if (mCurrentlySelectedEvent.getEventID().equals(marker.getSnippet())) {
                    //Get the personID associated with the currentlySelectedEvent
                    String personID = mCurrentlySelectedEvent.getPersonID();
                    //Remove the previous lines from the map and from storage if they already existed
                    if (Model.getInstance().getFamilyTreeLines() != null) {
                        Set<Polyline> familyLines = new HashSet<>(Model.getInstance().getFamilyTreeLines());
                        for (Polyline familyLine : familyLines) {
                            familyLine.remove();
                            Model.getInstance().getFamilyTreeLines().remove(familyLine);
                        }
                    }
                    //Draw the family tree lines on the map, starting with a width of 15
                    createFamilyTreeLines(personID, 15);
                }
            }
        }
    }

    /**
     * Draws the Family Tree Lines on the map
     *
     * @param personID            the base person to draw lines to his ancestors
     * @param familyTreeLineWidth the width of the lines to be drawn
     */
    private void createFamilyTreeLines(String personID, int familyTreeLineWidth) {
        Person basePerson = Model.getInstance().findPerson(personID);
        //Set the base familyTreeLineWidth to 2.
        if (familyTreeLineWidth < 2) {
            familyTreeLineWidth = 2;
        }
        if (basePerson != null) {
            List<Event> basePersonEvent = Model.getInstance().getFilteredPersonEvents(personID);
            if (!basePersonEvent.isEmpty()) {
                Collections.sort(basePersonEvent);
                Event basePersonFirstEvent = basePersonEvent.get(0);

                if (basePerson.getFather() != null) {
                    //Draw lines to the person's father
                    //Get Father's filtered events
                    List<Event> fatherEvents = Model.getInstance().getFilteredPersonEvents(basePerson.getFather());
                    //If there aren't any, don't draw a line
                    if (!fatherEvents.isEmpty()) {
                        // Sort them and choose the first one
                        Collections.sort(fatherEvents);
                        Event fatherFirstEvent = fatherEvents.get(0);

                        //otherwise draw a line from the basePerson's first event to the father's first event
                        LatLng basePersonFirstEventLocation =
                                new LatLng(Double.valueOf(basePersonFirstEvent.getLatitude()), Double.valueOf(basePersonFirstEvent.getLongitude()));
                        LatLng fatherFirstEventLocation =
                                new LatLng(Double.valueOf(fatherFirstEvent.getLatitude()), Double.valueOf(fatherFirstEvent.getLongitude()));
                        Polyline newLifeStoryLine =
                                mMap.addPolyline(new PolylineOptions().add(basePersonFirstEventLocation, fatherFirstEventLocation)
                                        .color(Model.getInstance().getSettings().colorStringToHue(Model.getInstance().getSettings().getFamilyTreeLinesColor()))
                                        .width(familyTreeLineWidth));
                        Model.getInstance().getFamilyTreeLines().add(newLifeStoryLine);
                        //Recurse on the parents of the the basePerson's father, decreasing the width each time by 3
                        createFamilyTreeLines(basePerson.getFather(), familyTreeLineWidth - 3);
                    }
                }
                if (basePerson.getMother() != null) {
                    //Draw lines to the person's mother
                    List<Event> motherEvents = Model.getInstance().getFilteredPersonEvents(basePerson.getMother());
                    if (!motherEvents.isEmpty()) {
                        Collections.sort(motherEvents);
                        Event motherFirstEvent = motherEvents.get(0);
                        //otherwise draw a line from the basePerson's first event to the father's first event
                        LatLng basePersonFirstEventLocation =
                                new LatLng(Double.valueOf(basePersonFirstEvent.getLatitude()), Double.valueOf(basePersonFirstEvent.getLongitude()));
                        LatLng fatherFirstEventLocation =
                                new LatLng(Double.valueOf(motherFirstEvent.getLatitude()), Double.valueOf(motherFirstEvent.getLongitude()));
                        Polyline newLifeStoryLine =
                                mMap.addPolyline(new PolylineOptions().add(basePersonFirstEventLocation, fatherFirstEventLocation)
                                        .color(Model.getInstance().getSettings().colorStringToHue(Model.getInstance().getSettings().getFamilyTreeLinesColor()))
                                        .width(familyTreeLineWidth));
                        Model.getInstance().getFamilyTreeLines().add(newLifeStoryLine);
                        //Recurse on the parents of the basePerson's mother, decreasing the width each time by 3
                        createFamilyTreeLines(basePerson.getMother(), familyTreeLineWidth - 3);
                    }
                }
            }
        }
    }

}
