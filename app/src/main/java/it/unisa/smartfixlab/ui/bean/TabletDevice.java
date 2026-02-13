package it.unisa.smartfixlab.ui.bean;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.GregorianCalendar;

@Entity(tableName = "tablet")
public class TabletDevice extends Device {

    private double pollici;
    private TipoDisplay display;

    // Costruttore vuoto richiesto da Firebase
    @Ignore
    public TabletDevice() {
        super();
    }

    public TabletDevice(int id, String modello, String marca, String colore, int memoriaGb,
                        String dataAcquisto, double prezzoAcquisto,
                        String dataRivendita, double prezzoRivendita,
                        Stato stato, double pollici, TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto,
                dataRivendita, prezzoRivendita, stato);
        this.pollici = pollici;
        this.display = display;
    }

    // Costruttore per Vendita (senza rivendita obbligatoria)
    @Ignore
    public TabletDevice(int id, String modello, String marca, String colore, int memoriaGb,
                        String dataAcquisto, double prezzoAcquisto,
                        Stato stato, double pollici, TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato);
        this.pollici = pollici;
        this.display = display;
    }

    // Costruttore per Riparazione
    @Ignore
    public TabletDevice(int id, String modello, String marca, String colore, int memoriaGb,
                        String dataAcquisto, double prezzoAcquisto,
                        Stato stato, double prezzoRiparazione, String dataDiAffido,
                        double pollici, TipoDisplay display) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataDiAffido);
        this.pollici = pollici;
        this.display = display;
    }

    public double getPollici() {
        return pollici;
    }

    public void setPollici(double pollici) {
        this.pollici = pollici;
    }

    public TipoDisplay getDisplay() {
        return display;
    }

    public void setDisplay(TipoDisplay display) {
        this.display = display;
    }
}
