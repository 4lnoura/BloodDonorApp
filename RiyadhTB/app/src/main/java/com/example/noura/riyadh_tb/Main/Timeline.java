package com.example.noura.riyadh_tb.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.StartPages.About;
import com.example.noura.riyadh_tb.Adapters.AdapterTimeline;
import com.example.noura.riyadh_tb.StartPages.Login;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.StartPages.Start;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.example.noura.riyadh_tb.UserProfile.MyProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onesignal.OneSignal;

import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Timeline extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String Head_name, Head_email, nameUser , imageURL;
    private int Head_time;
    private TextView TextHead_name, TextHead_email, TextHead_time;
    private CircleImageView imageProfile;
    private ArrayList<Service> listServices;
    private ArrayList<User> userArrayList;
    private Spinner spinner;
    private AdapterTimeline adapterTimeline;
    private RecyclerView recyclerView;
    private int counter = 0, TimeCredit = 0;
    ArrayList<Service> TimelinelistServices = new ArrayList<>();
    private String UserUunique;
    //saharrrrrrrrr
    Button delete;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private String currentUserID;
    private DatabaseReference profileUserRef;

    public static String LoggedIn_User_Email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);



        /***********************/

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        //Setting the tags for current user "Notification"
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        LoggedIn_User_Email = firebaseUser.getEmail();
        OneSignal.sendTag("User_ID",LoggedIn_User_Email);

        /***********************/

        //sahaarrr
        progressBar = findViewById(R.id.progressBar2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextHead_name = (TextView) headerView.findViewById(R.id.head_name);
        TextHead_email = (TextView) headerView.findViewById(R.id.head_email);
        TextHead_time = (TextView) headerView.findViewById(R.id.head_time);
        imageProfile = (CircleImageView)headerView.findViewById(R.id.downloadImage);
        //logout
        mAuth = FirebaseAuth.getInstance();
        currentUserID =firebaseAuth.getCurrentUser().getUid();
        profileUserRef= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);



        //HEADER INFO.
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String UserID = user.getUid();
        String UserEmail = user.getEmail();

        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {


                    Head_name = dataSnapshot.child("name").getValue(String.class);
                    Head_email = dataSnapshot.child("email").getValue(String.class);
                    Head_time = dataSnapshot.child("timeCredit").getValue(Integer.class);
                    imageURL = dataSnapshot.child("photoUrl").getValue(String.class);
                    TextHead_name.setText(Head_name);
                    TextHead_email.setText(Head_email);
                    String time = Integer.toString(Head_time);
                    TextHead_time.setText(time);
                    Glide.with(Timeline.this).load(imageURL.isEmpty() ? getResources().getDrawable(R.drawable.user1) : imageURL)
                            .into(imageProfile);
                }
                catch (Exception E){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //BAR BAR


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.bar_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Timeline.this, SearchForUsers.class);
                startActivity(intent);
            }
        });


        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.bar_response);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Timeline.this, MyResponse.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.bar_publish);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


              /*  mDatabase.child("users").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Time_Credit=dataSnapshot.child("TimeCredit").getValue(String.class);


                        User Cuser=dataSnapshot.getValue(User.class);

                        username=dataSnapshot.child("username").getValue(String.class);
                        TimeCredit=Cuser.getTimeCredit();


                      //DatabaseReference reference= mDatabase.child("Services");
                        Query query=mDatabase.child("Services").orderByChild("issuedBy").equalTo(username);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshotS : dataSnapshot.getChildren()){
                                    Service service=snapshotS.getValue(Service.class);
                                    if(service.getIssuedBy().equals(username)&&service.isConfirm()==false){
                                        counter++;

                                    }

                                }





                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(TimeCredit<1) {
                    Toast.makeText(Timeline.this, "الساعات لديك لا تكفي لطلب اخر اصغر من ١", Toast.LENGTH_LONG).show();
                    return;
                }
                if(counter==TimeCredit) {
                    Toast.makeText(Timeline.this, "الساعات لديك لا تكفي لطلب اخر ", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TimeCredit>1 && counter!=TimeCredit){
                    Intent intent = new Intent(Timeline.this, PublishService.class);
                    startActivity(intent);
                }
*/


                Intent intent = new Intent(Timeline.this, PublishService.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.bar_map);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Timeline.this, NavigateGoogleMap.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setListService();





            try{

                LinearLayout all = findViewById(R.id.all3);
                all.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        adapterTimeline = new AdapterTimeline(Timeline.this, TimelinelistServices, userArrayList, Timeline.this);
                        recyclerView.setAdapter(adapterTimeline);

                    }
                });

                LinearLayout art = findViewById(R.id.art3);
                art.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات فنية");

                    }
                });


                LinearLayout car = findViewById(R.id.car);
                car.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات السيارات");

                    }
                });
                LinearLayout edu = findViewById(R.id.edu);
                edu.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات تعلمية");

                    }
                });
                LinearLayout computer = findViewById(R.id.computer);
                computer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات الحاسب");

                    }
                });
                LinearLayout home =findViewById(R.id.home);
                home.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات منزلية");

                    }
                });
                LinearLayout trans =findViewById(R.id.trans);
                trans.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات الترجمة");

                    }
                });
                LinearLayout other = findViewById(R.id.other);
                other.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setList("خدمات اخرى");

                    }
                });
        }
        catch (Exception e){

        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent intent = new Intent(Timeline.this, MyProfile.class);
            startActivity(intent);

        /*    profileUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        String myProfileImage =dataSnapshot.child("photoUrl").getValue().toString();
                        String myProfileName =dataSnapshot.child("name").getValue().toString();
                        String myProfileEmail =dataSnapshot.child("email").getValue().toString();

                        String myProfileUserName =dataSnapshot.child("username").getValue().toString();
                        String myProfileDOB =dataSnapshot.child("dob").getValue().toString();
                        String myProfileTimeCredit =dataSnapshot.child("timeCredit").getValue().toString();

                        String myProfileGender =dataSnapshot.child("gender").getValue().toString();
                        String myProfileSkills =dataSnapshot.child("skills").getValue().toString();
                        String myProfileInterst =dataSnapshot.child("interest").getValue().toString();
                        String myProfileExperience =dataSnapshot.child("experience").getValue().toString();

                        String serId =dataSnapshot.child("ID").getValue().toString();

                        Intent intent=new Intent(Timeline.this, User_Profile.class);
                        intent.putExtra("Name",myProfileName);
                        intent.putExtra("email",myProfileEmail);
                        intent.putExtra("image",myProfileImage);
                        intent.putExtra("skills",myProfileSkills);
                        intent.putExtra("Experience",myProfileExperience);
                        intent.putExtra("Interest",myProfileInterst);
                        intent.putExtra("Username",myProfileUserName);
                        intent.putExtra("DOB",myProfileDOB);
                        intent.putExtra("TimeCredit",myProfileTimeCredit);
                        intent.putExtra("id",serId);

                        startActivity(intent);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    throw databaseError.toException();

                }
            });

*/





        } else if (id == R.id.nav_request) {
/*
            Intent intent  = new Intent(Timeline.this, ViewLocationOfAcceptedService.class);
            Bundle bundle = new Bundle();
            //GET FROM Service ID
            String ServiceId="-LXVH0urxTV6HCsAcagG";
            bundle.putString("ServiceId", ServiceId);
            intent.putExtras(bundle);
            startActivity(intent);


*/
            startActivity(new Intent(Timeline.this, MyRequest.class));


        } else if (id == R.id.nav_donate) {

            Intent intent = new Intent(Timeline.this, DonateTime.class);
            startActivity(intent);

        } else if (id == R.id.nav_exit) {

            mAuth.signOut();
            finish();
            Intent intent = new Intent(Timeline.this, Login.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_delete) {

            DeleteMyAccount();

        } else if (id == R.id.nav_about) {


            Intent intent = new Intent(Timeline.this, About.class);
            startActivity(intent);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("ResourceType")
    private void setListService() {
        DatabaseReference reference;

        recyclerView = findViewById(R.id.recyclerTimeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetDataFromFirebaseTimeline().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        listServices = new ArrayList<>();
        check();
        reference = FirebaseDatabase.getInstance().getReference().child("Services");
      //  spinner = findViewById(R.id.spinnerTimeline);
//        spinner.setOnItemSelectedListener(this);
       /* String[] platforms = getResources().
                getStringArray(R.array.Category);
        ArrayAdapter<String> myadapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        platforms);
        spinner.setAdapter(myadapter);*/
        reference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        final Service p = dataSnapshot1.getValue(Service.class);
                        //add unqiuename

                        if (p.getResponseBy().isEmpty()&&!(p.getType().equals("One"))){
                      if(!p.getIssuedBy().equals(nameUser)) {
                          listServices.add(p);
                      }

                        }
                    } catch (Exception e) {

                    }
                }
                int listNum = listServices.size() - 1;

                for (int i = listNum; i >= 0; i--) {
                    TimelinelistServices.add(listServices.get(i));
                }


                // }
                FirebaseDatabase.getInstance().getReference().child("users").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            User user = dataSnapshot1.getValue(User.class);
                            userArrayList.add(user);

                        }
                        adapterTimeline = new AdapterTimeline(Timeline.this, TimelinelistServices, userArrayList, Timeline.this);
                        recyclerView.setAdapter(adapterTimeline);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // adapterTimeline = new AdapterTimeline(Timeline.this,listServices,Timeline.this);
                //recyclerView.setAdapter(adapterTimeline);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Timeline.this, "Ops.... Something is wrong  " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void setList(String Category) {
        int size = listServices.size();
        ArrayList<Service> listFlitter = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Service one = listServices.get(i);
            if (Category.equals(one.getCategory()))
                listFlitter.add(one);

        }
        adapterTimeline = new AdapterTimeline(Timeline.this, listFlitter, userArrayList, Timeline.this);
        recyclerView.setAdapter(adapterTimeline);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String text = spinner.getSelectedItem().toString();
        if (!text.equals("كل الطلبات"))
            setList(text);
        else
            adapterTimeline = new AdapterTimeline(Timeline.this, TimelinelistServices, userArrayList, Timeline.this);
        recyclerView.setAdapter(adapterTimeline);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    private void check() {
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

                        if (p.getEmail().equals(UserEmail))
                            nameUser = p.getUsername();

                    } catch (Exception ignored) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DeleteMyAccount(){

        profileUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    final String Username = dataSnapshot.child("username").getValue().toString();
                    Log.v("Username : ",Username);
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Services");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        int count=0;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Log.v("inter :","interLoop");
                                    Service service=snapshot.getValue(Service.class);
                                    if (service.getIssuedBy().equals(Username) || service.getResponseBy().equals(Username)){
                                        if (!service.isConfirm()){
                                            Log.v("title :",service.getTitle());
                                            count+=1;
                                        }

                                    }
                                }

                            }
                            if (count>0){
                                Log.v("counter :","more than 0");
                                // cannot remove account
                                AlertDialog.Builder dialog1 = new AlertDialog.Builder(Timeline.this);
                                dialog1.setTitle("لا تسطيع حذف الحساب");
                                dialog1.setMessage("لديك خدمات لم تكتمل ");
                                dialog1.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog1 = dialog1.create();
                                alertDialog1.show();
                                // cannot remove account

                            }else {

                                //delete account

                                AlertDialog.Builder dialog = new AlertDialog.Builder(Timeline.this);
                                dialog.setTitle("هل انت متاكد انك تريد حذف الحساب؟");
                                dialog.setMessage("عند حذف الحساب لن تتمكن من الوصول الى التطبيق بهذا الحساب ");
                                dialog.setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        profileUserRef.removeValue();
                                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Timeline.this, "تم حذف الحساب بنجاح", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Timeline.this, Start.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Timeline.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });

                                dialog.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = dialog.create();
                                alertDialog.show();

                                //delete account

                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}


class GetDataFromFirebaseTimeline extends AsyncTask<Void,Void,Boolean>{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
