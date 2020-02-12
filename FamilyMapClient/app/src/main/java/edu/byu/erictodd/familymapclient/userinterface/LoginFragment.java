package edu.byu.erictodd.familymapclient.userinterface;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.design.widget.*;
import android.widget.RadioGroup;
import android.widget.Toast;

import edu.byu.erictodd.familymapclient.R;
import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.model.UserLoginRequest;
import edu.byu.erictodd.familymapclient.model.UserRegisterRequest;
import edu.byu.erictodd.familymapclient.model.UserResult;
import edu.byu.erictodd.familymapclient.network.EventTask;
import edu.byu.erictodd.familymapclient.network.LoginTask;
import edu.byu.erictodd.familymapclient.network.PersonTask;
import edu.byu.erictodd.familymapclient.network.RegisterTask;
import edu.byu.erictodd.familymapclient.network.ServerProxy;

/**
 * A login fragment that handles the login screen and display,
 * as well as calling tasks to retrieve data if the login or register is successful
 */
public class LoginFragment extends Fragment implements RegisterTask.RegisterInterface, LoginTask.LoginInterface, PersonTask.PersonInterface, EventTask.EventInterface {
    /**
     * A string TAG for logging errors inside the fragment
     */
    private final String TAG = "LoginFragment";

    /**
     * The loginInterface requires the implementation by its parent of what happens when a login is successful.
     */
    public interface LoginInterface {
        void onLoggedIn();
    }

    /**
     * Required default contsructor
     */
    public LoginFragment() {
    }

