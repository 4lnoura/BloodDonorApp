package com.blood_donor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.User;
import com.blood_donor.user.UserDashboard;
import com.blood_donor.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;



public class RegistrationActivity extends AppCompatActivity {


    protected TextInputLayout nameEditText, emailEditText, phoneEditText, ageEditText, locationEditText;
    protected RadioGroup diseasesRadioGroup, statusRadioGroup;
    protected Spinner bloodGroupSpinner;

    protected Button submitButton;
    protected FirebaseUser firebaseUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phoneNumber);
        ageEditText = findViewById(R.id.age);
        locationEditText = findViewById(R.id.location);
        bloodGroupSpinner = findViewById(R.id.bloodGroup);
        diseasesRadioGroup = findViewById(R.id.diseasesRadioGroup);
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        submitButton = findViewById(R.id.signUp);

        /**
         * Get some of the default values from the FirebaseUser instance
         */
        nameEditText.getEditText().setText(firebaseUser.getDisplayName());
        emailEditText.getEditText().setText(firebaseUser.getEmail());
        phoneEditText.getEditText().setText(firebaseUser.getPhoneNumber());
        if (!Tools.getText(phoneEditText).isEmpty()) {
            phoneEditText.getEditText().setEnabled(false);
        }
        nameEditText.getEditText().setEnabled(false);
        emailEditText.getEditText().setEnabled(false);
    }


    /**
     * Validate the data entered by the user
     *
     * @return
     */
    protected boolean validate() {
        if (Tools.getText(nameEditText).isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return false;
        }
        if (Tools.getText(emailEditText).isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return false;
        }
        if (!Tools.isEmailValid(Tools.getText(emailEditText))) {
            emailEditText.setError("Invalid email address");
            return false;
        }
        if (Tools.getText(phoneEditText).isEmpty()) {
            phoneEditText.setError("Phone number cannot be empty");
            return false;
        }
        if (Tools.getText(phoneEditText).length() < 8) {
            phoneEditText.setError("Please enter a valid phone number");
            return false;
        }

        if (Tools.getText(ageEditText).isEmpty()) {
            ageEditText.setError("Please provide an age");
            return false;
        }

        if (Tools.getText(locationEditText).isEmpty()) {
            locationEditText.setError("Please provide a location");
            return false;
        }

        return true;
    }

    public void saveDetails(View view) {
        saveDetails(false);
    }

    public void saveDetails(boolean update) {
        if (validate()) {
            // create the user
            boolean diseases = diseasesRadioGroup.getCheckedRadioButtonId() == R.id.diseasesYes;
            boolean status = statusRadioGroup.getCheckedRadioButtonId() == R.id.statusYes;

            final User user = new User();
            user.setAdmin(false);
            user.setAge(Integer.parseInt(Tools.getText(ageEditText)));
            user.setBloodType(bloodGroupSpinner.getSelectedItem().toString());
            user.setDiseases(diseases ? "Yes" : "No"); // if the diseases field is empty, set default to None
            user.setEmail(Tools.getText(emailEditText));
            user.setLocation(Tools.getText(locationEditText));
            user.setName(Tools.getText(nameEditText));
            user.setPhoneNumber(Tools.getText(phoneEditText));
            user.setPhotoUrl(firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString()); // set the profile photo
            user.setStatus(status ? 1 : 0);

            // the user can now be registered

            FirebaseTransaction transaction = new FirebaseTransaction(this, update ? "Update Profile" : "Registering", update ? "Updating profile details..." : "Creating your account...", true)
                    .child("users");
            if (update) {
                transaction = transaction.child(firebaseUser.getUid());
            } else {
                transaction = transaction.push(firebaseUser.getUid());
            }

            transaction
                    .setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            // once the user is registered, sign them in
                            Tools.saveUserDetails(RegistrationActivity.this, user); // save the user details
                            Intent intent = new Intent(RegistrationActivity.this, UserDashboard.class);
                            startActivity(intent);
                            finish(); // close the registration activity
                        }
                    });
        }
    }

}
