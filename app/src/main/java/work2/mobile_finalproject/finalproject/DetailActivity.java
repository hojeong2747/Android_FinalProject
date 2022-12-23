package work2.mobile_finalproject.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    final static String TAG = "DetailActivity";

    TextView tvName;
    TextView tvAddress;
    TextView tvPhone;
    TextView tvUri;
    TextView tvRating;
    ImageView imageView;
    Button btnCall;

    private PlacesClient placesClient;
    private PlaceDBManager placeDBManager;
    private PlaceDto placeDto;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(this);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvUri = findViewById(R.id.tvUri);
        tvRating = findViewById(R.id.tvRating);
        imageView = findViewById(R.id.ivPhoto);
        btnCall = findViewById(R.id.btnCall);

        placeDto = new PlaceDto();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        tvName.setText(name);
        placeDto.setName(name);

        String address = intent.getStringExtra("address");
        tvAddress.setText(address);
        placeDto.setAddress(address);

        String uri = intent.getStringExtra("uri");
        if (uri == null) {
            tvUri.setText("웹사이트 정보가 없습니다.");
        } else
            tvUri.setText(uri);
        placeDto.setUri(uri);


        phone = intent.getStringExtra("phone");
        if (phone == null) {
            tvPhone.setText("전화번호 정보가 없습니다.");
            btnCall.setEnabled(false);
        } else
            tvPhone.setText(phone);
        placeDto.setPhone(phone);


        String rating = String.valueOf(intent.getDoubleExtra("rating", 0.0));
        if (rating.equals("no rating info"))
            tvRating.setText("평점 정보가 없습니다.");
        else
            tvRating.setText(rating);

        // photo_MetaData
        String placeId = intent.getStringExtra("id");
        getPlaceDetail(placeId);
        placeDto.setPlaceId(placeId);

        LatLng currentLoc = intent.getParcelableExtra("currentLoc");
        placeDto.setLat(currentLoc.latitude);
        placeDto.setLng(currentLoc.longitude);
        Log.d("DetailActivity's loc : ", String.valueOf(currentLoc.latitude + " , " + currentLoc.longitude));

        placeDto.setKeyWord(intent.getStringExtra("keyword"));
    }

    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
//                // Get the photo metadata.
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                // Get the attribution text.
                final String attributions = photoMetadata.getAttributions();
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    imageView.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCall:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("전화 DIALOG")
                        .setMessage("전화 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phone));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();

                break;
            case R.id.btnBookMark:
                //즐겨찾기 구현
                placeDBManager = new PlaceDBManager(this);
                boolean result = placeDBManager.addNewBookmark(placeDto);
                if(result)
                    Toast.makeText(this, "즐겨찾기에 추가!", Toast.LENGTH_SHORT).show();
            case R.id.btnReview:
                // 리뷰 작성

                break;
            case R.id.btnBefore:
                finish();
                break;
        }
    }

}