    /**
     * A pointer to the mainActivity who is hosting the loginFragment
     */
    private LoginInterface parent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInterface) {
            parent = (LoginInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * A view that allows the user to input the server host
     */
    private TextInputEditText serverHostEditText;
    /**
     * A view that allows the user to input the port number
     */
    private TextInputEditText serverPortEditText;
    /**
     * A view that allows the user to input their username
     */
    private TextInputEditText userNameEditText;
    /**
     * A view that allows the user to input their password
     */
    private TextInputEditText passwordEditText;
    /**
     * A view that allows the user to input their first name
     */
    private TextInputEditText firstNameEditText;
    /**
     * A view that allows the user to input their last name
     */
    private TextInputEditText lastNameEditText;
    /**
     * A view that allows the user to input their first name
     */
    private TextInputEditText emailEditText;
    /**
     * A radioGroup that allows the user to select between male and female
     */
    private RadioGroup genderSelect;
    /**
     * A button for logging in the user
     */
    private Button loginButton;
    /**
     * A button for registering a new user
     */
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Get pointers to all the UI fields
        serverHostEditText = (TextInputEditText) view.findViewById(R.id.serverHostEditText);
        serverPortEditText = (TextInputEditText) view.findViewById(R.id.serverPortEditText);
        userNameEditText = (TextInputEditText) view.findViewById(R.id.userNameEditText);
        passwordEditText = (TextInputEditText) view.findViewById(R.id.passwordEditText);
        firstNameEditText = (TextInputEditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (TextInputEditText) view.findViewById(R.id.lastNameEditText);
        emailEditText = (TextInputEditText) view.findViewById(R.id.emailEditText);

        genderSelect = (RadioGroup) view.findViewById(R.id.genderGroup);

        loginButton = (Button) view.findViewById(R.id.signInButton);
        registerButton = (Button) view.findViewById(R.id.registerButton);

        //Wire up Listeners for each UI element
        serverHostEditText.addTextChangedListener(mLoginTextWatcher);
        serverPortEditText.addTextChangedListener(mLoginTextWatcher);
        userNameEditText.addTextChangedListener(mLoginTextWatcher);
        passwordEditText.addTextChangedListener(mLoginTextWatcher);

        serverHostEditText.addTextChangedListener(mRegisterTextWatcher);
        serverPortEditText.addTextChangedListener(mRegisterTextWatcher);
        userNameEditText.addTextChangedListener(mRegisterTextWatcher);
        passwordEditText.addTextChangedListener(mRegisterTextWatcher);
        firstNameEditText.addTextChangedListener(mRegisterTextWatcher);
        lastNameEditText.addTextChangedListener(mRegisterTextWatcher);
        emailEditText.addTextChangedListener(mRegisterTextWatcher);

        genderSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkRegisterForEmptyValues();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the Server Hostname and Port Number to the Server Proxy
                ServerProxy.getInstance().setHostname(serverHostEditText.getText().toString());
                ServerProxy.getInstance().setPortNumber(serverPortEditText.getText().toString());
                new LoginTask(LoginFragment.this).execute(getLoginRequest());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the Server Hostname and Port Number to the Server Proxy
                ServerProxy.getInstance().setHostname(serverHostEditText.getText().toString());
                ServerProxy.getInstance().setPortNumber(serverPortEditText.getText().toString());
                new RegisterTask(LoginFragment.this).execute(getRegisterRequest());
            }
        });

        //Set the Defaults for Testing Purposes:
        serverHostEditText.setText(R.string.tempLoginHostname);
        serverPortEditText.setText(R.string.tempLoginPortNumber);
        userNameEditText.setText(R.string.tempLoginUserName);
        passwordEditText.setText(R.string.tempLoginPassword);
        firstNameEditText.setText(R.string.tempLoginFirstName);
        lastNameEditText.setText(R.string.tempLoginLastName);
        emailEditText.setText(R.string.tempLoginEmail);
        genderSelect.check(R.id.maleRadioButton);

        //Check all the UI components on create view to disable the necessary buttons
        checkLoginForEmptyValues();
        checkRegisterForEmptyValues();

        return view;
    }

    private TextWatcher mLoginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // check Login Fields For Empty Values
            checkLoginForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher mRegisterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // check Login Fields For Empty Values
            checkRegisterForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * Disables the login button depending on whether or not the corresponding fields are filled in or not.
     */
    void checkLoginForEmptyValues() {
        String hostname = serverHostEditText.getText().toString();
        String portNumber = serverPortEditText.getText().toString();
        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (hostname.equals("") || portNumber.equals("") || username.equals("") || password.equals("")) {
            loginButton.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
        }
    }

    /**
     * Disables the register button depending on whether or not all the fields are filled in or not.
     */
    void checkRegisterForEmptyValues() {
        String hostname = serverHostEditText.getText().toString();
        String portNumber = serverPortEditText.getText().toString();
        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String gender;
        if (genderSelect.getCheckedRadioButtonId() == R.id.maleRadioButton) {
            gender = "m";
        } else if (genderSelect.getCheckedRadioButtonId() == R.id.femaleRadioButton) {
            gender = "f";
        } else {
            gender = "";
        }

        if (hostname.equals("") || portNumber.equals("") || username.equals("") || password.equals("")
                || firstName.equals("") || lastName.equals("") || email.equals("") || gender.equals("")) {
            registerButton.setEnabled(false);
        } else {
            registerButton.setEnabled(true);
        }
    }

    /**
     * Gets the values entered by the user to login and returns a login request to be sent to the
     * serverProxy.
     *
     * @return the loginRequest to be sent to the server.
     */
    public UserLoginRequest getLoginRequest() {
        UserLoginRequest loginRequest = new UserLoginRequest();

        //Create the LoginRequest by getting the information
        loginRequest.setUsername(userNameEditText.getText().toString());
        loginRequest.setPassword(passwordEditText.getText().toString());

        //Return the loginRequest
        return loginRequest;
    }

    /**
     * Gets the values entered by the user to register and returns a registerRequest to be sent to the
     * serverProxy.
     *
     * @return the registerRequest to be sent to the server.
     */
    public UserRegisterRequest getRegisterRequest() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();

        //Create a Register Request Object to send to the Server
        registerRequest.setUsername(userNameEditText.getText().toString());
        registerRequest.setPassword(passwordEditText.getText().toString());
        registerRequest.setFirstName(firstNameEditText.getText().toString());
        registerRequest.setLastName(lastNameEditText.getText().toString());
        registerRequest.setEmail(emailEditText.getText().toString());
        //Decide how to get Gender
        if (genderSelect.getCheckedRadioButtonId() == R.id.femaleRadioButton) {
            registerRequest.setGender("f");
        } else if (genderSelect.getCheckedRadioButtonId() == R.id.maleRadioButton) {
            registerRequest.setGender("m");
        }
        return registerRequest;
    }

    @Override
    public void onLoginSuccess(UserResult loginResult) {
        //Get Persons and Events and Store them in the local cache (model)
        if (ServerProxy.getInstance().getCurrentUserToken() != null) {
            new PersonTask(LoginFragment.this).execute(ServerProxy.getInstance().getCurrentUserToken());
            new EventTask(LoginFragment.this).execute(ServerProxy.getInstance().getCurrentUserToken());
        }
    }

    @Override
    public void onLoginFailure(UserResult loginResult) {
        //Display the Error message
        Toast.makeText(getContext(), loginResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterSuccess(UserResult registerResult) {
        //Get Persons and Events & Store them in the Local Cache (Model)
        if (ServerProxy.getInstance().getCurrentUserToken() != null) {
            new PersonTask(LoginFragment.this).execute(ServerProxy.getInstance().getCurrentUserToken());
            new EventTask(LoginFragment.this).execute(ServerProxy.getInstance().getCurrentUserToken());
        }
    }

    @Override
    public void onRegisterFailure(UserResult registerResult) {
        //Display the Error Message
        Toast.makeText(getContext(), registerResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPersonsRetrievalSuccess(PersonResult personResult) {
        Model.getInstance().setLoadPersonSuccess(true);
    }

    @Override
    public void onPersonsRetrievalFailure(PersonResult personResult) {
        Model.getInstance().setLoadPersonSuccess(false);
    }

    @Override
    public void onEventRetrievalSuccess(EventResult eventResult) {
        //Record in the Model that Event Retrieval was successful, and since it was after person retrieval,
        //we login if both succeeded
        Model.getInstance().setLoadEventSuccess(true);
        //Notify the Main Activity we are Logged In
        if (Model.getInstance().isLoadPersonSuccess() && Model.getInstance().isLoadEventSuccess()) {
            parent.onLoggedIn();
        } else {
            Toast.makeText(getContext(), R.string.fragmentLoginSyncErrorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventRetrievalFailure(EventResult eventResult) {
        Model.getInstance().setLoadEventSuccess(false);
        Toast.makeText(getContext(), R.string.fragmentLoginSyncErrorMessage, Toast.LENGTH_SHORT).show();
    }
}
