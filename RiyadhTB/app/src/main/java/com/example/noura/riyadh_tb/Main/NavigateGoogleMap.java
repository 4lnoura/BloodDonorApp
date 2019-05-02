package com.example.noura.riyadh_tb.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NavigateGoogleMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String nameUser;
    private DatabaseReference mDatabase;
    private double XLocationUser;
    private double YLocationUser;
    private String name;
    private String email;
    private String imgURL;
    TextView Name, username, email1, time, date, titleS, descS, Type;
    View dialogView1;
    private String ff;
    String id=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        setLocation();
        userLocation();
        // Add a marker in Sydney and move the camera


    }


    private void setLocation() {
        checkUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Services");
        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        final Service p = dataSnapshot1.getValue(Service.class);
                        if (p.getResponseBy().isEmpty()&&!(p.getType().equals("One"))) {

                            if (!p.getIssuedBy().equals(nameUser)) {

                                String ServicName = p.getTitle();
                                BitmapDescriptor iconSer = BitmapDescriptorFactory.fromResource(R.drawable.serivcemap);
                                double XLocationService = dataSnapshot1.child("Location").child("XLocation").getValue(Double.class);
                                double YLocationService = dataSnapshot1.child("Location").child("YLocation").getValue(Double.class);

                                LatLng Service = new LatLng(XLocationService, YLocationService);
                                mMap.addMarker(new MarkerOptions().position(Service).title(" خدمة " + ServicName).snippet(p.getID()).icon(iconSer));

                            }

                        }
                    } catch (Exception e) {

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NavigateGoogleMap.this, " " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String UserEmail = user.getEmail();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        User p = dataSnapshot1.getValue(User.class);

                        if (p.getEmail().equals(UserEmail)) {
                            nameUser = p.getUsername();

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

    private void userLocation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String UserID;
        UserID = user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    XLocationUser = dataSnapshot.child("Location").child("XLocation").getValue(Double.class);
                    YLocationUser = dataSnapshot.child("Location").child("YLocation").getValue(Double.class);
                    LatLng userLocation = new LatLng(XLocationUser, YLocationUser);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 14);
                    mMap.animateCamera(cameraUpdate);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
         id = marker.getSnippet();


        map(marker);

        //displayService(id);
        return true;
    }


    private void map(final Marker marker) {
        final AlertDialog.Builder optionDialog = new AlertDialog.Builder(this);
        final AlertDialog dialogView = setting(marker);
        setView(dialogView1);
        dialogView.findViewById(R.id.Accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
                database.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            try {
                                Service p = dataSnapshot1.getValue(Service.class);

                                if (p.getID().equals(id)) {
                                    check();
                                    HashMap<String, String> update = new HashMap<String, String>();
                                    update.put("state", "Accepted");
                                    String t = dataSnapshot1.getKey();
                                    DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                                    firebaseDel.updateChildren((HashMap) update);

                                    HashMap<String, String> updatename = new HashMap<String, String>();
                                    updatename.put("responseBy", nameUser);
                                    String keyname = dataSnapshot1.getKey();
                                    DatabaseReference firebaseDelname = FirebaseDatabase.getInstance().getReference().child("Services").child(keyname);
                                    firebaseDelname.updateChildren((HashMap) updatename);

                                    marker.setVisible(false);

                                }
                            } catch (Exception ignored) {

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


















                dialogView.dismiss();
            }




        });
        marker.setVisible(true);

        onResume();


        //  optionDialog.create();
        // optionDialog.show();
    }




    private AlertDialog setting(Marker marker) {
        //ViewGroup viewGroup = viewGroup.findViewById();

        //then we will inflate the custom alert dialog xml that we created

         dialogView1 =  LayoutInflater.from(this).inflate(R.layout.dialog_goolemap,null, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView1);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //finally creating the alert dialog and displaying it
        return alertDialog;
    }

    private void setView(View dialogView) {



        Name = (TextView) dialogView.findViewById(R.id.text_Name1);
        username = (TextView) dialogView.findViewById(R.id.text_userName1);
        email1 = (TextView) dialogView.findViewById(R.id.emailUSD);
        time = (TextView) dialogView.findViewById(R.id.timeS);
        date = (TextView) dialogView.findViewById(R.id.dateS);
        titleS = (TextView) dialogView.findViewById(R.id.text_title1);
        descS = (TextView) dialogView.findViewById(R.id.text_desc1);
        Type = (TextView) dialogView.findViewById(R.id.text_type1);




        mDatabase = FirebaseDatabase.getInstance().getReference().child("Services");
        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        final Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(id)) {


                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                            mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        try {
                                            final User t = dataSnapshot1.getValue(User.class);

                                            if(t.getUsername().equals(p.getIssuedBy())) {


                                                Name.setText(t.getName());
                                                username.setText(p.getIssuedBy());
                                                email1.setText(t.getEmail());
                                                time.setText(p.getIssuedTime());
                                                date.setText(p.getDate());
                                                titleS.setText(p.getTitle());
                                                descS.setText(p.getDescreption());
                                                Type.setText(p.getType());
                                            }

                                        } catch (Exception e) {

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                            } catch (Exception e) {

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NavigateGoogleMap.this, " " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void displayService(final String id) {
        final Intent intent = new Intent(NavigateGoogleMap.this, DetailsOfService.class);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Services");

        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        final Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(id)) {
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

                            mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                        try {
                                            final User p1 = dataSnapshot1.getValue(User.class);
                                            if (p1.getUsername().equals(p.getIssuedBy())) {
                                                intent.putExtra("Name", p1.getName());
                                                intent.putExtra("email", p1.getEmail());
                                                intent.putExtra("image", p1.getPhotoUrl());
                                                intent.putExtra("id", p.getID());
                                                intent.putExtra("date", p.getDate());
                                                intent.putExtra("time", p.getIssuedTime());
                                                intent.putExtra("username", p.getIssuedBy());
                                                intent.putExtra("Title", p.getTitle());
                                                intent.putExtra("category", p.getCategory());
                                                intent.putExtra("Disciption", p.getDescreption());
                                                intent.putExtra("go", "NavigateGoogleMap");


                                                startActivity(intent);


                                            }
                                        } catch (Exception e) {

                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setInfo(final String username){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    try {
                        final User p = dataSnapshot1.getValue(User.class);
                        if (p.getUsername().equals(username)) {
                            name = p.getName();

                            email = p.getEmail();
                            imgURL = p.getPhotoUrl();


                        }
                    } catch (Exception e) {

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void check() {
        DatabaseReference mDatabase;
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
}