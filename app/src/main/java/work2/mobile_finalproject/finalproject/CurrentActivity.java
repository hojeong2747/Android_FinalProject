package work2.mobile_finalproject.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentActivity extends AppCompatActivity {

    final static String TAG = "SearchActivity";
    final static int PERMISSION_REQ_CODE = 100;

    //    Map & Place
    FusedLocationProviderClient flpClient; // 위치 정보 수신
    Location mLastLocation;
    LatLng currentLoc;

    private GoogleMap mGoogleMap;       // 지도를 저장할 멤버변수 GoogleMap 객체
    private Marker initMarker;         // 초기 설정 Marker
    private Marker curMarker;           // 현재 위치 Marker

    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        flpClient = LocationServices.getFusedLocationProviderClient(this); // 위치 정보 수신

        if (checkPermission()) mapLoad();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMove:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("currentLoc", currentLoc);
                startActivity(intent);
                break;
        }
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            if (ActivityCompat.checkSelfPermission(CurrentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CurrentActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true); // 내 위치 버튼 활성화

            // 지도 위치 이동하기 - 1. 특정 위치로 이동하기
            LatLng latLng = new LatLng(37.604094, 127.042463);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            // 지도 특정 위치에 마커 추가 - 지도 준비되고 마커 추가해야 하므로 onMapReady 에 작성
            // 마커 옵션 설정
            markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("처음 위치")
                    .snippet("초기값")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            // 지도에 마커 추가 - 반환 값은 Marker (제거, 이동 등 제어 가능)
            initMarker = mGoogleMap.addMarker(markerOptions);
            initMarker.showInfoWindow();


            // 지도 롱클릭 이벤트 처리
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
//                executeGeocoding(latLng);
                }
            });

            // 마커 클릭 이벤트 - 마커 위의 윈도우 클릭 이벤트 처리
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    Toast.makeText(CurrentActivity.this, "marker window click : " + marker.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.currentMap);
        mapFragment.getMapAsync(mapReadyCallback);      // 매개변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        flpClient.requestLocationUpdates(
                getLocationRequest(),
                mLocCallback,
                Looper.getMainLooper()
        );
    }

    // FusedLocationProviderClient 객체 생성할 때 넣는 매개변수 중 하나
    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) { // 전달받은 LocationResult 로 지도 위치 이동
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();

                // 지도 위치 이동하기 - 2. GPS 수신 위치로 이동하기 : FusedLocationProviderClient, LocationCallback 사용
                mLastLocation = loc;
                currentLoc = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

                // 마커 옵션 설정
                markerOptions = new MarkerOptions()
                        .position(currentLoc)
                        .title("현재 위치")
                        .snippet("이동 중")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                // 지도에 마커 추가 - 반환 값은 Marker (제거, 이동 등 제어 가능)
                curMarker = mGoogleMap.addMarker(markerOptions);
                curMarker.showInfoWindow();

                // 지도 마커 위치 이동
                curMarker.setPosition(currentLoc);
            }
        }
    };

    // FusedLocationProviderClient 객체 생성할 때 넣는 매개변수 중 하나
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}