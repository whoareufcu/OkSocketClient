package com.example.oksocketclient.bean;

public class DeviceSet  {
    private boolean device_banallow;
    private boolean ismain;
    private String device_id;
    private int id;
    private int port;

    public boolean isDevice_banallow() {
        return device_banallow;
    }

    public void setDevice_banallow(boolean device_banallow) {
        this.device_banallow = device_banallow;
    }

    public boolean isIsmain() {
        return ismain;
    }

    public void setIsmain(boolean ismain) {
        this.ismain = ismain;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
