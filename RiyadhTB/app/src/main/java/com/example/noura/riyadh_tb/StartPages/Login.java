package com.example.noura.riyadh_tb.StartPages;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.util.Log;

import java.io.*;

import com.example.noura.riyadh_tb.Main.Timeline;
import com.example.noura.riyadh_tb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Login extends AppCompatActivity {

    //Declare
    private EditText email;
    private EditText password;
    private Button loginbutton;
    String filename = "file.txt";
    private static final String TAG = "Login";

    //Declare Firebase Database
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String UserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        LoginButton();
        email.setText(readFile(filename));
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void LoginButton() {


        //Setting up the Views
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        loginbutton = (Button) findViewById(R.id.login_button);


        //Getting firebase reference url
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Retrieves user inputs
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();


                if (Email.isEmpty()) {
                    email.setError("Enter your e-mail please!");
                    email.requestFocus();
                    return;
                }


                if (Password.isEmpty()) {
                    password.setError("Enter your password please!");
                    password.requestFocus();
                    return;
                }


                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithEmail", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        //responds to changes in the user's sign-in state
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());

                    // Authenticated successfully with authData

                 /*   if (email.getText().toString().equals("Admin1@gmail.com")) {
                        Intent intent = new Intent(Login.this, AdminDashboard.class);


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);} */


                        Intent intent = new Intent(Login.this, Timeline.class);


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };


    }//end login button


    //List of methods
    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            saveFile(filename, email.getText().toString());
        }
    }

    public void onForgotClicked(View view){

        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent); }


    public void saveFile(String file, String text) {

        try {
            FileOutputStream fos = Login.this.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(Login.this, "Your info. is Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            e.printStackTrace();
            /*Toast.makeText(Login.this, "Error saving file!",
                    Toast.LENGTH_SHORT).show();*/
        }
    }

    public String readFile(String file) {
        String text = "";

        try {
            FileInputStream fis = Login.this.openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            text = new String(buffer);
        } catch (Exception e) {

            e.printStackTrace();
          /*  Toast.makeText(Login.this, "Error reading file!",
                    Toast.LENGTH_SHORT).show();*/
        }

        return text;
    }


    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}//end