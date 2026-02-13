package it.unisa.smartfixlab.ui.dao;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.bean.PhoneDevice;

/**
 * DAO specifico per PhoneDevice.
 * Opera sul nodo Firebase "phones".
 */
public class PhoneDeviceDAO extends DeviceDAO {

    public PhoneDeviceDAO() {
        super("phones");
    }

    /**
     * Inserisce un telefono nel database Firebase.
     */
    public void insert(PhoneDevice phone) {
        collectionRef.document(String.valueOf(phone.getId())).set(phone);
    }

    /**
     * Aggiorna un telefono esistente.
     */
    public void update(PhoneDevice phone) {
        collectionRef.document(String.valueOf(phone.getId())).set(phone);
    }

    /**
     * Recupera tutti i telefoni con listener in tempo reale.
     */
    public void getAllPhones(FirebaseCallback<Device> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Device> phones = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        PhoneDevice phone = doc.toObject(PhoneDevice.class);
                        if (phone != null) {
                            phones.add(phone);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(phones);
            }
        });
    }
}
