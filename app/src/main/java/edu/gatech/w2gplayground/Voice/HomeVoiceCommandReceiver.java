package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import java.util.Arrays;

import edu.gatech.w2gplayground.Activities.HomeActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Home voice commands
 */
public class HomeVoiceCommandReceiver extends VoiceCommandReceiver<HomeActivity> {
    Phrase[] phrases = { Phrase.REFRESH };
    /**
     * @param activity Activity from which we are created
     */
    public HomeVoiceCommandReceiver(HomeActivity activity) {
        super(activity);

        insertPhrases(Arrays.asList(phrases));
        handlerMap.put(Phrase.REFRESH, activity::refresh);
    }

    /**
     * Handler for phrases
     *
     * @param command The phrase to handle
     */
    protected void handleCommand(String command) {
        Log.d(LOG_TAG, "Command received: " + command);

        for (Phrase phrase: handlerMap.keySet()) {
            if (command.equals(phrase.getPhrase())) {
                Runnable handler = handlerMap.get(phrase);

                if (handler != null) {
                    Log.d(LOG_TAG, "Handling command " + phrase.getPhrase());
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
