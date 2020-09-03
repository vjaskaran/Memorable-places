package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    static ArrayList<String> places;
    static ArrayList<Double> arrLat;
    static ArrayList<Double> arrLon;
    ArrayList<String> arrLatStr;
    ArrayList<String> arrLonStr;
    Intent intent;
    ArrayAdapter<String> adapter;
    ListView listView;
    SharedPreferences sharedPreferences;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

//        Intent mapIntent = getIntent();
//        if(mapIntent!=null){
//            String plc=mapIntent.getStringExtra("plname");
//            places.add(plc);
//        }else{
//            places = new ArrayList<String>();
//            places.add("Add a new place..");
//        }
        //default
        Double defLat = 28.644800;
        Double defLon = 77.216721;
//        arrLat = new ArrayList<Double>();
//        arrLon = new ArrayList<Double>();

        sharedPreferences = this.getSharedPreferences("com.example.memorableplaces",Context.MODE_PRIVATE);



        //sharedPreferences.edit().clear().apply();



        arrLatStr = new ArrayList<String>();
        arrLonStr = new ArrayList<String>();
        places = new ArrayList<String>();
        arrLat = new ArrayList<Double>();
        arrLon = new ArrayList<Double>();
        String LatString = sharedPreferences.getString("Latitude","");
        String LonString = sharedPreferences.getString("Longitude","");
        if( LatString.equals("") || LonString.equals("") ){

            Log.i("Arr update","Creating new Arr");
//            arrLatStr.set(0,defLat.toString());
//            arrLonStr.set(0,defLon.toString());
//            places.set(0,"Add a new place..");
            arrLatStr.add(defLat.toString());
            arrLonStr.add(defLon.toString());
            places.add("Add a new place..");
            arrLat.add(defLat);
            arrLon.add(defLon);

            try{
                sharedPreferences.edit().putString("Latitude",ObjectSerializer.serialize(arrLatStr)).apply();
                sharedPreferences.edit().putString("Longitude",ObjectSerializer.serialize(arrLonStr)).apply();
                sharedPreferences.edit().putString("Places",ObjectSerializer.serialize(places)).apply();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try{
            Log.i("array info","initiating arrLat arrLon");

            arrLatStr = (ArrayList<String>) ObjectSerializer.deserialize( sharedPreferences.getString("Latitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
            arrLonStr = (ArrayList<String>) ObjectSerializer.deserialize( sharedPreferences.getString("Longitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
            arrLat = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Latitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
            arrLon = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Longitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
            places = (ArrayList<String>) ObjectSerializer.deserialize( sharedPreferences.getString("Places",ObjectSerializer.serialize(new ArrayList<String>())) );
        }catch(Exception e){
            e.printStackTrace();
        }

//        // str -> double -> str
//        String str="djudoe";
//        Double db=32.232356;
//        String str1=db.toString();
//        Double db1=Double.parseDouble(str);


//        arrLat = new ArrayList<>();
//        arrLon = new ArrayList<>();
//        arrLat.add(28.644800);
//        arrLon.add(77.216721);
//
//        places = new ArrayList<String>();
//        places.add("Add a new place..");

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                arrLat.set(0,location.getLatitude());
                arrLon.set(0,location.getLongitude());

                Double tmpLatDouble = location.getLatitude();
                Double tmpLonDouble = location.getLongitude();

                if(arrLatStr!=null && arrLat.size()>0){
                    arrLatStr.set(0,tmpLatDouble.toString());
                    arrLonStr.set(0,tmpLonDouble.toString());
                }else{
                    arrLatStr.add(tmpLatDouble.toString());
                    arrLonStr.add(tmpLonDouble.toString());
                }

                try{
                    Log.i("coords info","Adding curr coords");
                    sharedPreferences.edit().putString("Latitude",ObjectSerializer.serialize(arrLatStr)).apply();
                    sharedPreferences.edit().putString("Longitude",ObjectSerializer.serialize(arrLonStr)).apply();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(getApplicationContext(),MapsActivity.class);
                // call func for lat and longitude

//                try{
//                    arrLat = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Latitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
//                    arrLon = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Longitude",ObjectSerializer.serialize(new ArrayList<Double>())) );
//                }catch(Exception e){
//                    e.printStackTrace();
//                }

                //Log.i("Double to str",arrLat.getClass().toString());
                //Log.i("Data type",arrLat.get(0).getClass().toString());
                //arrLat.add(21.22);

                Log.i("soln",arrLat.toString());

//                Double tmplat = arrLat.get(i);
//                Double tmplon = arrLon.get(i);


                intent.putExtra("latitude",arrLat.get(i));
                intent.putExtra("longitude",arrLon.get(i));
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==-1){ //result_ok
            Log.i("info","RESULT OK");
            Double latNew = data.getDoubleExtra("latReturn",0);
            Double lonNew = data.getDoubleExtra("lonReturn",0);
            String name=data.getStringExtra("name");

            //if( (latNew!=null)&&(lonNew!=null) ) Log.i("LatLng",latNew+"  "+lonNew);

            try{
                //arrLat = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Latitude",ObjectSerializer.serialize(new ArrayList<String>())) );
                //arrLon = (ArrayList<Double>) ObjectSerializer.deserialize( sharedPreferences.getString("Longitude",ObjectSerializer.serialize(new ArrayList<String>())) );
                //places = (ArrayList<String>) ObjectSerializer.deserialize( sharedPreferences.getString("Places",ObjectSerializer.serialize(new ArrayList<String>())) );

                arrLat.add(latNew);
                arrLon.add(lonNew);
                places.add(name);
                arrLatStr.add(latNew.toString());
                arrLonStr.add(lonNew.toString());

                Log.i("new place added",name);

                sharedPreferences.edit().putString("Latitude",ObjectSerializer.serialize(arrLatStr)).apply();
                sharedPreferences.edit().putString("Longitude",ObjectSerializer.serialize(arrLonStr)).apply();
                sharedPreferences.edit().putString("Places",ObjectSerializer.serialize(places)).apply();
            }catch(Exception e){
                e.printStackTrace();
            }

            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter); // not req

            Log.i("info","reached end");
            Log.i("Lat array",arrLat.toString());
            Log.i("Place array",places.toString());
//            Double tmp = arrLat.get(0);
////            Log.i("First Place",tmp.toString());
        }
        else{ //result_cancelled
            Log.i("info","RESULT CANCELLED");
        }

    }
}
