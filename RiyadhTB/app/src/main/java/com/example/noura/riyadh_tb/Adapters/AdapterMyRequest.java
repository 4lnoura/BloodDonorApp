package com.example.noura.riyadh_tb.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.noura.riyadh_tb.Main.MyRequest;
import com.example.noura.riyadh_tb.Main.MyRequestDetails;
import com.example.noura.riyadh_tb.R;
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

public class AdapterMyRequest  extends RecyclerView.Adapter<AdapterMyRequest.MyViewHolder> {
    private Context context;
    private ArrayList<Service> service;
    private ViewGroup viewGroup;
    private MyRequest myRequest;
    public AdapterMyRequest(Context c, ArrayList<Service> p, MyRequest myReq) {
        context = c;
        service = p;
        viewGroup = null;
        this.myRequest=myReq;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.my_requset_list_servicse, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.currentMyRequset=service.get(position);
       // holder.myReq=myRequest;

        if (service.get(position).getResponseBy().isEmpty()) {

            holder.reject.setVisibility(View.INVISIBLE);
            holder.accept.setImageResource(R.drawable.cancel);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog diaBox = AskOption(holder);
                    diaBox.show();
                }
            });

        }
            if (service.get(position).getRate() == 0 && service.get(position).isConfirm()&&!(service.get(position).getResponseBy().isEmpty())) {


                try {
                    holder.reject.setVisibility(View.INVISIBLE);
                    holder.accept.setImageResource(R.drawable.rate);
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rating(viewGroup, holder);
                        }

                    });


                } catch (Exception e) {

                }

            }
                if (!(service.get(position).isConfirm())&&(service.get(position).getState().equals("Delivered")&&!(service.get(position).getResponseBy().isEmpty()))) {
                    holder.accept.setImageResource(R.drawable.corfirm);
                    holder.reject.setVisibility(View.INVISIBLE);
                   // holder.accept.setVisibility(View.INVISIBLE);

                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String id = (String) holder.title.getContentDescription();

                            comfirm(id);




                            try {
                            holder.reject.setVisibility(View.INVISIBLE);
                            holder.accept.setImageResource(R.drawable.rate);
                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rating(viewGroup, holder);
                                }

                            });


                        } catch (Exception e) {

                        }




                }

                    });
                }


            if (service.get(position).getRate() > 0) {

                holder.reject.setVisibility(View.INVISIBLE);
                holder.accept.setVisibility(View.INVISIBLE);



            }

