package com.blood_donor.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blood_donor.ProfileActivity;
import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Request;
import com.blood_donor.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestViewHolder> {

    private ArrayList<Request> requests = new ArrayList<>();

    public RequestListAdapter(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.setRequest(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        private ImageView userProfile;
        private TextView userName;
        private Button acceptButton;
        private Button rejectButton;
        private Button viewProfileButton;
        private ImageView statusIcon;
        private TextView statusText;
        private LinearLayout statusLayout;
        private LinearLayout buttonsLayout;
        private Request request;
        private User user;

        public RequestViewHolder(View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            viewProfileButton = itemView.findViewById(R.id.viewProfile);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            statusText = itemView.findViewById(R.id.statusText);
            statusLayout = itemView.findViewById(R.id.statusLayout);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
        }

        public void setRequest(Request request) {
            this.request = request;
            loadProfile();

            if (request.getStatus().equals(Request.STATUS_PENDING)) {
                buttonsLayout.setVisibility(View.VISIBLE);
                statusLayout.setVisibility(View.GONE);
            } else {
                requestStatus();
            }

            final String senderId = request.getSenderId();
            viewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra(ProfileActivity.EXTRA_USER_EMAIL, senderId);
                    v.getContext().startActivity(intent);
                }
            });

            setStatus(acceptButton, true);
            setStatus(rejectButton, false);
        }

        private void setStatus(Button button, final boolean accept) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Donation Request")
                            .setMessage(accept ? "Are you sure you want to accept this request?" : "Are you sure you want to reject this request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    request.setStatus(accept ? Request.STATUS_ACCEPTED : Request.STATUS_REJECTED);
                                    new FirebaseTransaction(v.getContext(), "Donation Request", accept ? "Accepting request..." : "Rejecting request...", true)
                                            .child("requests")
                                            .child(request.getId())
                                            .setValue(request, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    requestStatus();
                                                }
                                            });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
            });
        }

        private void requestStatus() {
            buttonsLayout.setVisibility(View.GONE);
            statusLayout.setVisibility(View.VISIBLE);

            statusIcon.setImageDrawable(
                    itemView.getContext().getResources().getDrawable(
                            request.getStatus().equals(Request.STATUS_ACCEPTED) ?
                                    R.drawable.check : R.drawable.rejected
                    )
            );
            statusText.setText(request.getStatus().equals(Request.STATUS_REJECTED) ? "Rejected" : "Accepted");
            if (request.getStatus().equals(Request.STATUS_ACCEPTED)) {
                viewProfileButton.setVisibility(View.VISIBLE);
            }
        }

        private void loadProfile() {
            new FirebaseTransaction(itemView.getContext(), false)
                    .child("users")
                    .read(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                if (user.getEmail().equals(request.getSenderId())) {
                                    RequestViewHolder.this.user = user;

                                    Glide.with(itemView.getContext())
                                            .load(user.getPhotoUrl())
                                            .into(userProfile);
                                    userName.setText(user.getName());
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
