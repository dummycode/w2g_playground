package edu.gatech.w2gplayground.Audio;

import android.content.Context;
import android.media.MediaPlayer;


import edu.gatech.w2gplayground.R;

public class Beep {

    public static String LOG_TAG = Beep.class.getSimpleName();

    /**
     * This function beeps
     *
     * @param context context beep was called from
     */
    public static void beep(Context context) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.beep);

        // Set player settings
        player.setVolume(.1f, .1f);
        player.start();
    }
}
