package edu.gatech.w2gplayground.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import edu.gatech.w2gplayground.ApplicationState;
import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Models.Generators.UserGenerator;
import edu.gatech.w2gplayground.Models.User;
import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.Permissions.Permissions;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Voice.LoginVoiceCommandReceiver;

import static edu.gatech.w2gplayground.R.layout.activity_login;

public class LoginActivity extends AppCompatActivity implements Permissions.Listener, VoiceCommandActivity {

    public static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";

    private final String AUTH_KEY = "991001332141";
    private final User user = UserGenerator.userWithAuthKey(AUTH_KEY);

    LoginVoiceCommandReceiver voiceCommandReceiver;

    /*
     * Declare variables for UI components
     */
    public TextView instructions;
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;
    private ImageView listeningStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        Permissions permissionsFragment = (Permissions) getFragmentManager().findFragmentByTag(TAG_PERMISSIONS_FRAGMENT);
        if (permissionsFragment == null) {
            permissionsFragment = new Permissions();
            getFragmentManager().beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit();
        }
        // Register as a PermissionsFragment.Listener so our permissionsGranted() is called
        permissionsFragment.setListener(this);


        instructions = findViewById(R.id.instructions);

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
            voiceCommandReceiver = new LoginVoiceCommandReceiver(this);

        } catch (RuntimeException re) {
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }

        createScannerListener();
    }

    /**
     * On activity stop we should unregister the voice command receiver
     */
    @Override
    protected void onStop() {
        if (voiceCommandReceiver != null) {
            voiceCommandReceiver.unregister();
        }

        super.onStop();
    }

    /**
     * On activity destroy we should unregister the voice command receiver
     */
    @Override
    protected void onDestroy() {
        if (voiceCommandReceiver != null) {
            voiceCommandReceiver.unregister();
        }

        super.onDestroy();
    }

    /**
     * Show the scanner when the camera permissions were granted
     */
    @Override
    public void permissionsGranted() {
        showScanner();
    }

    /**
     * Displays the scanner on screen in the fragment container
     */
    private void showScanner() {
        try {
            ScannerFragment scannerFragment = new ScannerFragment();

            Bundle args = new Bundle();
            args.putParcelable(ScannerFragment.ARG_SCANNING_RECT, new ScanningRect(.5f, .5f));
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
     * @param bitmap the bitmap in which barcodes were found
     * @param results an array of ScanResult
     */
    private void onScanFragmentScanResult(Bitmap bitmap, ScanResult2[] results) {
        ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        scannerFragment.setListener2(null);

        ScanResult2 result = results[0];
        Log.d(LOG_TAG, results[0].getText());

        // TODO: authenticate via API
        if (result.getText().equals(user.getAuthKey())) {
            ApplicationState.currentUser = user;
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

        // Go to home activity
        Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);

        // Go after half a second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(myIntent);
            }
        }, 500);

        // All done!
        CustomToast.showTopToast(this, String.format("Welcome %s!", ApplicationState.currentUser.getFullName()));
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
        runOnUiThread(() -> updateListeningStatusText(isRecognizerActive));
    }

    /**
     * Handler for "scan" command
     */
    public void handleScanCommand() {
        CustomToast.showTopToast(this, "Scan command received at " + System.currentTimeMillis());
    }

    /**
     * Handler for "manual entry" command
     */
    public void handleManualEntryCommand() {
        CustomToast.showTopToast(this, "Manual entry command received at " + System.currentTimeMillis());
        instructions.setText(R.string.activity_login__instructions__manual);
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
