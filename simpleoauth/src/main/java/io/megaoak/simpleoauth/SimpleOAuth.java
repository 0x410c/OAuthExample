package io.megaoak.simpleoauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by 0x410c on 04/05/16.
 */
public class SimpleOAuth implements WebViewAuthResult{
    SERVER authFor;                 //server to perform authorisation for
    SimpleOAuthResult callback;     // result callback
    Activity cnxt;                  // context of the calling activity
    AlertDialog dialog;             // dialog which will have our webvoew
    String authCode;                // auth code recieved from first phase of authentication

    static String CLIENT_ID;        //client id of the application registered from the network
    static String CLIENT_SECRET;    // client secret of the application registeres
    static String REDIRECT_URI;     // redirection url
    static String GRANT_TYPE;       // grant type
    static String TOKEN_URL;        // service url to ask access token from
    static String OAUTH_URL;        // authorisation code provider service
    static String OAUTH_SCOPE;      // scope asked for


    //interface to pass back the result ot caller
    public interface SimpleOAuthResult{
        //send TokenData object, containing status and codes if success else error message
        void AuthResult(TokenData token);
    }

    //authorisation result flag
    public enum AuthResult{
        SUCCESS, FAILED, CANCELLED
    }

    //class which holds the recieved status and data
    public class TokenData{
        public String accessToken;      // access token
        public String refreshToken;     // refresh token to regenerate access token
        public String expiryDate;       // time the access token is valid
        public String errMessage;       // err message if failed
        public AuthResult status;       // authorisation status
    }

    //servers we support
    public enum SERVER{
        GOOGLE,     //done
        OUTLOOK,    //working on
        BOX,        //proposed
        DROPBOX,    //proposed
        GDRIVE,     // done left scope testing
        EXCHANGE,   // proposed
        OFFICE365   // proposed
    }


