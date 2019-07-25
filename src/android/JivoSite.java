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
            this.openNewActivity(args.getJSONObject(0));
            return true;
        }
        return false;
    }

    private void openNewActivity(JSONObject opts) {
        Intent intent = new Intent(this.webView.getContext(), JivoActivity.class);
        Bundle b = new Bundle();
        try {
            if (opts != null) {
                if (opts.getString("userToken") != null) {
                    b.putString("userToken", opts.getString("userToken"));
                }
                if (opts.getString("activityTitle") != null) {
                    b.putString("activityTitle", opts.getString("activityTitle"));
                }
                if (opts.getBoolean("backButton")) {
                    b.putBoolean("backButton", opts.getBoolean("backButton"));
                }
                if (opts.getBoolean("hideActivityTitle")) {
                    b.putBoolean("hideActivityTitle", opts.getBoolean("hideActivityTitle"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtras(b);
        this.cordova.getActivity().startActivity(intent);
    }

}
