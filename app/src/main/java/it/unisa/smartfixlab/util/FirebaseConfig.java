package it.unisa.smartfixlab.util;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.CollectionReference;
import android.util.Log;

public class FirebaseConfig {
    private static final String TAG = "FirebaseConfig";
    
    private static boolean initialized = false;

    public static void init(android.content.Context context) {
        if (!initialized) {
            try {
                // Firestore non ha bisogno dell'URL se usiamo il google-services.json corretto,
                // ma abilita comunque la persistenza offline (è abilitata di default, ma forziamo i settings)
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build();
                firestore.setFirestoreSettings(settings);
                
                Log.d(TAG, "Cloud Firestore initialized with persistence");
                initialized = true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize Firestore", e);
            }
        }
    }

    public static CollectionReference getCollection(String collectionName) {
        try {
            return FirebaseFirestore.getInstance().collection(collectionName);
        } catch (Exception e) {
            Log.e(TAG, "Error getting collection: " + collectionName, e);
            return null;
        }
    }
}
