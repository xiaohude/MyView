package com.smarttiger.myview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SiderBarActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.siderbar_activity);

        textView = (TextView) findViewById(R.id.textView);


        SideBar sideBar = (SideBar) findViewById(R.id.sideBar);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                Log.d(TAG, "select " + s);
                textView.setText("select " + s);
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("OnClick ");
            }
        });
    }
}
