package com.sample.socialnetworkfun.utils;

public class AppConstants {


    public static String TWITTER_CONSUMER_KEY = "M8gXwsQv4IpBQGNWZzSkka7Dh";
    public static String TWITTER_CONSUMER_SECRET = "v9gktDiFtKOAHmZF8f2dn4OdOJmB3gCIxWbFvSupM95FoPMEeK";
    public static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";


    public class SocialKeys {

        public static final int KEY_FB = 1;
        public static final int KEY_G_PLUS = 2;
        public static final int KEY_TWITTER = 3;

    }


    public class SharedPreferenceKeys {

        public static final String IS_LoggedIn = "IsLoggedIn";
        public static final String LoggedIn_With = "LoggedIn_With";
        public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
        public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
        public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    }


}
