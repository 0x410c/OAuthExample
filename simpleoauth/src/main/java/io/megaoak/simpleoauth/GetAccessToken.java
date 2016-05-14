package io.megaoak.simpleoauth;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 0x410c on 05/05/16.
 */
//@// TODO: 06/05/16 update the code bc
// org.apache.http.legacy has been deprecated, use URL() and reimplement this shit

/// complete bullshit bc
public class GetAccessToken {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    SimpleOAuth.SERVER authFor;

    // save which server to authorise for
    public GetAccessToken(SimpleOAuth.SERVER s) {
        authFor = s;
    }

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    DefaultHttpClient httpClient;
    HttpPost httpPost;

    public JSONObject gettoken(String address,String token,String client_id,String client_secret,String redirect_uri,String grant_type) {
        // Making HTTP request
        try {
            // DefaultHttpClient
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(address);

            params.add(new BasicNameValuePair("code", token));
            params.add(new BasicNameValuePair("client_id", client_id));
            if(authFor!= SimpleOAuth.SERVER.OUTLOOK)    //outlook mobile client don't need client secret
            {
                params.add(new BasicNameValuePair("client_secret", client_secret));
                params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
            }
            else
            {
                // params.add(new BasicNameValuePair("client_secret", client_secret));
                params.add(new BasicNameValuePair("redirect_uri", redirect_uri)); //use url encode if outlook
            }
            params.add(new BasicNameValuePair("grant_type", grant_type));

            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();

            json = sb.toString();
            Log.e("JSONStr", json);
        } catch (Exception e) {
            e.getMessage();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // Parse the String to a JSON Object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return JSON String
        return jObj;
    }

}
