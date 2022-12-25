package work2.mobile_finalproject.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class BookmarkPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "BookmarPlaceActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private TextView tvPlaceName;
    PlaceDto placeDto;
    String address;
    String keyWord;

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private LatLngResultReceiver latLngResultReceiver;

    private PlacesClient placesClient;
    LatLng currentLoc;

    private String mCurrentPhotoPath;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_place);


        Intent intent = getIntent();
        placeDto = (PlaceDto) intent.getSerializableExtra("bookmarkDTO");

        tvPlaceName = findViewById(R.id.tvPlaceName);
        tvPlaceName.setText(placeDto.getName());

        address = placeDto.getAddress();
        Log.d("recAddress : ", address);
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        currentLoc = new LatLng(placeDto.getLat(), placeDto.getLng());
        Log.d("currentLoc : ", String.valueOf(currentLoc.latitude + " , " + currentLoc.longitude));

        if (checkPermission()) mapLoad();

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(this);

        keyWord = placeDto.getKeyWord();
        Log.d(TAG, keyWord);
        searchStartByKeyWord(PlaceType.PARK);

        this.settingSideNavBar();
    }

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapBM);
        mapFragment.getMapAsync(this);      // 매개변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

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
        mGoogleMap.setMyLocationEnabled(true); // 내 위치 버튼 활성화

        // 지도 특정 위치에 마커 추가 - 지도 준비되고 마커 추가해야 하므로 onMapReady 에 작성
        markerOptions = new MarkerOptions();

