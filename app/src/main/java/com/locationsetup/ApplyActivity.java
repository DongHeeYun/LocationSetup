package com.locationsetup;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sky on 2017-11-30.
 */

public class ApplyActivity extends Activity implements View.OnClickListener {

    private LocationItem item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_apply);

        TextView txt_name = findViewById(R.id.name);

        ImageView img_wifi = findViewById(R.id.wifi);
        ImageView img_bt =  findViewById(R.id.bluetooth);
        ImageView img_sound = findViewById(R.id.sound);
        ImageView img_brt = findViewById(R.id.brightness);

        findViewById(R.id.apply).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        Intent intent = getIntent();
        item = (LocationItem) intent.getSerializableExtra("setting");

        String name = item.getName();
        int wifi = item.getWifi();
        int bluetooth = item.getBluetooth();
        int sound = item.getSound();
        int brightness = item.getBrightness();

        txt_name.setText(name);

        // Image Initialize
        if (wifi == 0)
            img_wifi.setImageResource(R.drawable.ic_wifi_none);
        else if (wifi == 1)
            img_wifi.setImageResource(R.drawable.ic_wifi_on);
        else if (wifi == 2)
            img_wifi.setImageResource(R.drawable.ic_wifi_off);


        if (bluetooth == 0)
            img_bt.setImageResource(R.drawable.ic_bt_none);
        else if (bluetooth == 1)
            img_bt.setImageResource(R.drawable.ic_bt_on);
        else if (bluetooth == 2)
            img_bt.setImageResource(R.drawable.ic_bt_off);


        if (sound == 0)
            img_sound.setImageResource(R.drawable.ic_vol_none);
        else if (sound == 1)
            img_sound.setImageResource(R.drawable.ic_vol_on);
        else if (sound == 2)
            img_sound.setImageResource(R.drawable.ic_vol_vib);
        else if (sound == 3)
            img_sound.setImageResource(R.drawable.ic_vol_off);


        if (brightness == -1)
            img_brt.setImageResource(R.drawable.ic_brt_none);
        else if (brightness < 25)
            img_brt.setImageResource(R.drawable.ic_brt_0);
        else if (brightness < 50)
            img_brt.setImageResource(R.drawable.ic_brt_1);
        else if (brightness < 75)
            img_brt.setImageResource(R.drawable.ic_brt_2);
        else
            img_brt.setImageResource(R.drawable.ic_brt_3);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.apply:
                apply();
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void apply() {
        int wifi = item.getWifi();
        int bluetooth = item.getBluetooth();
        int sound = item.getSound();
        int volume = item.getVolume();
        int brightness = item.getBrightness();

        // Change Wifi settings
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == 1)
            wifiManager.setWifiEnabled(true);
        else if (wifi == 2)
            wifiManager.setWifiEnabled(false);

        // Change Bluetooth settings
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == 1)
            bluetoothAdapter.enable();
        else if (bluetooth == 2)
            bluetoothAdapter.disable();

        // Change Sound settings
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (sound == 1) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * volume/100,
                    AudioManager.FLAG_PLAY_SOUND);
        } else if (sound == 2) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (sound == 3) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }

        // Change Brightness settings
        if (brightness == -1) return;

        /*WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness/100;
        getWindow().setAttributes(layoutParams);*/

        int value = 255 / 100 * brightness;
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);

    }

}
