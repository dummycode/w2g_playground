package edu.gatech.w2gplayground.Voice;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Pick List voice commands
 */
public class PickListVoiceCommandReceiver extends VoiceCommandReceiver<PickListActivity> {
    /**
     * @param activity Activity from which we are created
     */
    public PickListVoiceCommandReceiver(PickListActivity activity) {
        super(activity);
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

    /**
     * Configure voice command receiver for bin configuration screen
     */
    public void binConfig() {
        // Remove old phrases
        removePhrases(phrases);
        phrases = new LinkedList<>(Arrays.asList(Phrase.SELECT, Phrase.HELP));
        insertPhrases(phrases);

        handlerMap.put(Phrase.SELECT, activity::binConfiguationDone);
        handlerMap.put(Phrase.HELP, activity::binConfigurationHelp);
    }

    /**
     * Configure voice command receiver for next location screen
     */
    public void nextLocation() {
        // Remove old phrases
        removePhrases(phrases);
        phrases = new LinkedList<>(Arrays.asList(Phrase.SELECT, Phrase.HELP));
        insertPhrases(phrases);

        handlerMap.put(Phrase.SELECT, activity::nextLocationDone);
        handlerMap.put(Phrase.HELP, activity::nextLocationHelp);
    }
}
