package com.sevstar.jivosite;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import com.sevstar.jivosite.sdk.*;

import android.content.Intent;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class JivoSite extends CordovaPlugin {

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        ResourceHelper.INSTANCE.init(cordova.getActivity().getResources(), cordova.getActivity().getPackageName());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open_chat")) {
            this.openNewActivity();
            return true;
        }
        return false;
    }

    private void openNewActivity() {
        Intent intent = new Intent(this.webView.getContext(), JivoActivity.class);
        this.cordova.getActivity().startActivity(intent);
    }

}
