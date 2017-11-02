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
    private int wifi;       //0=default 1=on 2=off
    private int bluetooth;  //0=default 1=on 2=off
    private int location;   //0=default 1=on 2=off
    private int sound;      //0=default 1=off 2=진동 3=on
    private int volume;     //0~100
    private int nfc;        //0=default 1=on 2=off
    private int data;       //0=default 1=on 2=off

    public LocationItem(String locationName, String address){
        this.locationName=locationName;
        this.address = address;
    }

    public LocationItem(String locationName, double latitude, double longitude){
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeWifi(){
        if(wifi<2)
            wifi++;
        else
            wifi=0;
    }

    public void changeBluetooth(){
        if(bluetooth<2)
            bluetooth++;
        else
            bluetooth=0;
    }

    public void changeLocation(){
        if(location<2)
            location++;
        else
            location=0;
    }

    public void changeSound(){
        if(sound<3)
            sound++;
        else
            sound=0;
    }

    public void changeNfc(){
        if(nfc<2)
            nfc++;
        else
            nfc=0;
    }

    public void changeData(){
        if(data<2)
            data++;
        else
            data=0;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getWifi() {
        return wifi;
    }

    public int getBluetooth() {
        return bluetooth;
    }

    public int getLocation() {
        return location;
    }

    public int getSound() {
        return sound;
    }

    public void setVolume(int volume){
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public int getNfc() {
        return nfc;
    }

    public int getData() {
        return data;
    }



}
