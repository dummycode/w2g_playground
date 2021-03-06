package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.barcode.ScanningRect;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;


/**
 * A fragment to scan the items at a given location
 */
public class ScanItemsFragment extends Fragment {

    private static final String TAG_PERMISSIONS_FRAGMENT = "permissions";
    public static final String LOG_TAG = ScanItemsFragment.class.getSimpleName();

    // UI components
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;
    private PickListActivity activity;
    FrameLayout binPlacementFragmentContainer;

    private int totalScanned = 0;

    String upc = "012345678905";

    /*
     * Declare line variables
     */
    List<Line> lines = new ArrayList<>();

    /**
     * Inflate the correct layout upon creation
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState  If non-null, this fragment is being re-constructed from a previous saved state as given here.

     * @return - Returns the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan_items, container, false);
    }

    /**
     * Once our view is created, we will show the scan items fragment
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.activity = (PickListActivity) getActivity();

        if (this.activity == null) {
            throw new RuntimeException("Activity cannot be null");
        }

        this.resultIcon = view.findViewById(R.id.result_icon);
        this.resultIcon.setVisibility(View.GONE);

        // Start out on bin configuration
        binPlacementFragmentContainer = activity.findViewById(R.id.bin_placement_container);
        getChildFragmentManager()
                .beginTransaction()
                .replace(binPlacementFragmentContainer.getId(), new BinPlacementFragment())
                .addToBackStack(null)
                .commit();

        binPlacementFragmentContainer.setVisibility(View.GONE);

        createScannerListener();
        showScanner();
    }

    /**
     * Show the scanner
     */
    private void showScanner() {
        try {
            ScannerFragment scannerFragment = new ScannerFragment();

            Bundle args = new Bundle();
            args.putParcelable(ScannerFragment.ARG_SCANNING_RECT, new ScanningRect(.5f, .5f));
            args.putBoolean(ScannerFragment.ARG_ZOOM_IN_MODE, true);

            scannerFragment.setArguments(args);

            activity.getFragmentManager().beginTransaction().replace(R.id.scan_item_container, scannerFragment).commit();
            scannerFragment.setListener2(scannerListener);
        } catch (RuntimeException re) {
            CustomToast.showTopToast(activity, getString(R.string.only_on_mseries));
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
            CustomToast.showTopToast(activity, getString(R.string.only_on_mseries));
        }
    }

    /**
     * This callback gives us the scan result.
     *
     * @param bitmap the bitmap in which barcodes were found
     * @param results an array of ScanResult
     */
    private void onScanFragmentScanResult(Bitmap bitmap, ScanResult2[] results) {
        ScannerFragment scannerFragment = (ScannerFragment) activity.getFragmentManager().findFragmentById(R.id.scan_item_container);
        scannerFragment.setListener2(null);

        ScanResult2 result = results[0];
        Log.d(LOG_TAG, result.getText());

        if (result.getText().equals(upc)) {
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
        CustomToast.showTopToast(activity, getString(R.string.scanner__failure_message));
    }

    /**
     * Helper function to handle a good scan
     */
    private void goodScan() {
        resultIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_check_solid));
        resultIcon.setVisibility(View.VISIBLE);

        int binNum = totalScanned == 0 ? 1 : 2;

        Bundle args = new Bundle();
        args.putInt("binNum", binNum);

        BinPlacementFragment binPlacementFragment = new BinPlacementFragment();
        binPlacementFragment.setArguments(args);

        getChildFragmentManager()
                .beginTransaction()
                .replace(binPlacementFragmentContainer.getId(), binPlacementFragment)
                .addToBackStack(null)
                .commit();

        binPlacementFragmentContainer.setVisibility(View.VISIBLE);

        totalScanned++;

        activity.instructions.setText(String.format(getString(R.string.activity_picklist__scan_items_instructions), activity.currQuantity - totalScanned, activity.currItemName));

        if (totalScanned == activity.currQuantity) {
            // All done!
            Handler handler = new Handler();
            handler.postDelayed(this::doneScanning, 2000);
        } else {
            // Add listener back after two (2) seconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScannerFragment scannerFragment = (ScannerFragment) activity.getFragmentManager().findFragmentById(R.id.scan_item_container);
                    scannerFragment.setListener2(scannerListener);

                    resultIcon.setVisibility(View.GONE);

                    binPlacementFragmentContainer.setVisibility(View.GONE);
                }
            }, 2000);
        }

    }

    /**
     * Helper function to handle a bad scan
     */
    private void badScan() {
        resultIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_times_solid));
        resultIcon.setVisibility(View.VISIBLE);

        Beep.beep(activity);

        // Add listener back after one (1) second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScannerFragment scannerFragment = (ScannerFragment) activity.getFragmentManager().findFragmentById(R.id.scan_item_container);
                scannerFragment.setListener2(scannerListener);

                resultIcon.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * Method to be called when done with scanning
     */
    private void doneScanning() {
        resultIcon.setVisibility(View.GONE);

        activity.scanItemsDone();
    }

    /**
     * You may prefer using explicit intents for each recognized phrase. This receiver demonstrates that.
     */
    private ScanLocationFragment.MyIntentReceiver myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CustomToast.showTopToast(context, "Custom Intent Detected");
        }
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


