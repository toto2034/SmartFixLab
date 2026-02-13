package it.unisa.smartfixlab.ui.dao;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Accessories;
import it.unisa.smartfixlab.ui.bean.Device;

/**
 * DAO specifico per Accessories.
 * Opera sul nodo Firebase "accessories".
 */
public class AccessoriesDAO extends DeviceDAO {

    public AccessoriesDAO() {
        super("accessories");
    }

    /**
     * Inserisce un accessorio nel database Firebase.
     */
    public void insert(Accessories accessory) {
        collectionRef.document(String.valueOf(accessory.getId())).set(accessory);
    }

    /**
     * Aggiorna un accessorio esistente.
     */
    public void update(Accessories accessory) {
        collectionRef.document(String.valueOf(accessory.getId())).set(accessory);
    }

    /**
     * Recupera tutti gli accessori con listener in tempo reale.
     */
    public void getAllAccessories(FirebaseCallback<Device> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Device> accessories = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        Accessories accessory = doc.toObject(Accessories.class);
                        if (accessory != null) {
                            accessories.add(accessory);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(accessories);
            }
        });
    }
}
