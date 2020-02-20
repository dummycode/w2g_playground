package edu.gatech.w2gplayground;

import android.content.Context;
import android.view.Gravity;

public class CustomToast {
    /**
     * Short length CustomToast message at the top of the screen
     *
     * @param context context of CustomToast
     * @param text text to display
     */
    public static void showTopToast(Context context, String text) {
        android.widget.Toast toast = android.widget.Toast.makeText(
                context,
                text,
                android.widget.Toast.LENGTH_LONG
        );
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.setDuration(android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }
}
