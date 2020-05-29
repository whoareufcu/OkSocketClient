package com.example.oksocketclient.bean;

public class IpSave {
    private String ip;
    private String clinettag;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getClinettag() {
        return clinettag;
    }

    public void setClinettag(String clinettag) {
        this.clinettag = clinettag;
    }
}
