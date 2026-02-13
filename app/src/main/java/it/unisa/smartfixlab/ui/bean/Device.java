package it.unisa.smartfixlab.ui.bean;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.GregorianCalendar;

@Entity(tableName = "Device")
public class Device implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String modello;
    private String marca;
    private String colore;
    private int memoriaGb;
    private String dataAcquisto;
    private double prezzoAcquisto;
    private String dataRivendita;
    private double prezzoRivendita;
    private Stato stato;

    // Nuovi campi per Riparazione
    private boolean isRiparazione;
    private double prezzoRiparazione;
    private String dataDiAffido;

    // Costruttore vuoto richiesto da Firebase
    @Ignore
    public Device() {}

    public Device(int id, String modello, String marca, String colore, int memoriaGb, String dataAcquisto, double prezzoAcquisto, String dataRivendita, double prezzoRivendita, Stato stato) {
        this.id = id;
        this.modello = modello;
        this.marca = marca;
        this.colore = colore;
        this.memoriaGb = memoriaGb;
        this.dataAcquisto = dataAcquisto;
        this.prezzoAcquisto = prezzoAcquisto;
        this.dataRivendita = dataRivendita;
        this.prezzoRivendita = prezzoRivendita;
        this.stato = stato;
        this.isRiparazione = false;
    }

    // Costruttore per Vendita (senza rivendita obbligatoria)
    @Ignore
    public Device(int id, String modello, String marca, String colore, int memoriaGb, String dataAcquisto, double prezzoAcquisto, Stato stato) {
        this.id = id;
        this.modello = modello;
        this.marca = marca;
        this.colore = colore;
        this.memoriaGb = memoriaGb;
        this.dataAcquisto = dataAcquisto;
        this.prezzoAcquisto = prezzoAcquisto;
        this.stato = stato;
        this.isRiparazione = false;
    }

    // Costruttore per Riparazione
    @Ignore
    public Device(int id, String modello, String marca, String colore, int memoriaGb, String dataAcquisto, double prezzoAcquisto, Stato stato, double prezzoRiparazione, String dataDiAffido) {
        this.id = id;
        this.modello = modello;
        this.marca = marca;
        this.colore = colore;
        this.memoriaGb = memoriaGb;
        this.dataAcquisto = dataAcquisto;
        this.prezzoAcquisto = prezzoAcquisto; // Qui potrebbe essere il costo della riparazione per noi (pezzi)
        this.stato = stato;
        this.isRiparazione = true;
        this.prezzoRiparazione = prezzoRiparazione;
        this.dataDiAffido = dataDiAffido;
    }

    // Calcola il profitto (non salvato su Firebase/Room)
    @Exclude
    @androidx.room.Ignore
    public double getProfitto() {
        if (isRiparazione) {
            // Se è in riparazione, il profitto è il prezzo della riparazione
            // ma solo se la riparazione è conclusa.
            if (stato == Stato.RIPARAZIONE_CONCLUSA) {
                return prezzoRiparazione;
            }
            return 0;
        }
        return prezzoRivendita - prezzoAcquisto;
    }

    public int getId() {
        return id;
    }

    public String getModello() {
        return modello;
    }

    public String getMarca() {
        return marca;
    }

    public String getColore() {
        return colore;
    }

    public int getMemoriaGb() {
        return memoriaGb;
    }

    public String getDataAcquisto() {
        return dataAcquisto;
    }

    public double getPrezzoAcquisto() {
        return prezzoAcquisto;
    }

    public String getDataRivendita() {
        return dataRivendita;
    }

    public double getPrezzoRivendita() {
        return prezzoRivendita;
    }

    public Stato getStato() {
        return stato;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public void setMemoriaGb(int memoriaGb) {
        this.memoriaGb = memoriaGb;
    }

    public void setDataAcquisto(String dataAcquisto) {
        this.dataAcquisto = dataAcquisto;
    }

    public void setPrezzoAcquisto(double prezzoAcquisto) {
        this.prezzoAcquisto = prezzoAcquisto;
    }

    public void setDataRivendita(String dataRivendita) {
        this.dataRivendita = dataRivendita;
    }

    public void setPrezzoRivendita(double prezzoRivendita) {
        this.prezzoRivendita = prezzoRivendita;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public boolean isRiparazione() {
        return isRiparazione;
    }

    public void setRiparazione(boolean riparazione) {
        isRiparazione = riparazione;
    }

    public double getPrezzoRiparazione() {
        return prezzoRiparazione;
    }

    public void setPrezzoRiparazione(double prezzoRiparazione) {
        this.prezzoRiparazione = prezzoRiparazione;
    }

    public String getDataDiAffido() {
        return dataDiAffido;
    }

    public void setDataDiAffido(String dataDiAffido) {
        this.dataDiAffido = dataDiAffido;
    }
}
