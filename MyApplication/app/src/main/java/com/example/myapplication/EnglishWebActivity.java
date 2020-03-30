package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EnglishWebActivity extends AppCompatActivity {
private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_web);
        Intent intent =getIntent();
       String name=intent.getStringExtra("name");
        // if(url.equals(""))url="http://www.baike.com/gwiki/"+name;
        // Log.d("TAG," ,"onCreateweb: "+url);
        String url="https://en.m.wikipedia.org/wiki/"+name;
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
