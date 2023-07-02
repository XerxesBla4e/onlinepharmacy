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

import com.example.fixit.Interface.OnMoveToPatientDets;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.Model.Orders;
import com.example.fixit.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainOrder extends ListAdapter<Orders, MainOrder.OrderViewHolder> {

    private OnMoveToPatientDets onMoveToPatientDets;

    public MainOrder() {
        super(CALLBACK);
    }

    public void OnMoveToPatient(OnMoveToPatientDets listener) {
        onMoveToPatientDets = listener;
    }

    private static final DiffUtil.ItemCallback<Orders> CALLBACK = new DiffUtil.ItemCallback<Orders>() {

        @Override
        public boolean areItemsTheSame(@NonNull Orders oldItem, @NonNull Orders newItem) {
            return Objects.equals(oldItem.getOrderID(), newItem.getOrderID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Orders oldItem, @NonNull Orders newItem) {
            return oldItem.getOrderTime().equals(newItem.getOrderTime())
                    && oldItem.getOrderStatus().equals(newItem.getOrderStatus())
                    && oldItem.getOrderBy().equals(newItem.getOrderBy());
        }

    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders ordersModel = getItem(position);
        holder.bind(ordersModel);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView patientidTextView;
        private TextView orderidTextView;
        //private ImageView imageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            patientidTextView = itemView.findViewById(R.id.patientName);
            orderidTextView = itemView.findViewById(R.id.orderId);
            itemView.setOnClickListener(this);
        }

        public void bind(Orders ordersModel) {
            patientidTextView.setText(ordersModel.getOrderBy());
            orderidTextView.setText(ordersModel.getOrderID());
            //Picasso.get().load(ordersModel.getImage()).into(imageView);
        }

        @Override
        public void onClick(View view) {
            if (onMoveToPatientDets != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Orders orders = getItem(position);
                    onMoveToPatientDets.onMoveToDets(orders);
                }
            }
        }
    }
}
