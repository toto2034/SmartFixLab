package it.unisa.smartfixlab.ui.dao;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.ComputerDevice;
import it.unisa.smartfixlab.ui.bean.Device;

/**
 * DAO specifico per ComputerDevice.
 * Opera sul nodo Firebase "computers".
 */
public class ComputerDeviceDAO extends DeviceDAO {

    public ComputerDeviceDAO() {
        super("computers");
    }

    /**
     * Inserisce un computer nel database Firebase.
     */
    public void insert(ComputerDevice computer) {
        collectionRef.document(String.valueOf(computer.getId())).set(computer);
    }

    /**
     * Aggiorna un computer esistente.
     */
    public void update(ComputerDevice computer) {
        collectionRef.document(String.valueOf(computer.getId())).set(computer);
    }

    /**
     * Recupera tutti i computer con listener in tempo reale.
     */
    public void getAllComputers(FirebaseCallback<Device> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Device> computers = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        ComputerDevice computer = doc.toObject(ComputerDevice.class);
                        if (computer != null) {
                            computers.add(computer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(computers);
            }
        });
    }
}
