package com.blood_donor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blood_donor.adapters.ClinicListAdapter;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Clinic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClinicListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClinicListAdapter clinicListAdapter;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_list);

        recyclerView = findViewById(R.id.recyclerView);
        empty = findViewById(R.id.empty);

        clinicListAdapter = new ClinicListAdapter();
        recyclerView.setAdapter(clinicListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
    }

    private void loadData() {
        new FirebaseTransaction(this)
                .child("clinics")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Clinic> clinics = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            clinics.add(snapshot.getValue(Clinic.class));
                        }
                        clinicListAdapter.setClinics(clinics);
                        empty.setVisibility(clinics.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerView.setVisibility(clinics.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
