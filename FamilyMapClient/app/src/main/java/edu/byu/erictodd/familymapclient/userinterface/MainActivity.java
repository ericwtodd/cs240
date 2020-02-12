package edu.byu.erictodd.familymapclient.userinterface;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.Model;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginInterface {
    /**
     * A string TAG for logging errors
     */
    private static final String TAG = "MainActivity";
    /**
     * The main login fragment to be displayed before a user has logged in.
     */
    private LoginFragment loginFragment;
    /**
     * The mapFragment to be displayed after the user has logged in.
     */
    private MapFragment mapFragment;
    /**
     * A flag determining whether or not the user has logged in, locally.
     */
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Allows us to use pre-drawn icons to not have to draw our own
        Iconify.with(new FontAwesomeModule());

        //Check if we're logged in or not
        if (Model.getInstance().isLoggedIn()) {
            loggedIn = true;
        } else {
            loggedIn = false;
        }

        Log.d(TAG, "onCreate(Bundle) Called");

        //Get a Pointer to the LoginFragment and Display it
        FragmentManager fm = this.getSupportFragmentManager();
        //If we haven't logged in yet, bring up the login fragment
        if (!loggedIn) {
            loginFragment = (LoginFragment) fm.findFragmentById(R.id.fragment_login);
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_login, loginFragment)
                        .commit();
            }
        }
        //Otherwise we bring up the map fragment
        else if (loggedIn) {
            mapFragment = (MapFragment) fm.findFragmentById(R.id.fragment_map);
            if (mapFragment == null) {
                mapFragment = new MapFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_map, mapFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onLoggedIn() {
        //Set loggedIn to true
        loggedIn = true;
        Model.getInstance().setLoggedIn(true);
        //Switch views to the map fragment
        FragmentManager fm = this.getSupportFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.fragment_map);
        if (mapFragment == null) {
            mapFragment = new MapFragment();
            fm.beginTransaction()
                    .remove(loginFragment)
                    .add(R.id.fragment_map, mapFragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
