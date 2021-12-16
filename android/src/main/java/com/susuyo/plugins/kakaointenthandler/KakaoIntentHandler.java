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

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView childWebVeiw = new WebView(this);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();

        newWebView.setWebViewClient(
            new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    startActivity(browserIntent);
                    return true;
                }
            }
        );

        return true;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        MyLog.toastMakeTextShow(view.getContext(), "TAG", "window.open 협의가 필요합니다.");
        WebView newWebView = new WebView(view.getContext());
        WebSettings webSettings = newWebView.getSettings();
        WebSettings settings = newWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        //final Dialog dialog = new Dialog(view.getContext(),R.style.Theme_DialogFullScreen);
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(newWebView);
        dialog.show();

        dialog.setOnKeyListener(
            new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //MyLog.toastMakeTextShow(view.getContext(), "TAG", "KEYCODE_BACK");
                        if (newWebView.canGoBack()) {
                            newWebView.goBack();
                        } else {
                            MyLog.toastMakeTextShow(view.getContext(), "TAG", "Window.open 종료");
                            dialog.dismiss();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        );
        newWebView.setWebViewClient(new MyWebViewClient(view.getContext()));
        newWebView.setWebChromeClient(
            new MyWebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            }
        );

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        MyLog.i(getClass().getName(), "onCloseWindow");
        window.setVisibility(View.GONE);
        window.destroy();
        //mWebViewSub=null;
        super.onCloseWindow(window);
    }
}
