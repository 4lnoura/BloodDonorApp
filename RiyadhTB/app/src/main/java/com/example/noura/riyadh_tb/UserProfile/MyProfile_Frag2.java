package com.example.noura.riyadh_tb.UserProfile;

import com.example.noura.riyadh_tb.Adapters.CommentAdapter;
import com.example.noura.riyadh_tb.Adapters.SearchAdapter;
import com.example.noura.riyadh_tb.Main.SearchForUsers;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
import com.example.noura.riyadh_tb.model.Comment;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class MyProfile_Frag2 extends Fragment {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private TextView comment_Empty;
    private ArrayList<Comment> comments = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_myprofile_frag2, container, false);


        recyclerView = v.findViewById(R.id.c_recyclerview);
        comment_Empty = v.findViewById(R.id.comment_empty);


        commentAdapter = new CommentAdapter(MyProfile_Frag2.this);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = user.getUid();
        loadData(UserID);

        return v;
    }

    private void update(ArrayList<Comment> comments) {
        commentAdapter.setUser(comments);
        comment_Empty.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);

    }

    private void loadData(String Key) {


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users").child(Key).child("comment");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);

                    comments.add(comment);
                }

                update(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
