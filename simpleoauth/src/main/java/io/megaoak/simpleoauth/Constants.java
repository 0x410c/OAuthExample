package io.megaoak.simpleoauth;

/**
 * Created by 0x410c on 05/05/16.
 */
public class Constants {

    // oauth and tokne urls of various services
    static public String GOOGLE_AUTHURL = "https://accounts.google.com/o/oauth2/auth";
    static public String GOOGLE_TOKENURL = "https://accounts.google.com/o/oauth2/token";


    //microsoft all are same, access token works crossly
    static public String OUTLOOK_AUTHURL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";
    static public String OUTLOOK_TOKENURL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    static public String OFFICE365_AUTHURL = "";
    static public String OFFICE365_TOKENURL = "";

    static public String EXCHANGE_AUTHURL = "";
    static public String EXCHANGE_TOKENURL = "";

    static public String BOX_AUTHURL = "https://account.box.com/api/oauth2/authorize";
    static public String BOX_TOKENURL = "https://api.box.com/oauth2/token";

    //proposed
    static public String GDRIVE_AUTHURL = "";
    static public String GDRIVE_TOKENURL = "";

    static public String DROPBOX_AUTHURL = "";
    static public String DROPBOX_TOKENURL = "";
}
