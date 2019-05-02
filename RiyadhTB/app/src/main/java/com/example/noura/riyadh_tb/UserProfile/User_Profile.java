package com.example.noura.riyadh_tb.UserProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.Adapters.CommentAdapter;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
import com.example.noura.riyadh_tb.model.Comment;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class User_Profile extends AppCompatActivity {



    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private TextView comment_Empty;
    private ArrayList<Comment>comments=new ArrayList<>();

    private DatabaseReference mDatabase;
    FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String issuedByName,issuedByUsername,imageURL,key;
    TextView Name,UserName,Email,skills,Experience,Interest,DOB;
    CircleImageView profile_image;
    RatingBar profile_ratingBar;
    EditText comment;
    Button AddComment;
    View CommentForm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        recyclerView = findViewById(R.id.recyclerviewComment);
        comment_Empty = findViewById(R.id.comment_empty);

        commentAdapter=new CommentAdapter();
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Name=findViewById(R.id.nameOfUser);
        UserName=findViewById(R.id.usernameOfUser);
        Email=findViewById(R.id.userprofile_email);
        skills=findViewById(R.id.userprofile_skills);
        Experience=findViewById(R.id.userprofile_Experience);
        Interest=findViewById(R.id.userprofile_Interest);
        DOB=findViewById(R.id.userprofile_DOB);
        profile_image=findViewById(R.id.userprofile_image);
        profile_ratingBar=findViewById(R.id.UserProfile_ratingBar);

        CommentForm=findViewById(R.id.commentForm);

        comment=findViewById(R.id.commentText);
        AddComment=findViewById(R.id.AddComment);


       //profile_ratingBar.setClickable(false);
//profile_ratingBar.setEnabled(false);
        final Intent intent=getIntent();

        Name.setText(intent.getStringExtra("Name"));
        UserName.setText(intent.getStringExtra("Username"));
        Email.setText(intent.getStringExtra("email"));
        skills.setText(intent.getStringExtra("skills"));
        Experience.setText(intent.getStringExtra("Experience"));
        Interest.setText(intent.getStringExtra("Interest"));
        DOB.setText(intent.getStringExtra("DOB"));
       // Context context;
        //Glide.with(context.thi).load(intent.getStringExtra("").isEmpty() ? context.getContext().getResources().getDrawable(R.drawable.placeholder) :
          //      users.getPhotoUrl())
            //    .into(image);




        // rating


        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Services");
        Query query=databaseReference.orderByChild("responseBy").equalTo(intent.getStringExtra("Username"));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter=0;
               int rate=0;
                int five=0;
                        int four=0;
                                int three=0;
                        int twe=0;
                                int one=0;

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Service service=snapshot.getValue(Service.class);
                    if(service.isConfirm()==true){
                     counter++;
                     if(service.getRate()==5)
                         five++;
                        if(service.getRate()==4)
                            four++;
                        if(service.getRate()==3)
                            three++;
                        if(service.getRate()==2)
                            twe++;
                        if(service.getRate()==1)
                            one++;
                    }

                }
                if((five+four+three+twe+one)!=0){
                rate=((5*five)+(4*four)+(3*three)+(2*twe)+(1*one))/(five+four+three+twe+one);
                profile_ratingBar.setRating(rate);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //end rating




        String UserID = CurrentUser.getUid().toString();
        String emailCU=CurrentUser.getEmail().toString();


        if (emailCU.equals(intent.getStringExtra("email"))){
            CommentForm.setVisibility(View.GONE);

        }
        /*
        if (!emailCU.equals(intent.getStringExtra("email"))){
            editProfileButtonView.setVisibility(View.GONE);

        }
        if (!emailCU.equals(intent.getStringExtra("email"))){
            MyHoursView.setVisibility(View.GONE);

        }
*/


        DatabaseReference referenceCurU=FirebaseDatabase.getInstance().getReference("users").child(UserID);
        referenceCurU.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotCU) {
                final User CUser=dataSnapshotCU.getValue(User.class);
                issuedByUsername=CUser.getUsername();
                issuedByName=CUser.getName();
                imageURL=CUser.getPhotoUrl();
                Log.v("Name: ",issuedByName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    if (user.getEmail().equals(intent.getStringExtra("email"))){
                        Log.v("Email",user.getEmail());
                        key=snapshot.getKey();
                        loadData(key);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //////////////////




        AddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String commentText=comment.getText().toString().trim();

             if (commentText.isEmpty()){
                 comment.setError("اكتب التعليق ؟!");
                 comment.requestFocus();
                 return;
             }

                insertComment(issuedByName,issuedByUsername,imageURL,commentText,intent.getStringExtra("email"));

            }
        });





    }

    public void insertComment(String Name, String UserName, String image, String comment, final String email){

        final Comment commentObject=new Comment(Name,UserName,image,comment);



        new FirebaseTransaction(User_Profile.this).child("users").child(key).child("comment").push().setValue(commentObject);

 

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    if (user.getEmail().equals(email)){
                        Log.v("Email",user.getEmail());
                        key=snapshot.getKey();
                        loadData(key);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void update(ArrayList<Comment> comments) {
        for(int i=0;i<comments.size();i++){
            Log.v("Comment",comments.get(i).getTextComment());
        }
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
