package com.ldh.android.dragslidelayout;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private Button mButton;

    private DragLayout mDragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragLayout = findViewById(R.id.drag_layout);
        mButton = findViewById(R.id.sample_text);
        mDragLayout.setDragView(mButton);
        //mDragLayout.setViewToTop();
        //mDragLayout.setViewBelowTitle();
    }

}
