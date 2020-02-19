package edu.gatech.w2gplayground;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.barcode.ScanResult;

import java.io.IOException;

import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Permissions.Permissions;

import static edu.gatech.w2gplayground.R.layout.activity_scan_item;

public class ScanItemActivity extends AppCompatActivity implements Permissions.Listener  {

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";

    private View scanInstructionsView;
    private ScannerFragment.Listener2 scannerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_scan_item);

        Permissions permissionsFragment = (Permissions) getFragmentManager().findFragmentByTag(TAG_PERMISSIONS_FRAGMENT);
        if (permissionsFragment == null) {
            permissionsFragment = new Permissions();
            getFragmentManager().beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit();
        }
        // Register as a PermissionsFragment.Listener so our permissionsGranted() is called
        permissionsFragment.setListener(this);


        // Hide the instructions until we have permission granted
        scanInstructionsView = findViewById(R.id.scan_instructions);
        scanInstructionsView.setVisibility(View.GONE);

        createScannerListener();
    }

    @Override
    public void permissionsGranted() {
        showScanner();
    }

    private void showScanner() {
        try {
            ScannerFragment scannerFragment = new ScannerFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, scannerFragment).commit();
            scannerFragment.setListener2(scannerListener);                 // Required to get scan results
            scanInstructionsView.setVisibility(View.VISIBLE);  // Put the instructions back on the screen
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
     * This callback gives us the scan result.  This is relayed through mScannerListener.onScanResult
     *
     * This sample calls a helper class to display the result to the screen
     *
     * @param bitmap the bitmap in which barcodes were found
     * @param results an array of ScanResult
     */
    private void onScanFragmentScanResult(Bitmap bitmap, ScanResult2[] results) {
        ScannerFragment scannerFragment = (ScannerFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
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
     * @param bitmap -  the bitmap in which barcodes were found
     * @param result -  an array of ScanResult
     */
    private void showScanResult(Bitmap bitmap, ScanResult2 result) {
        scanInstructionsView.setVisibility(View.GONE);
        ScanResultFragment scanResultFragment = new ScanResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(ScanResultFragment.ARG_BITMAP, bitmap);
        args.putParcelable(ScanResultFragment.ARG_SCAN_RESULT, result);
        scanResultFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, scanResultFragment).commit();

        // Give beep as feedback
        Beep.beep(getResources());
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
     * @return True if showing
     */
    private boolean isScanResultShowing() {
        return getFragmentManager().findFragmentById(R.id.fragment_container) instanceof ScanResultFragment;
    }

}
