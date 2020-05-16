package org.insbaixcamp.projectem13.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.insbaixcamp.projectem13.R;

public class ServicesActivity extends AppCompatActivity {
    WebView wvServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        wvServices = findViewById(R.id.wv_services);
        wvServices.setWebViewClient(new WebViewClient());
        wvServices.loadUrl("http://massatgesreus.blogspot.com");
    }
}
