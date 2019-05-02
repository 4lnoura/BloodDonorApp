package com.blood_donor.admin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.News;
import com.blood_donor.util.ImageUploadActivity;
import com.blood_donor.util.Tools;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class PostNewsActivity extends ImageUploadActivity {

    private TextInputLayout titleEditText, descriptionEditText;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);


        titleEditText = findViewById(R.id.title);
        descriptionEditText = findViewById(R.id.description);

        imageView = findViewById(R.id.uploadImage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean validate() {
        if (Tools.getText(titleEditText).isEmpty()) {
            titleEditText.setError("Please provide the title");
            return false;
        }
        if (Tools.getText(descriptionEditText).isEmpty()) {
            descriptionEditText.setError("Please provide some description");
            return false;
        }
        titleEditText.setError("");
        descriptionEditText.setError("");

        return true;
    }

    public void postNews(View view) {
        if (validate()) {
            if (getBitmap() == null) {
                // save news without image
                saveNews("");
            } else {
                beginImageUpload(true);
            }
        }
    }

    public void chooseImage(View view) {
        this.chooseImage();
    }

    private void saveNews(String imageUrl) {
        News news = new News();
        news.setTitle(Tools.getText(titleEditText));
        news.setDescription(Tools.getText(descriptionEditText));
        news.setImage(imageUrl);

        // now save the image to firebase
        new FirebaseTransaction(this, "Post News", "Posting news....", true)
                .child("news")
                .push()
                .setValue(news, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(PostNewsActivity.this, "News posted!", Toast.LENGTH_SHORT).show();

                        // clear the input fields
                        titleEditText.getEditText().setText("");
                        descriptionEditText.getEditText().setText("");
                    }
                });
    }

    @Override
    public void imageChosen(Bitmap bitmap) {
        // display an image on the image view
        imageView.setImageDrawable(bitmap == null ? getResources().getDrawable(R.drawable.placeholder) :
                new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void imageUploadComplete(Uri uri) {
        // save the news with the image url
        saveNews(uri == null ? "" : uri.toString());
    }

    @Override
    public void imageUploadFailed() {
        Toast.makeText(this, "Unable to upload image! Please try again!", Toast.LENGTH_SHORT).show();
    }
}
