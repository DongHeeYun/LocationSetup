package com.locationsetup;

/**
 * Created by inter on 2017-11-01.
 */

public class LocationItem {

    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    private int wifi;
    private int bluetooth;
    private int sound;
    private int volume;
    private int brightness;

    private boolean enabled = true;

    public LocationItem() {
    }

    public LocationItem(String name, String address, double latitude, double longitude, int wifi, int bluetooth, int sound, int volume, int brightness) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.sound = sound;
        this.volume = volume;
        this.brightness = brightness;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void changeWifi() {
        if(wifi < 2)
            wifi++;
        else
            wifi = 0;
    }

    public void changeBluetooth() {
        if(bluetooth < 2)
            bluetooth++;
        else
            bluetooth=0;
    }

    public void changeSound() {
        if(sound < 3)
            sound++;
        else
            sound=0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getSound() {
        return sound;
    }

    public void setVolume(int volume){
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
