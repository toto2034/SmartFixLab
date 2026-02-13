package it.unisa.smartfixlab.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Device;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private PieChart chart;
    private TextView tvTotalProfit, tvTotalDevices, tvRepairCount;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        chart = root.findViewById(R.id.chart_profits);
        tvTotalProfit = root.findViewById(R.id.tv_total_profit);
        tvTotalDevices = root.findViewById(R.id.tv_total_devices);
        tvRepairCount = root.findViewById(R.id.tv_repair_count);
        progressBar = root.findViewById(R.id.progress_bar);

        setupChart();

        viewModel.getDevices().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        return root;
    }

    private void setupChart() {
        chart.setUsePercentValues(false);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(61f);

        chart.setHoleRadius(58f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(180f);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        chart.setMaxAngle(180f); // Half chart
        chart.getLegend().setEnabled(false);
    }

    private void updateUI(List<Device> devices) {
        if (devices == null) return;

        double totalProfit = 0;
        int repairCount = 0;
        
        double totalPurchase = 0;
        double totalSale = 0;

        for (Device d : devices) {
            double p = d.getProfitto();
            totalProfit += p;
            if (d.isRiparazione()) repairCount++;

            if (d.isRiparazione()) {
                if (d.getStato() == it.unisa.smartfixlab.ui.bean.Stato.RIPARAZIONE_CONCLUSA) {
                    totalSale += d.getPrezzoRiparazione();
                }
            } else {
                totalPurchase += d.getPrezzoAcquisto();
                if (d.getStato() == it.unisa.smartfixlab.ui.bean.Stato.VENDUTO) {
                    totalSale += d.getPrezzoRivendita();
                }
            }
        }

        tvTotalProfit.setText(String.format(Locale.ITALY, "€ %.2f", totalProfit));
        tvTotalDevices.setText(String.valueOf(devices.size()));
        tvRepairCount.setText(String.valueOf(repairCount));

        // Update Chart
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalPurchase, "Acquisto"));
        entries.add(new PieEntry((float) totalSale, "Vendita"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(new int[]{
                Color.parseColor("#FF9800"), // Orange (Acquisto)
                Color.parseColor("#03A9F4")  // Celeste (Vendita)
        });

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        
        chart.setData(pieData);
        chart.setCenterText(String.format(Locale.ITALY, "Total\n€ %.0f", totalProfit));
        chart.setCenterTextSize(16f);
        
        chart.invalidate();
    }
}
