package com.example.rishabhcha.vinnovate;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mAnsRef, mDustRef;

    ArrayList<Integer> garbageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        garbageList.add(0);
        garbageList.add(0);

        mAnsRef = FirebaseDatabase.getInstance().getReference().child("image_ans");

        mAnsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.d("-------snap:-----", String.valueOf(dataSnapshot));

                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    String rawOutput = String.valueOf(snap);
                    int i = rawOutput.indexOf("value");

                    Log.d("-------snap:-----", String.valueOf(snap));
                    Log.d("-----index------", String.valueOf(i));
                    String jsonString = rawOutput.substring(i + 8, rawOutput.length() - 2);
                    Log.d("------json---", jsonString);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String latitude = jsonObject.getString("latitude");
                        String longitude = jsonObject.getString("longitude");
                        String garbage = jsonObject.getString("garbage");
                        garbageList.add(Integer.valueOf(garbage));
                        if (garbageList.get(garbageList.size() - 1) == 1 && garbageList.get(garbageList.size() - 2) == 1 && garbageList.get(garbageList.size() - 3) == 1) {

                            new SendMailAndTweet().execute();

                        } else if (garbageList.get(garbageList.size() - 1) == 1 ) {

                            new SendMailAndTweet().execute();

                            LatLng garbageFrom1day = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            mMap.addMarker(new MarkerOptions().position(garbageFrom1day).title("Garbage Here From More Than One Day"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(garbageFrom1day, 15));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDustRef = FirebaseDatabase.getInstance().getReference().child("Dustbin");

        mDustRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("------data---",dataSnapshot.toString());

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Log.d("----datasnap-----", (String) snapshot.child("fillpercent").getValue());
                    if (((String) snapshot.child("fillpercent").getValue()).equals("75")){

                        Log.d("----yes---","true");
                        Log.d("-----lata-----",((String) snapshot.child("latitude").getValue()));
                        LatLng dustMarker = new LatLng(Double.parseDouble((String) snapshot.child("latitude").getValue()),Double.parseDouble((String) snapshot.child("longitude").getValue()));
                        mMap.addMarker(new MarkerOptions().position(dustMarker).title("Pick this Dustbin 75% filled").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dustMarker, 15));

                    }else {

                        Log.d("----yes---","true");
                        Log.d("-----lata-----",((String) snapshot.child("latitude").getValue()));
                        LatLng dustMarker = new LatLng(Double.parseDouble((String) snapshot.child("latitude").getValue()),Double.parseDouble((String) snapshot.child("longitude").getValue()));
                        mMap.addMarker(new MarkerOptions().position(dustMarker).title("Pick this Dustbin 90% filled").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dustMarker, 15));

                    }


                }

                /*if ((dataSnapshot.child("dustbin1").child("fillpercent").getValue().toString()).equals("75")) {

                    LatLng dustMarker = new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                    mMap.addMarker(new MarkerOptions().position(dustMarker).title("Pick this Dustbin 75% filled").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dustMarker, 15));

                } else {

                    LatLng dustMarker = new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                    mMap.addMarker(new MarkerOptions().position(dustMarker).title("Pick this Dustbin 90% filled").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dustMarker, 15));

                }
                */

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    class SendMailAndTweet extends AsyncTask<Object, Object, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Object... urls) {

            try {
                URL url = new URL("https://emailtweetieee.herokuapp.com/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    urlConnection.disconnect();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {

            Log.i("INFO", response);

        }
    }

}
