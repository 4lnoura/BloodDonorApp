package com.example.noura.riyadh_tb.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noura.riyadh_tb.R;
import com.example.noura.riyadh_tb.UserProfile.MyProfile_Frag2;
import com.example.noura.riyadh_tb.model.Comment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<Comment> comments=new ArrayList<>();
    private MyProfile_Frag2 myProfile_frag2;

    public CommentAdapter(MyProfile_Frag2 myProfile_frag2) {
        this.myProfile_frag2 = myProfile_frag2;
    }

    public CommentAdapter() {

    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      //  View view = new TextView(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        holder.setComment(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setUser(ArrayList<Comment> comments) {
        this.comments = new ArrayList<>(comments);
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        Comment comments;
        CircleImageView image;
        TextView Comment_Name, Comment_Username,Comment_Text;
        CardView parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.comment_image);
            Comment_Name=itemView.findViewById(R.id.comment_name);
            Comment_Username=itemView.findViewById(R.id.comment_username);
            Comment_Text=itemView.findViewById(R.id.comment_text);
            parentLayout=itemView.findViewById(R.id.parent_comment);


        }


        public void setComment(Comment comments) {
            this.comments=comments;
        /*    Glide.with(itemView.getContext())
                    .load(comments.getImageURL().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            comments.getImageURL())
                    .into(image);*/
            Comment_Name.setText(comments.getIssuedByName());
            Comment_Username.setText("@"+comments.getIssuedUsername());
            Comment_Text.setText(comments.getTextComment());


        }

    }

}
