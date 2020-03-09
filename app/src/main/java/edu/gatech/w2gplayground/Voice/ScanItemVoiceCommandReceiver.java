package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import edu.gatech.w2gplayground.Activities.ScanItemActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Scan Item voice commands
 */
public class ScanItemVoiceCommandReceiver extends VoiceCommandReceiver<ScanItemActivity> {
    Phrase[] phrases = { Phrase.QUANTITY_OVERRIDE};

    /**
     * @param activity Activity from which we are created
     */
    public ScanItemVoiceCommandReceiver(ScanItemActivity activity) {
        super(activity);

        insertPhrases(phrases);
    }

    protected void handleCommand(String command) {
        Log.i("TEST_VOICE_COMMAND", "Command received");
    }
}
