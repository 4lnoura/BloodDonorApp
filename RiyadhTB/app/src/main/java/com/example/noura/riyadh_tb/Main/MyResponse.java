package com.example.noura.riyadh_tb.Main;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.Adapters.AdapterMyRequest;
import com.example.noura.riyadh_tb.Adapters.MyResponseAdapter;
import com.example.noura.riyadh_tb.Adapters.DirectResponseAdapter;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.SekizbitSwitch;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
import com.example.noura.riyadh_tb.model.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;


public class MyResponse extends AppCompatActivity{

    final Activity that = this;

    private TextView Res_Empty;
    private ImageView Res_Notify;
    private RecyclerView recyclerView;
    private MyResponseAdapter myResponseAdapter;
    private DirectResponseAdapter directResponseAdapter;
    private ArrayList<Service> services = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String current_username;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_response);


        recyclerView = findViewById(R.id.res_recyclerview);
        Res_Empty = findViewById(R.id.res_empty);
        Res_Notify = findViewById(R.id.res_notify);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = user.getUid();


        mDatabase.child("users").child(UserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_username = dataSnapshot.child("username").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });




        SekizbitSwitch mySwitch = new SekizbitSwitch(findViewById(R.id.sekizbit_switch));
        recyclerView.setAdapter(myResponseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyResponse.this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        mySwitch.setOnChangeListener(new SekizbitSwitch.OnSelectedChangeListener() {
            @Override
            public void OnSelectedChange(SekizbitSwitch sender) {
                if(sender.getCheckedIndex() ==0 )
                {
                    recyclerView.setAdapter(myResponseAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MyResponse.this));

                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    loadData();
                    //Toast.makeText(that,"Left Button Selected",Toast.LENGTH_SHORT).show();

                }
                else if(sender.getCheckedIndex() ==1 )
                {

                    directResponseAdapter = new DirectResponseAdapter();
                    recyclerView.setAdapter(directResponseAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MyResponse.this));
                    loadData2();
                   // Toast.makeText(that,"Right Button Selected",Toast.LENGTH_SHORT).show();
                }
            }
        });

/*******************************/


    }


    private void update(ArrayList<Service> service) {
      //  myResponseAdapter.setService(this,service);

        MyResponseAdapter adapter = new MyResponseAdapter(MyResponse.this, service);
        recyclerView.setAdapter(adapter);

        Res_Empty.setVisibility(service.isEmpty() ? View.VISIBLE : View.GONE);

    }

    private void update2(ArrayList<Service> service) {
        directResponseAdapter.setService2(MyResponse.this,service);
        Res_Empty.setVisibility(service.isEmpty() ? View.VISIBLE : View.GONE);
        Res_Notify.setVisibility(service.isEmpty() ? View.GONE : View.VISIBLE);

    }

    private void loadData() {


        new FirebaseTransaction(this)
                .child("Services")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        services = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Service s = snapshot.getValue(Service.class);
                          if (s.getResponseBy().equals(current_username))
                              if(s.getState().equals("Accepted") || s.getState().equals("Preparing") || s.getState().equals("Delivered")){
                                services.add(s);
                           }

                        }
                        update(services);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadData2() {


        new FirebaseTransaction(this)
                .child("Services")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        services = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Service s = snapshot.getValue(Service.class);
                            if (s.getResponseBy().equals(current_username) && s.getState().equals("Published")) {
                                services.add(s);
                            }

                        }
                        update2(services);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



}//end class