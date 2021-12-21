package com.sevstar.jivosite.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;

public class JivoSdk {

    private WebView webView;
    private ProgressDialog progr;
    private String language;
    public JivoDelegate delegate = null;

    public JivoSdk(WebView webView){
        this.webView = webView;
        this.language = "";
    }

    public JivoSdk(WebView webView, String language){
        this.webView = webView;
        this.language = language;
    }

    @SuppressLint("SetJavaScriptEnabled")
	public void prepare(){
        JivoActivity act = ((JivoActivity) delegate);

        //создаем спиннер
        progr = new ProgressDialog(webView.getContext());
        progr.setTitle("Чат с техподдержкой");
        progr.setMessage("Загрузка...");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
        //пробрасываем JivoInterface в Javascript
		if(Build.VERSION.SDK_INT >= 21){
			webSettings.setMixedContentMode(0);
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}else {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

        webView.addJavascriptInterface(new JivoInterface(webView), "JivoInterface");
        webView.setWebViewClient(new MyWebViewClient());

		loadPage();

		webView.setWebChromeClient(act.new MyWebChromeClient());
	}

	public void loadPage() {
		if (this.language.length() > 0){
			webView.loadUrl("file:///android_asset/www/jivosite/index_"+this.language+".html");
		} else {
			webView.loadUrl("file:///android_asset/www/jivosite/index_en.html");
		}
	}

    public class JivoInterface{

        private WebView mAppView;
        public JivoInterface  (WebView appView) {
            this.mAppView = appView;
        }

        @JavascriptInterface
        public void send(String name, String data){
            if (delegate != null){
                delegate.onEvent(name, data);
            }
        }
    }

    public static String decodeString(String encodedURI) {
        char actualChar;

        StringBuffer buffer = new StringBuffer();

        int bytePattern, sumb = 0;

        for (int i = 0, more = -1; i < encodedURI.length(); i++) {
            actualChar = encodedURI.charAt(i);

            switch (actualChar) {
                case '%': {
                    actualChar = encodedURI.charAt(++i);
                    int hb = (Character.isDigit(actualChar) ? actualChar - '0'
                            : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                    actualChar = encodedURI.charAt(++i);
                    int lb = (Character.isDigit(actualChar) ? actualChar - '0'
                            : 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
                    bytePattern = (hb << 4) | lb;
                    break;
                }
                case '+': {
                    bytePattern = ' ';
                    break;
                }
                default: {
                    bytePattern = actualChar;
                }
            }

            if ((bytePattern & 0xc0) == 0x80) { // 10xxxxxx
                sumb = (sumb << 6) | (bytePattern & 0x3f);
                if (--more == 0)
                    buffer.append((char) sumb);
            } else if ((bytePattern & 0x80) == 0x00) { // 0xxxxxxx
                buffer.append((char) bytePattern);
            } else if ((bytePattern & 0xe0) == 0xc0) { // 110xxxxx
                sumb = bytePattern & 0x1f;
                more = 1;
            } else if ((bytePattern & 0xf0) == 0xe0) { // 1110xxxx
                sumb = bytePattern & 0x0f;
                more = 2;
            } else if ((bytePattern & 0xf8) == 0xf0) { // 11110xxx
                sumb = bytePattern & 0x07;
                more = 3;
            } else if ((bytePattern & 0xfc) == 0xf8) { // 111110xx
                sumb = bytePattern & 0x03;
                more = 4;
            } else { // 1111110x
                sumb = bytePattern & 0x01;
                more = 5;
            }
        }
        return buffer.toString();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.toLowerCase().indexOf("jivoapi://") == 0){
                String[] components = url.replace("jivoapi://", "").split("/");

                String apiKey = components[0];
                String data = "";
                if (components.length > 1){
                    data = decodeString(components[1]);
                }

                if (delegate != null) {
                    delegate.onEvent(apiKey, data);
                }

                return true;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
		}

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progr.dismiss();
        }

    }

    public void execJS(String script){
        webView.post(new Runnable() {
            public void run() {
                webView.loadUrl("javascript:" + script);
            }
        });
    }

    public void callApiMethod(String methodName, String data){
        execJS("window.jivo_api." + methodName + "("+ data +");");
    }

}
