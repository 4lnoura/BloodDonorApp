package com.blood_donor;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;



public abstract class BaseActivity extends AppCompatActivity {

    public abstract Toolbar getToolBar();

    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
            if (showHomeAsUp) {
                Toolbar toolbar = getToolBar();
                if (toolbar != null)
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
            }
        }
    }
}
