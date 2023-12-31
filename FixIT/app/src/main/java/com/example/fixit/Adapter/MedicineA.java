package com.example.fixit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit.Model.Medicine;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit.Model.Medicine;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MedicineA extends RecyclerView.Adapter<MedicineA.ViewHolder> {

    private List<Medicine> medicineList;

    public MedicineA(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.bind(medicine);
    }
    public Medicine getMed(int position) {
        if (position >= 0 && position < medicineList.size()) {
            return medicineList.get(position);
        }
        return null;
    }
    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView categoryTextView;
        private TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.medimage);
            nameTextView = itemView.findViewById(R.id.tvName);
            categoryTextView = itemView.findViewById(R.id.tvCategory);
            priceTextView = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(Medicine medicine) {
            nameTextView.setText(medicine.getMName());
            categoryTextView.setText(medicine.getMCategory());
            priceTextView.setText(medicine.getMPrice());

            // Load image using Picasso library
            // Picasso.get().load(medicine.getMImage()).into(imageView);
            String imagePath = medicine.getMImage();
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

            // Handle click events or any other operations for the item here
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click event
                }
            });
        }
    }
}
