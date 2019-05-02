package com.example.noura.riyadh_tb.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.Main.SearchForUsers;
import com.example.noura.riyadh_tb.UserProfile.User_Profile;
import com.example.noura.riyadh_tb.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "SearchAdapter";
    private ArrayList<User> users = new ArrayList<>();

    private SearchForUsers searchForUsers;

    public SearchAdapter(SearchForUsers searchForUsers){

        this.searchForUsers=searchForUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_items,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;}

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.setUser(users.get(position));
        //holder.users=users.get(position);

    }

    @Override
    public int getItemCount() { return users.size();}


    public void setUser(ArrayList<User> users) {
        this.users = new ArrayList<>(users);
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        User users;
        CircleImageView image;
        TextView imageName, imageEmail;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            imageEmail = itemView.findViewById(R.id.image_email);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            parentLayout.setOnClickListener(this);


        }

        public void setUser(User users) {
            this.users = users;
            Glide.with(itemView.getContext())
                    .load(users.getPhotoUrl().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            users.getPhotoUrl())
                    .into(image);
            imageName.setText(users.getName());
            imageEmail.setText(users.getEmail());
        }


        @Override
        public void onClick(View view) {

            Intent intent=new Intent(searchForUsers, User_Profile.class);
            intent.putExtra("Name",users.getName());
            intent.putExtra("email",users.getEmail());
            intent.putExtra("image",users.getPhotoUrl());
            intent.putExtra("rate",users.getRate());
            intent.putExtra("skills",users.getSkills());
            intent.putExtra("Experience",users.getExperience());
            intent.putExtra("Interest",users.getInterest());
            intent.putExtra("Username",users.getUsername());
            intent.putExtra("DOB",users.getDOB());
            intent.putExtra("TimeCredit",users.getTimeCredit());

            searchForUsers.startActivity(intent);

        }
    }//end class ViewHolder

}//end class SearchAdapter

