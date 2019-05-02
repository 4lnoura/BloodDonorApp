package com.example.noura.riyadh_tb.StartPages;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;

import com.example.noura.riyadh_tb.Main.Timeline;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.UserProfile.EditMyProfile;
import com.example.noura.riyadh_tb.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    public static final int RESULT_LOAD_IMAGE = 1;

    //upload image
    private Uri filePath;
    protected ImageView imageView;
    private final int PICK_IMAGE_REQUEST = 1;
    private String imageUrl;


    protected EditText editTextName,  editTextusername, editTextEmail, editTextPassword, Passconfirm;
    protected FloatingActionButton fabDate1;
    protected TextView tvDate1;
    protected RadioGroup genderRadioGroup;


    //Add Firebase Database stuff
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String UserID;
    private int ageNewUser;
    protected LocationListener listener;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //assign value of form
        editTextName=(EditText) findViewById(R.id.signup_name);
        editTextusername=(EditText) findViewById(R.id.signup_username);
        editTextEmail=(EditText) findViewById(R.id.signup_email);
        editTextPassword=(EditText) findViewById(R.id.signup_password);
        Passconfirm = (EditText) findViewById(R.id.confirm);
        genderRadioGroup = findViewById(R.id.statusRadioGroup);

        fabDate1 = (FloatingActionButton) findViewById(R.id.fabDate);
        tvDate1 = (TextView) findViewById(R.id.tvDate);

        imageView = (ImageView) findViewById(R.id.uploadImage);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               chooseImage();

            }
        });

        fabDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(view.getContext(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.getDatePicker().setMaxDate(new Date().getTime());
                dateDialog.show();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signup_button).setOnClickListener(this);


    }//end of method onCreate

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

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }


    private void ValidateUser(){

        final String Name      =editTextName.getText().toString().trim();
        final String Username  =editTextusername.getText().toString().trim();
        final String Email     =editTextEmail.getText().toString().trim();
        final String Password  =editTextPassword.getText().toString().trim();
        final String DOB       =tvDate1.getText().toString().trim();
        boolean gen = genderRadioGroup.getCheckedRadioButtonId() == R.id.female;
        final String Gender;

        if(gen == true)
            Gender = "female";
                    else Gender = "male";


        if(Name.isEmpty()){
            editTextName.setError("enter the email");
            editTextName.requestFocus();
            return;
        }

        if(Username.isEmpty()){
            editTextusername.setError("enter the email");
            editTextusername.requestFocus();
            return;
        }

        if(Email.isEmpty()){
            editTextEmail.setError("enter the email");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }


        if(Password.isEmpty()){
            editTextPassword.setError("enter the password");
            editTextPassword.requestFocus();
            return;
        }
        if(Password.length()<6){
            editTextPassword.setError("password should be at lest 6 characters long");
            editTextPassword.requestFocus();
            return;
        }

        String pass = editTextPassword.getText().toString();
        String cpass = Passconfirm.getText().toString();

        if(!pass.equals(cpass)){
            Toast.makeText(SignUp.this,"Password Not matching",Toast.LENGTH_SHORT).show();
            Passconfirm.setError("Re confirmed the passwrod");
            Passconfirm.requestFocus();
            return;}

        if (ageNewUser<15){
            tvDate1.setText("يجب ان يكون العمر>=١٥");
            tvDate1.requestFocus();

            return;
        }

        if(tvDate1.equals("اليوم / الشهر / السنة")){
            Toast.makeText(SignUp.this, "الرجاء تحديد تاريخ الميلاد ", Toast.LENGTH_LONG).show();
            return;
        }


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
        Query query=reference.orderByChild("username").equalTo(Username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    editTextusername.setError("ادخل اسم مستخدم اخر");
                    editTextusername.requestFocus();
                    return;

                }else{

                    uploadImage();
                    /*

                    final Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){


                                        User user = new User(Name,Username,Email,DOB,Url,Gender);


                                        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SignUp.this, "User registration successfull", Toast.LENGTH_LONG).show();
                                                    location();
                                                    Intent intent = new Intent(SignUp.this, Timeline.class);
                                                    startActivity(intent);


                                                }else{
                                                    Toast.makeText(SignUp.this, "Try Again with another email", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }); //end of add a information of the user



                                    }else
                                        Toast.makeText(SignUp.this, "Try Again with another email", Toast.LENGTH_LONG).show();

                                }


                            });

                   */
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }//end of method registerUser


    private void registerUser(final String Url){

        final String Name      =editTextName.getText().toString().trim();
        final String Username  =editTextusername.getText().toString().trim();
        final String Email     =editTextEmail.getText().toString().trim();
        final String Password  =editTextPassword.getText().toString().trim();
        final String DOB       =tvDate1.getText().toString().trim();
        boolean gen = genderRadioGroup.getCheckedRadioButtonId() == R.id.female;
        final String Gender;
        if(gen == true)
            Gender = "female";
        else Gender = "male";



        final Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            User user = new User(Name,Username,Email,DOB,Url,Gender);


                            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUp.this, "User registration successfull", Toast.LENGTH_LONG).show();
                                        location();

                                        Intent intent = new Intent(SignUp.this, Timeline.class);
                                        startActivity(intent);


                                    }else{
                                        Toast.makeText(SignUp.this, "Try Again with another email", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }); //end of add a information of the user



                        }else
                            Toast.makeText(SignUp.this, "Try Again with another email", Toast.LENGTH_LONG).show();

                    }


                });

        /****************************************/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signup_button:
                ValidateUser();
                break;
        }

    }//end of method onClick



    //////////////////////////////

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
                imageView.setImageBitmap(bitmap);
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
                            Toast.makeText(SignUp.this, "Uploaded", Toast.LENGTH_SHORT).show();


                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.v("URL 123:",uri.toString());

                                    registerUser(uri.toString());


                                }
                            });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            registerUser("");
        }


    }

    ////////////////////////////////////


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            Uri selectedImage = data.getData();
            imageView.setImageURI(selectedImage);

        }}
        */
        private void location(){




            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            listener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    //X Location is getLatitude and Y Location  getLongitude

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String UserEmail = user.getEmail();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

                    mDatabase.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                try {
                                    User p = dataSnapshot1.getValue(User.class);

                                    if (p.getEmail().equals(UserEmail)){
                                        HashMap<String, Double> Xupdate=new HashMap<String, Double>();
                                        Xupdate.put("XLocation",location.getLatitude());
                                        HashMap<String, Double> Yupdate=new HashMap<String, Double>();
                                        Yupdate.put("YLocation",location.getLongitude());
                                        String t = dataSnapshot1.getKey();
                                        DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("users").child(t).child("Location");
                                        firebaseDel.updateChildren((HashMap)Xupdate);
                                        firebaseDel.updateChildren((HashMap)Yupdate);


                                    }



                                } catch (Exception ignored) {

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }




                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);

                }
            };


            configure_button();

            if (ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            assert locationManager != null;
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.

    }
}//end of class Registration
