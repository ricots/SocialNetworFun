package com.sample.socialnetworkfun.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sample.socialnetworkfun.AppConfig;
import com.sample.socialnetworkfun.R;
import com.sample.socialnetworkfun.model.UserLoginDetails;
import com.sample.socialnetworkfun.utils.AppConstants;
import com.sample.socialnetworkfun.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout layoutFullFb;
    private LinearLayout layoutFullGplus;
    private LinearLayout layoutFullTwitter;
    private int socialRequestKey = 0;

    //Facebook
    CallbackManager callbackManager;

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;

    //GPlus
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        doLogout();

        startGplusLogin();

        findViews();

        initializeViews();

        startFbLogin();

        startTwitterLogin();



    }


    private void doLogout() {


        if (AppConfig.preferenceGetBoolean(AppConstants.SharedPreferenceKeys.IS_LoggedIn, false)) {


            if (AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, 0)
                    == AppConstants.SocialKeys.KEY_FB) {

                LoginManager.getInstance().logOut();
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.IS_LoggedIn);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.LoggedIn_With);


            } else if (AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, 0)
                    == AppConstants.SocialKeys.KEY_G_PLUS) {


                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.IS_LoggedIn);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.LoggedIn_With);
               /* if (mGoogleApiClient != null)
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);*/


            } else if (AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, 0)
                    == AppConstants.SocialKeys.KEY_TWITTER) {

                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_TOKEN);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_SECRET);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.PREF_KEY_TWITTER_LOGIN);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.IS_LoggedIn);
                AppConfig.preferenceRemoveKey(AppConstants.SharedPreferenceKeys.LoggedIn_With);

            }
        }


    }

    private void findViews() {


        layoutFullFb = (LinearLayout) findViewById(R.id.layout_full_fb);
        layoutFullGplus = (LinearLayout) findViewById(R.id.layout_full_gplus);
        layoutFullTwitter = (LinearLayout) findViewById(R.id.layout_full_twitter);


    }

    private void initializeViews() {

        layoutFullFb.setOnClickListener(LoginActivity.this);
        layoutFullGplus.setOnClickListener(LoginActivity.this);
        layoutFullTwitter.setOnClickListener(LoginActivity.this);


    }

    private void startFbLogin() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,

                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        final Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,location");

                        final AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                        try {

                                            UserLoginDetails userData = new UserLoginDetails();
                                            Log.e(TAG, user.toString());
                                            Log.e(TAG, graphResponse.toString());
                                            userData.setFbID(user.optString("id"));
                                            userData.setEmail(user.optString("email"));
                                            userData.setFullName(user.optString("first_name") + " " + user.optString("last_name"));
                                            //fbUser.setCity(user.getJSONObject("location").getString("name"));
                                            userData.setIsFbLogin(true);
                                            userData.setIsSocial(true);
                                            userData.setIsAndroid(true);
                                            userData.setUserImageUrl("https://graph.facebook.com/" + user.optString("id") + "/picture?type=normal");

                                            Utility.showToast(LoginActivity.this, "Connected");

                                            AppConfig.preferencePutInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, AppConstants.SocialKeys.KEY_FB);
                                            AppConfig.preferencePutBoolean(AppConstants.SharedPreferenceKeys.IS_LoggedIn, true);

                                            hideProgressDialog();

                                           /* Intent i = new Intent(LoginActivity.this, ShareActivity.class);
                                            startActivity(i);
                                            finish();
                                            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
*/

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code

                        Log.e(TAG, "onCancel called ");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code

                        Utility.showToast(LoginActivity.this, "Error: " + exception.toString());

                        Log.e(TAG, "onError: " + exception.toString());
                    }
                });

    }

    private void loginToFacebook() {

        socialRequestKey = AppConstants.SocialKeys.KEY_FB;
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");

        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
    }

    private void startTwitterLogin() {


        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if (!AppConfig.preferenceGetBoolean(AppConstants.SharedPreferenceKeys.PREF_KEY_TWITTER_LOGIN, false)) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(AppConstants.TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri
                        .getQueryParameter(AppConstants.URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token
                    twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);

                    // Shared Preferences
                    AppConfig.preferencePutString(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    AppConfig.preferencePutString(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                    AppConfig.preferencePutBoolean(AppConstants.SharedPreferenceKeys.PREF_KEY_TWITTER_LOGIN, true);

                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

                    // Getting user details from twitter
                    // For now i am getting his name only
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);
                    //  String username = user.getName();

                    Utility.showToast(LoginActivity.this, "Connected");

                    AppConfig.preferencePutInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, AppConstants.SocialKeys.KEY_TWITTER);
                    AppConfig.preferencePutBoolean(AppConstants.SharedPreferenceKeys.IS_LoggedIn, true);

                    hideProgressDialog();

                   /* Intent i = new Intent(LoginActivity.this, ShareActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/


                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        } else {
            Log.e(TAG, "startTwitterLogin: ");
        }

    }

    private void loginToTwitter() {

        socialRequestKey = AppConstants.SocialKeys.KEY_TWITTER;

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter
                    .getOAuthRequestToken(AppConstants.TWITTER_CALLBACK_URL);
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(requestToken.getAuthenticationURL())));
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private void startGplusLogin() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    private void loginToGPlus() {

        socialRequestKey = AppConstants.SocialKeys.KEY_G_PLUS;

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, AppConstants.SocialKeys.KEY_G_PLUS);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (socialRequestKey == AppConstants.SocialKeys.KEY_FB) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == AppConstants.SocialKeys.KEY_G_PLUS) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.layout_full_fb:

                if (Utility.isConnectivityAvailable(LoginActivity.this)) {

                    showProgressDialog();
                    loginToFacebook();

                } else {
                    Utility.showToast(LoginActivity.this, "No internet connection available...");
                }


                break;

            case R.id.layout_full_gplus:

                showProgressDialog();
                loginToGPlus();

                break;

            case R.id.layout_full_twitter:

                showProgressDialog();
                loginToTwitter();


                break;
        }

    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();

            Utility.showToast(LoginActivity.this, "Connected ");

            AppConfig.preferencePutInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With, AppConstants.SocialKeys.KEY_G_PLUS);
            AppConfig.preferencePutBoolean(AppConstants.SharedPreferenceKeys.IS_LoggedIn, true);

            hideProgressDialog();

           /* Intent i = new Intent(LoginActivity.this, ShareActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);*/

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "Google plus onConnectionFailed: " + connectionResult.toString());

    }

   /* @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