if(!(service.get(position).getState().equals("Delivered"))&&!(service.get(position).getResponseBy().isEmpty())){
    holder.reject.setVisibility(View.INVISIBLE);
    holder.accept.setVisibility(View.INVISIBLE);
}



        holder.title.setText(service.get(position).getTitle());
        holder.date.setText(service.get(position).getDate());
        holder.title.setContentDescription((service.get(position).getID()));

    }

    @Override
    public int getItemCount() {
        return service.size();
    }

    private AlertDialog AskOption(final MyViewHolder holder) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("الغاء الطلب")
                .setMessage("هل انت متاكد من الغاء الطلب ")

                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteService(holder);


                        dialog.dismiss();
                    }

                })


                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }





    private void deleteService(final MyViewHolder holder) {

        final String id = (String) holder.title.getContentDescription();
        final int[] i = {0};
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(id)) {

                            String t = dataSnapshot1.getKey();
                            DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                            firebaseDel.removeValue();
                            AdapterMyRequest.this.notify();


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


    }


    @SuppressLint("WrongViewCast")
    private void rating(ViewGroup viewGroup, final MyViewHolder holder) {
        final AlertDialog.Builder optionDialog = new AlertDialog.Builder(context);
        final RatingBar rating = new RatingBar(context);
        rating.setMax(5);
        final AlertDialog dialogView = setting(viewGroup);
        final RatingBar mBar = dialogView.findViewById(R.id.dialog_rating_rating_bar);


        dialogView.findViewById(R.id.latter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
            }


        });


        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RatingBar bar = (RatingBar) v;

                int ratingNum = (int) mBar.getRating();
                final String id = (String) holder.title.getContentDescription();

                getrate(ratingNum, id);


                dialogView.dismiss();
            }

        });


        //  optionDialog.create();
        // optionDialog.show();
    }

    private void getrate(final int rate, final String id) {
        final int[] i = {0};
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(id)) {


                            HashMap<String, Integer> update = new HashMap<String, Integer>();
                            update.put("rate", rate);
                            String t = dataSnapshot1.getKey();
                            DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                            firebaseDel.updateChildren((HashMap) update);
                            //  service.remove(2);


//service.remove(i[0]);
//notifyDataSetChanged();
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


    }


    private AlertDialog setting(ViewGroup viewGroup) {
        //ViewGroup viewGroup = viewGroup.findViewById();

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //finally creating the alert dialog and displaying it
        return alertDialog;
    }

    private void comfirm(final String id) {
        final int[] i = {0};
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Services");
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        Service p = dataSnapshot1.getValue(Service.class);

                        if (p.getID().equals(id)) {

                            HashMap<String, Boolean> update = new HashMap<String, Boolean>();
                            update.put("confirm", true);
                            String t = dataSnapshot1.getKey();
                            DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("Services").child(t);
                            firebaseDel.updateChildren((HashMap) update);

                            //  service.remove(2);

                            /************/
if(p.getRate()==0) {
    IncreaseAndDetuctTime(p);
}
                            /************/

//service.remove(i[0]);
//notifyDataSetChanged();
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


    }

    private void IncreaseAndDetuctTime(Service p) {

        System.out.print("اناااااا دخلللللت هنااااااااااااا للمعلومية");
        final Service service = p;

        final String ServiceProvider = service.getResponseBy();
        final String ServiceRequester = service.getIssuedBy();

        DatabaseReference referenceCurU=FirebaseDatabase.getInstance().getReference("users");
        Query query=referenceCurU.orderByChild("username").equalTo(ServiceRequester);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotCU) {


                if(dataSnapshotCU.exists()) {

                    for (final DataSnapshot snapshotCU : dataSnapshotCU.getChildren()) {

                        final User CUser=snapshotCU.getValue(User.class);
                        final int NHCU= CUser.getTimeCredit();

                        //هنا نبحث عن المستخدم


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                        Query query = reference.orderByChild("username").equalTo(ServiceProvider);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                        User user = snapshot.getValue(User.class);



                                            try {
int old=user.getTimeCredit();
                                                HashMap<String, Integer> update = new HashMap<String, Integer>();
                                                int number=old+1;
                                                update.put("timeCredit", number);
                                                String t = snapshot.getKey();
                                                DatabaseReference firebaseDel = FirebaseDatabase.getInstance().getReference().child("users").child(t);
                                                firebaseDel.updateChildren((HashMap) update);



int old2=CUser.getTimeCredit();
                                                HashMap<String, Integer> update2 = new HashMap<String, Integer>();
                                                if(old2<0){
                                                    update2.put("timeCredit", (0));

                                                }
                                                else {
                                                    int num=old2 - 1;
                                                    update2.put("timeCredit", num);
                                                }
                                                String t2 = snapshotCU.getKey();
                                                DatabaseReference firebaseDel2 = FirebaseDatabase.getInstance().getReference().child("users").child(t2);
                                                firebaseDel2.updateChildren((HashMap) update2);



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


                    }//end for CU
                }//end if CU
                //نهاية البحث عن المستخدم



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    class MyViewHolder extends ViewHolder implements View.OnClickListener {
        public Service currentMyRequset;
       // public MyRequest myReq;
        TextView title, date;
        ImageView reject, accept, tracking;
        RecyclerView recyclerView;


        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.service_title);
            date = itemView.findViewById(R.id.service_date);
            reject = itemView.findViewById(R.id.but_reject);
            accept = itemView.findViewById(R.id.but_accept);
            tracking = itemView.findViewById(R.id.but_tracking);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            tracking.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {


            Intent intent=new Intent(myRequest,MyRequestDetails.class);


            intent.putExtra("id",currentMyRequset.getID());
            intent.putExtra("date",currentMyRequset.getDate());
            intent.putExtra("time",currentMyRequset.getIssuedTime());
            intent.putExtra("username",currentMyRequset.getIssuedBy());
            intent.putExtra("Title",currentMyRequset.getTitle());
            intent.putExtra("category",currentMyRequset.getCategory());
            intent.putExtra("Disciption",currentMyRequset.getDescreption());
            intent.putExtra("responseBy",currentMyRequset.getResponseBy());
            intent.putExtra("state",currentMyRequset.getState());

            myRequest.startActivity(intent);



        }
    }

}





