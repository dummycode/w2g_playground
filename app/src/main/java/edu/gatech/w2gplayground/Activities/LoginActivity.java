package edu.gatech.w2gplayground.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.barcode.ScanningRect;

import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Enums.Phrase;
import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.Permissions.Permissions;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Fragments.ScanItem.ScanResultFragment;
import edu.gatech.w2gplayground.Voice.LoginVoiceCommandReceiver;

import static edu.gatech.w2gplayground.R.layout.activity_scan_item;

public class LoginActivity extends AppCompatActivity implements Permissions.Listener, VoiceCommandActivity {

    public static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";

    private final String AUTH_KEY = "my_auth_key_embedded_in_qr_code";

    LoginVoiceCommandReceiver myVoiceCommandReceiver;

    /*
     * Declare variables for UI components
     */
    private TextView instructions;
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;
    private ImageView listeningStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_scan_item);

        // Handle passed in arguments
        Bundle bundle = getIntent().getExtras();

        Permissions permissionsFragment = (Permissions) getFragmentManager().findFragmentByTag(TAG_PERMISSIONS_FRAGMENT);
        if (permissionsFragment == null) {
            permissionsFragment = new Permissions();
            getFragmentManager().beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit();
        }
        // Register as a PermissionsFragment.Listener so our permissionsGranted() is called
        permissionsFragment.setListener(this);


        instructions = findViewById(R.id.scan_instructions);

        // Set instructions
        this.instructions.setText(getString(R.string.activity_login__instructions));

        // Hide instructions until we have permission
        instructions.setVisibility(View.GONE);

        this.resultIcon = findViewById(R.id.imageView2);
        this.resultIcon.setVisibility(View.GONE);

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        try {
            // Create the voice command receiver class
            myVoiceCommandReceiver = new LoginVoiceCommandReceiver<>(this);

            // Register another intent handler to demonstrate intents sent from the service
            myIntentReceiver =  new MyIntentReceiver();
            registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
        } catch (RuntimeException re) {
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }

        createScannerListener();
    }

    @Override
    protected void onStop() {
        myVoiceCommandReceiver.unregister();
        unregisterReceiver(myIntentReceiver);

        super.onStop();
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
     * Show the scanner when the camera permissions were granted
     */
    @Override
    public void permissionsGranted() {
        showScanner();
    }

    private void showScanner() {
        try {
            ScannerFragment scannerFragment = new ScannerFragment();

            Bundle args = new Bundle();
            args.putParcelable(ScannerFragment.ARG_SCANNING_RECT, new ScanningRect(.5f, .5f));

            scannerFragment.setArguments(args);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, scannerFragment).commit();
            scannerFragment.setListener2(scannerListener);

            // Put the instructions back on the screen
            instructions.setVisibility(View.VISIBLE);
        } catch (RuntimeException re) {
            finish();

            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }
    }

    private void createScannerListener() {
        try {
            /*
             * This is a simple wrapper class.
             *
             * We do this rather than having our MainActivity directly implement
             * ScannerFragment.Listener so we may gracefully catch the NoClassDefFoundError
             * if we are not running on an M-Series.
             */
            class OurScannerListener implements ScannerFragment.Listener2 {
                @Override
                public void onScan2Result(Bitmap bitmap, ScanResult2[] results) {
                    onScanFragmentScanResult(bitmap, results);
                }

                @Override
                public void onError() {
                    onScanFragmentError();
                }
            }

            scannerListener = new OurScannerListener();

        } catch (NoClassDefFoundError e) {
            // We get this exception if the SDK stubs against which we compiled cannot be resolved
            // at runtime. This occurs if the code is not being run on an M400 supporting the voice
            // SDK
            finish();

            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }
    }

    /**
     * This callback gives us the scan result.
     *
     * @param bitmap the bitmap in which barcodes were found
     * @param results an array of ScanResult
     */
    private void onScanFragmentScanResult(Bitmap bitmap, ScanResult2[] results) {
        ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        scannerFragment.setListener2(null);

        ScanResult2 result = results[0];
        Log.d(LOG_TAG, results[0].getText());

        // TODO: authenticate via API
        if (result.getText().equals(this.AUTH_KEY)) {
            goodScan();
        } else {
            badScan();
        }
    }

    /**
     * This callback gives us scan errors. This is relayed through mScannerListener.onError
     *
     * At a minimum the scanner fragment must be removed from the activity. This sample closes
     * the entire activity, since it has no other functionality
     */
    private void onScanFragmentError() {
        finish();

        CustomToast.showTopToast(this, getString(R.string.scanner__failure_message));
    }

    /**
     * Helper function to handle a good scan
     */
    private void goodScan() {
        resultIcon.setImageDrawable(getDrawable(R.drawable.ic_check_solid));
        resultIcon.setVisibility(View.VISIBLE);

        // All done!
        CustomToast.showTopToast(this, "Logged in!");
    }

    /**
     * Helper function to handle a bad scan
     */
    private void badScan() {
        resultIcon.setImageDrawable(getDrawable(R.drawable.ic_times_solid));
        resultIcon.setVisibility(View.VISIBLE);

        Beep.beep(this);

        // Add listener back after one (1) second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                scannerFragment.setListener2(scannerListener);

                resultIcon.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * Basic control to return from the result fragment to the scanner fragment, or exit the app from the scanner
     */
    @Override
    public void onBackPressed() {
        if (isScanResultShowing()) {
            showScanner();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Utility to determine if the scanner result fragment is showing
     *
     * @return True if showing
     */
    private boolean isScanResultShowing() {
        return getFragmentManager().findFragmentById(R.id.fragment_container) instanceof ScanResultFragment;
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
    private LoginActivity.MyIntentReceiver myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CustomToast.showTopToast(context, "Custom Intent Detected");
        }
    }

    /**
     * Handler for phrases
     *
     * @param phrase The phrase to handle
     */
    public void handleCommand(String phrase) {
        if (phrase.equals(Phrase.SCAN.getPhrase())) {
            this.handleScanCommand();
        } else if (phrase.equals(Phrase.MANUAL_ENTRY.getPhrase())){
            this.handleManualEntryCommand();
        } else {
            Log.e(LOG_TAG, "Phrase not handled");
        }
    }

    /**
     * Handler for "scan" command
     */
    private void handleScanCommand() {
        CustomToast.showTopToast(this, "Scan command received at " + System.currentTimeMillis());
    }

    /**
     * Handler for "scan" command
     */
    private void handleManualEntryCommand() {
        CustomToast.showTopToast(this, "Manual entry command received at " + System.currentTimeMillis());
    }

    /**
     * Utility to get the name of the current method for logging
     *
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }
}
