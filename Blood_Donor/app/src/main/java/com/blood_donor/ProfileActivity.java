package com.blood_donor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blood_donor.adapters.RequestListAdapter;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Request;
import com.blood_donor.models.User;
import com.blood_donor.user.UpdateAccountActivity;
import com.blood_donor.util.Tools;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "email";

    private ImageView profilePicture;
    private TextView nameTextView, emailTextView, phoneTextView, bloodGroupTextView, locationTextView, statusTextView, diseasesTextView, ageTextView;

    private FloatingActionButton editProfile;
    private LinearLayout donationRequestLayout, donationRequestSentLayout, donationRequests;
    private RecyclerView donationRequestsRecyclerView;

    private User user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = getIntent().getStringExtra(EXTRA_USER_EMAIL);

        profilePicture = findViewById(R.id.profilePicture);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        phoneTextView = findViewById(R.id.phoneNumber);
        bloodGroupTextView = findViewById(R.id.bloodGroup);
        locationTextView = findViewById(R.id.location);
        statusTextView = findViewById(R.id.state);
        diseasesTextView = findViewById(R.id.diseases);
        ageTextView = findViewById(R.id.age);
        donationRequestLayout = findViewById(R.id.donationRequestLayout);
        donationRequestSentLayout = findViewById(R.id.donationRequestSentLayout);
        donationRequests = findViewById(R.id.donationRequests);
        donationRequestsRecyclerView = findViewById(R.id.donationRequestsRecycler);

        editProfile = findViewById(R.id.editProfileButton);

        if (!email.equals(Tools.getUser(this).getEmail())) {
            editProfile.setVisibility(View.GONE);
        }

        loadProfile();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void sendDonationRequest(View view) {
        User current = Tools.getUser(this);
        Request request = new Request();
        request.setSenderId(current.getEmail());
        request.setRecipientId(user.getEmail());
        request.setCreatedAt(System.currentTimeMillis());

        FirebaseTransaction firebaseTransaction = new FirebaseTransaction(this, "Donation Request", "Sending request...", true)
                .child("requests")
                .push();

        // set request id
        request.setId(firebaseTransaction.getDatabaseReference().getKey());

        firebaseTransaction.setValue(request, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                donationRequestLayout.setVisibility(View.GONE);
                donationRequestSentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadProfile() {
        new FirebaseTransaction(this)
                .child("users")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null && user.getEmail().equals(email)) {
                                ProfileActivity.this.user = user;
                                setUserInfo();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }, false);

    }

    public void editProfile(View view) {
        Intent intent = new Intent(this, UpdateAccountActivity.class);
        startActivity(intent);
    }

    private void setUserInfo() {
        // check if the user has sent a request to this user before
        new FirebaseTransaction(this)
                .child("requests")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean show = true;
                        User currentUser = Tools.getUser(ProfileActivity.this);
                        if (!user.getEmail().equals(currentUser.getEmail())) {
                            donationRequestLayout.setVisibility(View.VISIBLE);
                        }
                        ArrayList<Request> requests = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Request request = child.getValue(Request.class);
                            if (request != null) {
                                if (user.getEmail().equals(request.getRecipientId()) && currentUser.getEmail().equals(request.getSenderId())
                                        && !request.isRead()) {
                                    donationRequestLayout.setVisibility(View.GONE);
                                    donationRequestSentLayout.setVisibility(View.VISIBLE);
                                }
                                if( request.getRecipientId().equals(currentUser.getEmail())){
                                    requests.add(request);
                                }
                            }
                        }

                        if (!requests.isEmpty() && user.getEmail().equals(currentUser.getEmail())) {
                            RequestListAdapter adapter = new RequestListAdapter(requests);
                            donationRequestsRecyclerView.setAdapter(adapter);
                            donationRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                            donationRequests.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        setTitle(user.getName());
        // load profile picture
        Glide.with(this)
                .load(user.getPhotoUrl().isEmpty() ? getResources().getDrawable(R.drawable.placeholder) :
                        user.getPhotoUrl())
                .into(profilePicture);
        emailTextView.setText(user.getEmail());
        nameTextView.setText(user.getName());
        phoneTextView.setText(user.getPhoneNumber());
        bloodGroupTextView.setText(user.getBloodType());
        locationTextView.setText(user.getLocation());
        statusTextView.setText(user.getStatus() == 1 ? "Yes" : "No");
        diseasesTextView.setText(user.getDiseases());
        ageTextView.setText(String.valueOf(user.getAge()));
    }
}
