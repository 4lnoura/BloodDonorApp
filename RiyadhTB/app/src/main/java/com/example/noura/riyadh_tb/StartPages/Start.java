package com.example.noura.riyadh_tb.StartPages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.HashMap;

public class Start extends AppCompatActivity {
    protected LocationListener listener;
    private DatabaseReference mDatabase;
    private boolean ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);


    }

    public void onLoginClicked(View view) {
ok=true;
        Intent intent = new Intent(Start.this, Login.class);
        startActivity(intent);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //X Location is getLatitude and Y Location  getLongitude
try{
                mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String UserEmail = user.getEmail();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                mDatabase.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                User p = dataSnapshot1.getValue(User.class);

                             try{   if (p.getEmail().equals(UserEmail)) {
                               if(ok) {
                                     HashMap<String, Double> Xupdate = new HashMap<String, Double>();
                                     Xupdate.put("XLocation", location.getLatitude());
                                     HashMap<String, Double> Yupdate = new HashMap<String, Double>();
                                     Yupdate.put("YLocation", location.getLongitude());
                                     String t = dataSnapshot1.getKey();
                                     DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("users").child(t).child("Location");
                                     firebaseDel.updateChildren((HashMap) Xupdate);
                                     firebaseDel.updateChildren((HashMap) Yupdate);
                                    ok=false;
                                }

                                }}

                   catch (Exception ignored){}

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
} catch (Exception ignored) {

}
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

        if (ActivityCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        assert locationManager != null;
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);




}




    public void onSignUpClicked(View view) {

        Intent intent = new Intent(Start.this, SignUp.class);
        startActivity(intent);
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
}

