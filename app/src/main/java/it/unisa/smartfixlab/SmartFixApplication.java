package it.unisa.smartfixlab;

import android.app.Application;

import android.util.Log;

public class SmartFixApplication extends Application {
    private static final String TAG = "SmartFixApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Inizializzazione centralizzata tramite utility
        it.unisa.smartfixlab.util.FirebaseConfig.init(this);
    }
}
