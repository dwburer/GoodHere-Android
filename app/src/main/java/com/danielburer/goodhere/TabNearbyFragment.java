package com.danielburer.goodhere;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import static android.app.Activity.RESULT_OK;


public class TabNearbyFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition mCameraPosition;
    private final String TAG = TabNearbyFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    //Default Location
    private final LatLng mDefaultLocation = new LatLng(-34,151);
    private final static int DEFAULT_ZOOM = 15;
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    //Last known location
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;

    //Store State
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //Place API
    private final int mMaxEntries = 5;
    private String[] mLikelyPlaceNames = new String[mMaxEntries];
    private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
    private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
    private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];

    int PLACE_PICKER_REQUEST = 1;

    Place selectedPlace;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_nearby_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }

                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

                googleMap.setMyLocationEnabled(true);

            }
        });

        //Build Google Play Services client
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        closePlace();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        //Nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Nothing
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

    }

    private void getDeviceLocation(){
        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
        }

        else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if(mLocationPermissionGranted){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if(mCameraPosition != null){
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }

        else if(mLastKnownLocation != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLatitude()),DEFAULT_ZOOM));
        }

        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
            }
        }

        updateLocationUI();

    }

    private void showCurrentPlace(){

        if(googleMap == null){
            return;
        }

        if(mLocationPermissionGranted){
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);

            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    int i = 0;
                    mLikelyPlaceNames = new String[mMaxEntries];
                    mLikelyPlaceAddresses = new String[mMaxEntries];
                    mLikelyPlaceAttributions = new String[mMaxEntries];
                    mLikelyPlaceLatLngs = new LatLng[mMaxEntries];

                    for(PlaceLikelihood placeLikelihood : likelyPlaces){
                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;

                        if(i > (mMaxEntries - 1)){
                            break;
                        }
                    }

                    likelyPlaces.release();
                    openPlacesDialog();

                }
            });
        }

        else {
            googleMap.addMarker(new MarkerOptions().title(getString(R.string.default_google_button_text))
            .position(mDefaultLocation).snippet(getString(R.string.default_google_button_text)));
        }

    }


    private void openPlacesDialog(){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String marketSnippet = mLikelyPlaceAddresses[which];

                if(mLikelyPlaceAttributions[which] != null){
                    marketSnippet += "\n" + mLikelyPlaceAttributions[which];
                }

                googleMap.addMarker(new MarkerOptions().title(mLikelyPlaceNames[which])
                .position(markerLatLng).snippet(marketSnippet));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,DEFAULT_ZOOM));
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(this.getContext()).setTitle("Pick Place")
                .setItems(mLikelyPlaceNames,listener).show();
    }

    private void updateLocationUI(){

        if(googleMap == null){
            return;
        }

        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }

        if(mLocationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }

    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void closePlace(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this.getActivity()),PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PLACE_PICKER_REQUEST){

            if(resultCode == RESULT_OK) {

                selectedPlace = PlacePicker.getPlace(this.getContext(),data);

                String toast = String.format("Place: %s", selectedPlace.getName());

                Toast.makeText(this.getContext(),toast, Toast.LENGTH_LONG).show();
            }

        }

    }






}