    //our constructor, takes the context and the config data of the server to authorise for
    public SimpleOAuth(Activity act, SimpleOAuthConfig config)
    {
        authFor = config.authFor;       // set the authorisation server
        //set auth and token urls according to the server we are authorising for
        switch (authFor){
            case GOOGLE:
                TOKEN_URL       =   Constants.GOOGLE_TOKENURL;
                OAUTH_URL       =   Constants.GOOGLE_AUTHURL;
                break;
            case OUTLOOK:
                TOKEN_URL   =   Constants.OUTLOOK_TOKENURL;
                OAUTH_URL   =   Constants.OUTLOOK_AUTHURL;
                break;
            case BOX:
                TOKEN_URL   =   Constants.BOX_TOKENURL;
                OAUTH_URL   =   Constants.BOX_AUTHURL;
                break;
            case GDRIVE:
                TOKEN_URL   =   Constants.GDRIVE_TOKENURL;
                OAUTH_URL   =   Constants.GDRIVE_AUTHURL;
                break;
            case DROPBOX:
                TOKEN_URL   =   Constants.DROPBOX_TOKENURL;
                OAUTH_URL   =   Constants.DROPBOX_AUTHURL;
                break;
            case EXCHANGE:
                TOKEN_URL   =   Constants.EXCHANGE_TOKENURL;
                OAUTH_URL   =   Constants.EXCHANGE_AUTHURL;
                break;
            case OFFICE365:
                TOKEN_URL   =   Constants.OFFICE365_TOKENURL;
                OAUTH_URL   =   Constants.OFFICE365_AUTHURL;
                break;
            default:
                break;
        }

        // get the details of client
        CLIENT_ID       =   config.CLIENT_ID;
        CLIENT_SECRET   =   config.CLIENT_SECRET;
        GRANT_TYPE      =   config.GRANT_TYPE;

        // outlook needs its paramaters to be url encoded
        if(SERVER.OUTLOOK == config.authFor) {
            try {
                OAUTH_SCOPE = URLEncoder.encode(config.OAUTH_SCOPE, "utf-8");
                REDIRECT_URI = URLEncoder.encode(config.REDIRECT_URI, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else    //google is ok without any url encoding
        {
            OAUTH_SCOPE = config.OAUTH_SCOPE;
            REDIRECT_URI = config.REDIRECT_URI;
        }
        // save the context, almost forgot :P
        cnxt = act;
    }

    // perform the authorisation
    public void auth(SimpleOAuthResult cbck)
    {
        //save the callback address
        callback = cbck;
        //prepare the dialog containing our web view that will server the authorisation page
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(cnxt);

        //Create a linear vertical layout
        LinearLayout ll = new LinearLayout(cnxt);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //preapare a webview with our webview client
        WebView webView = new WebView(cnxt){
            // android webview has a bug, webview rendered page input fields dont get focus, this is a fix
            @Override
            public boolean onCheckIsTextEditor() {
                return true;
            }

        };
        //part of the fix
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setFocusable(true);

        //enable javascript
        webView.getSettings().setJavaScriptEnabled(true);

        //set our inherited webview client to override url requests
        webView.setWebViewClient(new OAuthWebViewClient(cnxt, this));
        //set view paramaters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800, 0.98f);
        webView.setLayoutParams(params);
        //add webview to the linear layout
        ll.addView(webView);

        //a cancel button at the bottom of dialog
        Button cancelAuth = new Button(cnxt);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.02f);
        cancelAuth.setLayoutParams(param);
        cancelAuth.setText("Cancel");
        cancelAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //add button to layout
        ll.addView(cancelAuth);

        //set the view for the dialog we just created
        dialogBuilder.setView(ll);
        //create the dialog instance
        dialog = dialogBuilder.create();

        //start the authorisation process
        webView.loadUrl(OAUTH_URL+"?redirect_uri="+REDIRECT_URI+"&response_type=code&client_id="+CLIENT_ID+"&scope="+OAUTH_SCOPE);
        //show the dialog
        dialog.show();
    }

    // authorisation code is received here if successful
    @Override
    public void authorisationCodeReceived(String aCode) {
        // no need of webview now dismiss the dialog
        dialog.dismiss();
        //save the received auth code
        authCode = aCode;
        //if not null
        if(authCode!=null)
        {
            //proceeed to get acess token :)
            //Log.i("SimpleOAuth",(authCode==null?"":authCode));
            new TokenGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else    //auth process failed,call back the caller
        {
            TokenData tmp   =   new TokenData();
            tmp.status      =   AuthResult.FAILED;      //auth failed
            tmp.errMessage  =   "Authorisation Failed, Can't Get Auth Code From Server";
            tmp.expiryDate  =   null;
            tmp.refreshToken =  null;
            tmp.accessToken =   null;
            callback.AuthResult(tmp);
        }
    }


    // request the token from the server using the auth code received
    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //usual fuck
            pDialog = new ProgressDialog(cnxt);
            pDialog.setMessage("Asking For Permissions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            // our request class
            GetAccessToken jParser = new GetAccessToken(authFor);
            JSONObject json = jParser.gettoken(TOKEN_URL,authCode,CLIENT_ID,CLIENT_SECRET,REDIRECT_URI,GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            // if we received the data
            if (json != null){
                try {
                    // success my friend, sweet success
                    TokenData tmp = new TokenData();
                    tmp.errMessage = null;
                    tmp.status = AuthResult.SUCCESS;
                    tmp.accessToken = json.getString("access_token");
                    tmp.refreshToken = json.getString("refresh_token");
                    tmp.expiryDate = json.getString("expires_in");

                    // callback the caller
                    callback.AuthResult(tmp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // another face of failure :P
                    TokenData tmp   =   new TokenData();
                    tmp.status      =   AuthResult.FAILED;      //auth failed
                    tmp.errMessage  =   "Authorisation Failed, Json Parse Failure.";
                    tmp.expiryDate  =   null;
                    tmp.refreshToken =  null;
                    tmp.accessToken =   null;
                    callback.AuthResult(tmp);
                }
            }else{// failed again bro :o
                TokenData tmp = new TokenData();
                tmp.accessToken = null;
                tmp.errMessage = "Authorisation Failed, Nothing received after redirection";
                tmp.status = AuthResult.FAILED;
                tmp.refreshToken = null;
                tmp.expiryDate = null;
                callback.AuthResult(tmp);
            }
        }
    }


}

