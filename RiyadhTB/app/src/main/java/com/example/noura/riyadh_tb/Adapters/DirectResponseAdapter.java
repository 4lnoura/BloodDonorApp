package com.example.noura.riyadh_tb.Adapters;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.noura.riyadh_tb.Main.MyRequestDetails;
import com.example.noura.riyadh_tb.Main.MyResponse;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

public class DirectResponseAdapter extends RecyclerView.Adapter<DirectResponseAdapter.ViewHolder> {

    private ArrayList<Service> services = new ArrayList<>();
    MyResponse myResponse;
    String RequesterName;
    DatabaseReference mDatabase;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_direct_list_services,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;}

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Service service = services.get(position);
        holder.setServices2(services.get(position));


        holder.res_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String service_id = service.getID();

                AcceptService(service_id);

                /*********************/


            }
        });


        holder.res_Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String service_id = service.getID();

                RejectService(service_id);

            }
        });

    }//end onBindViewHolder

    @Override
    public int getItemCount() { return services.size();}


    public void setService2(MyResponse myResp, ArrayList<Service> service) {
        this.services = new ArrayList<>(service);
        this.myResponse=myResp;
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        Service services;
        CircleImageView image;
        ImageView res_Accept, res_Reject,Res_Notify;
        TextView res_Name, res_username, res_Title;
        RelativeLayout parentLayout;
        RelativeLayout Dreponcse;

        public ViewHolder(View itemView) {
            super(itemView);

            // image = itemView.findViewById(R.id.image);
            res_Name = itemView.findViewById(R.id.res_name2);
            res_Title = itemView.findViewById(R.id.res_title2);
            res_username = itemView.findViewById(R.id.res_username2);
            parentLayout = itemView.findViewById(R.id.parent_layout3);
            res_Accept = itemView.findViewById(R.id.res_accept);
            res_Reject = itemView.findViewById(R.id.res_reject);
            Res_Notify = itemView.findViewById(R.id.res_notify);
            // Dreponcse = itemView.findViewById(R.id.Dreponcse);

            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(myResponse,MyRequestDetails.class);


                    intent.putExtra("id",services.getID());
                    intent.putExtra("date",services.getDate());
                    intent.putExtra("time",services.getIssuedTime());
                    intent.putExtra("username",services.getIssuedBy());
                    intent.putExtra("Title",services.getTitle());
                    intent.putExtra("category",services.getCategory());
                    intent.putExtra("Disciption",services.getDescreption());
                    intent.putExtra("responseBy",services.getResponseBy());
                    intent.putExtra("IssuedBy", services.getIssuedBy());

                    intent.putExtra("state","");
                    //intent.putExtra("state",services.getState());

                    myResponse.startActivity(intent);
                }
            });

        }

        public void setServices2(Service services) {
            this.services = services;
            /*Glide.with(itemView.getContext())
                    .load(users.getPhotoUrl().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            users.getPhotoUrl())
                    .into(image);*/
            res_Title.setText(services.getTitle());
            res_username.setText("@"+services.getIssuedBy());


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



        }


    }//end class ViewHolder


    public void AcceptService(final String service_id){

        final int[] i = {0};
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(service_id)) {

                            HashMap<String, String> update = new HashMap<String, String>();
                            update.put("state", "Accepted");
                            String t = dataSnapshot1.getKey();
                            DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                            firebaseDel.updateChildren((HashMap) update);
                            //  service.remove(2);
                            startNotification(p);

                        }
                        i[0]++;
                    } catch (Exception ignored) {

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }//Accept Service method


    public void RejectService(final String service_id){
        final int[] i = {0};
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(service_id)) {


                            HashMap<String, String> update = new HashMap<String, String>();
                            update.put("state", "Rejected");
                            String t = dataSnapshot1.getKey();
                            DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                            firebaseDel.updateChildren((HashMap) update);
                            //  service.remove(2);

                        }
                        i[0]++;
                    } catch (Exception ignored) {

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }//Reject Service method


    public void startNotification(Service service) {

        System.out.print("HHHHHHEEEEERRRRREEEEE 2222 out");
        final String ServiceProvider = service.getResponseBy();
        String ServiceRequester = service.getIssuedBy();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("username").equalTo(ServiceRequester);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);
                    String send_email = user.getEmail();
                    System.out.print("HHHHHHEEEEERRRRREEEEE 2222 in");
                    sendNotification(ServiceProvider,send_email);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }







    private void sendNotification(String ServiceProvider2, String send_email2) {

        System.out.print("HHHHHHEEEEERRRRREEEEE 3333 out");
        final  String ServiceProvider = ServiceProvider2;
        final  String send_email = send_email2;


        // Toast.makeText(DirectResponseAdapter.this, "Current Recipients is : user1@gmail.com ( Just For Demo )", Toast.LENGTH_SHORT).show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                 /*   String send_email;


                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (Timeline.LoggedIn_User_Email.equals("noura@gmail.com")) {
                        send_email = "raghed@gmail.com";
                    } else {
                        send_email = "noura@gmail.com";
                    }*/

                    System.out.print("HHHHHHEEEEERRRRREEEEE 3333 in");

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MThjNDk2ZDItZmZhYi00MjZlLTk5ZjYtOGUxNGMwMTQ1ODU5");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"4afe1964-128b-46db-b30c-e383d3f68546\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Congrats your request is accepted by\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }




}//end class DirectResponseAdapter
