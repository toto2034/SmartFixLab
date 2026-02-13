package it.unisa.smartfixlab.ui.dao;

import java.util.List;

/**
 * Interfaccia di callback generica per le operazioni Firebase.
 * @param <T> Tipo dell'oggetto restituito
 */
public interface FirebaseCallback<T> {

    /**
     * Chiamato quando l'operazione ha successo.
     * @param result Lista di risultati
     */
    void onSuccess(List<T> result);

    /**
     * Chiamato quando l'operazione fallisce.
     * @param e Eccezione
     */
    void onFailure(Exception e);
}
