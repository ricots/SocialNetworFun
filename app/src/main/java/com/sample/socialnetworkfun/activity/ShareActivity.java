package com.sample.socialnetworkfun.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.sample.socialnetworkfun.AppConfig;
import com.sample.socialnetworkfun.R;
import com.sample.socialnetworkfun.utils.AppConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ShareActivity";
    private ImageView ivFbShare;
    private ImageView ivGplusShare;
    private ImageView ivTwitterShare;
    private Toolbar toolbar;

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        setContentView(R.layout.activity_share);
        findViews();

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                Log.e(TAG, "onSuccess: " + result.toString() );

            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error.toString() );
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void findViews() {
        ivFbShare = (ImageView)findViewById( R.id.ivFbShare );
        ivGplusShare = (ImageView)findViewById( R.id.ivGplusShare );
        ivTwitterShare = (ImageView)findViewById( R.id.ivTwitterShare );
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setClickListeners();
        checkSocialAuthentication();
    }

    private void setClickListeners() {

        ivFbShare.setOnClickListener(this);
        ivGplusShare.setOnClickListener(this);
        ivTwitterShare.setOnClickListener(this);
    }

    private void checkSocialAuthentication() {


        if(AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With,0)
                == AppConstants.SocialKeys.KEY_FB){

            ivFbShare.setVisibility(View.VISIBLE);

        }else if(AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With,0)
                == AppConstants.SocialKeys.KEY_G_PLUS){

            ivGplusShare.setVisibility(View.VISIBLE);

        }else if(AppConfig.preferenceGetInteger(AppConstants.SharedPreferenceKeys.LoggedIn_With,0)
                == AppConstants.SocialKeys.KEY_TWITTER){

            ivTwitterShare.setVisibility(View.VISIBLE);

        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ivFbShare:


                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Hello Facebook")
                            .setContentDescription(
                                    "The 'Hello Facebook' sample  showcases simple Facebook integration")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();

                    shareDialog.show(linkContent);
                }


                break;

            case R.id.ivGplusShare:

               /* PlusShare.Builder share = new PlusShare.Builder(this);
                share.setText("hello everyone!");
                share.addStream(selectedImage);
                share.setType(mime);
                startActivityForResult(share.getIntent(), REQ_START_SHARE);*/

              /*  Intent shareIntent = new PlusShare.Builder(this)
                        .setType("text/plain")
                        .setText("Welcome to the Google+ platform.")
                        .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                        .getIntent();

                startActivityForResult(shareIntent, 0);*/

                break;



            case R.id.ivTwitterShare:

                new updateTwitterStatus().execute();

                break;

        }
    }
    private class updateTwitterStatus extends AsyncTask<String, String, Void> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShareActivity.this);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {
            try {
                final ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);

                // Access Token
                final String access_token = AppConfig.preferenceGetString(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                final String access_token_secret = AppConfig.preferenceGetString(AppConstants.SharedPreferenceKeys.PREF_KEY_OAUTH_SECRET, "");


                final AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                final Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                final StatusUpdate statusUpdate = new StatusUpdate("Sharing on Twitter with Social Network fun... :) ");
                InputStream is = null;
                try {
                    is = new URL("http://pbs.twimg.com/profile_images/691961071561744384/OQEwvsla_normal.jpg").openStream();
                } catch (final MalformedURLException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                statusUpdate.setMedia("test.jpg", is);
                final twitter4j.Status response = twitter.updateStatus(statusUpdate);
                Log.d("Status", response.getText());
            } catch (final TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

    }

}
