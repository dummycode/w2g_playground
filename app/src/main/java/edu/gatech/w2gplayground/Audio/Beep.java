package edu.gatech.w2gplayground.Audio;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

import edu.gatech.w2gplayground.R;

public class Beep {

    /**
     * This function beeps
     *
     * @param resources resources
     */
    public static void beep(Resources resources) {
        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        try {
            AssetFileDescriptor file = resources.openRawResourceFd(R.raw.beep);
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            player.setVolume(.1f, .1f);
            player.prepare();
            player.start();
            player.release();
        } catch (IOException e) {
            player.release();
        }
    }
}
