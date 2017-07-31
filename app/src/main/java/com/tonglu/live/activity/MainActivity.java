package com.tonglu.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tonglu.live.R;
import com.tonglu.live.base.BaseTitleActivity;

public class MainActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("直播列表");

        final String url = "rtmp://live.otofuturestore.com/malls/dianneizhibo?auth_key=1502265257-0-0-d56929ae83d1de1a1c1683e7cd2bc9cb";

        Button mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LivePlayerActivity.class);
                intent.putExtra("url_address", url);
                startActivity(intent);
            }
        });

    }
}
