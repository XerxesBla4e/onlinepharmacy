package com.example.fixit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.fixit.Interface.OnMoveToDets;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DoctorAd extends RecyclerView.Adapter<DoctorAd.DoctorViewHolder> {

    private Context context;
    private List<DoctorModel> doctorList;
    private OnMoveToDets onMoveToDets;

    public void setOnMoveToDets(OnMoveToDets listener) {
        onMoveToDets = listener;
    }

    public void setDoctorList(List<DoctorModel> doctorList) {
        this.doctorList = doctorList;
        notifyDataSetChanged();
    }

    public DoctorAd(Context context, List<DoctorModel> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorModel doctor = doctorList.get(position);

        holder.doctorNameTextView.setText(doctor.getName());
        holder.specializationTextView.setText(doctor.getSpecialization());
        String imagePath = doctor.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            Picasso.get().load(imagePath).into(holder.doctorImageView);
        } else {
            // Load a default image from the mipmap folder
            holder.doctorImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView doctorImageView;
        TextView doctorNameTextView;
        TextView specializationTextView;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImageView = itemView.findViewById(R.id.doctorimage2);
            doctorNameTextView = itemView.findViewById(R.id.doctorName2);
            specializationTextView = itemView.findViewById(R.id.doctorSpecialization2);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (onMoveToDets != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DoctorModel doctorModel = doctorList.get(position);
                    onMoveToDets.onMoveToDets(doctorModel);
                }
            }
        }
    }
}
