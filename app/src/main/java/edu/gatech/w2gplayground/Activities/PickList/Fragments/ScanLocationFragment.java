package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.vuzix.sdk.barcode.ScanResult2;
import com.vuzix.sdk.barcode.ScannerFragment;
import com.vuzix.sdk.barcode.ScanningRect;

import edu.gatech.w2gplayground.Audio.Beep;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;


/**
 * A fragment to scan the location
 */
public class ScanLocationFragment extends Fragment {

    public static final String LOG_TAG = ScanLocationFragment.class.getSimpleName();

    // UI components
    private ScannerFragment.Listener2 scannerListener;
    private ImageView resultIcon;
    FragmentActivity activity;

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
        return inflater.inflate(R.layout.fragment_scan_location, container, false);
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.activity = getActivity();

        // Handle passed in arguments
        Bundle args = getArguments();
        String locationId = "LOC-01";

        if (args != null) {
            locationId = args.getString("nextLocation");
        }

        this.resultIcon = view.findViewById(R.id.result_icon);
//        this.resultIcon.setVisibility(View.GONE);

        createScannerListener();
        showScanner();
    }

    private void showScanner() {
        try {
            ScannerFragment scannerFragment = new ScannerFragment();

            Bundle args = new Bundle();
            args.putParcelable(ScannerFragment.ARG_SCANNING_RECT, new ScanningRect(.5f, .5f));
            args.putBoolean(ScannerFragment.ARG_ZOOM_IN_MODE, true);

            scannerFragment.setArguments(args);

            activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, scannerFragment).commit();
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
        ScannerFragment scannerFragment = (ScannerFragment) activity.getFragmentManager().findFragmentById(R.id.fragment_container);
        scannerFragment.setListener2(null);

        ScanResult2 result = results[0];

        if (result.getText().equals("LOC TARGET")) {
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
        resultIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_solid));
        resultIcon.setVisibility(View.VISIBLE);

        CustomToast.showTopToast(activity, "YAY");
    }

    /**
     * Helper function to handle a bad scan
     */
    private void badScan() {
        resultIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_times_solid));
        resultIcon.setVisibility(View.VISIBLE);

        Beep.beep(getActivity());

        // Add listener back after one (1) second
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScannerFragment scannerFragment = (ScannerFragment) activity.getFragmentManager().findFragmentById(R.id.fragment_container);
                scannerFragment.setListener2(scannerListener);

                resultIcon.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * Method to be called when done with scanning
     */
    private void doneScanning() {
        CustomToast.showTopToast(activity, "Success!");

        resultIcon.setVisibility(View.GONE);
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


