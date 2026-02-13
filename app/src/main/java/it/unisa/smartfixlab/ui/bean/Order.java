package it.unisa.smartfixlab.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String id; // Numero Ordine
    private String data;
    private StatoOrder statoorder;
    private List<Device> devices;

    // Costruttore vuoto per Firebase
    public Order() {
        this.devices = new ArrayList<>();
    }

    public Order(String id, String data) {
        this.id = id;
        this.data = data;
        this.devices = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
    
    public StatoOrder getStatoorder() {
        return statoorder;
    }

    public void setStatoorder(StatoOrder statoorder) {
        this.statoorder = statoorder;
    }

    public void addDevice(Device device) {
        if (this.devices == null) this.devices = new ArrayList<>();
        this.devices.add(device);
    }
}
