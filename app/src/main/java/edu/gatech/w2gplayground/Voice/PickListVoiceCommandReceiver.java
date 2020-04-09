package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Scan Item voice commands
 */
public class PickListVoiceCommandReceiver extends VoiceCommandReceiver<PickListActivity> {
    List<Phrase> phrases;

    /**
     * @param activity Activity from which we are created
     */
    public PickListVoiceCommandReceiver(PickListActivity activity) {
        super(activity);

        insertPhrases(phrases);
    }

    protected void handleCommand(String command) {
        Log.i("TEST_VOICE_COMMAND", "Command received");
    }

    protected void binConfig() {
        // Remove old phrases
        removePhrases(phrases);
        phrases = new LinkedList<>(Arrays.asList(Phrase.SELECT, Phrase.HELP));
        insertPhrases(phrases);
    }
}
