package com.blood_donor.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blood_donor.R;
import com.blood_donor.firebase.FirebaseTransaction;
import com.blood_donor.models.Clinic;
import com.blood_donor.models.User;
import com.blood_donor.util.Tools;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class ClinicListAdapter extends RecyclerView.Adapter<ClinicListAdapter.ClinicViewHolder> {

    private ArrayList<Clinic> clinics = new ArrayList<>();

    @NonNull
    @Override
    public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClinicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicViewHolder holder, int position) {
        holder.setClinic(clinics.get(position));
    }

    @Override
    public int getItemCount() {
        return clinics.size();
    }

    public void setClinics(ArrayList<Clinic> clinics) {
        this.clinics = new ArrayList<>(clinics);
        notifyDataSetChanged();
    }

    public class ClinicViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTextView, phoneTextView, locationTextView;

        private Button deleteButton;
        private Clinic clinic;

        public ClinicViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.name);
            locationTextView = itemView.findViewById(R.id.location);
            phoneTextView = itemView.findViewById(R.id.phoneNumber);
            deleteButton = itemView.findViewById(R.id.deleteClinic);
        }

        public void setClinic(Clinic clinic) {
            this.clinic = clinic;
            Glide.with(itemView.getContext())
                    .load(clinic.getImage().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            clinic.getImage())
                    .into(imageView);
            User loggedInUser = Tools.getUser(itemView.getContext());
            if (!loggedInUser.isAdmin()) {
                deleteButton.setVisibility(View.GONE);
            }

            nameTextView.setText(clinic.getName());
            locationTextView.setText(clinic.getLocation());
            phoneTextView.setText(clinic.getPhoneNumber());

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Clinic")
                            .setMessage("Are you sure you want to delete this clinic?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete the clinic
                                    deleteClinic();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
            });
        }

        private void deleteClinic() {
            new FirebaseTransaction(itemView.getContext(), "Clinic", "Removing clinic...", true)
                    .child("clinics").read(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Clinic c = child.getValue(Clinic.class);
                        if (c != null) {
                            if (c.getId().equals(clinic.getId()) && c.getName().equals(clinic.getName()) && c.getPhoneNumber().equals(clinic.getPhoneNumber())
                                    && c.getLocation().equals(clinic.getLocation())) {
                                // delete this child

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
