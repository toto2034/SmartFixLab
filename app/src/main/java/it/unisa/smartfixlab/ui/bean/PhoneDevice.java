package it.unisa.smartfixlab.ui.bean;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.GregorianCalendar;

@Entity(tableName = "phone")
public class PhoneDevice extends Device {
    private TipoDisplay display;

    // Costruttore vuoto richiesto da Firebase
    @Ignore
    public PhoneDevice() {
        super();
    }

    public PhoneDevice(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       String dataRivendita, double prezzoRivendita,
                       Stato stato, TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto,
                dataRivendita, prezzoRivendita, stato);
        this.display = display;
    }

    // Costruttore per Vendita (senza rivendita obbligatoria)
    @Ignore
    public PhoneDevice(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       Stato stato, TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato);
        this.display = display;
    }

    // Costruttore per Riparazione
    @Ignore
    public PhoneDevice(int id, String modello, String marca, String colore, int memoriaGb,
                       String dataAcquisto, double prezzoAcquisto,
                       Stato stato, double prezzoRiparazione, String dataDiAffido,
                       TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataDiAffido);
        this.display = display;
    }

    public TipoDisplay getDisplay() {
        return display;
    }

    public void setDisplay(TipoDisplay display) {
        this.display = display;
    }
}
