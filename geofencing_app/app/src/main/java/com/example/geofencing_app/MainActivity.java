package com.example.geofencing_app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final float GEOFENCE_RADIUS_IN_METERS = 200 ;
    private static final double LATITUDE = 31.481158;
    private static final double LONGITUDE = 74.303179;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =Geofence.NEVER_EXPIRE;
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private ArrayList<Geofence> geofenceList=new ArrayList<>();

    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGeofenceMonitor();

    }
    private void startGeofenceMonitor()
    {
        Log.i("info", "Start geofence monitoring");
        try
        {
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSIONS_REQUEST_CODE);
                Log.i("info", "Permission not available! ");
            }
            else
            {
                mGeofencingClient = LocationServices.getGeofencingClient(this);
                geofenceList.add(new Geofence.Builder().setRequestId("point0")
                        .setCircularRegion(LATITUDE, LONGITUDE, GEOFENCE_RADIUS_IN_METERS)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(1)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.i("info", "geofence added!: ");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.i("info", "geofence failed! " + e.toString());
                            }
                        });
            }
        }
        catch (Exception e)
        {
            Log.i("er", e.getMessage());
        }
    }
    private GeofencingRequest getGeofencingRequest()
    {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(geofenceList)
                .build();
    }
    private PendingIntent getGeofencePendingIntent()
    {
        if (mGeofencePendingIntent != null)
        {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
}
