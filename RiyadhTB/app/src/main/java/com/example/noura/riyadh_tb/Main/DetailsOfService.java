package com.example.noura.riyadh_tb.Main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class DetailsOfService extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
    TextView Name, username, email, time, date, titleS, descS, Type;
    Button Accept_Button;
    String goTo;
    String nameUser;
    private MapView mapView;
    private GoogleMap gmap;
    private double XLocationService;
    private double YLocationService;
    private String ServicName;
    private String ID;
    private DatabaseReference reference;
    private String MAP_VIEW_BUNDLE_KEY = "AIzaSyBfSDzhviNjpouTwzB1_SkLrUvHQ4yG0ng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_service);
        mapView = findViewById(R.id.map_view);
        CardView cardView = findViewById(R.id.card);
        Name = (TextView) findViewById(R.id.text_Name1);
        username = (TextView) findViewById(R.id.text_userName1);
        email = (TextView) findViewById(R.id.emailUSD);
        time = (TextView) findViewById(R.id.timeS);
        date = (TextView) findViewById(R.id.dateS);
        titleS = (TextView) findViewById(R.id.text_title1);
        descS = (TextView) findViewById(R.id.text_desc1);
        Type = (TextView) findViewById(R.id.text_type1);
        Accept_Button = (Button) findViewById(R.id.Accept_button);
        Bundle mapViewBundle = null;

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);


        mapView.getMapAsync(this);
        final Intent intant = getIntent();
        cardView.setRadius(50);

        Name.setText(intant.getStringExtra("Name"));
        username.setText(intant.getStringExtra("username"));
        email.setText(intant.getStringExtra("email"));
        time.setText(intant.getStringExtra("time"));
        date.setText(intant.getStringExtra("date"));
        titleS.setText(intant.getStringExtra("Title"));
        descS.setText(intant.getStringExtra("Disciption"));
        Type.setText(intant.getStringExtra("category"));
        goTo = intant.getStringExtra("go");
        ID=intant.getStringExtra("id");

        String userEmail = currentuser.getEmail();

        View p = findViewById(R.id.Accept_button);

        if (userEmail.equals(intant.getStringExtra("email"))) {

            p.setVisibility(View.GONE);
        } else {
            p.setVisibility(View.VISIBLE);
        }


        //accepte button
        Accept_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccepteService(intant.getStringExtra("id"));

                if (goTo.equals("NavigateGoogleMap")) {
                    Intent intent = new Intent(DetailsOfService.this, NavigateGoogleMap.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DetailsOfService.this, Timeline.class);
                    startActivity(intent);
                }
            }
        });


    }

    private void AccepteService(final String id) {

        check();


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
                            startNotification(p);

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        gmap.setMinZoomPreference(12);


        reference = FirebaseDatabase.getInstance().getReference().child("Services"); // change from db
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);
                        String ids = p.getID();
                        Boolean y;
                        if (ids.equals(ID)) {
                            ServicName=p.getTitle();

                            XLocationService = dataSnapshot1.child("Location").child("XLocation").getValue(Double.class);
                            YLocationService= dataSnapshot1.child("Location").child("YLocation").getValue(Double.class);

                            LatLng ny = new LatLng(XLocationService, YLocationService);

                            BitmapDescriptor iconSer = BitmapDescriptorFactory.fromResource(R.drawable.serivcemap);

                            gmap.addMarker(new MarkerOptions().position(ny).title(" خدمة " + ServicName).icon(iconSer));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ny, 16);
                            gmap.animateCamera(cameraUpdate);
                        }
                    } catch (Exception ignored) {
                        Toast.makeText(DetailsOfService.this, ignored.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailsOfService.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void startNotification(Service service) {

        System.out.print("HHHHHHEEEEERRRRREEEEE 2222 out");
        final String ServiceProvider = service.getResponseBy();
        String ServiceRequester = service.getIssuedBy();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("username").equalTo(ServiceRequester);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);
                    String send_email = user.getEmail();
                    System.out.print("HHHHHHEEEEERRRRREEEEE 2222 in");
                    sendNotification(ServiceProvider,send_email);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotification(String ServiceProvider2, String send_email2) {

        System.out.print("HHHHHHEEEEERRRRREEEEE 3333 out");
        final  String ServiceProvider = ServiceProvider2;
        final  String send_email = send_email2;


        // Toast.makeText(DirectResponseAdapter.this, "Current Recipients is : user1@gmail.com ( Just For Demo )", Toast.LENGTH_SHORT).show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                 /*   String send_email;


                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (Timeline.LoggedIn_User_Email.equals("noura@gmail.com")) {
                        send_email = "raghed@gmail.com";
                    } else {
                        send_email = "noura@gmail.com";
                    }*/

                    System.out.print("HHHHHHEEEEERRRRREEEEE 3333 in");

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MThjNDk2ZDItZmZhYi00MjZlLTk5ZjYtOGUxNGMwMTQ1ODU5");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"4afe1964-128b-46db-b30c-e383d3f68546\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Congrats your request is accepted\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

}