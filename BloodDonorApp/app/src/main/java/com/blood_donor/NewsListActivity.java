package com.blood_donor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blood_donor.adapters.NewsListAdapter;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_list);

        recyclerView = findViewById(R.id.recyclerView);
        empty = findViewById(R.id.empty);

        newsListAdapter = new NewsListAdapter();
        recyclerView.setAdapter(newsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
    }

    private void loadData() {
        new FirebaseTransaction(this)
                .child("news")
                .read(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<News> news = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            news.add(snapshot.getValue(News.class));
                        }
                        newsListAdapter.setNews(news);
                        empty.setVisibility(news.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerView.setVisibility(news.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
