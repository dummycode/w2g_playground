package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import java.util.HashMap;

import edu.gatech.w2gplayground.Activities.LoginActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Login voice commands
 */
public class LoginVoiceCommandReceiver extends VoiceCommandReceiver<LoginActivity> {
    HashMap<Phrase, Runnable> handlerMap = new HashMap<>();

    /**
     * @param activity Activity from which we are created
     */
    public LoginVoiceCommandReceiver(LoginActivity activity) {
        super(activity);

        Phrase[] phrases = { Phrase.SCAN, Phrase.MANUAL_ENTRY };

        handlerMap.put(Phrase.SCAN, activity::handleScanCommand);
        handlerMap.put(Phrase.MANUAL_ENTRY, activity::handleManualEntryCommand);

        insertPhrases(phrases);
    }

    /**
     * Handler for phrases
     *
     * @param command The phrase to handle
     */
    public void handleCommand(String command) {
        Log.d(LOG_TAG, "Command received: " + command);

        for (Phrase phrase: handlerMap.keySet()) {
            if (command.equals(phrase.getPhrase())) {
                Runnable handler = handlerMap.get(phrase);

                if (handler != null) {
                    handler.run();
                } else {
                    Log.e(LOG_TAG, "Handler for command was null!");
                }

                return;
            }
        }

        Log.e(LOG_TAG, "Handler for command not found!");
    }
}
