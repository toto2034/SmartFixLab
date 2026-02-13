package it.unisa.smartfixlab.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.dao.AccessoriesDAO;
import it.unisa.smartfixlab.ui.dao.ComputerDeviceDAO;
import it.unisa.smartfixlab.ui.dao.PhoneDeviceDAO;
import it.unisa.smartfixlab.ui.dao.TabletDeviceDAO;
import it.unisa.smartfixlab.ui.dao.FirebaseCallback;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";

    private final MutableLiveData<List<Device>> devices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    private final List<Device> phonesList = new ArrayList<>();
    private final List<Device> tabletsList = new ArrayList<>();
    private final List<Device> computersList = new ArrayList<>();
    private final List<Device> accessoriesList = new ArrayList<>();

    public HomeViewModel() {
        loadData();
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void loadData() {
        isLoading.setValue(true);
        
        FirebaseCallback<Device> phonesCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (phonesList) {
                    phonesList.clear();
                    phonesList.addAll(result);
                }
                updateMainList();
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading phones", e);
            }
        };

        FirebaseCallback<Device> tabletsCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (tabletsList) {
                    tabletsList.clear();
                    tabletsList.addAll(result);
                }
                updateMainList();
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading tablets", e);
            }
        };

        FirebaseCallback<Device> computersCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (computersList) {
                    computersList.clear();
                    computersList.addAll(result);
                }
                updateMainList();
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading computers", e);
            }
        };

        FirebaseCallback<Device> accessoriesCallback = new FirebaseCallback<Device>() {
            @Override
            public void onSuccess(List<Device> result) {
                synchronized (accessoriesList) {
                    accessoriesList.clear();
                    accessoriesList.addAll(result);
                }
                updateMainList();
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading accessories", e);
            }
        };

        new PhoneDeviceDAO().getAllPhones(phonesCallback);
        new TabletDeviceDAO().getAllTablets(tabletsCallback);
        new ComputerDeviceDAO().getAllComputers(computersCallback);
        new AccessoriesDAO().getAllAccessories(accessoriesCallback);
    }

    private void updateMainList() {
        List<Device> aggregated = new ArrayList<>();
        synchronized (phonesList) { aggregated.addAll(phonesList); }
        synchronized (tabletsList) { aggregated.addAll(tabletsList); }
        synchronized (computersList) { aggregated.addAll(computersList); }
        synchronized (accessoriesList) { aggregated.addAll(accessoriesList); }
        
        devices.postValue(aggregated);
        isLoading.postValue(false);
    }
}
