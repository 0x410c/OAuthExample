package io.megaoak.simpleoauth;

/**
 * Created by 0x410c on 05/05/16.
 */
public class SimpleOAuthConfig {
    public SimpleOAuthConfig()
    {
        GRANT_TYPE="authorization_code";
    }
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public String REDIRECT_URI;
    public String GRANT_TYPE;
    public String TOKEN_URL;
    public String OAUTH_URL;
    public String OAUTH_SCOPE;
    public SimpleOAuth.SERVER authFor;     //server to perform authorisation for
}
