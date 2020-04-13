package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.gatech.w2gplayground.R;

/**
 * A fragment to show the summary
 */
public class BinPlacementFragment extends Fragment {
    public static final String LOG_TAG = BinPlacementFragment.class.getSimpleName();

    private Activity activity;
    ImageView binPlacement;
    private int binNum;

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
        return inflater.inflate(R.layout.fragment_bin_placement, container, false);
    }

    /**
     * Once our view is created, we will show bin placement fragment
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = getActivity();

        if (activity == null) {
            throw new RuntimeException("Activity cannot be null");
        }

        Bundle args = getArguments();
        if (args != null) {
            binNum = args.getInt("binNum", -1);

        } else {
            binNum = -1;
        }

        binPlacement = view.findViewById(R.id.bin_placement);

        switch (binNum) {
            case 1:
                break;
            case 2:
                break;
            default:
                Log.d(LOG_TAG, "No bin number supplied");
                binPlacement.setImageResource(R.drawable.ic_bin_placement_1);
                break;
        }



    }
}


