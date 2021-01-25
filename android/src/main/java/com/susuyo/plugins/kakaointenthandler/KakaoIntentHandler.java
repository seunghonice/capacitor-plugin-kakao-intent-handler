package com.susuyo.plugins.kakaointenthandler;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import java.net.URISyntaxException;

@NativePlugin
public class KakaoIntentHandler extends Plugin {

    static String TAG = "kakaointenthandler";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
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
