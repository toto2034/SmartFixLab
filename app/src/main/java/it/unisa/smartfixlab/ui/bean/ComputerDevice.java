package it.unisa.smartfixlab.ui.bean;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.GregorianCalendar;

@Entity(tableName = "computer")
public class ComputerDevice extends Device {
    private String pollici;
    private TipoDisplay display;
    private String processore;
    private int ramGb;
    private String gpu;

    // Costruttore vuoto richiesto da Firebase
    @Ignore
    public ComputerDevice() {
        super();
    }

    // Costruttore senza display (computer senza monitor)
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          String dataRivendita, double prezzoRivendita,
                          Stato stato, String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto,
                dataRivendita, prezzoRivendita, stato);
        this.pollici = "Privo di display";
        this.display = TipoDisplay.PRIVO_DI_DISPLAY;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    // Costruttore per Vendita per Computer senza display
    @Ignore
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          Stato stato, String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato);
        this.pollici = "Privo di display";
        this.display = TipoDisplay.PRIVO_DI_DISPLAY;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    // Costruttore per Riparazione per Computer senza display
    @Ignore
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          Stato stato, double prezzoRiparazione, String dataDiAffido,
                          String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataDiAffido);
        this.pollici = "Privo di display";
        this.display = TipoDisplay.PRIVO_DI_DISPLAY;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    // Costruttore con display
    @Ignore
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          String dataRivendita, double prezzoRivendita,
                          Stato stato, String pollici, TipoDisplay display,
                          String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto,
                dataRivendita, prezzoRivendita, stato);
        this.pollici = pollici;
        this.display = display;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    // Costruttore per Vendita per Computer con display
    @Ignore
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          Stato stato, String pollici, TipoDisplay display,
                          String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato);
        this.pollici = pollici;
        this.display = display;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    // Costruttore per Riparazione per Computer con display
    @Ignore
    public ComputerDevice(int id, String modello, String marca, String colore, int memoriaGb,
                          String dataAcquisto, double prezzoAcquisto,
                          Stato stato, double prezzoRiparazione, String dataDiAffido,
                          String pollici, TipoDisplay display,
                          String processore, int ramGb, String gpu) {
        super(id, modello, marca, colore, memoriaGb, dataAcquisto, prezzoAcquisto, stato, prezzoRiparazione, dataDiAffido);
        this.pollici = pollici;
        this.display = display;
        this.processore = processore;
        this.ramGb = ramGb;
        this.gpu = gpu;
    }

    public String getPollici() {
        return pollici;
    }

    public void setPollici(String pollici) {
        this.pollici = pollici;
    }

    public TipoDisplay getDisplay() {
        return display;
    }

    public void setDisplay(TipoDisplay display) {
        this.display = display;
    }

    public String getProcessore() {
        return processore;
    }

    public void setProcessore(String processore) {
        this.processore = processore;
    }

    public int getRamGb() {
        return ramGb;
    }

    public void setRamGb(int ramGb) {
        this.ramGb = ramGb;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }
}
