package it.unisa.smartfixlab.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Order;
import it.unisa.smartfixlab.ui.transform.DeviceAdapter;

public class OrderDetailsFragment extends Fragment {

    private Order order;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order_details, container, false);

        if (order != null) {
            TextView tvId = root.findViewById(R.id.tv_detail_order_id);
            TextView tvDate = root.findViewById(R.id.tv_detail_order_date);
            RecyclerView rvDevices = root.findViewById(R.id.rv_order_devices);

            tvId.setText("Ordine #" + order.getId());
            tvDate.setText("Data: " + order.getData());

            DeviceAdapter adapter = new DeviceAdapter();
            rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
            rvDevices.setAdapter(adapter);
            
            adapter.submitList(order.getDevices());
        }

        return root;
    }
}
