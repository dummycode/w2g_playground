package edu.gatech.w2gplayground.Voice;

import androidx.appcompat.app.AppCompatActivity;

import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Login voice commands
 *
 * @param <T> Activity in which we are created
 */
public class LoginVoiceCommandReceiver<T extends AppCompatActivity & VoiceCommandActivity> extends VoiceCommandReceiver<T> {
    Phrase[] phrases = { Phrase.SCAN, Phrase.MANUAL_ENTRY };

    /**
     * @param activity Activity from which we are created
     */
    public LoginVoiceCommandReceiver(T activity) {
        super(activity);

        insertPhrases(phrases);
    }
}
