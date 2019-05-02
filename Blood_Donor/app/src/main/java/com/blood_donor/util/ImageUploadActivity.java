package com.blood_donor.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.blood_donor.firebase.FirebaseTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by moses on 11/1/18.
 * <p>
 * Extend this class in order to get the ability to choose an image from the gallery and upload it
 */

public abstract class ImageUploadActivity extends AppCompatActivity {

    private static final int IMAGE_CHOOSE_REQUEST_CODE = 1238;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1253;
    private Bitmap bitmap;

    /**
     * Start the intent to pick the image
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra("return-date", true);

        startActivityForResult(intent, IMAGE_CHOOSE_REQUEST_CODE);
    }

    /**
     * This method is called whenever the activity needs to choose an image from the gallery
     */
    public void chooseImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_CHOOSE_REQUEST_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                try {
                    this.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    this.imageChosen(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public abstract void imageChosen(Bitmap bitmap);


    /**
     * Create a filename for the image in order to match the date uploaded
     *
     * @return
     */
    private String createFileName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_ssss");

        return dateFormat.format(calendar.getTime()) + ".jpg";
    }

    /**
     * Method called in order to upload an image to firebase
     *
     * @param bitmap
     */
    public void beginImageUpload(boolean showDialog) {
        if (bitmap == null) {
            return;
        }
        new FirebaseTransaction(this, "Uploading", "Uploading Photo...", showDialog)
                .uploadImage(bitmap, "images/" + createFileName(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageUploadFailed();
                    }
                }, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUploadComplete(uri);
                    }
                });
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public abstract void imageUploadComplete(Uri uri);

    public abstract void imageUploadFailed();
}
