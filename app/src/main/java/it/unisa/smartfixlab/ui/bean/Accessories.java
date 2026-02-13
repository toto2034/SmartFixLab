package it.unisa.smartfixlab.ui.bean;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.GregorianCalendar;

@Entity(tableName = "accessories")
public class Accessories extends Device {
    private String descrizione;

    // Costruttore vuoto richiesto da Firebase
    @Ignore
    public Accessories() {
        super();
    }

    public Accessories(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       String dataRivendita, double prezzoRivendita,
                       Stato stato, String descrizione) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto,
                dataRivendita, prezzoRivendita, stato);
        this.descrizione = descrizione;
    }

    // Costruttore per Vendita (senza rivendita obbligatoria)
    @Ignore
    public Accessories(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       Stato stato, String descrizione) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato);
        this.descrizione = descrizione;
    }

    // Costruttore per Riparazione
    @Ignore
    public Accessories(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       Stato stato, double prezzoRiparazione, String dataDiAffido,
                       String descrizione) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataDiAffido);
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
