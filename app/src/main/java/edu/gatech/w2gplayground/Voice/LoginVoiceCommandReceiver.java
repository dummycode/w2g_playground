package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;

import edu.gatech.w2gplayground.Activities.LoginActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Login voice commands
 */
public class LoginVoiceCommandReceiver extends VoiceCommandReceiver<LoginActivity> {
    HashMap<Phrase, Runnable> map;
    Phrase[] phrases = { Phrase.SCAN, Phrase.MANUAL_ENTRY };

    /**
     * @param activity Activity from which we are created
     */
    public LoginVoiceCommandReceiver(LoginActivity activity) {
        super(activity);

        map.put(Phrase.SCAN, activity::handleScanCommand);
        map.put(Phrase.MANUAL_ENTRY, activity::handleManualEntryCommand);

        insertPhrases(phrases);
    }

    /**
     * Handler for phrases
     *
     * @param command The phrase to handle
     */
    public void handleCommand(String command) {
        for (Phrase phrase: map.keySet()) {
            if (command.equals(phrase.getPhrase())) {
                if (map.get(phrase) != null) {
                    map.get(phrase).run();
                }
            }
        }
    }

}
