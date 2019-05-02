package com.example.noura.riyadh_tb.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noura.riyadh_tb.Main.MyRequestDetails;
import com.example.noura.riyadh_tb.Main.MyResponse;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.firebase.FirebaseTransaction;
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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyResponseAdapter extends RecyclerView.Adapter<MyResponseAdapter.ViewHolder> {

    private ArrayList<Service> services = new ArrayList<>();
    MyResponse myResponse;
    Service service;
    String RequesterName;
    Spinner State_Spinner;
    String previous;
    boolean confirm;
    DatabaseReference mDatabase;
    Context context;
public  MyResponseAdapter(Context e,ArrayList<Service>s){
    context=e;
    services=s;
}
//boolean ok=true;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_respones_list_services,parent,false);
        ViewHolder holder = new ViewHolder(view);



        return holder;}

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        service = services.get(position);
        holder.services=services.get(position);
        holder.setServices(services.get(position));

        final ArrayList<String> subSpinner = new ArrayList();
        String t=services.get(position).getState();
        String name=services.get(position).getIssuedBy();
        subSpinner.add(services.get(position).getState());
    if (services.get(position).getState().equals("Accepted")) {
        subSpinner.add("Preparing");
        //  subSpinner.add("Delivered");
    }

    if (services.get(position).getState().equals("Preparing")) {
        subSpinner.add("Delivered");
    }


    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, subSpinner);

    adapter.setDropDownViewResource(android.R.layout
            .simple_spinner_dropdown_item);

    State_Spinner.setAdapter(adapter);









       State_Spinner .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                String state = parent.getItemAtPosition(i).toString();
                String service_id = services.get(position).getID();

              if(!state.equals(subSpinner.get(0))) {
                  changeState(service_id, state);
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MyRequestDetails.class);


                intent.putExtra("id", services.get(position).getID());
                intent.putExtra("date", services.get(position).getDate());
                intent.putExtra("time", services.get(position).getIssuedTime());
                intent.putExtra("username", services.get(position).getIssuedBy());
                intent.putExtra("Title", services.get(position).getTitle());
                intent.putExtra("category", services.get(position).getCategory());
                intent.putExtra("Disciption", services.get(position).getDescreption());
                intent.putExtra("responseBy", services.get(position).getResponseBy());
                intent.putExtra("IssuedBy", services.get(position).getIssuedBy());

                //intent.putExtra("state",service.getState());
                intent.putExtra("state","");
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() { return services.size();}


    public void setService(MyResponse myResp, ArrayList<Service> service) {
        this.services = new ArrayList<>(service);
        this.myResponse=myResp;
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        Service services;
        CircleImageView image;
        TextView res_Name, res_username, res_Title, res_waiting;
        LinearLayout parentLayout,mylist;
        ImageView update_action;

        public ViewHolder(View itemView) {
            super(itemView);

            // image = itemView.findViewById(R.id.image);
            res_Name = itemView.findViewById(R.id.res_name);
            res_Title = itemView.findViewById(R.id.res_title);
            res_username = itemView.findViewById(R.id.res_username);
            parentLayout = itemView.findViewById(R.id.parent_layout2);
            State_Spinner = itemView.findViewById(R.id.res_spinner);
            mylist = itemView.findViewById(R.id.mylist);
            res_waiting = itemView.findViewById(R.id.res_waiting);

            ////////////////////*




            /////////////////*/
        }
        /*************************************************************************************/
        public void setServices(Service services) {
            this.services = services;
            /*Glide.with(itemView.getContext())
                    .load(users.getPhotoUrl().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            users.getPhotoUrl())
                    .into(image);*/
            // res_Name.setText(user);
            res_Title.setText(services.getTitle());
            res_username.setText(services.getIssuedBy());
            res_Title.setContentDescription((services.getID()));
            //getRequesterName(services.getIssuedBy());

            final String username = services.getIssuedBy();

            /*********************/

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            User p = dataSnapshot1.getValue(User.class);

                            if (p.getUsername().equals(username)){
                                RequesterName = p.getName();
                                res_Name.setText(RequesterName);}

                        } catch (Exception ignored) { }}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /*********************/

            previous = services.getState();
            int selection=0;
            if(previous.equals("Preparing"))
                selection =1;
            if(previous.equals("Delivered")) {
                selection = 2;
             //   mylist.setBackgroundColor(Color.rgb(237, 236, 234));
                res_waiting.setVisibility(View.VISIBLE);
                State_Spinner.setVisibility(View.GONE);
            }//

            State_Spinner.setSelection(selection);


            confirm = services.isConfirm();
            if(confirm==true){
             //   mylist.setBackgroundColor(Color.rgb(230, 237, 220));
                res_waiting.setText("تم تأكيد العميل");

            }


        }

    }//end class ViewHolder

    public void changeState(final String service_id, final String state){
        System.out.println("HHHHHEEEERREE 1111");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String stateOfService=dataSnapshot.getValue();
                // for (dataSnapshot)
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Service p = dataSnapshot1.getValue(Service.class);
                    if (p.getID().equals(service_id)) {
                       try {

                               HashMap<String, String> update = new HashMap<String, String>();
                           update.put("state", state);
                           String t = dataSnapshot1.getKey();
                           DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                           firebaseDel.updateChildren((HashMap) update);

                       }
                       catch (Exception r){

                       }






                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }//end method changeState


}//end class SearchAdapter
