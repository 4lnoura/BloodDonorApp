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

public class MyProfile_Frag3 extends Fragment {

    private boolean isDataFetched;
    private boolean mIsVisibleToUser;
    private Context context;

    //Declare Firebase Database
    private DatabaseReference mDatabase;
    private String skills, interest, experience;
    private TextView p_skills, p_interest, p_experience;


    public MyProfile_Frag3(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_myprofile_frag3, container, false);
        p_skills = v.findViewById(R.id.profile_skills);
        p_interest= v.findViewById(R.id.profile_interest);
        p_experience = v.findViewById(R.id.profile_experience);
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

        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                skills = dataSnapshot.child("skills").getValue(String.class);
                interest = dataSnapshot.child("interest").getValue(String.class);
                experience = dataSnapshot.child("experience").getValue(String.class);
                p_skills.setText(skills);
                p_interest.setText(interest);
                p_experience.setText(experience);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }


}