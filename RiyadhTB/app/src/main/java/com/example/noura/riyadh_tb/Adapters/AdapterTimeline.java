package com.example.noura.riyadh_tb.Adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.Main.DetailsOfService;
import com.example.noura.riyadh_tb.R;
//import com.example.noura.riyadh_tb.ServiceDetails;
import com.example.noura.riyadh_tb.Main.Timeline;
import com.example.noura.riyadh_tb.UserProfile.MyProfile;
import com.example.noura.riyadh_tb.model.Service;
import com.example.noura.riyadh_tb.model.User;

import java.util.ArrayList;

public class AdapterTimeline extends RecyclerView.Adapter<AdapterTimeline.MyViewHolderTimeline> implements View.OnClickListener {


    private Context context;
    private ArrayList<Service> service;
    private ArrayList<User>userArrayList;
    Timeline timeline;

    public AdapterTimeline(Context c, ArrayList<Service> p,ArrayList<User> usesList,Timeline T) {
        context = c;
        service = p;
        this.userArrayList=usesList;
        this.timeline=T;
    }

    @NonNull
    @Override
    public AdapterTimeline.MyViewHolderTimeline onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new MyViewHolderTimeline(LayoutInflater.from(context).inflate(R.layout.timeline_list_servicse ,viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTimeline myViewHolder, int i) {

        String Name = "";
        //String email="";
        //String imgURL="";


       /* for (int index=0;index<userArrayList.size();index++){
            if (userArrayList.get(i).getUsername().equals(service.get(i).getIssuedBy())){
                //userIndex=userArrayList.get(i);
                Name=userArrayList.get(index).getName();
                //email=userArrayList.get(i).getEmail();
                //imgURL=userArrayList.get(i).getPhotoUrl();
            }
        }
        Log.v("username",Name);*/

        myViewHolder.currentS=service.get(i);
        //myViewHolder
        String name =namefindNmae(service.get(i).getIssuedBy());
        myViewHolder.name.setText(name);
      //  myViewHolder.data.setText(service.get(i).getDate());

       // myViewHolder.UNname.setText("@"+service.get(i).getIssuedBy());
        myViewHolder.title.setText(service.get(i).getTitle());
        myViewHolder.time.setText(service.get(i).getIssuedTime());
      //  Glide.with(context).load(findImage(name).isEmpty()? user1:findImage(name))
        //        .into(myViewHolder.userImage);


        if (i % 2 == 0) {
            if (i % 4 == 0)
                myViewHolder.userImage.setImageResource(R.drawable.user1);
            else
                myViewHolder.userImage.setImageResource(R.drawable.user3);

        } else {
            if (i % 3 == 0)
                myViewHolder.userImage.setImageResource(R.drawable.user2);
            else
                myViewHolder.userImage.setImageResource(R.drawable.user4);

        }
    }
   private String  namefindNmae(String name){
        try {


            int size = userArrayList.size();
            for (int i = 0; i < size; i++) {
                if (userArrayList.get(i).getUsername().equals(name))
                    return userArrayList.get(i).getName();
            }
        }
        catch (Exception e){

        }
      return null;
    }


    @Override
    public int getItemCount() {
        try {
            return service.size();

        }
        catch (Exception e){

        }

        return 0;
    }

    @Override
    public void onClick(View view) {

    }

    public  class MyViewHolderTimeline extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Service currentS;
        public User userIndex;
        CardView cardView;
        TextView name, data,title,time,UNname;
        ImageView userImage;

        MyViewHolderTimeline(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView1);
            name = itemView.findViewById(R.id.service_user_name);
            data = itemView.findViewById(R.id.service_date);
            title = itemView.findViewById(R.id.service_title);
            userImage =  itemView.findViewById(R.id.service_user);
            time = itemView.findViewById(R.id.service_time);
           // UNname = itemView.findViewById(R.id.UNname);

            cardView.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            //int postion=(int)view.getTag();
            //  int postion=(int) view.getTag();
            //Service s=service.get(postion);

            final Intent intent=new Intent(timeline, DetailsOfService.class);




            String Name = "";
            String email="";
            String imgURL="";


            for (int i=0;i<userArrayList.size();i++){
                if (userArrayList.get(i).getUsername().equals(currentS.getIssuedBy())){
                    userIndex=userArrayList.get(i);
                    Name=userArrayList.get(i).getName();
                    email=userArrayList.get(i).getEmail();
                    imgURL=userArrayList.get(i).getPhotoUrl();
                }
            }
            Log.v("username",Name);

            intent.putExtra("Name",Name);
            intent.putExtra("email",email);
            intent.putExtra("image",imgURL);
            intent.putExtra("id",currentS.getID());
            intent.putExtra("date",currentS.getDate());
            intent.putExtra("time",currentS.getIssuedTime());
            intent.putExtra("username",currentS.getIssuedBy());
            intent.putExtra("Title",currentS.getTitle());
            intent.putExtra("category",currentS.getCategory());
            intent.putExtra("Disciption",currentS.getDescreption());
            intent.putExtra("go", "Timeline");

            timeline.startActivity(intent);




        }
    }
}
