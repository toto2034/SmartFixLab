package it.unisa.smartfixlab.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Order;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    protected OrderAdapter() {
        super(new DiffUtil.ItemCallback<Order>() {
            @Override
            public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return oldItem.getId().equals(newItem.getId()) &&
                        oldItem.getData().equals(newItem.getData()) &&
                        oldItem.getDevices().size() == newItem.getDevices().size();
            }
        });
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order, listener);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvOrderDate;
        private final TextView tvDeviceCount;
        private final TextView tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvDeviceCount = itemView.findViewById(R.id.tv_device_count);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(Order order, OnItemClickListener listener) {
            tvOrderId.setText("Ordine #" + order.getId());
            tvOrderDate.setText(order.getData());
            int count = order.getDevices() != null ? order.getDevices().size() : 0;
            tvDeviceCount.setText(count + " dispositivi inclusi");

            // Status Binding
            if (order.getStatoorder() != null) {
                tvStatus.setText(order.getStatoorder().name().replace("_", " "));
                tvStatus.setVisibility(View.VISIBLE);
                
                // Color logic for Order Status
                switch (order.getStatoorder()) {
                    case IN_ARRIVO:
                        tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800"))); // Orange
                        break;
                    case ARRIVATO_DA_VERIFICARE:
                        tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E91E63"))); // Pink/Red
                        break;
                    case VERIFICATO:
                         tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))); // Green
                         break;
                    case IN_RESTITUZIONE:
                         tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D32F2F"))); // Red
                         break;
                }
            } else {
                tvStatus.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(order);
            });
        }
    }
}
