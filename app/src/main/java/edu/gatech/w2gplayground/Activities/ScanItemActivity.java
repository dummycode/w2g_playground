package edu.gatech.w2gplayground.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.Fragments.ScanItem.ScanItemSuccessFragment;
import edu.gatech.w2gplayground.Permissions.Permissions;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Fragments.ScanItem.ScanResultFragment;
import edu.gatech.w2gplayground.Voice.ScanItemVoiceCommandReceiver;
import edu.gatech.w2gplayground.Voice.VoiceCommandReceiver;

import static edu.gatech.w2gplayground.R.layout.activity_scan_item;

public class ScanItemActivity extends AppCompatActivity implements Permissions.Listener  {

    public static final String LOG_TAG = ScanItemActivity.class.getSimpleName();

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";

    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";
    ScanItemVoiceCommandReceiver myVoiceCommandReceiver;

    /*
     * Declare variables for UI components
     */
    private TextView instructions;
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;
    private ImageView listeningStatus;

    /*
     * Declare item variables
     */
    private String name, upc;
    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_scan_item);

        // Handle passed in arguments
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.name = bundle.getString("name", "test");
            this.upc = bundle.getString("upc", "001");
            this.quantity = bundle.getInt("quantity", 1);
        }

        Permissions permissionsFragment = (Permissions) getFragmentManager().findFragmentByTag(TAG_PERMISSIONS_FRAGMENT);
        if (permissionsFragment == null) {
            permissionsFragment = new Permissions();
            getFragmentManager().beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit();
        }
        // Register as a PermissionsFragment.Listener so our permissionsGranted() is called
        permissionsFragment.setListener(this);


        instructions = findViewById(R.id.scan_instructions);

        // Set instructions
        this.instructions.setText(String.format(
                getString(R.string.activity_scan_item__instructions),
                this.quantity, this.name
        ));

        // Hide instructions until we have permission
        instructions.setVisibility(View.GONE);

        this.resultIcon = findViewById(R.id.imageView2);
        this.resultIcon.setVisibility(View.GONE);

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        try {
            VuzixSpeechClient speechClient = new VuzixSpeechClient(this);

            // Create the voice command receiver class
            myVoiceCommandReceiver = new ScanItemVoiceCommandReceiver(this);

            // Register another intent handler to demonstrate intents sent from the service
            myIntentReceiver = new ScanItemActivity.MyIntentReceiver();
            registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));
        } catch (RuntimeException re) {
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        } catch (RemoteException re) {
            CustomToast.showTopToast(this, "Error initializing VuzixSpeechClient");
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
            args.putBoolean(ScannerFragment.ARG_ZOOM_IN_MODE, true);
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
     * This sample calls a helper class to display the result to the screen
     *
     * @param bitmap the bitmap in which barcodes were found
     * @param results an array of ScanResult
     */
    private void onScanFragmentScanResult(Bitmap bitmap, ScanResult2[] results) {
        ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        scannerFragment.setListener2(null);

        showScanResult(bitmap, results[0]);
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
     * Helper method to show a scan result
     *
     * @param bitmap the bitmap in which barcodes were found
     * @param result an array of ScanResult
     */
    private void showScanResult(Bitmap bitmap, ScanResult2 result) {
        Log.d(LOG_TAG, result.getText());

        if (result.getText().equals(this.upc)) {
            goodScan();
        } else {
            badScan();
        }
    }

    /**
     * Helper function to handle a good scan
     */
    private void goodScan() {
        resultIcon.setImageDrawable(getDrawable(R.drawable.ic_check_solid));
        resultIcon.setVisibility(View.VISIBLE);

        this.quantity--;
        this.instructions.setText(String.format(
            getString(R.string.activity_scan_item__instructions),
            this.quantity, this.name
        ));

        if (this.quantity == 0) {
            // All done!
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doneScanning();
                }
            }, 500);

        } else {
            // Add listener back after two (2) seconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                    scannerFragment.setListener2(scannerListener);

                    resultIcon.setVisibility(View.GONE);
                }
            }, 2000);
        }
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
     * Method to be called when done with scanning
     */
    private void doneScanning() {
        CustomToast.showTopToast(this, "Success!");

        instructions.setVisibility(View.GONE);
        resultIcon.setVisibility(View.GONE);

        ScanItemSuccessFragment scanItemSuccessFragment = new ScanItemSuccessFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, scanItemSuccessFragment).commit();
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
     * Handler for "quantity override" command
     */
    public void handleQuantityOverrideCommand() {
        CustomToast.showTopToast(this, "Quantity override command received at " + System.currentTimeMillis());
    }

    /**
     * Handler for "numbers" command
     */
    public void handleNumbers() {
        // TODO handle "one", "two", "three", etc.
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
    private ScanItemActivity.MyIntentReceiver myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CustomToast.showTopToast(context, "Custom Intent Detected");
        }
    }
}
