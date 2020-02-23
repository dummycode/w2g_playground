package edu.gatech.w2gplayground;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;

import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Fragments.ScanItem.ScanItemSuccessFragment;
import edu.gatech.w2gplayground.Permissions.Permissions;

import static edu.gatech.w2gplayground.R.layout.activity_scan_item;

public class ScanItemActivity extends AppCompatActivity implements Permissions.Listener  {

    private static final String LOG_TAG = ScanItemActivity.class.getSimpleName();

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";

    private TextView instructions;
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;

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

        createScannerListener();
    }

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

        /*
        // Hide instructions
        instructions.setVisibility(View.GONE);

        ScanResultFragment scanResultFragment = new ScanResultFragment();

        Bundle args = new Bundle();
        args.putParcelable(ScanResultFragment.ARG_BITMAP, bitmap);
        args.putParcelable(ScanResultFragment.ARG_SCAN_RESULT, result);

        scanResultFragment.setArguments(args);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, scanResultFragment).commit();

        // Give beep as feedback
        Beep.beep(this);
        */
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
            doneScanning();
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

        // Add listener back after a half of a second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScannerFragment scannerFragment = (ScannerFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                scannerFragment.setListener2(scannerListener);

                resultIcon.setVisibility(View.GONE);
            }
        }, 500);
    }

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

}
