package com.locationsetup;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by inter on 2017-11-07.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback, SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "SettingActivity";

    private static final int RC_LOCATION=1;

    private Context context;

    private Marker marker;

    EditText set_title, set_address;
    Button set_wifi, set_blue, set_sound, set_bright, set_search, set_cancle, set_save;
    SeekBar sound_seekbar, bright_seekbar;
    SupportMapFragment mapFragment;
    LinearLayout soundLayout, brightLayout;


    private FusedLocationProviderClient mFusedLocationClient;
    LocationItem locationItem;
    Location location;

    FileManager fileManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        initView();
        fileManager = FileManager.getFileManager();
    }

    @SuppressWarnings("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private void getLastLocation(){
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                location = task.getResult();
                                locationItem = new LocationItem(null,location.getLatitude(),location.getLongitude());
                                try {
                                    Geocoder geocoder = new Geocoder(context, Locale.KOREA);
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                    if (addresses.size() >0) {
                                        Address address = addresses.get(0);
                                        locationItem.setAddress(address.getAddressLine(0));
                                        set_address.setText(locationItem.getAddress());
                                        LatLng latLng = new LatLng(locationItem.getLatitude(),locationItem.getLongitude());
                                        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG,e.toString());
                                }

                            } else {
                                Log.w(TAG, "getLastLocation:exception", task.getException());
                            }
                        }
                    });
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your location to know where you are.", RC_LOCATION, perms);
        }
    }

    private void searchLocation(String address){
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address,1);
            if(addresses.size()>0){
                Address best = addresses.get(0);
                String add = best.getAddressLine(0);
                locationItem.setAddress(add);
                locationItem.setLatitude(best.getLatitude());
                locationItem.setLongitude(best.getLongitude());
                set_address.setText(add);
                LatLng latLng = new LatLng(locationItem.getLatitude(),locationItem.getLongitude());
                marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        set_title = (EditText)findViewById(R.id.setting_title);
        set_address = (EditText)findViewById(R.id.setting_address);
        set_wifi = (Button)findViewById(R.id.setting_wifi);
        set_blue = (Button)findViewById(R.id.setting_blue);
        set_bright = (Button)findViewById(R.id.setting_bright);
        set_sound = (Button)findViewById(R.id.setting_sound);
        set_search = (Button)findViewById(R.id.setting_search);
        set_cancle = (Button)findViewById(R.id.setting_cancle);
        set_save = (Button)findViewById(R.id.setting_save);
        sound_seekbar = (SeekBar)findViewById(R.id.sound_seekbar);
        bright_seekbar = (SeekBar)findViewById(R.id.bright_seekbar);
        soundLayout = (LinearLayout)findViewById(R.id.sound_layout);
        brightLayout = (LinearLayout)findViewById(R.id.bright_layout);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.setting_map);
        mapFragment.getMapAsync(this);

        set_wifi.setOnClickListener(this);
        set_blue.setOnClickListener(this);
        set_bright.setOnClickListener(this);
        set_sound.setOnClickListener(this);
        set_search.setOnClickListener(this);
        set_cancle.setOnClickListener(this);
        set_save.setOnClickListener(this);
        sound_seekbar.setOnSeekBarChangeListener(this);
        bright_seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setting_wifi:
                locationItem.changeWifi();
                set_wifi.setText("wifi"+Integer.toString(locationItem.getWifi()));
                break;
            case R.id.setting_blue:
                locationItem.changeBluetooth();
                set_blue.setText("bluetooth"+Integer.toString(locationItem.getBluetooth()));
                break;
            case R.id.setting_sound:
                if(locationItem.getSound()==0||locationItem.getSound()==1){
                    locationItem.changeSound();
                }
                else if(locationItem.getSound()==2) {
                    locationItem.changeSound();
                    soundLayout.setVisibility(LinearLayout.VISIBLE);
                }
                else{
                    soundLayout.setVisibility(LinearLayout.GONE);
                    locationItem.changeSound();
                }
                set_sound.setText("sound"+Integer.toString(locationItem.getSound()));
                break;
            case R.id.setting_bright:
                if(locationItem.getBright()==0) {
                    locationItem.changeBright();
                    brightLayout.setVisibility(LinearLayout.VISIBLE);
                }
                else {
                    locationItem.changeBright();
                    brightLayout.setVisibility(LinearLayout.GONE);
                }
                set_bright.setText("bright"+Integer.toString(locationItem.getBright()));
                break;
            case R.id.setting_cancle:
                locationItem = (LocationItem) fileManager.getItems().get(2);
                sound_seekbar.setProgress(locationItem.getVolume());
                bright_seekbar.setProgress(locationItem.getBrightness());
                //finish();
                break;
            case R.id.setting_save:
                locationItem.setTitle(set_title.getText().toString());
                fileManager.addItem(locationItem);
                fileManager.saveFile();
                break;
            case R.id.setting_search:
                searchLocation(set_address.getText().toString());
                break;
        }
    }

    GoogleMap googleMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.equals(sound_seekbar)){
            locationItem.setVolume(progress);
        }else{
            locationItem.setBrightness(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
