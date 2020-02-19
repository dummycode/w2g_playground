package edu.gatech.w2gplayground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Best practice to lock orientation once app has started
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


        Button button = findViewById(R.id.scan_item);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Execute on button click
                onScanItemClick();
            }
        });

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLoginClick();
            }
        });
    }

    /**
     * Handler for scan item button click
     */
    protected void onScanItemClick() {
        Intent intent = new Intent(this, ScanItemActivity.class);
        startActivity(intent);
    }

    /**
     * Handler for login button click
     */
    protected void onLoginClick() {
        CustomToast.showTopToast(this, "Login pressed!");
    }
}
