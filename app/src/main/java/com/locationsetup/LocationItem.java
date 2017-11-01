package com.locationsetup;

/**
 * Created by inter on 2017-11-01.
 */

public class LocationItem {

    //위치를 위한 변수
    private String locationName;
    private String address;
    private double latitude;
    private double longitude;

    //설정을 위한 변수
    private int wifi=0;       //0=default 1=on 2=off
    private int bluetooth=0;  //0=default 1=on 2=off
    private int location=0;   //0=default 1=on 2=off
    private int sound=0;      //0=default 1=off 2=진동 3=on
    private int volume=0;     //0~100
    private int nfc=0;        //0=default 1=on 2=off
    private int data=0;       //0=default 1=on 2=off

    public LocationItem(String locationName, String address){
        this.locationName=locationName;
        this.address = address;
    }

    public LocationItem(String locationName, double latitude, double longitude){
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void rotateWifi(){
        if(wifi<2)
            wifi++;
        else
            wifi=0;
    }

    public void rotateBluetooth(){
        if(bluetooth<2)
            bluetooth++;
        else
            bluetooth=0;
    }

    public void rotateLocation(){
        if(location<2)
            location++;
        else
            location=0;
    }

    public void rotateSound(){
        if(sound<3)
            sound++;
        else
            sound=0;
    }

    public void rotateNfc(){
        if(nfc<2)
            nfc++;
        else
            nfc=0;
    }

    public void rotateData(){
        if(data<2)
            data++;
        else
            data=0;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public int getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(int bluetooth) {
        this.bluetooth = bluetooth;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getNfc() {
        return nfc;
    }

    public void setNfc(int nfc) {
        this.nfc = nfc;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }



}
