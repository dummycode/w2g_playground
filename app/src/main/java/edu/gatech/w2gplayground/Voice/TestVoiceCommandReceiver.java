package edu.gatech.w2gplayground.Voice;

import androidx.appcompat.app.AppCompatActivity;


import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Test voice commands
 *
 * @param <T> Activity in which we are created
 */
public class TestVoiceCommandReceiver<T extends AppCompatActivity & VoiceCommandActivity> extends VoiceCommandReceiver<T> {
    Phrase[] phrases = { Phrase.TEST };

    /**
     * Constructor which takes care of all speech recognizer registration
     *
     * @param activity activity from which we are created
     */
    public TestVoiceCommandReceiver(T activity) {
        super(activity);

        this.insertPhrases(phrases);
    }
}
