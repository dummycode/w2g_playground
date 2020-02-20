package edu.gatech.w2gplayground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

import edu.gatech.w2gplayground.Voice.VoiceCommandReceiver;

public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = "MainActivity";
    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";
    VoiceCommandReceiver myVoiceCommandReceiver;
    private boolean myRecognizerActive;

    TextView listeningStatus;
    Button scanButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Best practice to lock orientation once app has started
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);


        listeningStatus = findViewById(R.id.listening_status);

        scanButton = findViewById(R.id.scan_item);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Execute on button click
                onScanItemClick();
            }
        });

        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLoginClick();
            }
        });

        try {
            VuzixSpeechClient speechClient = new VuzixSpeechClient(this);

            // Create the voice command receiver class
            myVoiceCommandReceiver = new VoiceCommandReceiver(this);

            // Now register another intent handler to demonstrate intents sent from the service
            myIntentReceiver = new MyIntentReceiver();
            registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
        } catch (RemoteException re) {
            CustomToast.showTopToast(this, "Error initializing VuzixSpeechClient");
        }
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

    /**
     * Handler for "test" command
     */
    public void handleTestCommand() {
        System.out.println("Test command received");
    }

    /**
     * Handler for "scan" voice command
     */
    public void handleScanCommand() {
        scanButton.performClick();
    }


    /**
     * Unregister from the speech SDK
     */
    @Override
    protected void onDestroy() {
        myVoiceCommandReceiver.unregister();
        unregisterReceiver(myIntentReceiver);
        super.onDestroy();
    }


    /**
     * Utility to get the name of the current method for logging
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Update the button from "Listen" to "Stop" based on our cached state
     */
    private void updateListeningStatusText() {
        if (myRecognizerActive) {
            listeningStatus.setText("Listening...");
        } else {
            listeningStatus.setText("Not listening");
        }

        System.out.println("Update Listen Button Text");
    }

    /**
     * A callback for the SDK to notify us if the recognizer starts or stop listening
     *
     * @param isRecognizerActive boolean - true when listening
     */
    public void RecognizerChangeCallback(boolean isRecognizerActive) {
        Log.d(LOG_TAG, getMethodName());

        myRecognizerActive = isRecognizerActive;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateListeningStatusText();
            }
        });
    }

    /**
     * You may prefer using explicit intents for each recognized phrase. This receiver demonstrates that.
     */
    private MyIntentReceiver myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG_TAG, getMethodName());
            CustomToast.showTopToast(context, "Custom Intent Detected");
        }
    }
}
