package edu.gatech.w2gplayground.Permissions;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import edu.gatech.w2gplayground.R;

/**
 * A fragment to encapsulate the run-time permissions
 */
public class Permissions extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 0;

    private Listener listener;

    /**
     * One-time initialization. Sets up the view
     *
     * @param savedInstanceState we have no saved state. Just pass through to superclass
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        requestPermissions();
    }

    /**
     * Make the permissions request to the system
     */
    private void requestPermissions() {
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted();
        } else {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
        }
    }

    /**
     * Called upon the permissions being granted. Notifies the permission listener.
     */
    private synchronized void permissionsGranted() {
        if (listener != null) {
            listener.permissionsGranted();
        }
    }


    /**
     * Sets the listener on which we will call permissionsGranted()
     * @param listener pointer to the class implementing the PermissionsFragment.Listener
     */
    public synchronized void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Required interface for any activity that requests a run-time permission
     *
     * @see <a href="https://developer.android.com/training/permissions/requesting.html">https://developer.android.com/training/permissions/requesting.html</a>
     * @param requestCode int: The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions String: The requested permissions. Never null.
     * @param grantResults int: The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissions.length == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestPermissions();
                } else {
                    // Permission was denied. Give the user a hint, and exit
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();

                    Toast.makeText(getContext(), R.string.grant_camera_permissions, Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Define the interface of a permission fragment listener
     */
    public interface Listener {
        void permissionsGranted();
    }
}
