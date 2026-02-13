package it.unisa.smartfixlab.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import it.unisa.smartfixlab.R;

public class OrdersFragment extends Fragment {

    private OrdersViewModel viewModel;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rv_orders);
        View emptyState = root.findViewById(R.id.order_empty_state);
        FloatingActionButton fab = root.findViewById(R.id.fab_add_order);

        adapter = new OrderAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.submitList(orders);
            emptyState.setVisibility(orders == null || orders.isEmpty() ? View.VISIBLE : View.GONE);
        });

        adapter.setOnItemClickListener(order -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", order);
            Navigation.findNavController(root).navigate(R.id.action_nav_orders_to_nav_order_details, bundle);
        });

        fab.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_nav_orders_to_nav_add_order);
        });

        return root;
    }
}
