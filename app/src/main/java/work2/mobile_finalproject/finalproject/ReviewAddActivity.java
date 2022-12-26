package work2.mobile_finalproject.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewAddActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 200;

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private EditText etDate;
    private ImageView etPhoto;
    private EditText etContent;
    private RatingBar etRating;

    private String mCurrentPhotoPath;
    PlaceDBHelper reviewDBHelper;
    PlaceDBManager reviewDBManager;
    PlaceDto reviewDto;

    String name;
    String phone;
    String address;
    float ratingNum;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_add);

        createNotificationChannel(); // onCreate()에서 알림 채널 생성

        reviewDBHelper = new PlaceDBHelper(this);

        tvName = findViewById(R.id.tvRVName);
        tvPhone = findViewById(R.id.tvRVPhone);
        tvAddress = findViewById(R.id.tvRVAddress);
        etDate = findViewById(R.id.etRVDate);
        etPhoto = findViewById(R.id.etRVImage);
        etContent = findViewById(R.id.etRVContents);
        etRating = findViewById(R.id.etRVRating);

        // DetailActivity 에서 넘어온 정보 setting
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        tvName.setText(name);
        address = intent.getStringExtra("address");
        tvAddress.setText(address);
        phone = intent.getStringExtra("phone");
        tvPhone.setText(phone);
        boolean isPhone = true;
        try{
            if(phone.equals(""))
                tvPhone.setText("전화번호 정보가 없습니다.");
        }catch (NullPointerException e){
            tvPhone.setText("전화번호 정보가 없습니다.");
            isPhone = false;
        }
        if (isPhone)
            tvPhone.setText(phone);
        etPhoto.setImageResource(R.drawable.ic_baseline_add_a_photo_24);

        //사진 찍기
        etPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(photoFile != null){
                            Uri photoUri = FileProvider.getUriForFile(ReviewAddActivity.this,
                                    "work2.mobile_finalproject.finalprojet.fileprovider",
                                    photoFile
                            );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        etRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingNum = rating;
            }
        });

        this.settingSideNavBar();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }
    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = etPhoto.getWidth();
        int targetH = etPhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        etPhoto.setImageBitmap(bitmap);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAddSave:
//                DB에 촬영한 사진의 파일 경로 및 메모 저장
                String date = etDate.getText().toString();
                String content = etContent.getText().toString();

                reviewDto = new PlaceDto();

                reviewDto.setName(name);
                reviewDto.setPhone(phone);
                reviewDto.setAddress(address);

                if(date.equals("")){
                    long today = System.currentTimeMillis();
                    Date todayDate = new Date(today);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

                    String tDate = simpleDateFormat.format(todayDate);
                    date = tDate;
                }
                reviewDto.setDate(date);

                try{
                    if(mCurrentPhotoPath.equals("") || mCurrentPhotoPath == null)
                        mCurrentPhotoPath = "";
                }catch (NullPointerException e){
                    reviewDto.setPhotoPath("");
                }finally {
                    reviewDto.setPhotoPath(mCurrentPhotoPath);
                }

                if(content.equals("")){
                    content = "리뷰 작성 정보가 없습니다.";
                }
                reviewDto.setContent(content);
                reviewDto.setRating(ratingNum);

                reviewDBManager = new PlaceDBManager(this);
                boolean result = reviewDBManager.addNewReview(reviewDto);
                if(result) {
                    // 알림 생성
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MY_CHANNEL")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("알림")
                            .setContentText("리뷰 저장")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("리뷰 저장\n리뷰를 저장하였습니다."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

                    // 알림 실행
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    int notificationId = 100; // 알림 구분 위한 정수형 식별자 지정
                    notificationManager.notify(notificationId, builder.build()); // 생성 알림 실행


                    AlertDialog.Builder moveBuilder = new AlertDialog.Builder(ReviewAddActivity.this);
                    moveBuilder.setTitle("리뷰를 저장하였습니다.")
                            .setMessage("내가 쓴 리뷰 목록으로 이동하시겠습니까?")
                            .setPositiveButton("이동", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ReviewAddActivity.this, ReviewActivity.class);
                                    startActivity(intent);
                                    reviewDBHelper.close();
                                    finish();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reviewDBHelper.close();
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                break;

            case R.id.btnAddCancel:
                if(mCurrentPhotoPath != null) {
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
                finish();
                break;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);       // strings.xml 에 채널명 기록
            String description = getString(R.string.channel_description);       // strings.xml에 채널 설명 기록
            int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 알림의 우선순위 지정
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);    // CHANNEL_ID 지정
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);  // 채널 생성
            notificationManager.createNotificationChannel(channel);
        }
    }

    // menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01: // 첫 화면으로
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.item02: //앱 종료
                AlertDialog.Builder  builder = new AlertDialog.Builder(ReviewAddActivity.this);
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
                ReviewAddActivity.this,
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

                if (id == R.id.menu_item1){
                    Intent intent = new Intent(ReviewAddActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(ReviewAddActivity.this, ReviewActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "내가 쓴 리뷰", Toast.LENGTH_SHORT).show();
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