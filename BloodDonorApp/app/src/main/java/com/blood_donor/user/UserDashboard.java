package com.blood_donor.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blood_donor.ClinicListActivity;
import com.blood_donor.DonorListActivity;
import com.blood_donor.MainActivity;
import com.blood_donor.NewsListActivity;
import com.blood_donor.ProfileActivity;
import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Request;
import com.blood_donor.models.User;
import com.blood_donor.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserDashboard extends AppCompatActivity {

    private TextView requestsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        requestsLabel = findViewById(R.id.requestsLabel);
        loadRequests();
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

    private void loadRequests() {
        new FirebaseTransaction(this, false)
                .child("requests")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = Tools.getUser(UserDashboard.this);
                        int numRequests = 0;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Request request = child.getValue(Request.class);
                            if (request.getRecipientId().equals(user.getEmail()) && request.getStatus().equals(Request.STATUS_PENDING)) {
                                numRequests++;
                            }
                        }
                        if (numRequests > 0) {
                            requestsLabel.setText(String.valueOf(numRequests));
                            requestsLabel.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void bloodDonors(View view) {
        Intent intent = new Intent(this, DonorListActivity.class);
        startActivity(intent);
    }

    public void myProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_EMAIL, Tools.getUser(this).getEmail());
        startActivity(intent);
    }

    public void viewClinics(View view) {
        Intent intent = new Intent(this, ClinicListActivity.class);
        startActivity(intent);
    }

    public void viewNews(View view) {
        Intent intent = new Intent(this, NewsListActivity.class);
        startActivity(intent);
    }
}
