package com.example.noura.riyadh_tb.UserProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noura.riyadh_tb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfile_Frag1 extends Fragment {

    private boolean isDataFetched;
    private boolean mIsVisibleToUser;
    private Context context;

    //Declare Firebase Database
    private DatabaseReference mDatabase;
    private String Name, Email, Gender, DOB ;
    private TextView p_username, p_email, p_gender, p_dop;


    public MyProfile_Frag1(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_myprofile_frag1, container, false);
        p_username = v.findViewById(R.id.profile_username);
        p_email= v.findViewById(R.id.profile_email);
        p_gender= v.findViewById(R.id.profile_gender);
        p_dop= v.findViewById(R.id.profile_dob);
        return v;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mIsVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && !isDataFetched && getContext() != null) {
            context = getContext();
            LoadData(); //Remove this call from onCreateView
        }
    }


    private void LoadData(){


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = user.getUid();
        Email=user.getEmail();

        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("username").getValue(String.class);
                DOB = dataSnapshot.child("dob").getValue(String.class);
                Gender = dataSnapshot.child("gender").getValue(String.class);
                p_username.setText(Name);
                p_email.setText(Email);
                p_dop.setText(DOB);
                p_gender.setText(Gender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }


}
