package work2.mobile_finalproject.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        goToMain(2); // 스플래쉬 화면 2초동안 보임
    }

    private void goToMain(int sec) {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class); // 2초 후 첫 화면인 메인 화면으로 이동
                startActivity(intent);
                finish();
            }
        }, 1000 * sec);
    }
}