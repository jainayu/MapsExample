package com.example.root.mapsexample;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener{
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private Marker helperloc;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude,longitude;
    double end_latitude = 30.324230,end_longitude = 78.041663;
    int PROXIMITY_RADIUS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else //permission Denied
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
                return;
        }
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buidGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                // return;
            }
        } else {
            buidGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
        //mMap.setOnMarkerClickListener(this);
        //mMap.setOnMarkerDragListener(this);
    }

    protected synchronized void buidGoogleApiClient(){
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    public void onClick(View v){
        Object dataTransfer[] = new Object[3];
        Object latln[] = new Object[4];
        latln[0] = new LatLng(30.324652,78.040428);
        latln[1] = new LatLng(30.327615,78.045641);
        latln[2] = new LatLng(30.330619,78.029569);
        latln[3] = new LatLng(30.320748,78.040751);
        int i;


        switch (v.getId()) {
            case R.id.B_to:

                 String url = getDirectionUrl();
                 GetDirectionsData getDirectionsData = new GetDirectionsData();
                 dataTransfer[0] = mMap;
                 dataTransfer[1] = url;
                 dataTransfer[2] = new LatLng(end_latitude,end_longitude);

                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(new LatLng(end_latitude,end_longitude));
                markerOptions1.title("Helper Location");
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                helperloc = mMap.addMarker(markerOptions1);

                 for(i=0; i<4; i++)
                 {   Object latLng = (LatLng) latln[i];
                     markerOptions1.position((LatLng) latLng);
                     markerOptions1.title("Helper Location");
                     markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                     helperloc= mMap.addMarker(markerOptions1);
                 }

                 //getDirectionsData.execute(dataTransfer);
                 break;
        }
    }


    private  String getDirectionUrl(){
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyD6LqBkdMUrV_4zkaaThL-UU74Qc17FF10");
        return googleDirectionsUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged","entered");

        lastLocation = location;
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
            Log.d("onLocationChanged","Removing Location Updates");
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
        return true;
    }

   private String getURL(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("Location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&key="+"AIzaSyD6LqBkdMUrV_4zkaaThL-UU74Qc17FF10");
        return googlePlaceUrl.toString();
   }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
    }
}
