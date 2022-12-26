package work2.mobile_finalproject.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class ReviewDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvDate;
    private ImageView ivPhoto;
    private TextView tvContent;
    private RatingBar ratingbar;

    private String mCurrentPhotoPath;
    PlaceDBHelper showDBHelper;
    long data;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        showDBHelper = new PlaceDBHelper(this);

        tvName = findViewById(R.id.tvRVDName);
        tvPhone = findViewById(R.id.tvRVDPhone);
        tvAddress = findViewById(R.id.tvRVDAddress);
        tvDate = findViewById(R.id.tvRVDDate);
        ivPhoto = findViewById(R.id.tvRVDImage);
        tvContent = (findViewById(R.id.tvRVDContents));
        ratingbar = findViewById(R.id.etRVDRating);

//      ReviewActivity 에서 전달 받은 _id 값을 사용하여 DB 레코드를 가져온 후 ImageView 와 TextView 설정
        Intent intent = getIntent();
        data = intent.getLongExtra("id", 0);
        showDBHelper = new PlaceDBHelper(this);
        SQLiteDatabase myDB = showDBHelper.getWritableDatabase();

        String selection = PlaceDBHelper.COL_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(data)};

        Cursor cursor = myDB.query(PlaceDBHelper.TABLE_NAME, null, selection, selectArgs,
                null,null,null,null);

        String name = "";
        String phone = "";
        String address = "";
        String date = "";
        String path = "";
        String content = "";
        String rating = "";
        while(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_NAME));
            phone = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_PHONE));
            address = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_ADDRESS));
            date = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_DATE));
            path = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_PHOTOPATH));
            content = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_CONTENT));
            rating = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_RATING));
        }

        tvName.setText(name);
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
        tvAddress.setText(address);
        tvDate.setText(date);
        mCurrentPhotoPath = path;
        boolean isPath = true;
        try{
            if(mCurrentPhotoPath.equals("")){
                ivPhoto.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
            }
        }catch (NullPointerException e){
            ivPhoto.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
            isPath = false;
        }
        if(isPath)
            setPic();
        tvContent.setText(content);
        ratingbar.setRating(Float.parseFloat(rating));

        cursor.close();

        this.settingSideNavBar();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoUpdate:
                Intent intent = new Intent(ReviewDetailActivity.this, ReviewUpdateActivity.class);
                intent.putExtra("id", data);
                startActivity(intent);
                break;
            case R.id.btnDetailBack:
                finish();
                break;
        }
    }


    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = 1080;
        int targetH = 720;

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
        ivPhoto.setImageBitmap(bitmap);
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
                AlertDialog.Builder  builder = new AlertDialog.Builder(ReviewDetailActivity.this);
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
                ReviewDetailActivity.this,
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
                    Intent intent = new Intent(ReviewDetailActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(ReviewDetailActivity.this, ReviewActivity.class);
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