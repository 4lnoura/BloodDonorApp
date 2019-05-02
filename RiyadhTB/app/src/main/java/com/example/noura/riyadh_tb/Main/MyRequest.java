package com.example.noura.riyadh_tb.Main;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.noura.riyadh_tb.Adapters.AdapterMyRequest;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyRequest extends AppCompatActivity  {
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Service> list;
    AdapterMyRequest adapter;
    Spinner spinner;
     Button Rigt ;
     Button left ;
    private String nameUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);
        list = new ArrayList<>();
        adapter = new AdapterMyRequest(null, null,null);

         Rigt = findViewById(R.id._switch_btn_left);
   left = findViewById(R.id._switch_btn_right);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //spinner = findViewById(R.id.spinnerReq);






        reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String UserEmail = user.getEmail();


        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {

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



                reference = FirebaseDatabase.getInstance().getReference().child("Services");
                reference.addValueEventListener(new ValueEventListener() {



                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            try {
                                Service p = dataSnapshot1.getValue(Service.class);

                                if (p.getIssuedBy().equals(nameUser))
                                    list.add(p);

                            } catch (Exception ignored) {

                            }
                        }
                        ArrayList<Service> listDispliy;

                        listDispliy = checkTo_order(list, "all");
                        try {
                            adapter = new AdapterMyRequest(MyRequest.this, listDispliy, MyRequest.this);

                            recyclerView.setAdapter(adapter);
                        } catch (Exception ignored) {

                        }


                        left.setOnClickListener(new View.OnClickListener() {


                            public void onClick(View v) {
                                try {
                                    setColor();
                                    int iconSer = R.drawable.sekizbit_selector_btn_left;
                                    left.setBackgroundResource(iconSer);
                                    adapter = new AdapterMyRequest(MyRequest.this, checkTo_order(list, "all"), MyRequest.this);
                                    recyclerView.setAdapter(adapter);
                                } catch (Exception ignored) {

                                }
                            }
                        });



                        Rigt.setOnClickListener(new View.OnClickListener() {


                            public void onClick(View v) {
                                try {
                                    setColor1();
                                    int iconSer = R.drawable.sekizbit_selector_btn_right;
                                    Rigt.setBackgroundResource(iconSer);
                                    adapter = new AdapterMyRequest(MyRequest.this, checkTo_order(list, "past"), MyRequest.this);
                                    recyclerView.setAdapter(adapter);
                                } catch (Exception ignored) {

                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

            private ArrayList<Service> checkTo_order(ArrayList<Service> p,String type) {
        ArrayList<Service> all = new ArrayList<>();

                ArrayList<Service> old = new ArrayList<>();

        int size=0;
        try {
            size = p.size();

        }
        catch (Exception ignored){

        }


for(int j=0;j<size;j++) {

            Service t = p.get(j);





            if (t.getResponseBy().isEmpty() && t.getState().equals("Published"))
                all.add(t);


}
                for (int i = 0; i < size; i++) {//change


                    Service t = p.get(i);
                    if (t.getRate() == 0&&!t.getResponseBy().isEmpty()&&!t.isConfirm()&&!(t.getState().equals("Rejected")))//to confirm
                        all.add(t);

                }
                for (int i = 0; i < size; i++) {
                Service t = p.get(i);
                if (t.getRate() == 0&&!t.getResponseBy().isEmpty()&&t.isConfirm()&&!(t.getState().equals("Rejected")))//for rate
                    all.add(t);

        }

                if ("past".equals(type))
                {
                    for (int i = 0; i < size; i++) {//change

                        Service a = p.get(i);
                        if (a.getRate() != 0 && !a.getResponseBy().isEmpty() && a.isConfirm())
                            old.add(a);

                        if(a.getState().equals("Rejected")){
                            old.add(a);
                        }
                    }
                    return old;
        }




        return all;

    }
   private void setColor() {
        Rigt.setBackgroundColor(Color.WHITE);
       Rigt.setTextColor(Color.BLACK);

   }




   private void setColor1() {
        left.setBackgroundColor(Color.WHITE);
        left.setTextColor(Color.BLACK);
    }


}


class GetDataFromFirebase extends AsyncTask<Void,Void,Boolean>{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}

