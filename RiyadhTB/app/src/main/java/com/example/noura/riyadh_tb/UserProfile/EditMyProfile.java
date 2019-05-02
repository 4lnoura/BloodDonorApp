package com.example.noura.riyadh_tb.UserProfile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.Main.Timeline;
import com.example.noura.riyadh_tb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class EditMyProfile extends AppCompatActivity {

    Button save ;
    EditText nameUP ,expereinceUP ,skillUP ,interestUP ;

    //date
    protected FloatingActionButton fabDate1;
    protected TextView tvDate1;

    //upload image
    private Uri filePath;
    private ImageView image_view;
    private final int PICK_IMAGE_REQUEST = 1;
    private String imageUrl;

    private DatabaseReference profileUserRef;
    private FirebaseAuth firebaseAuth;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference,mstorageRef;

    private String currentUserID;
    private int ageNewUser;
    //declare boolean
    private boolean clicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);


        // String profileImage;
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserID =firebaseAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profileUserRef= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        mstorageRef=FirebaseStorage.getInstance().getReference().child("users").child(currentUserID);

        save = (Button) findViewById(R.id.Save);
        nameUP = (EditText) findViewById(R.id.name_up);
        interestUP = (EditText)findViewById(R.id.interest_up);
        expereinceUP = (EditText)findViewById(R.id.experience_up);
        skillUP = (EditText)findViewById(R.id.skill_up);
        image_view= findViewById(R.id.image_up);
        fabDate1 = (FloatingActionButton) findViewById(R.id.fabDate_EditDOB);
        tvDate1 = (TextView) findViewById(R.id.tvDate_EditDOB);

        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    String myProfileName = dataSnapshot.child("name").getValue().toString();
                    String myProfileExperience = dataSnapshot.child("experience").getValue().toString();
                    String myProfileInterest = dataSnapshot.child("interest").getValue().toString();
                    String myProfileDOB = dataSnapshot.child("dob").getValue().toString();
                    String myProfileSkills = dataSnapshot.child("skills").getValue().toString();
                    String url= dataSnapshot.child("photoUrl").getValue().toString();


                    Glide.with(EditMyProfile.this).load(url.isEmpty()? getResources().getDrawable(R.drawable.placeholder):url)
                            .into(image_view);

                    nameUP.setText(myProfileName);
                    expereinceUP.setText(myProfileExperience);
                    skillUP.setText(myProfileSkills);
                    interestUP.setText(myProfileInterest);
                    tvDate1.setText(myProfileDOB);




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(EditMyProfile.this, "خطأ", Toast.LENGTH_LONG).show();

            }
        });


        fabDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=true;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(view.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAccountINFO();
            }
        });

    }


    //DOB-------------------------------------------------


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String format = new SimpleDateFormat("dd MMM YYYY").format(c.getTime());
            tvDate1.setText(format);
            //tvAge.setText(Integer.toString(calculateAge(c.getTimeInMillis())));
            String age = Integer.toString(calculateAge(c.getTimeInMillis()));
            ageNewUser=calculateAge(c.getTimeInMillis());
        }
    };

    int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }



    //End DOB---------------------------------------------


    private void validateAccountINFO(){

        String name = nameUP.getText().toString();
        String interst =interestUP.getText().toString();
        String experience =expereinceUP.getText().toString();
        String skill = skillUP.getText().toString();
        String dob= tvDate1.getText().toString();

        if(TextUtils.isEmpty(name)) {
            nameUP.setError("الرجاء إدخال الاسم");
            nameUP.requestFocus();
            return ;
        }
        if(TextUtils.isEmpty(interst)){
            interestUP.setError("الرجاء إدخال الاهتمامات");
            interestUP.requestFocus();
            return ;
        }

        if(TextUtils.isEmpty(skill)) {
            skillUP.setError("الرجاء إدخال المهارات");
            skillUP.requestFocus();
            return ;
        }
        if(TextUtils.isEmpty(experience)){
            expereinceUP.setError("الرجاء إدخال الخبرات");
            expereinceUP.requestFocus();
            return ;

        }

        if (clicked) {

            if (ageNewUser < 15) {
                tvDate1.setText("يجب ان يكون العمر>=١٥");
                tvDate1.requestFocus();

                return;
            }
        }

        if(dob.equals("اليوم / الشهر / السنة")){
            Toast.makeText(EditMyProfile.this, "الرجاء تحديد تاريخ الميلاد ", Toast.LENGTH_LONG).show();
            return;
        }


        uploadImage();

    }

    private void UpdateaccountINFO(String Url) {

        String name1 = nameUP.getText().toString();
        String interst1 =interestUP.getText().toString();
        String experience1 =expereinceUP.getText().toString();
        String skill1 = skillUP.getText().toString();
        final String DOB       =tvDate1.getText().toString().trim();


        HashMap userMap =new HashMap();
        userMap.put("name",name1);
        userMap.put("interest",interst1);
        userMap.put("dob",DOB);
        userMap.put("experience",experience1);
        userMap.put("skills",skill1);

        if(!Url.isEmpty()){
            userMap.put("photoUrl",Url);
        }



        profileUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful())
                {
                    sendUserToMainActivity();
                    Toast.makeText(EditMyProfile.this, "تم تحديث الملف الشخصي بنجاح", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(EditMyProfile.this, "فشل تحديث الملف الشخصي", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void sendUserToMainActivity(){
        Intent intent= new Intent(EditMyProfile.this, Timeline.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image_view.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditMyProfile.this, "Uploaded", Toast.LENGTH_SHORT).show();


                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.v("URL 123:",uri.toString());

                                    UpdateaccountINFO(uri.toString());


                                }
                            });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditMyProfile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }

                    });
        }else {
            UpdateaccountINFO("");
        }


    }

}
