package com.example.noura.riyadh_tb.Main;

import android.content.Intent;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class ViewLocationOfAcceptedService extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double XLocationService;
    private double YLocationService;
    private double XLocationUser;
    private double YLocationUser;
    private String ServicName;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location_of_accepted_service);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent intent = getIntent();
        String id = intent.getStringExtra("ServiceId");
        setServiceLocation(id);
        setUserLocation();
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        // Add a marker in Sydney and move the camera
        LatLng dest = new LatLng(XLocationService,YLocationService);;



        // LatLng origin = new LatLng(XLocationUser, YLocationUser);;
        LatLng origin = new LatLng(XLocationUser,YLocationUser);;

        List<LatLng> path = new ArrayList();

        path.add(origin);
        path.add(dest);

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(path)
                .width(13)
                .color(Color.BLUE));
        BitmapDescriptor iconSer = BitmapDescriptorFactory.fromResource(R.drawable.serivcemap);
        BitmapDescriptor iconUser = BitmapDescriptorFactory.fromResource(R.drawable.usermap);

        mMap.addMarker(new MarkerOptions().position(origin).title("انت").icon(iconUser));
        mMap.addMarker(new MarkerOptions().position(dest).title("خدمة" +ServicName).icon(iconSer));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(origin, 18);
        mMap.animateCamera(cameraUpdate);





    }






    private void setServiceLocation(final String id) {
        reference = FirebaseDatabase.getInstance().getReference().child("Services"); // change from db
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);
                        String ids = p.getID();
                        Boolean y;
                        if (ids.equals(id)) {
                            ServicName=p.getTitle();

                            XLocationService = dataSnapshot1.child("Location").child("XLocation").getValue(Double.class);
                            YLocationService=dataSnapshot1.child("Location").child("YLocation").getValue(Double.class);

                        }
                    } catch (Exception ignored) {
                        Toast.makeText(ViewLocationOfAcceptedService.this, ignored.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewLocationOfAcceptedService.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void setUserLocation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String UserID;
        UserID = user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                XLocationUser = dataSnapshot.child("Location").child("XLocation").getValue(Double.class);
                YLocationUser = dataSnapshot.child("Location").child("YLocation").getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
