package io.megaoak.oauthexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import io.megaoak.simpleoauth.SimpleOAuth;
import io.megaoak.simpleoauth.SimpleOAuthConfig;

public class MainActivity extends AppCompatActivity{
    Button btnGetToken;
    SimpleOAuth smp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGetToken = (Button) findViewById(R.id.getToken);

        SimpleOAuthConfig googleConfig = new SimpleOAuthConfig();
        googleConfig.authFor = SimpleOAuth.SERVER.GOOGLE;
        googleConfig.CLIENT_ID = "817024910462-urpgmi3g01nup0n5c7pbg3kb7pj8q1bj.apps.googleusercontent.com";
        googleConfig.CLIENT_SECRET = "TyUKsIMwaM-sfV_T6JNhCGsE";
        googleConfig.REDIRECT_URI = "http://localhost";
        googleConfig.OAUTH_SCOPE = "https://www.googleapis.com/auth/calendar";

        SimpleOAuthConfig outlookConfig = new SimpleOAuthConfig();
        outlookConfig.authFor = SimpleOAuth.SERVER.OUTLOOK;
        outlookConfig.CLIENT_ID = "87fa4e45-d3f1-4d1b-9918-28da6d921dae";
        outlookConfig.CLIENT_SECRET = null;
        outlookConfig.REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
        outlookConfig.OAUTH_SCOPE = "https://outlook.office.com/calendars.readwrite";

        smp = new SimpleOAuth(this, outlookConfig);
        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smp.auth(new SimpleOAuth.SimpleOAuthResult() {
                    @Override
                    public void AuthResult(SimpleOAuth.TokenData token) {
                        switch(token.status)
                        {
                            case SUCCESS:
                                break;
                            case CANCELLED:
                                break;
                            case FAILED:
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }
}
