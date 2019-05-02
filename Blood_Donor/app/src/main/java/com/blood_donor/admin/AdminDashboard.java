package com.blood_donor.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blood_donor.ClinicListActivity;
import com.blood_donor.DonorListActivity;
import com.blood_donor.MainActivity;
import com.blood_donor.R;
import com.blood_donor.models.User;
import com.blood_donor.user.UserDashboard;
import com.blood_donor.util.Tools;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = Tools.getUser(this); //prevent other users from accessing this dashboard
        if (user == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (!user.isAdmin()) {
            Intent intent = new Intent(this, UserDashboard.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Tools.clearUserInfo(this);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    public void addClinic(View view) {
        Intent intent = new Intent(this, AddClinicActivity.class);
        startActivity(intent);
    }

    public void postNews(View view) {
        Intent intent = new Intent(this, PostNewsActivity.class);
        startActivity(intent);
    }

    public void removeUser(View view) {
        Intent intent = new Intent(this, DonorListActivity.class);
        startActivity(intent);
    }

    public void removeClinic(View view) {
        Intent intent = new Intent(this, ClinicListActivity.class);
        startActivity(intent);
    }
}
