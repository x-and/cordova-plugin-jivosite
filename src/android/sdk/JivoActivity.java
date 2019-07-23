package com.sevstar.jivosite.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

//************************/
import com.sevstar.jivosite.sdk.JivoDelegate;
import com.sevstar.jivosite.sdk.JivoSdk;
import com.sevstar.jivosite.sdk.ResourceHelper;
import java.util.Locale;

//**********
public class JivoActivity extends Activity implements JivoDelegate{

    //**************
    JivoSdk jivoSdk;
    private static final ResourceHelper _R = ResourceHelper.INSTANCE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	String package_name = getApplication().getPackageName();
    	setContentView(getApplication().getResources().getIdentifier("activity_jivo_web_view", "layout", package_name));

        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_jivo);

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
    }


}
