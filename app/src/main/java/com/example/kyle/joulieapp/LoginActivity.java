package com.example.kyle.joulieapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;


import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    static final String LOG_TAG = LoginActivity.class.getCanonicalName();

    //// TODO: 2016-12-03 put these values in better place
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a25zcl5ezj00nu.iot.us-west-2.amazonaws.com";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "us-west-2:c258b633-9752-4ae3-8e7e-6612a4159327";
    // Name of the AWS IoT policy to attach to a newly created certificate
    private static final String AWS_IOT_POLICY_NAME = "testpolicy";
    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_WEST_2;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "iot_keystore";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "password";
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "default";

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginButton loginButton;

    private CallbackManager callbackManager;

    public static CognitoCachingCredentialsProvider credentialsProvider;
    public static AWSIotClient androidIoTClient; // TODO: 2016-12-03 probably should'nt be public static, move somewhere better
    public static AWSIotMqttManager mqttManager; // TODO: 2016-12-03 probably should'nt be public static, move somewhere better
    private String clientId;
    private String keystorePath;
    private String keystoreName;
    private String keystorePassword;

    private KeyStore clientKeyStore = null;
    private String certificateId;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setup aws login
        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );

        //setup facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // get credentials from AWS
                AwsLogin(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // App code
                //// TODO: 2016-12-03 handle cancel
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                //// TODO: 2016-12-03 handle errors 
            }
        });

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setText(DUMMY_CREDENTIALS[0].split(":")[0]);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(DUMMY_CREDENTIALS[0].split(":")[1]);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()){
            AwsLogin(AccessToken.getCurrentAccessToken().getToken());
        }

        // Initialize application
        AppHelper.init(getApplicationContext());
        findCurrent();

    }

    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        email = user.getUserId();
        if(email != null) {
            AppHelper.setUser(email);
            mEmailView.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    // Implement authentication handler,
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice device) {
            // Authentication was successful, the "userSession" will have the current valid tokens
            // Time to do awesome stuff

            AppHelper.setCurrSession(userSession);
            AppHelper.newDevice(device);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            openMainActivity();
        }

        @Override
        public void getAuthenticationDetails(final AuthenticationContinuation continuation, final String userID) {
            // User authentication details, userId and password are required to continue.
            // Use the "continuation" object to pass the user authentication details

            // After the user authentication details are available, wrap them in an AuthenticationDetails class
            // Along with userId and password, parameters for user pools for Lambda can be passed here
            // The validation parameters "validationParameters" are passed in as a Map<String, String>
            AuthenticationDetails authDetails = new AuthenticationDetails(email, password, null);


            // Now allow the authentication to continue
            continuation.setAuthenticationDetails(authDetails);
            continuation.continueTask();
        }

        @Override
        public void getMFACode(final MultiFactorAuthenticationContinuation continuation) {
            // Multi-factor authentication is required to authenticate
            // A code was sent to the user, use the code to continue with the authentication


            // Find where the code was sent to
            String codeSentHere = continuation.getParameters().toString();

            // When the verification code is available, continue to authenticate
            //continuation.setMfaCode(code);
            continuation.continueTask();
        }

        @Override
        public void authenticationChallenge(final ChallengeContinuation continuation) {
            // A custom challenge has to be solved to authenticate
            int i = 0;
            // Set the challenge responses

            // Call continueTask() method to respond to the challenge and continue with authentication.
        }



        @Override
        public void onFailure(final Exception exception) {
            // Authentication failed, probe exception for the cause
            int i = 0;
        }
    };

    private void setupAWSClient(){

        clientId = UUID.randomUUID().toString();

        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        //// TODO: 2016-12-03 implement lastWillAndTestament
        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);


        // IoT Client (for creation of certificate if needed)
        androidIoTClient = new AWSIotClient(credentialsProvider);
        androidIoTClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                    //btnConnect.setEnabled(true);
                    IotConnect();
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if(clientKeyStore == null){
            //start new thread to create new certificate and key
            new CreateNewCert().execute(null,null,null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void AwsLogin(String token){
        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com",token);
        credentialsProvider.setLogins(logins);
        
        //start new thread to get credentials from AWS server
        new GetAWSCredentials().execute(null, null, null);
    }

    private void IotConnect(){
        mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(AWSIotMqttClientStatus status, Throwable throwable) {
                Log.i(LOG_TAG, status.name());
                if(status == AWSIotMqttClientStatus.Connected) {

                }
            }
        });
        openMainActivity();
    }

    private void openMainActivity(){
        showProgress(true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        showProgress(false);        
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            AppHelper.setUser(email);
            AppHelper.getPool().getUser(email).getSessionInBackground(authenticationHandler);


        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class GetAWSCredentials extends AsyncTask<Object, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            AWSSessionCredentials credentials = credentialsProvider.getCredentials();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                setupAWSClient();
                //openMainActivity();
            }
        }
    }

    private class CreateNewCert extends AsyncTask<Object, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            try {
                // Create a new private key and certificate. This call
                // creates both on the server and returns them to the
                // device.
                CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                        new CreateKeysAndCertificateRequest();
                createKeysAndCertificateRequest.setSetAsActive(true);
                final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                createKeysAndCertificateResult =
                        androidIoTClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                Log.i(LOG_TAG,
                        "Cert ID: " +
                                createKeysAndCertificateResult.getCertificateId() +
                                " created.");

                // store in keystore for use in MQTT client
                // saved as alias "default" so a new certificate isn't
                // generated each run of this application
                AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                        createKeysAndCertificateResult.getCertificatePem(),
                        createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                        keystorePath, keystoreName, keystorePassword);

                // load keystore from file into memory to pass on
                // connection
                clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                        keystorePath, keystoreName, keystorePassword);

                // Attach a policy to the newly created certificate.
                // This flow assumes the policy was already created in
                // AWS IoT and we are now just attaching it to the
                // certificate.
                AttachPrincipalPolicyRequest policyAttachRequest =
                        new AttachPrincipalPolicyRequest();
                policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                        .getCertificateArn());
                androidIoTClient.attachPrincipalPolicy(policyAttachRequest);

            } catch (Exception e) {
                Log.e(LOG_TAG,
                        "Exception occurred when generating new private key and certificate.",
                        e);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
            // TODO: 2016-12-03 allow signin
                IotConnect();
            }
        }
    }


}

