package it.unisa.smartfixlab.ui.transform;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.dao.DeviceDAO;
import it.unisa.smartfixlab.ui.dao.FirebaseCallback;

/**
 * ViewModel per il fragment Transform.
 * Carica i dispositivi da Cloud Firestore
 * e li espone tramite LiveData.
 */
public class TransformViewModel extends ViewModel {

    private static final String TAG = "TransformViewModel";

    private final MutableLiveData<List<Device>> mDevices;
    private final MutableLiveData<Boolean> mIsLoading;
    private final MutableLiveData<String> mErrorMessage;
    private final DeviceDAO deviceDAO;

    public TransformViewModel() {
        mDevices = new MutableLiveData<>(new ArrayList<>());
        mIsLoading = new MutableLiveData<>(true);
        mErrorMessage = new MutableLiveData<>();
        deviceDAO = new DeviceDAO();
        loadDevices();
    }

    /**
     * Carica tutti i dispositivi da Firebase.
     * Il listener è in tempo reale, quindi si aggiorna automaticamente.
     */
    private final List<Device> phonesList = new ArrayList<>();
    private final List<Device> tabletsList = new ArrayList<>();
    private final List<Device> computersList = new ArrayList<>();
    private final List<Device> accessoriesList = new ArrayList<>();

    private void loadDevices() {
        mIsLoading.setValue(true);
        
        // Timeout safety net (10 seconds)
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (Boolean.TRUE.equals(mIsLoading.getValue())) {
                Log.w(TAG, "Loading timed out. Force stopping spinner.");
                mIsLoading.setValue(false);
                if (mDevices.getValue() == null || mDevices.getValue().isEmpty()) {
                    mErrorMessage.setValue("Tempo di connessione scaduto. Verifica la tua connessione o le regole del database.");
                }
            }
        }, 30000);

        // Callback wrapper to update specific list and then refresh main list
        FirebaseCallback<Device> phoneCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                Log.d(TAG, "Phones loaded: " + result.size());
                synchronized (phonesList) {
                    phonesList.clear();
                    phonesList.addAll(result);
                    updateMainList();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Errore caricamento phones", e);
                // On failure, maybe clear or keep old? Let's keep old for now or handle error.
                // For simplicity, just update main list (potentially showing partial data)
                updateMainList();
            }
        };

        FirebaseCallback<Device> tabletCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (tabletsList) {
                    tabletsList.clear();
                    tabletsList.addAll(result);
                    updateMainList();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Errore caricamento tablets", e);
                updateMainList();
            }
        };

        FirebaseCallback<Device> computerCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (computersList) {
                    computersList.clear();
                    computersList.addAll(result);
                    updateMainList();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Errore caricamento computer", e);
                updateMainList();
            }
        };

        FirebaseCallback<Device> accessoriesCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                Log.d(TAG, "Accessories loaded: " + result.size());
                synchronized (accessoriesList) {
                    accessoriesList.clear();
                    accessoriesList.addAll(result);
                    updateMainList();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Errore caricamento accessori", e);
                updateMainList();
            }
        };

        new it.unisa.smartfixlab.ui.dao.PhoneDeviceDAO().getAllPhones(phoneCallback);
        new it.unisa.smartfixlab.ui.dao.TabletDeviceDAO().getAllTablets(tabletCallback);
        new it.unisa.smartfixlab.ui.dao.ComputerDeviceDAO().getAllComputers(computerCallback);
        new it.unisa.smartfixlab.ui.dao.AccessoriesDAO().getAllAccessories(accessoriesCallback);
    }

    public void deleteDevice(Device device) {
        if (device == null) return;
        
        it.unisa.smartfixlab.ui.dao.DeviceDAO dao;
        
        // Determina il DAO corretto in base alla classe dell'oggetto
        if (device instanceof it.unisa.smartfixlab.ui.bean.PhoneDevice) {
            dao = new it.unisa.smartfixlab.ui.dao.PhoneDeviceDAO();
        } else if (device instanceof it.unisa.smartfixlab.ui.bean.TabletDevice) {
            dao = new it.unisa.smartfixlab.ui.dao.TabletDeviceDAO();
        } else if (device instanceof it.unisa.smartfixlab.ui.bean.ComputerDevice) {
            dao = new it.unisa.smartfixlab.ui.dao.ComputerDeviceDAO();
        } else if (device instanceof it.unisa.smartfixlab.ui.bean.Accessories) {
            dao = new it.unisa.smartfixlab.ui.dao.AccessoriesDAO();
        } else {
            dao = new it.unisa.smartfixlab.ui.dao.DeviceDAO();
        }
        
        dao.delete(device.getId());
        Log.d(TAG, "Requested deletion of device ID: " + device.getId());
    }

    private void updateMainList() {
        List<Device> aggregated = new ArrayList<>();
        synchronized (phonesList) { aggregated.addAll(phonesList); }
        synchronized (tabletsList) { aggregated.addAll(tabletsList); }
        synchronized (computersList) { aggregated.addAll(computersList); }
        synchronized (accessoriesList) { aggregated.addAll(accessoriesList); }
        
        mDevices.postValue(aggregated);
        mIsLoading.postValue(false);
        mErrorMessage.postValue(null);
    }

    public LiveData<List<Device>> getDevices() {
        return mDevices;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }
}