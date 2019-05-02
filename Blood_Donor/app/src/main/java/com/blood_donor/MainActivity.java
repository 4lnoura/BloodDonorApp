package com.blood_donor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blood_donor.admin.AdminDashboard;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.User;
import com.blood_donor.user.UserDashboard;
import com.blood_donor.util.Tools;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GET_STARTED_REQUEST_CODE = 10001;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // if firebaseUser is null, this means that the user is logged out
        if (firebaseUser != null) {
            User user = Tools.getUser(this); // get the user information from the settings

            if (user == null) {
                completeLogin(firebaseUser);
            } else {
                loginUser(user);
            }
        } else {
            Tools.clearUserInfo(this); // no user is logged out, ensure that the user information is cleared
        }
        setContentView(R.layout.activity_main);
    }

    public void getStarted(View view) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                GET_STARTED_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_STARTED_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                completeLogin(user);
            }
        }
    }

    /**
     * Checks if the user is already registered, if not display the registration page
     *
     * @param firebaseUser
     */
    private void completeLogin(FirebaseUser firebaseUser) {
        new FirebaseTransaction(this)
                .child("users")
                .child(firebaseUser.getUid())
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            // register the user
                            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                        } else {
                            // the user can proceed, log them in
                            loginUser(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }, false);
    }

    private void loginUser(User user) {
        Intent intent;
        // save the user to settings
        Tools.saveUserDetails(this, user);
        if (user.isAdmin()) {
            // log the user as an admin
            intent = new Intent(this, AdminDashboard.class);
        } else {
            // login the user as a normal user
            intent = new Intent(this, UserDashboard.class);
        }
        startActivity(intent);
        finish();
    }
}
