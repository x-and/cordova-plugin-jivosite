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

public class JivoSite extends CordovaPlugin {

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        ResourceHelper.INSTANCE.init(cordova.getActivity().getResources(), cordova.getActivity().getPackageName());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("open_chat")) {
            this.openNewActivity(args.getString(0), args.getBoolean(1), args.getString(2));
            return true;
        }
        return false;
    }

    private void openNewActivity(String userToken, boolean backButton, String activityTitle) {
        Intent intent = new Intent(this.webView.getContext(), JivoActivity.class);
        Bundle b = new Bundle();

        if (userToken != null) {
            b.putString("userToken", userToken);
        }
        if (activityTitle != null) {
            b.putString("activityTitle", activityTitle);
        }
        if (backButton) {
            b.putBoolean("backButton", backButton);
        }

        intent.putExtras(b);
        this.cordova.getActivity().startActivity(intent);
    }

}
