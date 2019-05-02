package com.example.noura.riyadh_tb.Main;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.noura.riyadh_tb.Adapters.SearchAdapter;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SearchForUsers extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private EditText Search_Field;
    private TextView Search_Empty;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_for_users);

        recyclerView = findViewById(R.id.recyclerview);
        Search_Field = findViewById(R.id.search_field);
        Search_Empty = findViewById(R.id.search_empty);


        searchAdapter = new SearchAdapter(SearchForUsers.this);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Search_Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  loadData();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // loadData();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadData();

            }
        });


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });*/

    }


    private void update(ArrayList<User> users) {
        searchAdapter.setUser(users);
        Search_Empty.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);

    }

    private void loadData() {

        final String search_field_value =Search_Field.getText().toString().trim();
        final String search_faild=search_field_value.toLowerCase();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String emailCU=CurrentUser.getEmail();
        new FirebaseTransaction(this)
                .child("users")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);

                            if(!(emailCU.equals(user.getEmail()))){
                            String Name=user.getName().toLowerCase();
                            String UserName=user.getUsername().toLowerCase();
                            String Interest=user.getInterest().toLowerCase();
                            String Skills=user.getSkills().toLowerCase();
                            String Experience=user.getExperience().toLowerCase();

                            if (Name.matches("(.*)"+search_faild+"(.*)")||UserName.matches("(.*)"+search_faild+"(.*)")||Interest.matches("(.*)"+search_faild+"(.*)")||Skills.matches("(.*)"+search_faild+"(.*)")||Experience.matches("(.*)"+search_faild+"(.*)")) {
                                users.add(user);
                            }}

                        }

                        update(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}