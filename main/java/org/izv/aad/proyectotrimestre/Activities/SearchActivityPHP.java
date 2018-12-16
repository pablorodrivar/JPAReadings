package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.izv.aad.proyectotrimestre.R;

public class SearchActivityPHP extends AppCompatActivity {
    private WebView wv;
    private boolean cargo = false;
    private void cargarWebView(WebView wv){
        SharedPreferences prefs = getSharedPreferences("user",Context.MODE_PRIVATE);
        String uid = prefs.getString("user", "null");
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(SearchActivityPHP.this)
                        .setTitle("Resumen: ")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int wicht)
                                    {
                                        result.confirm();
                                    }
                                }).setCancelable(false)
                        .create()
                        .show();
                return true;
            };
        });
        String url = "https://php-firebase-puvlo.c9users.io/index.php?uid="+uid;
        wv.loadUrl(url);
        this.cargo = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeActivity.leerSharedTheme(this,true);
        setContentView(R.layout.activity_search_php);
        getSupportActionBar().setTitle("Buscar Readings");
        getSupportActionBar().setSubtitle("PHP WebApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!cargo) cargarWebView((WebView) findViewById(R.id.webview_mode));

    }
}
