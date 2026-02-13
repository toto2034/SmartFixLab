package it.unisa.smartfixlab.ui.dao;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.bean.TabletDevice;

/**
 * DAO specifico per TabletDevice.
 * Opera sul nodo Firebase "tablets".
 */
public class TabletDeviceDAO extends DeviceDAO {

    public TabletDeviceDAO() {
        super("tablets");
    }

    /**
     * Inserisce un tablet nel database Firebase.
     */
    public void insert(TabletDevice tablet) {
        collectionRef.document(String.valueOf(tablet.getId())).set(tablet);
    }

    /**
     * Aggiorna un tablet esistente.
     */
    public void update(TabletDevice tablet) {
        collectionRef.document(String.valueOf(tablet.getId())).set(tablet);
    }

    /**
     * Recupera tutti i tablet con listener in tempo reale.
     */
    public void getAllTablets(FirebaseCallback<Device> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Device> tablets = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        TabletDevice tablet = doc.toObject(TabletDevice.class);
                        if (tablet != null) {
                            tablets.add(tablet);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(tablets);
            }
        });
    }
}
