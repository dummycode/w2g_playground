package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;

/**
 * A fragment to show the item information for a given location
 */
public class LocationInfoFragment extends Fragment {

    private PickListActivity activity;

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
        // Register receiver for key down event
        BroadcastReceiver receiver = new KeyBroadcastReceiver();
        getContext().registerReceiver(receiver, new IntentFilter(PickListActivity.keyDownAction));

        return inflater.inflate(R.layout.fragment_location_info, container, false);
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = (PickListActivity) getActivity();

        if (activity == null) {
            throw new RuntimeException("Activity cannot be null");
        }

        ImageView binConfigurationImage = activity.findViewById(R.id.next_location);

        Bundle args = getArguments();
        int orderCount = 0;

        if (args != null) {
            orderCount = args.getInt("orderCount");
        }

        switch (orderCount) {
            case 0:
            case 1:
            case 2:
                binConfigurationImage.setImageResource(R.drawable.ic_bin_config);
                break;
            default:
                CustomToast.showTopToast(activity, "Bin configuration unknown");
                break;
        }
    }

    public class KeyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Ignore if this fragment is not on the screen
            if (!(getFragmentManager().findFragmentById(R.id.fragment_container) instanceof LocationInfoFragment)) {
                return;
            }

            int keyCode = intent.getIntExtra("KEY_CODE", 0);

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                activity.locationInfoDone();
            }
        }
    }
}



