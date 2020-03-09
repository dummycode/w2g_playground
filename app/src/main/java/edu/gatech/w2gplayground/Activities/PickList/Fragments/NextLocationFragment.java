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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;

/**
 * A fragment to show the next location
 */
public class NextLocationFragment extends Fragment {

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

        return inflater.inflate(R.layout.fragment_next_location, container, false);
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Bundle args = getArguments();
        String nextLocation = "LOC-01";

        if (args != null) {
            nextLocation = args.getString("nextLocation");
        }

        TextView nextLocationText = view.findViewById(R.id.next_location);
        nextLocationText.setText(nextLocation);
    }

    public class KeyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("KEY_CODE", 0);

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                ((PickListActivity) getActivity()).nextLocationDone();
            }
        }
    }
}


