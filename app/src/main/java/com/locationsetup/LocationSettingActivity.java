package com.locationsetup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by inter on 2017-11-28.
 */

public class LocationSettingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    EditText editText;
    ListView listView;
    Button searchBtn;

    LocationItem locationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);

        editText = (EditText)findViewById(R.id.locset_address);
        listView = (ListView)findViewById(R.id.locset_listview);
        searchBtn = (Button)findViewById(R.id.locset_search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation(editText.getText().toString());
            }
        });

        Intent intent = getIntent();
        locationItem = (LocationItem)intent.getSerializableExtra("locationItem");
        String address = intent.getStringExtra("address");
        editText.setText(address);

        searchLocation(address);
    }

    private void searchLocation(String address){
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses =geocoder.getFromLocationName(address,10);
            ArrayList<String> names = new ArrayList<String>();
            if(addresses.size()>0){
                for(int i =0;i<addresses.size();i++){
                    names.add(addresses.get(i).getAddressLine(0));
                }
                ArrayAdapter<String> aa = new ArrayAdapter<String>(this,R.layout.location_list_item,names);
                listView.setAdapter(aa);
                listView.setOnItemClickListener(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.putExtra("result",i);
        this.setResult(RESULT_OK,intent);
        finish();
    }
}
