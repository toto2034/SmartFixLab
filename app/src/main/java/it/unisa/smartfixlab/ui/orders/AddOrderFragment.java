package it.unisa.smartfixlab.ui.orders;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.bean.Order;
import it.unisa.smartfixlab.ui.bean.Stato;

public class AddOrderFragment extends Fragment {

    private OrdersViewModel viewModel;
    private TextInputEditText etOrderNumber, etOrderDate, etModello, etMarca, etPrezzoAcquisto, etQuantity;
    private LinearLayout containerSalePrices;
    private final Calendar calendar = Calendar.getInstance();
    private final List<TextInputEditText> salePriceFields = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_order, container, false);

        viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        initViews(root);
        setupDatePicker();
        setupQuantityListener();

        root.findViewById(R.id.btn_save_order).setOnClickListener(v -> saveOrder());

        return root;
    }

    private void initViews(View root) {
        etOrderNumber = root.findViewById(R.id.et_order_number);
        etOrderDate = root.findViewById(R.id.et_order_date);
        etModello = root.findViewById(R.id.et_batch_modello);
        etMarca = root.findViewById(R.id.et_batch_marca);
        etPrezzoAcquisto = root.findViewById(R.id.et_batch_acquisto);
        etQuantity = root.findViewById(R.id.et_batch_quantity);
        containerSalePrices = root.findViewById(R.id.container_sale_prices);

        // Inizializza con 1 campo di vendita
        updateSalePriceFields(1);
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        etOrderDate.setOnClickListener(v -> new DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.ITALY);
        etOrderDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupQuantityListener() {
        etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int qty = Integer.parseInt(etQuantity.getText().toString());
                    if (qty > 0 && qty <= 50) { // Limite ragionevole
                        updateSalePriceFields(qty);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void updateSalePriceFields(int quantity) {
        containerSalePrices.removeAllViews();
        salePriceFields.clear();

        for (int i = 0; i < quantity; i++) {
            TextInputEditText et = new TextInputEditText(requireContext());
            et.setHint("Prezzo Vendita Unità " + (i + 1));
            et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 16, 0, 0);
            et.setLayoutParams(lp);
            
            containerSalePrices.addView(et);
            salePriceFields.add(et);
        }
    }

    private void saveOrder() {
        String orderNum = etOrderNumber.getText().toString();
        String date = etOrderDate.getText().toString();
        String modello = etModello.getText().toString();
        String marca = etMarca.getText().toString();
        String purchasePriceStr = etPrezzoAcquisto.getText().toString();

        if (orderNum.isEmpty() || date.isEmpty() || modello.isEmpty() || purchasePriceStr.isEmpty()) {
            Toast.makeText(getContext(), "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        double purchasePrice = Double.parseDouble(purchasePriceStr);
        Order order = new Order(orderNum, date);

        for (int i = 0; i < salePriceFields.size(); i++) {
            String salePriceStr = salePriceFields.get(i).getText().toString();
            double salePrice = salePriceStr.isEmpty() ? 0 : Double.parseDouble(salePriceStr);
            
            // Crea il device (id fittizio per ora, poi gestito dal DAO o Firebase)
            Device d = new Device((int) (System.currentTimeMillis() + i), modello, marca, "", 0, date, purchasePrice, Stato.DISPONIBILE);
            d.setPrezzoRivendita(salePrice);
            order.addDevice(d);
        }

        viewModel.addOrder(order);
        Toast.makeText(getContext(), "Ordine salvato con successo", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }
}
