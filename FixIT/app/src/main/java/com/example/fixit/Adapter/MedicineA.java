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

import com.example.fixit.Model.MedicineModel;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MedicineA extends ListAdapter<MedicineModel, MedicineA.MedicineViewHolder> {

    public MedicineA() {
        super(CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MedicineModel> CALLBACK = new DiffUtil.ItemCallback<MedicineModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MedicineModel oldItem, @NonNull MedicineModel newItem) {
            return Objects.equals(oldItem.getMId(), newItem.getMId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MedicineModel oldItem, @NonNull MedicineModel newItem) {
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
        MedicineModel medicineModel = getItem(position);
        holder.bind(medicineModel);
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
            imageView = itemView.findViewById(R.id.medImage);
        }

        public void bind(MedicineModel medicineModel) {
            nameTextView.setText(medicineModel.getMName());
            categoryTextView.setText(medicineModel.getMCategory());
            priceTextView.setText(medicineModel.getMPrice());
            Picasso.get().load(medicineModel.getMImage()).into(imageView);
        }


    }
}
