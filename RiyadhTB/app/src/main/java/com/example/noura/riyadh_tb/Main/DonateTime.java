package com.example.noura.riyadh_tb.Main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DonateTime extends AppCompatActivity {

    FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();


    EditText UserNameD,NH;
    TextView emailU;
    Button donateBT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate_time);

        UserNameD=(EditText)findViewById(R.id.usernameD);
        NH=(EditText)findViewById(R.id.NH);
       // emailU=(TextView)findViewById(R.id.emailuser);
        donateBT=(Button)findViewById(R.id.donateTC);




        //Onclick button do search method
        donateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /************************************************/
                DonateTimeCreditMethod();


                /*************************************/
            }
        });

    }


    private void DonateTimeCreditMethod(){
        UserNameD=(EditText)findViewById(R.id.usernameD);
        final String UN=UserNameD.getText().toString().trim();

        NH=(EditText)findViewById(R.id.NH);
        String hour=NH.getText().toString().trim();
        final int Nhour= Integer.parseInt(hour);
        int nhcu,nhsu;



        if(UN.isEmpty()){
            UserNameD.setError("اسم المستخدم غير موجود");
            UserNameD.requestFocus();
            return;
        }

        if(hour.isEmpty()){
            NH.setError("ادخل عدد الساعات المراد التبرع بها");
            NH.requestFocus();
            return;
        }



        String CurrentUID=currentuser.getUid().toString();
        DatabaseReference referenceCurU=FirebaseDatabase.getInstance().getReference("users").child(CurrentUID);
        referenceCurU.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotCU) {
                final User CUser=dataSnapshotCU.getValue(User.class);
                final int NHCU= CUser.getTimeCredit();




                if(NHCU<Nhour){
                    NH.setError("يجب ان تكون عدد الساعات اقل من او تساوي عدد ساعاتك الحاليه");
                    NH.requestFocus();
                    return;
                }


                //هنا نبحث عن المستخدم


                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
                Query query=reference.orderByChild("username").equalTo(UN);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            UserNameD.setError("اسم المستخدم خطاء ");
                            UserNameD.requestFocus();
                            return;

                        }else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                /*
                                try {

                               HashMap<String, String> update = new HashMap<String, String>();
                           update.put("state", state);
                           String t = dataSnapshot1.getKey();
                           DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                           firebaseDel.updateChildren((HashMap) update);

                       }
                       catch (Exception r){

                       }
                                 */


                                User user = snapshot.getValue(User.class);
                                HashMap<String,Integer> DonateTo = new HashMap<String,Integer>();
                                Log.v("Name",user.getName());
                                DonateTo.put("timeCredit",user.getTimeCredit()+Nhour);
                                String KeyOfUser=snapshot.getKey();
                                DatabaseReference donateToRe=FirebaseDatabase.getInstance().getReference().child("users").child(KeyOfUser);
                                donateToRe.updateChildren((HashMap)DonateTo);
                                //user.setTimeCredit(user.getTimeCredit()+Nhour);
                                //snapshot.getRef().setValue(user);


                                HashMap<String,Integer> DonateFrom = new HashMap<String,Integer>();
                                DonateFrom.put("timeCredit",CUser.getTimeCredit()-Nhour);
                                String KeyOfCUser= dataSnapshotCU.getKey();
                                DatabaseReference donateFromRe=FirebaseDatabase.getInstance().getReference().child("users").child(KeyOfCUser);
                                donateFromRe.updateChildren((HashMap)DonateFrom);

                                //CUser.setTimeCredit(CUser.getTimeCredit()-Nhour);
                                //dataSnapshotCU.getRef().setValue(CUser);

                                Toast.makeText(DonateTime.this,"تمت عملية التبرع بنجاح",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent( DonateTime.this, Timeline.class);
                                startActivity(intent);



                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //نهاية البحث عن المستخدم



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }//end DonateTimeCredit method







}

