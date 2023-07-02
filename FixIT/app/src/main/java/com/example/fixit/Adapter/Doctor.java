package com.example.fixit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit.Interface.OnAddToCartClickListener;
import com.example.fixit.Interface.OnMoveToDets;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Doctor extends ListAdapter<DoctorModel, Doctor.DoctorViewHolder> {

    private OnMoveToDets onMoveToDets;

    public Doctor() {
        super(CALLBACK);
    }

    public void setOnMoveToDets(OnMoveToDets listener) {
        onMoveToDets = listener;
    }

    private static final DiffUtil.ItemCallback<DoctorModel> CALLBACK = new DiffUtil.ItemCallback<DoctorModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DoctorModel oldItem, @NonNull DoctorModel newItem) {
            return Objects.equals(oldItem.getDid(), newItem.getDid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DoctorModel oldItem, @NonNull DoctorModel newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.getLocation().equals(newItem.getLocation())
                    && oldItem.getSpecialization().equals(newItem.getSpecialization())
                    && oldItem.getContact().equals(newItem.getContact())
                    && oldItem.getDid().equals(newItem.getDid())
                    && oldItem.getTimestamp().equals(newItem.getTimestamp())
                    && oldItem.getUid().equals(newItem.getUid())
                    && oldItem.getImage().equals(newItem.getImage());
        }
    };

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_view_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorModel doctorModel = getItem(position);
        holder.bind(doctorModel);
    }

    public DoctorModel getDoctor(int position) {
        return getItem(position);
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView specializationTextView;
        private ImageView imageView;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.doctorName);
            specializationTextView = itemView.findViewById(R.id.doctorSpecialization);
            imageView = itemView.findViewById(R.id.doctorImage);
        }

        public void bind(DoctorModel doctorModel) {
            nameTextView.setText(doctorModel.getName());
            specializationTextView.setText(doctorModel.getSpecialization());
            Picasso.get().load(doctorModel.getImage()).into(imageView);
            itemView.setOnClickListener(
                    this
            );
        }

        @Override
        public void onClick(View view) {
            if (onMoveToDets != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DoctorModel doctorModel = getItem(position);
                    onMoveToDets.onMoveToDets(doctorModel);
                }
            }
        }
    }

}

