package com.sevstar.jivosite;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import com.sevstar.jivosite.sdk.*;

import android.content.Intent;
import android.os.Bundle;

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
            String userToken = args.getString(0);
            this.openNewActivity(userToken);
            return true;
        } else if (action.equals("set_user_token")) {
            String userToken = args.getString(0);
        }
        return false;
    }

    private void openNewActivity(String userToken) {
        Intent intent = new Intent(this.webView.getContext(), JivoActivity.class);
        Bundle b = new Bundle();
        b.putString("userToken", userToken);
        intent.putExtras(b);
        this.cordova.getActivity().startActivity(intent);
    }

}
