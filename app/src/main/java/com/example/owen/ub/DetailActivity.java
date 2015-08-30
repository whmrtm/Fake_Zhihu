package com.example.owen.ub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = this.getIntent();
        String contentHtml = intent.getStringExtra(Intent.EXTRA_TEXT);
        setTitle("Content");

        WebView webView =(WebView) findViewById(R.id.ub_webview);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);

        webView.loadDataWithBaseURL(null, getHtmlData(contentHtml), "text/html", "utf-8", null);
    }
    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>" +
                "img{max-width: 100%; width:auto; height: auto;}" +
                "a{text-decoration:none;}" +
                "body{font-family:Arial;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
