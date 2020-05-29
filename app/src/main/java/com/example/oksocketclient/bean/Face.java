package com.example.oksocketclient.bean;

import java.util.Arrays;

public class Face {
    private Long id;
    private String only_id;
    private String name;
    private String idcard_num;
    private byte[] face_pic_byte;
    private String face_pic;
    private String type;
    private String system_permissions;
    private boolean isbanallow;//人员门禁权限
    private String startdate;
    private String enddate;
    private String starttime;
    private String endtime;

    @Override
    public String toString() {
        return "Face{" +
                "id=" + id +
                ", only_id='" + only_id + '\'' +
                ", name='" + name + '\'' +
                ", idcard_num='" + idcard_num + '\'' +
                ", face_pic_byte=" + Arrays.toString(face_pic_byte) +
                ", face_pic='" + face_pic + '\'' +
                ", type='" + type + '\'' +
                ", system_permissions='" + system_permissions + '\'' +
                ", isbanallow=" + isbanallow +
                ", startdate='" + startdate + '\'' +
                ", enddate='" + enddate + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSystem_permissions() {
        return system_permissions;
    }

    public void setSystem_permissions(String system_permissions) {
        this.system_permissions = system_permissions;
    }

    public boolean isIsbanallow() {
        return isbanallow;
    }

    public void setIsbanallow(boolean isbanallow) {
        this.isbanallow = isbanallow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFace_pic() {
        return face_pic;
    }
    public void setFace_pic(String face_pic) {
        this.face_pic = face_pic;
    }
    public String getOnly_id() {
        return only_id;
    }

    public void setOnly_id(String only_id) {
        this.only_id = only_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard_num() {
        return idcard_num;
    }

    public void setIdcard_num(String idcard_num) {
        this.idcard_num = idcard_num;
    }

    public byte[] getFace_pic_byte() {
        return face_pic_byte;
    }

    public void setFace_pic_byte(byte[] face_pic_byte) {
        this.face_pic_byte = face_pic_byte;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
