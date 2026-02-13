package it.unisa.smartfixlab.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Device;

/**
 * Fragment che mostra la lista dei dispositivi caricati da Firebase.
 * Ogni card mostra modello, marca, stato, prezzi e profitto calcolato.
 */
public class TransformFragment extends Fragment {

    private TransformViewModel viewModel;
    private DeviceAdapter adapter;
    private List<Device> allDevices = new ArrayList<>();
    private ChipGroup chipGroup;

    private RecyclerView recyclerView;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TransformViewModel.class);

        View root = inflater.inflate(R.layout.fragment_transform, container, false);

        recyclerView = root.findViewById(R.id.recyclerview_transform);
        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        emptyState = root.findViewById(R.id.empty_state);
        LinearLayout errorState = root.findViewById(R.id.error_state);
        TextView tvErrorMessage = root.findViewById(R.id.tv_error_message);

        // Setup RecyclerView
        adapter = new DeviceAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Set Listener
        adapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Device device) {
                onEditDevice(device);
            }

            @Override
            public void onDeleteClick(Device device) {
                onDeleteDevice(device);
            }
        });

        // Setup Filters
        chipGroup = root.findViewById(R.id.chip_group_filter);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> applyFilter());
        
        // Osserva i dispositivi
        viewModel.getDevices().observe(getViewLifecycleOwner(), devices -> {
            Log.d("TransformFragment", "Devices update received. Size: " + (devices != null ? devices.size() : "null"));
            allDevices = devices != null ? devices : new ArrayList<>();
            applyFilter();
        });

        // Osserva lo stato di caricamento
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                emptyState.setVisibility(View.GONE);
                errorState.setVisibility(View.GONE);
            }
        });

        // Osserva errori
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            } else {
                errorState.setVisibility(View.GONE);
            }
        });
        
        return root;
    }

    private void onEditDevice(Device device) {
        // Navigate to AddDeviceFragment with arguments for editing
        Bundle bundle = new Bundle();
        bundle.putSerializable("device_to_edit", device); 
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_transform_to_addDeviceFragment, bundle);
    }

    private void onDeleteDevice(Device device) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Conferma eliminazione")
                .setMessage("Sei sicuro di voler eliminare " + device.getModello() + "?")
                .setPositiveButton("Elimina", (dialog, which) -> {
                    viewModel.deleteDevice(device);
                    Toast.makeText(getContext(), "Dispositivo eliminato", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private void applyFilter() {
        int checkedId = chipGroup.getCheckedChipId();
        List<Device> filtered = new ArrayList<>();

        for (Device d : allDevices) {
            if (checkedId == R.id.chip_all) {
                filtered.add(d);
            } else if (checkedId == R.id.chip_sale) {
                if (!d.isRiparazione() && d.getStato() == it.unisa.smartfixlab.ui.bean.Stato.DISPONIBILE) {
                    filtered.add(d);
                }
            } else if (checkedId == R.id.chip_repair) {
                if (d.isRiparazione()) filtered.add(d);
            } else if (checkedId == R.id.chip_sold) {
                if (d.getStato() == it.unisa.smartfixlab.ui.bean.Stato.VENDUTO) filtered.add(d);
            }
        }

        adapter.submitList(filtered);

        if (filtered.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}