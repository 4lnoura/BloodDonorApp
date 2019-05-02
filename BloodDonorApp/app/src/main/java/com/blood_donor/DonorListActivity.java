package com.blood_donor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.blood_donor.adapters.DonorListAdapter;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonorListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonorListAdapter donorListAdapter;
    private TextView empty;
    private Spinner locationSpinner;
    private Spinner bloodGroupSpinner;

    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);

        recyclerView = findViewById(R.id.recyclerView);
        empty = findViewById(R.id.empty);
        locationSpinner = findViewById(R.id.locations);
        bloodGroupSpinner = findViewById(R.id.bloodTypes);

        donorListAdapter = new DonorListAdapter();
        recyclerView.setAdapter(donorListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        locationSpinner.setOnItemSelectedListener(onItemSelectedListener);
        bloodGroupSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    private void update(ArrayList<User> users) {
        donorListAdapter.setUsers(users);
        empty.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(users.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public void filter() {
        ArrayList<User> filtered = new ArrayList<>();
        String location = locationSpinner.getSelectedItem() == null ? "" : locationSpinner.getSelectedItem().toString();
        String bloodGroup = bloodGroupSpinner.getSelectedItem() == null ? "" : bloodGroupSpinner.getSelectedItem().toString();
        for (User user : users) {
            if ((user.getLocation().equals(location) || location.equals("All")) && (user.getBloodType().equals(bloodGroup) || bloodGroup.equals("All"))) {
                filtered.add(user);
            }
        }
        update(filtered);
    }

    private void loadData() {
        new FirebaseTransaction(this)
                .child("users")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users = new ArrayList<>();
                        ArrayList<String> locations = new ArrayList<>();
                        locations.add("All");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user.getDiseases().equals("No") && user.getStatus() == 1) {
                                users.add(user);
                            }
                            if (!locations.contains(user.getLocation())) {
                                locations.add(user.getLocation());
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DonorListActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, locations);
                            locationSpinner.setAdapter(arrayAdapter);
                        }
                        filter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
