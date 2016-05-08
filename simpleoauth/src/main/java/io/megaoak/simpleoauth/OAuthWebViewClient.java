package io.megaoak.simpleoauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by 0x410c on 04/05/16.
 */
public class OAuthWebViewClient extends WebViewClient {
    boolean authComplete = false;
    Intent resultIntent = new Intent();
    String authCode;
    Activity cnxt;
    WebViewAuthResult callback;

    public OAuthWebViewClient(Activity con, WebViewAuthResult cbck)
    {
        cnxt = con;
        callback = cbck;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon){
        super.onPageStarted(view, url, favicon);
        Log.d("WebView:",url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d("WebView: done",url);
        if (url.contains("?code=") && !authComplete) {
            Uri uri = Uri.parse(url);
            try {
                authCode = uri.getQueryParameter("code");
            }
            catch (Exception e)
            {
                url = "http://localhost/abc" + url.substring(url.indexOf("?"),url.length());
                Log.d("WebView: done",url);
                uri = Uri.parse(url);
                authCode = uri.getQueryParameter("code");
            }
           // Log.i("", "CODE : " + authCode);
            authComplete = true;
            callback.authorisationCodeReceived(authCode);
        }else if(url.contains("error=access_denied")){
       //     Log.i("", "ACCESS_DENIED_HERE");
            authComplete = true;
            callback.authorisationCodeReceived(null);
            Toast.makeText(cnxt, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }
}
