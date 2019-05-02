package com.example.noura.riyadh_tb.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ServiceLocation extends  FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserMarker;
    private static final int code = 99;
    private LatLng LocationOfService;
    String title;
    String Description;
    String Category;
    private Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        final Intent intent = getIntent();
         title = intent.getStringExtra("title");
         Description = intent.getStringExtra("Description");
        Category = intent.getStringExtra("Category");

        userLocation();


        // Getting a reference to the map


    }



    @Override
    public void onMapReady(GoogleMap google) {
        googleMap = google;


        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title("انا");

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
                LocationOfService=latLng;
            }
        });
    }



    public void saveLoc(View view) {
        if (LocationOfService != null) {
            String XLocation = "" + LocationOfService.longitude;
            String YLocation = "" + LocationOfService.latitude;

            Intent intent = new Intent(ServiceLocation.this, PublishService.class);

            intent.putExtra("XLocation", XLocation);
            intent.putExtra("YLocation", YLocation);


            intent.putExtra("title", title);
            intent.putExtra("Description", Description);

            intent.putExtra("Category", Category);
            startActivity(intent);

        } else {

            Intent intent = new Intent(ServiceLocation.this, PublishService.class);
            intent.putExtra("title", title);
            intent.putExtra("Description", Description);

            intent.putExtra("Category", Category);

            startActivity(intent);
        }
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
                  double  XLocationUser = dataSnapshot.child("Location").child("XLocation").getValue(Double.class);
                   double YLocationUser = dataSnapshot.child("Location").child("YLocation").getValue(Double.class);
                    LatLng userLocation = new LatLng(XLocationUser, YLocationUser);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 14);
                    googleMap.animateCamera(cameraUpdate);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
