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

public class MedicineAdapter1 extends ListAdapter<Medicinecart, MedicineAdapter1.MedicineViewHolder> {

    public MedicineAdapter1() {
        super(CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Medicinecart> CALLBACK = new DiffUtil.ItemCallback<Medicinecart>() {
        @Override
        public boolean areItemsTheSame(@NonNull Medicinecart oldItem, @NonNull Medicinecart newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Medicinecart oldItem, @NonNull Medicinecart newItem) {
            return oldItem.getMName().equals(newItem.getMName())
                    && oldItem.getMCategory().equals(newItem.getMCategory())
                    && Objects.equals(oldItem.getMPrice(), newItem.getMPrice())
                    && oldItem.getMTimestamp().equals(newItem.getMTimestamp())
                    && oldItem.getMUid().equals(newItem.getMUid())
                    && oldItem.getMImage().equals(newItem.getMImage());
        }
    };

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_view_item, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicinecart medicine = getItem(position);
        holder.bind(medicine);
    }

    public Medicinecart getMed(int position) {
        return getItem(position);
    }

    public void clearCart() {
        submitList(null);
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView categoryTextView;
        private TextView priceTextView;
        private ImageView imageView;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            categoryTextView = itemView.findViewById(R.id.tvCategory);
            priceTextView = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.medimage);
        }

        public void bind(Medicinecart medicine) {
            nameTextView.setText(medicine.getMName());
            categoryTextView.setText(medicine.getMCategory());
            priceTextView.setText(medicine.getMPrice());
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
        }
    }
}
