package work2.mobile_finalproject.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnWalk:
                Intent intent = new Intent(this, CurrentActivity.class);
                startActivity(intent);
                break;
        }
    }
}