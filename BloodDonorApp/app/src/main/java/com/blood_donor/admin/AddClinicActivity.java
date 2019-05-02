package com.blood_donor.admin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Clinic;
import com.blood_donor.util.ImageUploadActivity;
import com.blood_donor.util.Tools;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class AddClinicActivity extends ImageUploadActivity {

    private TextInputLayout idEditText, nameEditText, phoneEditText, locationEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clinic);

        idEditText = findViewById(R.id.id);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phoneNumber);
        locationEditText = findViewById(R.id.location);
        imageView = findViewById(R.id.uploadImage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * To validate data before saving to database
     *
     * @return
     */
    private boolean validate() {
        if (Tools.getText(idEditText).isEmpty()) {
            idEditText.setError("Please provide a clinic ID");
            return false;
        }
        if (Tools.getText(nameEditText).isEmpty()) {
            nameEditText.setError("Please provide a clinic Name");
            return false;
        }
        if (Tools.getText(phoneEditText).isEmpty()) {
            phoneEditText.setError("Please provide a phone number");
            return false;
        }
        if (Tools.getText(locationEditText).isEmpty()) {
            locationEditText.setError("Please provide a location");
            return false;
        }
        idEditText.setError("");
        nameEditText.setError("");
        locationEditText.setError("");
        phoneEditText.setError("");

        return true;
    }

    public void addClinic(View view) {
        if(validate()){
            // start the image upload
            if (getBitmap() == null) {
                // save clinic without image
                saveClinic("");
            } else {
                beginImageUpload(true); // upload the image first
            }
        }
    }

    /**
     * Choose image from gallery
     *
     * @param view
     */
    public void chooseImage(View view) {
        this.chooseImage();
    }

    @Override
    public void imageChosen(Bitmap bitmap) {
        // display an image on the image view
        imageView.setImageDrawable(bitmap == null ? getResources().getDrawable(R.drawable.placeholder) :
                new BitmapDrawable(getResources(), bitmap));
    }

    /**
     * When the image upload is complete, save the clinic details
     *
     * @param uri
     */
    @Override
    public void imageUploadComplete(Uri uri) {
        // if image upload is complete, save clinic
        saveClinic(uri == null ? "" : uri.toString());
    }

    private void saveClinic(String imageUrl) {
        Clinic clinic = new Clinic();
        clinic.setId(Tools.getText(idEditText));
        clinic.setImage(imageUrl);
        clinic.setLocation(Tools.getText(locationEditText));
        clinic.setName(Tools.getText(nameEditText));
        clinic.setPhoneNumber(Tools.getText(phoneEditText));

        // now save the clinic details
        new FirebaseTransaction(this, "Add Clinic", "Saving clinic details....", true)
                .child("clinics")
                .push()
                .setValue(clinic, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(AddClinicActivity.this, "Clinic added successfully!", Toast.LENGTH_SHORT).show();
                        // clear the input fields

                        idEditText.getEditText().setText("");
                        locationEditText.getEditText().setText("");
                        nameEditText.getEditText().setText("");
                        phoneEditText.getEditText().setText("");
                        imageChosen(null);
                    }
                });
    }

    @Override
    public void imageUploadFailed() {
        Toast.makeText(this, "Unable to upload photo! Please try again!", Toast.LENGTH_SHORT).show();
    }
}
