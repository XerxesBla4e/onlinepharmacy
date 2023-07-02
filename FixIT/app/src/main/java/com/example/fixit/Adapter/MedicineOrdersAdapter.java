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
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MedicineOrdersAdapter extends ListAdapter<Medicine, MedicineOrdersAdapter.MedicineOrdersViewHolder> {

    public MedicineOrdersAdapter() {
        super(CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Medicine> CALLBACK = new DiffUtil.ItemCallback<Medicine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return Objects.equals(oldItem.getMId(), newItem.getMId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return oldItem.getMName().equals(newItem.getMName())
                    && oldItem.getMCategory().equals(newItem.getMCategory())
                    && oldItem.getMPrice() == newItem.getMPrice()
                    && oldItem.getMId().equals(newItem.getMId())
                    && oldItem.getMTimestamp().equals(newItem.getMTimestamp())
                    && oldItem.getMUid().equals(newItem.getMUid())
                    && oldItem.getMImage().equals(newItem.getMImage());
        }
    };

    @NonNull
    @Override
    public MedicineOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_view_item, parent, false);
        return new MedicineOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineOrdersViewHolder holder, int position) {
        Medicine medicine = getItem(position);
        holder.bind(medicine);
    }

    public Medicine getMedicine(int position) {
        return getItem(position);
    }

    public class MedicineOrdersViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mCategoryTextView;
        private TextView mPriceTextView;
        private ImageView mImageView;

        public MedicineOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.tvName);
            mCategoryTextView = itemView.findViewById(R.id.tvCategory);
            mPriceTextView = itemView.findViewById(R.id.tvPrice);
            mImageView = itemView.findViewById(R.id.medImage);
        }

        public void bind(Medicine medicine) {
            mNameTextView.setText(medicine.getMName());
            mCategoryTextView.setText(medicine.getMCategory());
            mPriceTextView.setText(String.valueOf(medicine.getMPrice()));
            Picasso.get().load(medicine.getMImage()).into(mImageView);
        }
    }
}

