package com.locationsetup;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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

    static Geocoder geocoder;



    public LocationItem(String locationName, String address){
        this.locationName=locationName;
        this.address = address;
        findAddress(address);
    }

    public LocationItem(String locationName, double latitude, double longitude){
        this.locationName = locationName;
        this.address = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void findAddress(String address){
        if(LocationItem.geocoder==null){
            geocoder = new Geocoder(MainActivity.context, Locale.KOREA);
        }
        try {
            List<Address> addresses = LocationItem.geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                Address addr = (Address) addresses.get(0);
                this.latitude = addr.getLatitude();
                this.longitude = addr.getLongitude();
            }
        } catch (IOException e){
            return;
        }
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
