package it.unisa.smartfixlab.ui.adddevice;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Accessories;
import it.unisa.smartfixlab.ui.bean.ComputerDevice;
import it.unisa.smartfixlab.ui.bean.PhoneDevice;
import it.unisa.smartfixlab.ui.bean.Stato;
import it.unisa.smartfixlab.ui.bean.TabletDevice;
import it.unisa.smartfixlab.ui.bean.TipoDisplay;
import it.unisa.smartfixlab.ui.dao.AccessoriesDAO;
import it.unisa.smartfixlab.ui.dao.ComputerDeviceDAO;
import it.unisa.smartfixlab.ui.dao.DeviceDAO;
import it.unisa.smartfixlab.ui.dao.PhoneDeviceDAO;
import it.unisa.smartfixlab.ui.dao.TabletDeviceDAO;

/**
 * Fragment per l'inserimento di un nuovo dispositivo.
 * Mostra/nasconde i campi specifici in base al tipo di dispositivo selezionato.
 */
public class AddDeviceFragment extends Fragment {

    // Tipi disponibili
    private static final String[] TIPI_DISPOSITIVO = {"Telefono", "Tablet", "Computer", "Accessorio"};
    private static final String[] STATI = {"DISPONIBILE", "DA_RIPARARE", "VENDUTO"};

    private static final java.util.Map<String, java.util.Map<String, String[]>> BRAND_MODELS_MAP = new java.util.HashMap<>();
    static {
        // Phones
        java.util.Map<String, String[]> phoneBrands = new java.util.HashMap<>();
        phoneBrands.put("Apple", new String[]{"iPhone 15 Pro", "iPhone 15", "iPhone 14", "iPhone 13"});
        phoneBrands.put("Samsung", new String[]{"S24 Ultra", "S24+", "S23", "A54"});
        phoneBrands.put("Xiaomi", new String[]{"14 Ultra", "13T", "Redmi Note 13"});
        BRAND_MODELS_MAP.put("Telefono", phoneBrands);

        // Tablets
        java.util.Map<String, String[]> tabletBrands = new java.util.HashMap<>();
        tabletBrands.put("Apple", new String[]{"iPad Pro M2", "iPad Air", "iPad Mini"});
        tabletBrands.put("Samsung", new String[]{"Tab S9", "Tab A9"});
        BRAND_MODELS_MAP.put("Tablet", tabletBrands);

        // Computers
        java.util.Map<String, String[]> pcBrands = new java.util.HashMap<>();
        pcBrands.put("Apple", new String[]{"MacBook Pro", "MacBook Air", "iMac", "Mac Mini"});
        pcBrands.put("HP", new String[]{"Spectre x360", "Pavilion"});
        pcBrands.put("Dell", new String[]{"XPS 13", "Latitude"});
        BRAND_MODELS_MAP.put("Computer", pcBrands);

        // Accessories
        java.util.Map<String, String[]> accBrands = new java.util.HashMap<>();
        accBrands.put("Apple", new String[]{"AirPods Pro", "Magic Mouse"});
        accBrands.put("Logitech", new String[]{"MX Master 3S", "G Pro X"});
        BRAND_MODELS_MAP.put("Accessorio", accBrands);
    }

    // Views
    private AutoCompleteTextView dropdownTipo;
    private AutoCompleteTextView etModello, etMarca;
    private TextInputEditText etColore, etMemoria;
    private TextInputEditText etPrezzoAcquisto, etPrezzoVendita, etPrezzoRiparazione;
    private AutoCompleteTextView dropdownStato, dropdownDisplay;
    private TextInputEditText etPollici, etProcessore, etRam, etGpu, etDescrizione;
    private TextInputEditText etDataAcquisto, etDataRivendita, etDataAffido;

    // Layouts condizionali
    private TextInputLayout tilDisplay, tilPollici, tilProcessore, tilRam, tilGpu, tilDescrizione, tilMemoria;
    private TextInputLayout tilPrezzoVendita, tilPrezzoRiparazione, tilDataRivendita, tilDataAffido, tilPrezzoAcquisto;

