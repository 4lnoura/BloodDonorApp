package com.blood_donor.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioButton;

import com.blood_donor.R;
import com.blood_donor.RegistrationActivity;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.User;
import com.blood_donor.util.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UpdateAccountActivity extends RegistrationActivity {

    private RadioButton diseasesYes, diseasesNo, statusYes, statusNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");
        submitButton.setText("Update");

        diseasesNo = findViewById(R.id.diseasesNo);
        diseasesYes = findViewById(R.id.diseasesYes);
        statusYes = findViewById(R.id.statusYes);
        statusNo = findViewById(R.id.statusNo);
        loadProfile();
    }

    private void loadProfile() {
        new FirebaseTransaction(this)
                .child("users")
                .child(firebaseUser.getUid())
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setDetails(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }, false);
    }

    @Override
    public void saveDetails(View view) {
        saveDetails(true);
    }

    public void setDetails(User user) {
        Tools.setText(this.nameEditText, user.getName());
        Tools.setText(this.emailEditText, user.getEmail());
        Tools.setText(this.phoneEditText, user.getPhoneNumber());
        Tools.setText(this.ageEditText, String.valueOf(user.getAge()));
        Tools.setText(this.locationEditText, String.valueOf(user.getLocation()));
        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        for (int i = 0; i < bloodGroups.length; i++) {
            if (bloodGroups[i].equals(user.getBloodType())) {
                bloodGroupSpinner.setSelection(i);
            }
        }
        diseasesYes.setChecked(user.getDiseases().equals("Yes"));
        diseasesNo.setChecked(!diseasesYes.isChecked());

        statusYes.setChecked(user.getStatus() == 1);
        statusNo.setChecked(user.getStatus() == 0);
    }
}
