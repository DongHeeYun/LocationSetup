package com.locationsetup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback,
        SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerDragListener {
    private static final String TAG = "SettingActivity";

    private static final int RC_LOCATION=1;

    private Context context;

    private Marker marker;

    private int ACT_LOCSET = 111;

    private int position;

    TextView set_cancle, set_save;
    EditText set_title, set_address;
    ImageButton set_wifi, set_blue, set_sound, set_bright, set_search;
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
        fileManager = FileManager.getFileManager(this);

        // LocationItem 수정일 경우
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if (position != -1) {
            locationItem = FileManager.items.get(position);
        } else {
            locationItem = new LocationItem(null,null,0,0);
            getLastLocation();
        }
        initView();
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
                                locationItem = new LocationItem(null,null, location.getLatitude(), location.getLongitude());
                                try {
                                    Geocoder geocoder = new Geocoder(context, Locale.KOREA);
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                    if (addresses.size() >0) {
                                        Address address = addresses.get(0);
                                        locationItem.setAddress(address.getAddressLine(0));
                                        set_address.setText(locationItem.getAddress());
                                        LatLng latLng = new LatLng(locationItem.getLatitude(),locationItem.getLongitude());
                                        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
                                        marker.setDraggable(true);
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                                        locationItem.setAddress(address.getAddressLine(0));
                                        locationItem.setName(address.getAddressLine(0));
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

    private void getItemLocation() {
        LatLng latLng = new LatLng(locationItem.getLatitude(),locationItem.getLongitude());
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
        marker.setDraggable(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
    }

    private void searchLocation(String address){
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address,10);
            if(addresses.size()>0){
                if(addresses.size()>=2){
                    Intent intent = new Intent(this, LocationSettingActivity.class);
                    intent.putExtra("address",set_address.getText().toString());
                    intent.putExtra("locationItem",locationItem);
                    startActivityForResult(intent,ACT_LOCSET);
                }
                else {
                    Address best = addresses.get(0);
                    String add = best.getAddressLine(0);
                    locationItem.setAddress(add);
                    locationItem.setLatitude(best.getLatitude());
                    locationItem.setLongitude(best.getLongitude());
                    set_address.setText(add);
                    LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                    if (marker != null){
                        marker.remove();
                    }
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
                    marker.setDraggable(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchLocation(String address, int num){
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address,10);
            if(addresses.size()>0){
                Address best = addresses.get(num);
                String add = best.getAddressLine(0);
                locationItem.setAddress(add);
                locationItem.setLatitude(best.getLatitude());
                locationItem.setLongitude(best.getLongitude());
                set_address.setText(add);
                LatLng latLng = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
                //marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("여기"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            searchLocation(set_address.getText().toString(),data.getIntExtra("result",0));
        }else{

        }
    }

    private void initView(){
        set_title = (EditText)findViewById(R.id.setting_title);
        set_address = (EditText)findViewById(R.id.setting_address);
        set_wifi = (ImageButton) findViewById(R.id.setting_wifi);
        set_blue = (ImageButton) findViewById(R.id.setting_blue);
        set_bright = (ImageButton) findViewById(R.id.setting_bright);
        set_sound = (ImageButton) findViewById(R.id.setting_sound);
        set_search = (ImageButton) findViewById(R.id.setting_search);
        set_cancle = (TextView) findViewById(R.id.setting_cancle);
        set_save = (TextView) findViewById(R.id.setting_save);
        sound_seekbar = (SeekBar)findViewById(R.id.sound_seekbar);
        bright_seekbar = (SeekBar)findViewById(R.id.bright_seekbar);
        soundLayout = (LinearLayout)findViewById(R.id.sound_layout);
        brightLayout = (LinearLayout)findViewById(R.id.bright_layout);

        // value initializing
        initImage();

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
        int index;
        switch(v.getId()){
            case R.id.setting_wifi:
                index = locationItem.changeWifi();
                if (index == 0) {
                    set_wifi.setImageResource(R.drawable.ic_wifi_none);
                } else if (index == 1) {
                    set_wifi.setImageResource(R.drawable.ic_wifi_on);
                } else if (index == 2) {
                    set_wifi.setImageResource(R.drawable.ic_wifi_off);
                }
                break;
            case R.id.setting_blue:
                index = locationItem.changeBluetooth();
                if (index == 0) {
                    set_blue.setImageResource(R.drawable.ic_bt_none);
                } else if (index == 1) {
                    set_blue.setImageResource(R.drawable.ic_bt_on);
                } else if (index == 2) {
                    set_blue.setImageResource(R.drawable.ic_bt_off);
                }
                break;
            case R.id.setting_sound:
                index = locationItem.changeSound();
                if (index == 1) {
                    set_sound.setImageResource(R.drawable.ic_vol_on);
                    soundLayout.setVisibility(LinearLayout.VISIBLE);
                } else {
                    soundLayout.setVisibility(LinearLayout.GONE);
                    if (index == 0) {
                        set_sound.setImageResource(R.drawable.ic_vol_none);
                    } else if (index == 2) {
                        set_sound.setImageResource(R.drawable.ic_vol_vib);
                    } else if (index == 3) {
                        set_sound.setImageResource(R.drawable.ic_vol_off);
                        soundLayout.setVisibility(LinearLayout.GONE);
                    }
                }

                break;
            case R.id.setting_bright:
                if (brightLayout.getVisibility() == View.VISIBLE) {
                    locationItem.setBrightness(-1);
                    set_bright.setImageResource(R.drawable.ic_brt_none);
                    brightLayout.setVisibility(View.GONE);
                } else {
                    set_bright.setImageResource(R.drawable.ic_brt_0);
                    brightLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.setting_cancle:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.setting_save:
                locationItem.setName(set_title.getText().toString());
                if (position != -1) {
                    FileManager.items.set(position, locationItem);
                } else {
                    fileManager.addItem(locationItem);
                }
                Intent intent = new Intent();
                intent.putExtra("item", locationItem);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.setting_search:
                searchLocation(set_address.getText().toString());
                break;
        }
    }

    private void initImage() {

        set_title.setText(locationItem.getName());
        set_address.setText(locationItem.getAddress());

        int wifi = locationItem.getWifi();
        int bluetooth = locationItem.getBluetooth();
        int sound = locationItem.getSound();
        int brightness = locationItem.getBrightness();

        // Image Initialize
        if (wifi == 0)
            set_wifi.setImageResource(R.drawable.ic_wifi_none);
        else if (wifi == 1)
            set_wifi.setImageResource(R.drawable.ic_wifi_on);
        else if (wifi == 2)
            set_wifi.setImageResource(R.drawable.ic_wifi_off);

        if (bluetooth == 0)
            set_blue.setImageResource(R.drawable.ic_bt_none);
        else if (bluetooth == 1)
            set_blue.setImageResource(R.drawable.ic_bt_on);
        else if (bluetooth == 2)
            set_blue.setImageResource(R.drawable.ic_bt_off);

        if (sound == 0)
            set_sound.setImageResource(R.drawable.ic_vol_none);
        else if (sound == 1)
            set_sound.setImageResource(R.drawable.ic_vol_on);
        else if (sound == 2)
            set_sound.setImageResource(R.drawable.ic_vol_vib);
        else if (sound == 3)
            set_sound.setImageResource(R.drawable.ic_vol_off);

        if (brightness == -1)
            set_bright.setImageResource(R.drawable.ic_brt_none);
        else if (brightness < 25)
            set_bright.setImageResource(R.drawable.ic_brt_0);
        else if (brightness < 50)
            set_bright.setImageResource(R.drawable.ic_brt_1);
        else if (brightness < 75)
            set_bright.setImageResource(R.drawable.ic_brt_2);
        else
            set_bright.setImageResource(R.drawable.ic_brt_3);

        if (locationItem.getBrightness() != -1) {
            brightLayout.setVisibility(LinearLayout.VISIBLE);
            bright_seekbar.setProgress(locationItem.getBrightness());
        }
        if (locationItem.getSound() == 2) {
            soundLayout.setVisibility(LinearLayout.VISIBLE);
            sound_seekbar.setProgress(locationItem.getVolume());
        }
    }

    GoogleMap googleMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerDragListener(this);

        if (position != -1) {
            locationItem = FileManager.items.get(position);
            getItemLocation();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(sound_seekbar)) {
            locationItem.setVolume(progress);
        } else {
            if (progress < 25) {
                set_bright.setImageResource(R.drawable.ic_brt_0);
            } else if (progress < 50) {
                set_bright.setImageResource(R.drawable.ic_brt_1);
            } else if (progress < 75) {
                set_bright.setImageResource(R.drawable.ic_brt_2);
            } else {
                set_bright.setImageResource(R.drawable.ic_brt_3);
            }
            locationItem.setBrightness(progress);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        locationItem.setLatitude(latLng.latitude);
        locationItem.setLongitude(latLng.longitude);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
