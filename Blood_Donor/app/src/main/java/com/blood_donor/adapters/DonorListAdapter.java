package com.blood_donor.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blood_donor.ProfileActivity;
import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.User;
import com.blood_donor.util.Tools;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class DonorListAdapter extends RecyclerView.Adapter<DonorListAdapter.DonorViewHolder> {

    private ArrayList<User> users = new ArrayList<>();

    public DonorListAdapter(ArrayList<User> users) {
        this.users = new ArrayList<>(users);
    }

    public DonorListAdapter() {

    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DonorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        holder.setUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public class DonorViewHolder extends RecyclerView.ViewHolder {

        private final User loggedInUser;
        private CircleImageView circleImageView;
        private TextView nameTextView, emailTextView, bloodGroupTextView;
        private User user;
        private Button deleteUser;

        public DonorViewHolder(View itemView) {
            super(itemView);
            loggedInUser = Tools.getUser(itemView.getContext()); // get the current logged in user
            circleImageView = itemView.findViewById(R.id.profilePicture);
            nameTextView = itemView.findViewById(R.id.name);
            emailTextView = itemView.findViewById(R.id.email);
            bloodGroupTextView = itemView.findViewById(R.id.bloodGroup);

            deleteUser = itemView.findViewById(R.id.deleteUser);
            if (loggedInUser == null || !loggedInUser.isAdmin()) { // do not show the delete button if the user is not an admin
                deleteUser.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra(ProfileActivity.EXTRA_USER_EMAIL, user.getEmail());
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void setUser(User user) {
            this.user = user;
            if (this.user.getEmail().equals(this.loggedInUser.getEmail())) {
                deleteUser.setVisibility(View.GONE); // prevent a user from deleting themselves, so hide the delete button
            }
            Glide.with(itemView.getContext())
                    .load(user.getPhotoUrl().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            user.getPhotoUrl())
                    .into(circleImageView);
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            bloodGroupTextView.setText(user.getBloodType());

            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Remove User")
                            .setMessage("Are you sure you want to remove this user?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeUser();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
            });
        }

        private void removeUser() {
            // read the database for this reference
            new FirebaseTransaction(itemView.getContext(), false)
                    .child("users")
                    .read(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                User u = child.getValue(User.class);
                                if (u != null) {
                                    if (u.getEmail().equals(user.getEmail())) {
                                        // delete this reference
                                        child.getRef().removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }, false);
        }
    }
}