    private boolean isRepairMode = false;
    private MaterialButton btnChangeMode;

    private it.unisa.smartfixlab.ui.bean.Device deviceToEdit = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_device, container, false);

        initViews(root);
        setupDropdowns();
        setupTipoListener();
        setupBrandListener();
        setupDatePickers();
        
        // Check for edit argument
        if (getArguments() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                deviceToEdit = getArguments().getSerializable("device_to_edit", it.unisa.smartfixlab.ui.bean.Device.class);
            } else {
                @SuppressWarnings("deprecation")
                it.unisa.smartfixlab.ui.bean.Device d = (it.unisa.smartfixlab.ui.bean.Device) getArguments().getSerializable("device_to_edit");
                deviceToEdit = d;
            }
            
            if (deviceToEdit != null) {
                populateFields(deviceToEdit);
            } else {
                showModeChoiceDialog();
            }
        } else {
            showModeChoiceDialog();
        }
        
        setupSaveButton(root);

        return root;
    }

    private void initViews(View root) {
        dropdownTipo = root.findViewById(R.id.dropdown_tipo);
        etModello = root.findViewById(R.id.et_modello);
        etMarca = root.findViewById(R.id.et_marca);
        etColore = root.findViewById(R.id.et_colore);
        etMemoria = root.findViewById(R.id.et_memoria);
        etPrezzoAcquisto = root.findViewById(R.id.et_prezzo_acquisto);
        etPrezzoVendita = root.findViewById(R.id.et_prezzo_vendita);
        dropdownStato = root.findViewById(R.id.dropdown_stato);
        dropdownDisplay = root.findViewById(R.id.dropdown_display);
        etPollici = root.findViewById(R.id.et_pollici);
        etProcessore = root.findViewById(R.id.et_processore);
        etRam = root.findViewById(R.id.et_ram);
        etGpu = root.findViewById(R.id.et_gpu);
        etDescrizione = root.findViewById(R.id.et_descrizione);
        etDataAcquisto = root.findViewById(R.id.et_data_acquisto);
        etDataRivendita = root.findViewById(R.id.et_data_rivendita);
        btnChangeMode = root.findViewById(R.id.btn_change_mode);

        tilDisplay = root.findViewById(R.id.til_display);
        tilPollici = root.findViewById(R.id.til_pollici);
        tilProcessore = root.findViewById(R.id.til_processore);
        tilRam = root.findViewById(R.id.til_ram);
        tilGpu = root.findViewById(R.id.til_gpu);
        tilDescrizione = root.findViewById(R.id.til_descrizione);
        tilMemoria = root.findViewById(R.id.til_memoria);

        etPrezzoRiparazione = root.findViewById(R.id.et_prezzo_riparazione);
        etDataAffido = root.findViewById(R.id.et_data_affido);

        tilPrezzoVendita = root.findViewById(R.id.til_prezzo_vendita);
        tilPrezzoRiparazione = root.findViewById(R.id.til_prezzo_riparazione);
        tilDataRivendita = root.findViewById(R.id.til_data_rivendita);
        tilDataAffido = root.findViewById(R.id.til_data_affido);
        tilPrezzoAcquisto = root.findViewById(R.id.til_prezzo_acquisto);
    }

    private void setupDropdowns() {
        // Tipo dispositivo
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, TIPI_DISPOSITIVO);
        dropdownTipo.setAdapter(tipoAdapter);

        // Stato - Formatta i nomi per la UI (rimuove _)
        String[] statiFormatted = new String[Stato.values().length];
        for (int i = 0; i < Stato.values().length; i++) {
            statiFormatted[i] = Stato.values()[i].name().replace("_", " ");
        }
        ArrayAdapter<String> statoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, statiFormatted);
        dropdownStato.setAdapter(statoAdapter);

        // Tipo Display
        String[] displayNames = new String[TipoDisplay.values().length];
        for (int i = 0; i < TipoDisplay.values().length; i++) {
            displayNames[i] = TipoDisplay.values()[i].name();
        }
        ArrayAdapter<String> displayAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, displayNames);
        dropdownDisplay.setAdapter(displayAdapter);
    }

    private void setupDatePickers() {
        etDataAcquisto.setOnClickListener(v -> showDatePicker(etDataAcquisto));
        etDataRivendita.setOnClickListener(v -> showDatePicker(etDataRivendita));
        etDataAffido.setOnClickListener(v -> showDatePicker(etDataAffido));
        
        // Default data acquisto a oggi se nuovo
        if (deviceToEdit == null) {
            Calendar c = Calendar.getInstance();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String today = sdf.format(c.getTime());
            etDataAcquisto.setText(today);
            etDataAffido.setText(today);
        }
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
            target.setText(date);
        }, year, month, day);
        dialog.show();
    }

    private void setupBrandListener() {
        etMarca.setOnItemClickListener((parent, view, position, id) -> {
            String tipo = dropdownTipo.getText().toString();
            String brand = (String) parent.getItemAtPosition(position);
            updateModels(tipo, brand);
        });
    }

    private void updateModels(String tipo, String brand) {
        java.util.Map<String, String[]> brandsMap = BRAND_MODELS_MAP.get(tipo);
        if (brandsMap != null) {
            String[] models = brandsMap.get(brand);
            if (models != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_dropdown_item_1line, models);
                etModello.setAdapter(adapter);
                etModello.setText("", false); // Reset modello
            }
        }
    }

    /**
     * Mostra/nasconde i campi specifici del tipo di dispositivo selezionato.
     */
    private void setupTipoListener() {
        dropdownTipo.setOnItemClickListener((parent, view, position, id) -> {
            String tipo = TIPI_DISPOSITIVO[position];
            updateFieldsVisibility(tipo);
            updateBrands(tipo);
        });
    }

    private void updateBrands(String tipo) {
        java.util.Map<String, String[]> brandsMap = BRAND_MODELS_MAP.get(tipo);
        if (brandsMap != null) {
            String[] brands = brandsMap.keySet().toArray(new String[0]);
            java.util.Arrays.sort(brands);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_dropdown_item_1line, brands);
            etMarca.setAdapter(adapter);
            etMarca.setText("", false);
            etModello.setText("", false);
            etModello.setAdapter(null);
        }
    }

    private void updateFieldsVisibility(String tipo) {
        // Nascondi tutto il condizionale
        tilDisplay.setVisibility(View.GONE);
        tilPollici.setVisibility(View.GONE);
        tilProcessore.setVisibility(View.GONE);
        tilRam.setVisibility(View.GONE);
        tilGpu.setVisibility(View.GONE);
        tilDescrizione.setVisibility(View.GONE);
        
        // Memoria GB (sempre visibile tranne per accessori)
        tilMemoria.setVisibility(tipo.equals("Accessorio") ? View.GONE : View.VISIBLE);

        // Gestione visibilità in base a Vendita o Riparazione
        if (isRepairMode) {
            tilPrezzoVendita.setVisibility(View.GONE);
            tilDataRivendita.setVisibility(View.GONE);
            tilPrezzoRiparazione.setVisibility(View.VISIBLE);
            tilDataAffido.setVisibility(View.VISIBLE);
        } else {
            tilPrezzoVendita.setVisibility(View.VISIBLE);
            tilDataRivendita.setVisibility(View.VISIBLE);
            tilPrezzoRiparazione.setVisibility(View.GONE);
            tilDataAffido.setVisibility(View.GONE);
        }

        switch (tipo) {
            case "Telefono":
                tilDisplay.setVisibility(View.VISIBLE);
                break;
            case "Tablet":
                tilDisplay.setVisibility(View.VISIBLE);
                tilPollici.setVisibility(View.VISIBLE);
                break;
            case "Computer":
                tilDisplay.setVisibility(View.VISIBLE);
                tilPollici.setVisibility(View.VISIBLE);
                tilProcessore.setVisibility(View.VISIBLE);
                tilRam.setVisibility(View.VISIBLE);
                tilGpu.setVisibility(View.VISIBLE);
                break;
            case "Accessorio":
                tilDescrizione.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupSaveButton(View root) {
        MaterialButton btnSalva = root.findViewById(R.id.btn_salva);
        btnSalva.setOnClickListener(v -> {
            if (validateFields()) {
                saveDevice();
            }
        });

        btnChangeMode.setOnClickListener(v -> showModeChoiceDialog());
    }

    /**
     * Valida i campi obbligatori.
     */
    private boolean validateFields() {
        boolean valid = true;
        TextInputLayout tilModello = requireView().findViewById(R.id.til_modello);
        TextInputLayout tilMarca = requireView().findViewById(R.id.til_marca);
        TextInputLayout tilTipo = requireView().findViewById(R.id.til_tipo);
        TextInputLayout tilPrezzoAcquisto = requireView().findViewById(R.id.til_prezzo_acquisto);
        TextInputLayout tilStato = requireView().findViewById(R.id.til_stato);

        if (getText(dropdownTipo).isEmpty()) {
            tilTipo.setError("Seleziona un tipo");
            valid = false;
        } else {
            tilTipo.setError(null);
        }

        if (getText(etModello).isEmpty()) {
            tilModello.setError("Inserisci il modello");
            valid = false;
        } else {
            tilModello.setError(null);
        }

        if (getText(etMarca).isEmpty()) {
            tilMarca.setError("Inserisci la marca");
            valid = false;
        } else {
            tilMarca.setError(null);
        }

        if (!isRepairMode && getText(etPrezzoAcquisto).isEmpty()) {
            tilPrezzoAcquisto.setError("Inserisci il prezzo");
            valid = false;
        } else {
            tilPrezzoAcquisto.setError(null);
        }

        if (isRepairMode) {
            if (getText(etPrezzoRiparazione).isEmpty()) {
                tilPrezzoRiparazione.setError("Inserisci il prezzo riparazione");
                valid = false;
            } else {
                tilPrezzoRiparazione.setError(null);
            }
        } 

        if (getText(dropdownStato).isEmpty()) {
            tilStato.setError("Seleziona lo stato");
            valid = false;
        } else {
            tilStato.setError(null);
        }

        TextInputLayout tilDataAcquisto = requireView().findViewById(R.id.til_data_acquisto);
        if (getText(etDataAcquisto).isEmpty()) {
            tilDataAcquisto.setError("Inserisci la data di acquisto");
            valid = false;
        } else {
            tilDataAcquisto.setError(null);
        }

        return valid;
    }

    private void populateFields(it.unisa.smartfixlab.ui.bean.Device device) {
        // Common fields
        etModello.setText(device.getModello());
        etMarca.setText(device.getMarca());
        etColore.setText(device.getColore());
        etMemoria.setText(String.valueOf(device.getMemoriaGb()));
        etPrezzoAcquisto.setText(String.format(java.util.Locale.US, "%.2f", device.getPrezzoAcquisto()));
        
        isRepairMode = device.isRiparazione();
        if (isRepairMode) {
            etPrezzoRiparazione.setText(String.format(java.util.Locale.US, "%.2f", device.getPrezzoRiparazione()));
            etDataAffido.setText(device.getDataDiAffido());
        } else {
            etPrezzoVendita.setText(String.format(java.util.Locale.US, "%.2f", device.getPrezzoRivendita()));
            if (device.getDataRivendita() != null) etDataRivendita.setText(device.getDataRivendita());
        }

        if (device.getStato() != null) dropdownStato.setText(device.getStato().name().replace("_", " "), false);
        etDataAcquisto.setText(device.getDataAcquisto());
        
        // Disable type change during edit for simplicity, or handle it carefully.
        // Let's disable type selection and pre-select it.
        dropdownTipo.setEnabled(false);
        
        if (device instanceof PhoneDevice) {
            dropdownTipo.setText("Telefono", false);
            // Trigger listener manually or set visibility
            tilDisplay.setVisibility(View.VISIBLE);
            PhoneDevice phone = (PhoneDevice) device;
            if (phone.getDisplay() != null) dropdownDisplay.setText(phone.getDisplay().name(), false);
        } else if (device instanceof TabletDevice) {
            dropdownTipo.setText("Tablet", false);
            tilDisplay.setVisibility(View.VISIBLE);
            tilPollici.setVisibility(View.VISIBLE);
            TabletDevice tablet = (TabletDevice) device;
            if (tablet.getDisplay() != null) dropdownDisplay.setText(tablet.getDisplay().name(), false);
            etPollici.setText(String.valueOf(tablet.getPollici()));
        } else if (device instanceof ComputerDevice) {
            dropdownTipo.setText("Computer", false);
            tilDisplay.setVisibility(View.VISIBLE);
            tilPollici.setVisibility(View.VISIBLE);
            tilProcessore.setVisibility(View.VISIBLE);
            tilRam.setVisibility(View.VISIBLE);
            tilGpu.setVisibility(View.VISIBLE);
            ComputerDevice pc = (ComputerDevice) device;
            if (pc.getDisplay() != null) dropdownDisplay.setText(pc.getDisplay().name(), false);
            etPollici.setText(pc.getPollici()); // String for PC
            etProcessore.setText(pc.getProcessore());
            etRam.setText(String.valueOf(pc.getRamGb()));
            etGpu.setText(pc.getGpu());
        } else if (device instanceof Accessories) {
            dropdownTipo.setText("Accessorio", false);
            tilDescrizione.setVisibility(View.VISIBLE);
            Accessories acc = (Accessories) device;
            etDescrizione.setText(acc.getDescrizione());
        }
    }

    private void saveDevice() {
        String tipo = getText(dropdownTipo);
        // Use existing ID if editing, else new random
        int id = (deviceToEdit != null) ? deviceToEdit.getId() : new Random().nextInt(100000);
        
        String modello = getText(etModello);
        String marca = getText(etMarca);
        String colore = getText(etColore);
        int memoriaGb = parseIntSafe(getText(etMemoria));
        
        String dataAcquisto = getText(etDataAcquisto);
        String dataRivendita = getText(etDataRivendita);
        if (dataRivendita.isEmpty()) dataRivendita = null;
        
        double prezzoAcquisto = parseDoubleSafe(getText(etPrezzoAcquisto));
        double prezzoVendita = parseDoubleSafe(getText(etPrezzoVendita));
        double prezzoRiparazione = parseDoubleSafe(getText(etPrezzoRiparazione));
        String dataAffido = getText(etDataAffido);
        Stato stato = Stato.valueOf(getText(dropdownStato).replace(" ", "_"));

        it.unisa.smartfixlab.ui.bean.Device device;

        switch (tipo) {
            case "Telefono": {
                TipoDisplay display = parseDisplay(getText(dropdownDisplay));
                PhoneDevice phone;
                if (isRepairMode) {
                    phone = new PhoneDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataAffido, display);
                } else if (dataRivendita == null) {
                    phone = new PhoneDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, display);
                } else {
                    phone = new PhoneDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, dataRivendita, prezzoVendita, stato, display);
                }
                new PhoneDeviceDAO().insert(phone);
                break;
            }
            case "Tablet": {
                TipoDisplay display = parseDisplay(getText(dropdownDisplay));
                double pollici = parseDoubleSafe(getText(etPollici));
                TabletDevice tablet;
                if (isRepairMode) {
                    tablet = new TabletDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataAffido, pollici, display);
                } else if (dataRivendita == null) {
                    tablet = new TabletDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, pollici, display);
                } else {
                    tablet = new TabletDevice(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, dataRivendita, prezzoVendita, stato, pollici, display);
                }
                new TabletDeviceDAO().insert(tablet);
                break;
            }
            case "Computer": {
                String processore = getText(etProcessore);
                int ramGb = parseIntSafe(getText(etRam));
                String gpu = getText(etGpu);
                TipoDisplay display = parseDisplay(getText(dropdownDisplay));
                String pollici = getText(etPollici);
                
                ComputerDevice pc;
                if (isRepairMode) {
                    pc = new ComputerDevice(id, modello, marca, colore, memoriaGb, dataAcquisto, 
                            prezzoAcquisto, stato, prezzoRiparazione, dataAffido, pollici, display, processore, ramGb, gpu);
                } else if (dataRivendita == null) {
                    pc = new ComputerDevice(id, modello, marca, colore, memoriaGb, dataAcquisto, 
                            prezzoAcquisto, stato, pollici, display, processore, ramGb, gpu);
                } else {
                    pc = new ComputerDevice(id, modello, marca, colore, memoriaGb, dataAcquisto, 
                            prezzoAcquisto, dataRivendita, prezzoVendita, stato, pollici, display, processore, ramGb, gpu);
                }
                new ComputerDeviceDAO().insert(pc);
                break;
            }
            case "Accessorio": {
                String descrizione = getText(etDescrizione);
                Accessories accessory;
                if (isRepairMode) {
                    accessory = new Accessories(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataAffido, descrizione);
                } else if (dataRivendita == null) {
                    accessory = new Accessories(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, descrizione);
                } else {
                    accessory = new Accessories(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, dataRivendita, prezzoVendita, stato, descrizione);
                }
                new AccessoriesDAO().insert(accessory);
                break;
            }
            default: {
                if (isRepairMode) {
                    device = new it.unisa.smartfixlab.ui.bean.Device(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataAffido);
                } else if (dataRivendita == null) {
                    device = new it.unisa.smartfixlab.ui.bean.Device(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, stato);
                } else {
                    device = new it.unisa.smartfixlab.ui.bean.Device(id, modello, marca, colore, memoriaGb,
                            dataAcquisto, prezzoAcquisto, dataRivendita, prezzoVendita, stato);
                }
                new it.unisa.smartfixlab.ui.dao.DeviceDAO().insert(device);
                break;
            }
        }

        Toast.makeText(requireContext(), deviceToEdit != null ? "Modifiche salvate!" : "Dispositivo creato!", Toast.LENGTH_SHORT).show();

        // Torna alla lista
        Navigation.findNavController(requireView()).popBackStack();
    }

    // --- Utility ---

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private String getText(AutoCompleteTextView autoComplete) {
        return autoComplete.getText() != null ? autoComplete.getText().toString().trim() : "";
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private TipoDisplay parseDisplay(String value) {
        try {
            return TipoDisplay.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TipoDisplay.PRIVO_DI_DISPLAY;
        }
    }

    private void showModeChoiceDialog() {
        String[] options = {"In Vendita", "In Riparazione"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cosa vuoi aggiungere?")
                .setItems(options, (dialog, which) -> {
                    isRepairMode = (which == 1);
                    updateUIForMode();
                })
                .setCancelable(false)
                .show();
    }

    private void updateUIForMode() {
        if (isRepairMode) {
            tilPrezzoAcquisto.setVisibility(View.GONE);
            tilPrezzoVendita.setVisibility(View.GONE);
            tilDataRivendita.setVisibility(View.GONE);
            tilPrezzoRiparazione.setVisibility(View.VISIBLE);
            tilDataAffido.setVisibility(View.VISIBLE);
            if (dropdownStato.getText().toString().isEmpty()) {
                dropdownStato.setText(Stato.DA_RIPARARE.name().replace("_", " "), false);
            }
        } else {
            tilPrezzoAcquisto.setVisibility(View.VISIBLE);
            tilPrezzoVendita.setVisibility(View.VISIBLE);
            tilDataRivendita.setVisibility(View.VISIBLE);
            tilPrezzoRiparazione.setVisibility(View.GONE);
            tilDataAffido.setVisibility(View.GONE);
            if (dropdownStato.getText().toString().isEmpty()) {
                dropdownStato.setText(Stato.DISPONIBILE.name().replace("_", " "), false);
            }
        }
    }
}