//        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(BookmarkPlaceActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mGoogleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(BookmarkPlaceActivity.this,
                        String.format("현재 위치: (%f, %f)", location.getLatitude(), location.getLongitude()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String placeId = marker.getTag().toString();
                getPlaceDetail(placeId);
            }
        });
    }

    // PlaceType.PARK 유형으로 주변 정보 검색
    private void searchStartByKeyWord(String type) {

        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(currentLoc.latitude, currentLoc.longitude)
                .radius(10000) // 현재 위치 반경 10km 중 검색 // 개수 제한이 있나 왜 안되지
                .type(type)
                .keyword("공원")
                .build()
                .execute();
    }


    PlacesListener placesListener = new PlacesListener() {
        int count = 0;

        @Override
        public void onPlacesSuccess(final List<Place> places) {
            //마커 추가
            runOnUiThread(() -> {
                for (Place place : places) {
                    markerOptions.title(place.getName())
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    if (place.getPlaceId().equals(placeDto.getPlaceId())) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                        count++;
                    } else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_GREEN));

                    Marker newMarker = mGoogleMap.addMarker(markerOptions);
                    newMarker.setTag(place.getPlaceId());

                    Log.d(TAG, place.getName() + "  " + place.getPlaceId());
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) {
        }

        @Override
        public void onPlacesStart() {
        }

        @Override
        public void onPlacesFinished() {
            if (count == 0) {
                startLatLngService();
            }
        }

    };

        // newMarker Tag 에 저장해둔 placeId로 장소에 대한 세부정보 획득
        private void getPlaceDetail(String placeId) {
            List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(
                    com.google.android.libraries.places.api.model.Place.Field.ID,
                    com.google.android.libraries.places.api.model.Place.Field.NAME,
                    com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                    com.google.android.libraries.places.api.model.Place.Field.WEBSITE_URI,
                    com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER,
                    com.google.android.libraries.places.api.model.Place.Field.RATING);

            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    com.google.android.libraries.places.api.model.Place place = response.getPlace();
                    callDetailActivity(place);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        Log.e(TAG, "Place not found: " + statusCode + " " + e.getMessage());
                    }
                }
            });
        }

        private void callDetailActivity(com.google.android.libraries.places.api.model.Place place) {
            Intent intent = new Intent(BookmarkPlaceActivity.this, DetailActivity.class);
            intent.putExtra("id", place.getId()); // 사진 가져올 때 필요함
            intent.putExtra("name", place.getName());
            intent.putExtra("address", place.getAddress());
            intent.putExtra("uri", place.getWebsiteUri());
            intent.putExtra("phone", place.getPhoneNumber());

            try {
                intent.putExtra("rating", place.getRating());
            } catch (NullPointerException e) {
                intent.putExtra("rating", "no rating info");
            }

            // 어떤 위치 보내는지 확인하기
            intent.putExtra("currentLoc", currentLoc);
            intent.putExtra("keyword", "공원");

            startActivity(intent);
        }

        // 즐겨찾기 목록에 위치 실 주소는 있음. Geocoding 수행
        /* 주소 → 위도/경도 변환 IntentService 실행 */
        private void startLatLngService() {
            Intent intent = new Intent(BookmarkPlaceActivity.this, FetchLatLngIntentService.class);
            intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
            intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);
            startService(intent);
        }

        /* 주소 → 위도/경도 변환 ResultReceiver */
        class LatLngResultReceiver extends ResultReceiver {
            public LatLngResultReceiver(Handler handler) {
                super(handler);
            }

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                LatLng cafeLoc;
                ArrayList<LatLng> latLngList = null;
                if (resultCode == Constants.SUCCESS_RESULT) {
                    Toast.makeText(BookmarkPlaceActivity.this, "성공", Toast.LENGTH_SHORT).show();
                    if (resultData == null) return;
                    latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                    if (latLngList == null) {
//                    lat = (String) etLat.getHint();
//                    lng = (String) etLng.getHint();
                    } else {

//                        LatLng latlng = latLngList.get(0);
//                        cafeLoc = latlng;
//                        cafeMarkerOptions.title(placeDto.getName());
//                        cafeMarkerOptions.position(cafeLoc);
//                        cafeMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//
//                        Marker newMarker = mGoogleMap.addMarker(cafeMarkerOptions);
//                        newMarker.setTag(placeDto.getPlaceId());
//                        newMarker.showInfoWindow();
                    }

                } else {
//                etLat.setText(getString(R.string.no_address_found));
//                etLng.setText(getString(R.string.no_address_found));
                }
            }
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBeforeBM:
                    finish();
                    break;
                case R.id.btnShare:
                    shareMap();
                    break;
            }
        }

        public void shareMap() {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            takeCaptureMap();
            //문자 발송
            if (mCurrentPhotoPath != null) {
                Uri uri;
                if (Build.VERSION.SDK_INT < 24) { //nougat 전 버전
                    uri = Uri.fromFile(photoFile);
                } else {
                    uri = FileProvider.getUriForFile(BookmarkPlaceActivity.this,
                            "work2.mobile_finalproject.finalproject",
                            photoFile);
                }

                String text = "산책할래?";
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra("sms_body", text);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void takeCaptureMap() {
            GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    try {
                        FileOutputStream fos = null;

                        fos = new FileOutputStream(mCurrentPhotoPath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:/" + mCurrentPhotoPath)));
                        Log.d(TAG, "캡쳐 완료!");

                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mGoogleMap.snapshot(snapshotReadyCallback);
        }

        /*현재 시간 정보를 사용하여 파일 정보 생성*/
        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }


        /* 필요 permission 요청 */
        private boolean checkPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BookmarkPlaceActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_REQ_CODE) {
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


        // menu
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.option_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item01: // 첫 화면으로
                    Intent intent = new Intent(BookmarkPlaceActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item02: //앱 종료
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkPlaceActivity.this);
                    builder.setTitle("종료")
                            .setMessage("앱을 종료하시겠습니까?")
                            .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    moveTaskToBack(true); // 태스크를 백그라운드로 이동
                                    finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                                    android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
                                }
                            })
                            .setNegativeButton("취소", null)
                            .setCancelable(false)
                            .show();
                    break;
            }
            return true;
        }

        public void settingSideNavBar() {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

            DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_menu);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                    BookmarkPlaceActivity.this,
                    drawLayout,
                    toolbar,
                    R.string.open,
                    R.string.close
            );

            drawLayout.addDrawerListener(actionBarDrawerToggle);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    int id = menuItem.getItemId();

                    if (id == R.id.menu_item1) {
                        Intent intent = new Intent(BookmarkPlaceActivity.this, BookmarkActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "즐겨찾기", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.menu_item2) {
                        Intent intent = new Intent(BookmarkPlaceActivity.this, ReviewActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "내가 쓴 리뷰", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.menu_item3) {
                        Intent intent = new Intent(BookmarkPlaceActivity.this, SearchActivity.class);
//                    intent.putExtra("currentLoc", currentLoc);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "다른 공원 보기", Toast.LENGTH_SHORT).show();
                    }

                    DrawerLayout drawer = findViewById(R.id.drawer_menu);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });

        }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = findViewById(R.id.drawer_menu);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }
