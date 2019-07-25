package com.sevstar.jivosite.sdk;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.Window;

//************************/
import com.sevstar.jivosite.sdk.JivoDelegate;
import com.sevstar.jivosite.sdk.JivoSdk;
import com.sevstar.jivosite.sdk.ResourceHelper;
import java.util.Locale;

//**********
    public class JivoActivity extends Activity implements JivoDelegate{

        private static final ResourceHelper _R = ResourceHelper.INSTANCE;

        //**************
        JivoSdk jivoSdk;
        String userToken = "";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle b = this.getIntent().getExtras();
            ActionBar a =  this.getActionBar();

            if (b != null) {
                if (b.containsKey("userToken")) {
                    this.userToken = b.getString("userToken");
                }
                if (b.containsKey("activityTitle")) {
                    a.setTitle(b.getString("activityTitle"));
                }
                if (b.containsKey("backButton") && b.getBoolean("backButton")) {
                    a.setDisplayHomeAsUpEnabled(true);
                }
                if (b.containsKey("hideActivityTitle") && b.getBoolean("hideActivityTitle")) {
                    requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
                }
            }

            String package_name = getApplication().getPackageName();
            setContentView(getApplication().getResources().getIdentifier("activity_jivo_web_view", "layout", package_name));

            String lang = Locale.getDefault().getLanguage().indexOf("ru") >= 0 ? "ru" : "en";

            //*********************************************************

            jivoSdk = new JivoSdk((WebView) findViewById(_R.getIdentifier("jivowebview")), lang);
            jivoSdk.delegate = this;
            jivoSdk.prepare();
        }

    //*********************************************
    @Override
    public void onEvent(String name, String data) {
        if(name.equals("url.click")){
            if(data.length() > 2){
                String url = data.substring(1, data.length() - 1);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }
        if (name.equals("chat.ready") && this.userToken.length() > 0) {
            jivoSdk.callApiMethod("setUserToken",this.userToken);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
