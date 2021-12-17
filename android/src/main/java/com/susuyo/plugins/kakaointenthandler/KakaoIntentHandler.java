package com.susuyo.plugins.kakaointenthandler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.NativePlugin;
import java.net.URISyntaxException;

@NativePlugin
public class KakaoIntentHandler extends Plugin {
    private WebView webView;
    static String TAG = "kakaointenthandler";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod
    public void load(){
        Log.d("Called0", "onCreateWindow Called======================");
        this.getBridge().getWebView().setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                        Log.d("Called", "onCreateWindow Called======================");

                        WebView newWebView = new WebView(view.getContext());
                        WebSettings settings = newWebView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setJavaScriptCanOpenWindowsAutomatically(true);
                        settings.setSupportMultipleWindows(true);
                        newWebView.setWebChromeClient(this);
                        newWebView.setWebViewClient(new WebViewClient());

                        newWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                        transport.setWebView(newWebView);
                        resultMsg.sendToTarget();
                        view.addView(newWebView);
                        return true;
                    }
                    @Override
                    public void onCloseWindow(WebView window) {
                        super.onCloseWindow(window);
                        bridge.getWebView().removeView(window);
                    }

                });

    }

    @PluginMethod
    public Boolean shouldOverrideLoad(Uri url) {
        if (url.getScheme().equals("intent")) {
            try {

                // Intent 생성
                Intent intent = Intent.parseUri(url.toString(), Intent.URI_INTENT_SCHEME);

                Context context = getContext();
                // 실행 가능한 앱이 있으면 앱 실행
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    Log.d(TAG, "ACTIVITY: ${intent.`package`}");
                    return true;
                }

                // Fallback URL이 있으면 현재 웹뷰에 로딩
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    bridge.getWebView().loadUrl(fallbackUrl);
                    Log.d(TAG, "FALLBACK: $fallbackUrl");
                    return true;
                }

                Log.e(TAG, "Could not parse anythings");
                return false;
            } catch (URISyntaxException e) {
                Log.e(TAG, "Invalid intent request", e);
                return false;
            }
        } else if (url.getScheme().equals("capacitor")) {
            // capacitor://app.moranique.com
            bridge.getWebView().loadUrl(url.toString().replace("capacitor:", "https:"));
            return true;
        } else {
            return false;
        }
    }
}
