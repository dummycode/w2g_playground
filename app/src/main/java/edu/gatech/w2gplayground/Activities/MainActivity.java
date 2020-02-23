package edu.gatech.w2gplayground.Activities;

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
import android.widget.ImageView;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Voice.VoiceCommandReceiver;

public class MainActivity extends AppCompatActivity {
    public final static String LOG_TAG = MainActivity.class.getSimpleName();
    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";

    VoiceCommandReceiver myVoiceCommandReceiver;

    ImageView listeningStatus;
    Button scanButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // According to Vuzix, best practice is to lock orientation once app has started
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

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

            // Register another intent handler to demonstrate intents sent from the service
            myIntentReceiver = new MyIntentReceiver();
            registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
        } catch (RuntimeException re) {
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        } catch (RemoteException re) {
            CustomToast.showTopToast(this, "Error initializing VuzixSpeechClient");
        }
    }

    @Override
    protected void onStop() {
        myVoiceCommandReceiver.unregister();

        super.onStop();
    }

    /**
     * Handler for scan item button click
     */
    protected void onScanItemClick() {
        Intent intent = new Intent(this, ScanItemActivity.class);

        // Add extras
        Bundle extras = new Bundle();
        extras.putString("name", "Jiffy");
        extras.putInt("quantity", 2);
        extras.putString("upc", "072486002205");
        intent.putExtras(extras);

        // Clear up

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
        CustomToast.showTopToast(this, "Test command received at " + System.currentTimeMillis());
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
     *
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Update the text from "Listening..." to "Not listening" based on the state
     */
    private void updateListeningStatusText(boolean isRecognizerActive) {
        if (isRecognizerActive) {
            listeningStatus.setVisibility(View.VISIBLE);
        } else {
            listeningStatus.setVisibility(View.GONE);
        }
    }

    /**
     * A callback for the SDK to notify us if the recognizer starts or stop listening
     *
     * @param isRecognizerActive boolean - true when listening
     */
    public void RecognizerChangeCallback(final boolean isRecognizerActive) {
        Log.d(LOG_TAG, getMethodName());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateListeningStatusText(isRecognizerActive);
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
