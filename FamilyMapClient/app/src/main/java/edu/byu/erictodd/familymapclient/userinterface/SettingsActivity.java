package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.Person;
import edu.byu.erictodd.familymapclient.model.Settings;
import edu.byu.erictodd.familymapclient.network.ResyncTask;

/**
 * An activity that allows the user to turn on and off line displays for life story lines, family tree lines, and spouse lines,
 * as well as choose colors for each line type, and choose the map Type as well. The user can also resync the data with the
 * server, as well as logout
 */
public class SettingsActivity extends AppCompatActivity implements ResyncTask.ResyncInterface {
    /**
     * A Spinner that displays color options for the Life Story Lines
     */
    private Spinner mLifeStoryLinesSpinner;
    /**
     * A switch that determines whether or not Life Story Lines are enabled
     */
    private Switch mLifeStoryLinesSwitch;
    /**
     * A Spinner that displays color options for the Family Tree Lines
     */
    private Spinner mFamilyTreeLinesSpinner;
    /**
     * A switch that determines whether or not Family Tree Lines are enabled
     */
    private Switch mFamilyTreeLinesSwitch;
    /**
     * A Spinner that displays color options for the Spouse Lines
     */
    private Spinner mSpouseLinesSpinner;
    /**
     * A switch that determines whether or not Spouse Lines are enabled
     */
    private Switch mSpouseLinesSwitch;
    /**
     * A Spinner that displays the different options for map types
     */
    private Spinner mMapTypeSpinner;
    /**
     * A button that resyncs the user's data with the database without changing the settings
     */
    private LinearLayout mResyncDataButton;
    /**
     * A button that logs the user out, and returns to the login screen
     */
    private LinearLayout mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Wire up the LifeStoryLines Spinner, display currently selected color, but off, and mirror changes in model settings
        mLifeStoryLinesSpinner = (Spinner) findViewById(R.id.settingsLifeStoryLinesSpinner);
        int currentLifeStoryLinesColor = Model.getInstance().getSettings().getColorIndex(Model.getInstance().getSettings().getLifeStoryLinesColor());
        mLifeStoryLinesSpinner.setSelection(currentLifeStoryLinesColor);
        mLifeStoryLinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Edit the Settings in the Model.
                Model.getInstance().getSettings().setLifeStoryLinesColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Wire up LifeStoryLines Switch, display current settings, and mirror changes in model settings
        mLifeStoryLinesSwitch = (Switch) findViewById(R.id.settingsLifeStoryLinesSwitch);
        mLifeStoryLinesSwitch.setChecked(Model.getInstance().getSettings().isLifeStoryLinesEnabled());
        mLifeStoryLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //This Switch is On
                    mLifeStoryLinesSpinner.setVisibility(View.VISIBLE);
                    Model.getInstance().getSettings().setLifeStoryLinesEnabled(true);
                } else {
                    //This Switch is Off - change settings in model
                    mLifeStoryLinesSpinner.setVisibility(View.INVISIBLE);
                    Model.getInstance().getSettings().setLifeStoryLinesEnabled(false);
                }
            }
        });
        //Wire up the FamilyTreeLines Spinner, display currently selected color, but off, and mirror changes in model settings
        mFamilyTreeLinesSpinner = (Spinner) findViewById(R.id.settingsFamilyTreeLinesSpinner);
        int currentFamilyLinesColor = Model.getInstance().getSettings().getColorIndex(Model.getInstance().getSettings().getFamilyTreeLinesColor());
        mFamilyTreeLinesSpinner.setSelection(currentFamilyLinesColor);
        mFamilyTreeLinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model.getInstance().getSettings().setFamilyTreeLinesColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Wire up the FamilyTreeLines Switch, display current settings, and mirror changes in model settings
        mFamilyTreeLinesSwitch = (Switch) findViewById(R.id.settingsFamilyTreeLinesSwitch);
        mFamilyTreeLinesSwitch.setChecked(Model.getInstance().getSettings().isFamilyTreeLinesEnabled());
        mFamilyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //This Switch is On
                    mFamilyTreeLinesSpinner.setVisibility(View.VISIBLE);
                    Model.getInstance().getSettings().setFamilyTreeLinesEnabled(true);
                } else {
                    //This Switch is Off
                    mFamilyTreeLinesSpinner.setVisibility(View.INVISIBLE);
                    Model.getInstance().getSettings().setFamilyTreeLinesEnabled(false);
                }
            }
        });

        //Wire up the SpouseLines Spinner, display currently selected color, but off, and mirror changes in model settings
        mSpouseLinesSpinner = (Spinner) findViewById(R.id.settingsSpouseLinesSpinner);
        int currentSpouseLinesColor = Model.getInstance().getSettings().getColorIndex(Model.getInstance().getSettings().getSpouseLinesColor());
        mSpouseLinesSpinner.setSelection(currentSpouseLinesColor);
        mSpouseLinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model.getInstance().getSettings().setSpouseLinesColor((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Wire up the SpouseLines Switch, display current settings, and mirror changes in model settings
        mSpouseLinesSwitch = (Switch) findViewById(R.id.settingsSpouseLinesSwitch);
        mSpouseLinesSwitch.setChecked(Model.getInstance().getSettings().isSpouseLinesEnabled());
        mSpouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //This Switch is On
                    mSpouseLinesSpinner.setVisibility(View.VISIBLE);
                    Model.getInstance().getSettings().setSpouseLinesEnabled(true);
                } else {
                    //This Switch is Off
                    mSpouseLinesSpinner.setVisibility(View.INVISIBLE);
                    Model.getInstance().getSettings().setSpouseLinesEnabled(false);
                }
            }
        });

        //Wire up the MapTypeSpinner, Display currently selected map type, and mirror changes in model settings
        mMapTypeSpinner = (Spinner) findViewById(R.id.settingsMapTypeSpinner);
        mMapTypeSpinner.setSelection(Model.getInstance().getSettings().getMapTypeIndex(Model.getInstance().getSettings().getMapType()));
        mMapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model.getInstance().getSettings().setMapType((String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mResyncDataButton = (LinearLayout) findViewById(R.id.settingsReSyncButton);
        mResyncDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reSync();
            }
        });

        mLogoutButton = (LinearLayout) findViewById(R.id.settingsLogoutButton);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    /**
     * Logs the user out and destroys their session
     * Returns to the main activity, displaying the login fragment so the user can re-login if they desire
     */
    private void logout() {
        Model.getInstance().setLoggedIn(false);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getBaseContext().startActivity(intent);
        this.finish();
    }

    /**
     * Re-syncs with the Family Map Server. This destroys locally cached family data and
     * re-loads all required data from the family map server.
     * This does not change any of the application settings.
     * <p>
     * If Re-Sync Fails, it displays a Toast with the error message, and remains on the settings activity
     * otherwise it returns to the main activity, but with the map fragment displayed.
     */
    private void reSync() {
        //Keep Current Settings for Now
        Settings currentSettings = Model.getInstance().getSettings();
        //Keep the logged in User
        Person user = Model.getInstance().getUser();
        //Destroy the Local Cache
        Model.getInstance().reset();
        //Restore the User in the model & Confirm we're logged in
        Model.getInstance().setUser(user);
        Model.getInstance().setLoggedIn(true);

        //Create a ReSync Task to re-sync with the Family Map Server
        new ResyncTask(this).execute();

        //Restore current Settings
        Model.getInstance().setSettings(currentSettings);
    }

    @Override
    public void onResyncSuccess() {
        if (Model.getInstance().isLoadEventSuccess() && Model.getInstance().isLoadPersonSuccess()) {
            //Return to the Main Activity
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getBaseContext().startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onResyncFailure(String errorMessage) {
        //Otherwise, print a toast with the error:
        Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
