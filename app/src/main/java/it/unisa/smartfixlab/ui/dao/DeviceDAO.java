package it.unisa.smartfixlab.ui.dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unisa.smartfixlab.ui.bean.Device;

/**
 * Data Access Object per la gestione dei dispositivi su Cloud Firestore.
 */
public class DeviceDAO {

    protected final CollectionReference collectionRef;

    public DeviceDAO() {
        // Usa l'utility centralizzata per ottenere il riferimento alla collezione "devices"
        collectionRef = it.unisa.smartfixlab.util.FirebaseConfig.getCollection("devices");
    }

    /**
     * Costruttore protetto per sottoclassi che usano una collezione diversa.
     * @param collectionName Nome della collezione Firestore (es. "phones", "tablets")
     */
    protected DeviceDAO(String collectionName) {
        collectionRef = it.unisa.smartfixlab.util.FirebaseConfig.getCollection(collectionName);
    }

    /**
     * Inserisce un dispositivo nel database.
     */
    public void insert(Device device) {
        collectionRef.document(String.valueOf(device.getId())).set(device);
    }

    /**
     * Aggiorna un dispositivo esistente nel database Firestore.
     */
    public void update(Device device) {
        collectionRef.document(String.valueOf(device.getId())).set(device);
    }

    /**
     * Elimina un dispositivo dal database.
     */
    public void delete(int id) {
        collectionRef.document(String.valueOf(id)).delete();
    }

    /**
     * Recupera tutti i dispositivi (generico).
     * Nota: Firestore non usa i ValueEventListener come RTDB, 
     * ma per coerenza con il resto dell'app useremo un approccio simile 
     * se necessario, o deleggeremo alle sottoclassi.
     * @param callback Callback per ricevere i risultati
     */
    public void getAll(FirebaseCallback<Device> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Device> devices = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        Device device = doc.toObject(Device.class);
                        if (device != null) {
                            devices.add(device);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(devices);
            }
        });
    }

    /**
     * Recupera un singolo dispositivo tramite ID.
     * @param id ID del dispositivo
     * @param callback Callback per ricevere il risultato
     */
    public void getById(int id, FirebaseCallback<Device> callback) {
        collectionRef.document(String.valueOf(id)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Device> result = new ArrayList<>();
                com.google.firebase.firestore.DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Device device = document.toObject(Device.class);
                    if (device != null) {
                        result.add(device);
                    }
                }
                callback.onSuccess(result);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
