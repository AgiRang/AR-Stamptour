package com.example.administrator.sar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "MapFragment";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;

    private GoogleMap googleMap = null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;

    private ImageButton closeBtn;
    private Dialog dialog;
    View dialogView;
    boolean b = false;

    private List<StampItem> stampItemList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    private FirebaseDatabase database;

    public MapFragment() {
        // required
    }

    public void setCurrentLocation(Location location) {
        Log.d(TAG, "setCurrentLocation");
//        if ( currentMarker != null ) currentMarker.remove();

        if (location != null) {
            //현재위치의 위도 경도 가져옴
            b = true;
            Log.d(TAG, "setCurrentLocation: location NotNULL");
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
/*
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = this.googleMap.addMarker(markerOptions);
*/
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            return;
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        getActivity().setTitle("Map");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View layout = inflater.inflate(R.layout.map_layout, container, false);
        //dialogView = (View) inflater.inflate(R.layout.pager_layout, container, false);

        mapView = (MapView) layout.findViewById(R.id.map);

        database = FirebaseDatabase.getInstance();

        database.getReference().child("Stamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stampItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StampItem stampItem = snapshot.getValue(StampItem.class);
                    Log.d(TAG,"stapItem: "+stampItem);
                    stampItemList.add(stampItem);
                }
                Log.d(TAG,"stampItemList: "+stampItemList);
                makeMarker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mapView.getMapAsync(this);
        return layout;
    }

    public void makeMarker(){
        Log.d(TAG,"makeMarker: "+stampItemList.size());
        markerList.clear();
        googleMap.clear();

        for(int i = 0; i<stampItemList.size(); i++){

            LatLng latLng = new LatLng(stampItemList.get(i).Latitude, stampItemList.get(i).Longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(stampItemList.get(i).StampName);
            markerOptions.snippet("자세한 정보 Click");
            markerOptions.draggable(false);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
            //googleMap.addMarker(markerOptions);
            currentMarker = googleMap.addMarker(markerOptions);

            markerList.add(currentMarker);
            Log.d(TAG,"LatLng: "+stampItemList.get(i).Latitude+" "+ stampItemList.get(i).Longitude);
            Log.d(TAG,"StampName: "+stampItemList.get(i).StampName);
            Log.d(TAG,"currentMarker: "+currentMarker);
        }
        Log.d(TAG,"markerList: "+markerList);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        b = false;
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mapView.onStop();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "onStop: disconnect");
            googleApiClient.disconnect();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mapView.onResume();

        if (googleApiClient != null) {
            Log.d(TAG, "onResume: connect");
            //googleApiClient.connect();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mapView.onPause();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "onPause: disconnect");
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory");
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mapView.onLowMemory();

        if (googleApiClient != null) {
            Log.d(TAG, "onDestroy: googleApiClient NotNULL");
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if (googleApiClient.isConnected()) {
                Log.d(TAG, "onDestroy: googleApiClient disconnect");
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");
        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (mapView != null) {
            Log.d(TAG, "onActivityCreated: mapView NotNULL");
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        // OnMapReadyCallback implements 해야 mapView.getMapAsync(this); 사용가능. this 가 OnMapReadyCallback

        this.googleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에 지도의 초기위치를 서울로 이동
        setCurrentLocation(null);

        //나침반이 나타나도록 설정
        googleMap.getUiSettings().setCompassEnabled(false);
        // 매끄럽게 이동함
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //  API 23 이상이면 런타임 퍼미션 처리 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "onMapReady: 퍼미션");
            // 사용권한체크
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                Log.d(TAG, "onMapReady: 퍼미션 사용권한없음 재요청");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            } else {
                //사용권한이 있는경우
                Log.d(TAG, "onMapReady: 퍼미션 사용권한 있음");
                if (googleApiClient == null) {
                    Log.d(TAG, "onMapReady: googleApiClient Null-> buildGoogleApiClient");
                    buildGoogleApiClient();
                } else {
                    Log.d(TAG, "onMapReady: googleApiClient Not Null: " + googleApiClient);
                    googleApiClient.stopAutoManage(getActivity());
                    googleApiClient.registerConnectionCallbacks(this);
                    googleApiClient.registerConnectionFailedListener(this);
                    googleApiClient.connect();
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {
            Log.d(TAG, "onMapReady: 퍼미션요청 필요 없음");
            if (googleApiClient == null) {
                Log.d(TAG, "onMapReady: 퍼미션요청 필요 없음 / googleApiClient Null-> buildGoogleApiClient");
                buildGoogleApiClient();
            }

            googleMap.setMyLocationEnabled(true);
        }

        /*
        database = FirebaseDatabase.getInstance();

        database.getReference().child("Stamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange: "+dataSnapshot);
                stampItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StampItem stampItem = snapshot.getValue(StampItem.class);
                    Log.d(TAG,"stapItem: "+stampItem);
                    stampItemList.add(stampItem);
                }
                Log.d(TAG,"stampItemList: "+stampItemList);
                makeMarker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "onInfoWindowClick : 마커 정보창 클릭" + marker);
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.pager_layout);

                closeBtn = (ImageButton) dialog.findViewById(R.id.closeBtn);
                TextView StampName = (TextView) dialog.findViewById(R.id.StampName);
                TextView StampContent = (TextView) dialog.findViewById(R.id.StampContent);
                List<PagerModel> pagerArr = new ArrayList<>();

                Log.d(TAG,"markerList:"+markerList);
                Log.d(TAG,"stampItemList:"+stampItemList);

                for(int i =0; i<stampItemList.size();i++){
                    if(marker.equals(markerList.get(i))){
                        Log.d(TAG,"markerList.get(i):"+markerList.get(i));
                        Log.d(TAG,"stampItemList.get(i):"+stampItemList.get(i));
                        StampName.setText(stampItemList.get(i).StampName);
                        StampContent.setText(stampItemList.get(i).StampContent);
                        pagerArr.add(new PagerModel("1",stampItemList.get(i).StapmImage1));
                        pagerArr.add(new PagerModel("2",stampItemList.get(i).StapmImage2));
                        pagerArr.add(new PagerModel("3",stampItemList.get(i).StapmImage3));
                        break;
                    }
                }
                //pagerArr.add(new PagerModel("1", "Pager Item#1"));
                //pagerArr.add(new PagerModel("2", "Pager Item#2"));
                //pagerArr.add(new PagerModel("3", "Pager Item#3"));

                PagerAdapter adapter = new PagerAdapter(getActivity(), pagerArr);

                AutoScrollViewPager pager = (AutoScrollViewPager) dialog.findViewById(R.id.pager);

                pager.setAdapter(adapter);

                CirclePageIndicator pageIndicator = (CirclePageIndicator) dialog.findViewById(R.id.page_indicator);
                pageIndicator.setViewPager(pager);
                pageIndicator.setCurrentItem(0);
                dialog.show();

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "closeBtnsetOnClickListener : 다이얼로그 종료버튼");
                        dialog.cancel();
                    }
                });
            }
        });


    }

    private void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient: " + googleApiClient);

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();
        googleApiClient.connect();
        Log.d(TAG, "buildGoogleApiClient: " + googleApiClient);
    }

    public boolean checkLocationServicesStatus() {
        Log.d(TAG, "checkLocationServicesStatus");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");

        if (!checkLocationServicesStatus()) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);

            this.googleMap.getUiSettings().setCompassEnabled(true);
        }
        //makeMarker();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        Location location = new Location("");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude((DEFAULT_LOCATION.longitude));

        setCurrentLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged call..");
        if (b == false) {
            setCurrentLocation(location);
        }

    }
}