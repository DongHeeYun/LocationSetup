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
import android.widget.SeekBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by inter on 2017-11-07.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "SettingActivity";

    private static final int RC_LOCATION=1;

    private Context context;

    EditText set_title, set_address;
    Button set_wifi, set_blue, set_loca, set_nfc, set_data, set_sound, set_search, set_cancle, set_save;
    SeekBar set_seekbar;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationItem locationItem;
    Location location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
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
                                locationItem = new LocationItem(null,location.getLatitude(),location.getLongitude());
                                try {
                                    Geocoder geocoder = new Geocoder(context, Locale.KOREA);
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                    if (addresses.size() >0) {
                                        Address address = addresses.get(0);
                                        String s = String.format("%s %s %s %s", address.getFeatureName(), address.getThoroughfare(), address.getLocality(), address.getCountryName());
                                        locationItem.setAddress(s);
                                        set_address.setText(locationItem.getAddress());
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
            EasyPermissions.requestPermissions(this,
                    "This app needs access to your location to know where you are.",
                    RC_LOCATION, perms);
        }
    }

    private void initView(){
        set_title = (EditText)findViewById(R.id.setting_title);
        set_address = (EditText)findViewById(R.id.setting_address);
        set_wifi = (Button)findViewById(R.id.setting_wifi);
        set_blue = (Button)findViewById(R.id.setting_blue);
        set_loca = (Button)findViewById(R.id.setting_loca);
        set_nfc = (Button)findViewById(R.id.setting_nfc);
        set_data = (Button)findViewById(R.id.setting_data);
        set_sound = (Button)findViewById(R.id.setting_sound);
        set_search = (Button)findViewById(R.id.setting_search);
        set_cancle = (Button)findViewById(R.id.setting_cancle);
        set_save = (Button)findViewById(R.id.setting_save);
        set_seekbar = (SeekBar)findViewById(R.id.setting_seekbar);

        set_wifi.setOnClickListener(this);
        set_blue.setOnClickListener(this);
        set_loca.setOnClickListener(this);
        set_nfc.setOnClickListener(this);
        set_data.setOnClickListener(this);
        set_sound.setOnClickListener(this);
        set_search.setOnClickListener(this);
        set_cancle.setOnClickListener(this);
        set_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setting_wifi:
            case R.id.setting_blue:
            case R.id.setting_loca:
            case R.id.setting_nfc:
            case R.id.setting_data:
            case R.id.setting_sound:

        }
    }
}
