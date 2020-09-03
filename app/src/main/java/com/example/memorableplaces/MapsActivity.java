package com.example.memorableplaces;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.memorableplaces.MainActivity.arrLat;
import static com.example.memorableplaces.MainActivity.arrLon;
import static com.example.memorableplaces.MainActivity.places;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent mainIntent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i("Map log","Map created");
        mainIntent = getIntent();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double lat1 = mainIntent.getDoubleExtra("latitude",0);
        Double lon1 = mainIntent.getDoubleExtra("longitude",0);
        LatLng curr = new LatLng(lat1, lon1);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(curr).title(lat1+" , "+lon1));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr,12));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent returnIntent = new Intent();

                returnIntent.putExtra("latReturn",latLng.latitude);
                returnIntent.putExtra("lonReturn",latLng.longitude);
                //returnIntent.putExtra("name","New Location");

                String name="Unknown location";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if(listAddresses!=null && listAddresses.size()>0 ){
                        //name=listAddresses.get(0).getAdminArea();
                        Log.i("actual address",listAddresses.get(0).toString());
                        int startindx = listAddresses.get(0).getAddressLine(0).indexOf(',');
                        int endindx = listAddresses.get(0).getAddressLine(0).indexOf(',',startindx+1);
                        name = listAddresses.get(0).getAddressLine(0).substring(0,endindx);
                        name+=", ";
                        name+=listAddresses.get(0).getAdminArea();
                        Log.i("final address",name);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                returnIntent.putExtra("name",name);

                setResult(RESULT_OK,returnIntent);
                Toast.makeText(MapsActivity.this, "Location added!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
