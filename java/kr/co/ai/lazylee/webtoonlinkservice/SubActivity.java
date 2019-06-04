package kr.co.ai.lazylee.webtoonlinkservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class SubActivity extends AppCompatActivity {
    WebView webView;
    //=====================================================

    // 웹뷰를 띄워주는 단순 이벤트

    //=====================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        webView  = (WebView) findViewById(R.id.webview);
        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("link");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(s);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass());
    }
    //=====================================================
    // 계속 새창이 띄워지는걸 방지
    private  class WebViewClientClass extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d("check URL", url);
            view.loadUrl(url);
            return true;
        }
    }

    //=====================================================
    // 뒤로가기를 눌렀을때 웹뷰가 꺼지는걸 방지
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
