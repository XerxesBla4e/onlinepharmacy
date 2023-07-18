package com.example.fixit.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit.Model.DoctorModel;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<DoctorModel> doctorList;

    public DoctorAdapter() {
        this.doctorList = new ArrayList<>();
    }

    public void setDoctorList(List<DoctorModel> doctorList) {
        this.doctorList = doctorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_view_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorModel doctor = doctorList.get(position);
        holder.bind(doctor);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
       // private TextView locationTextView;
        private TextView specializationTextView;
        private TextView contactTextView;
        private ImageView imageView;

        @SuppressLint("CutPasteId")
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.doctorName);
          //  locationTextView = itemView.findViewById(R.id.);
            specializationTextView = itemView.findViewById(R.id.doctorSpecialization);
            contactTextView = itemView.findViewById(R.id.doctorSpecialization);
            imageView = itemView.findViewById(R.id.doctorImage);
        }

        public void bind(DoctorModel doctor) {
            nameTextView.setText(doctor.getName());
           // locationTextView.setText(doctor.getLocation());
            specializationTextView.setText(doctor.getSpecialization());
            contactTextView.setText(doctor.getContact());
            String imagePath = doctor.getImage();
            try {
                if (imagePath != null && !imagePath.isEmpty()) {
                    Picasso.get().load(imagePath).into(imageView);
                } else {
                    // Load a default image from the mipmap folder
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (IllegalArgumentException e) {
                // Exception occurred, handle it
                e.printStackTrace();
                // Load a default image from the mipmap folder
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}
