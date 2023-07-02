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
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MedicineAdapter extends ListAdapter<Medicinecart, MedicineAdapter.MedicineViewHolder> {

    private OnAddToCartClickListener onAddToCartClickListener;

    public MedicineAdapter() {
        super(CALLBACK);
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        onAddToCartClickListener = listener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_item_layout, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicinecart medicinecart = getItem(position);
        holder.bind(medicinecart);
    }
    public Medicinecart getMed(int position) {
        return getItem(position);
    }
    public class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView categoryTextView;
        private TextView priceTextView;

        private ImageView imageView;

        private Button buttonAddToCart;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            categoryTextView = itemView.findViewById(R.id.tvCategory);
            priceTextView = itemView.findViewById(R.id.tvPrice);
            buttonAddToCart = itemView.findViewById(R.id.btnaddcart);
            imageView = itemView.findViewById(R.id.medImage);
            buttonAddToCart.setOnClickListener(this);
        }

        public void bind(Medicinecart medicinecart) {
            nameTextView.setText(medicinecart.getMName());
            categoryTextView.setText(medicinecart.getMCategory());
            priceTextView.setText(medicinecart.getMPrice());
            Picasso.get().load(medicinecart.getMImage()).into(imageView);
        }

        @Override
        public void onClick(View view) {
            if (onAddToCartClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Medicinecart medicinecart = getItem(position);
                    onAddToCartClickListener.onAddToCartClick(medicinecart);
                }
            }
        }
    }
}
