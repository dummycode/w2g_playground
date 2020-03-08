package edu.gatech.w2gplayground.Voice;

import androidx.appcompat.app.AppCompatActivity;

import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Enums.Phrase;

/**
 * Class to take care of Scan Item voice commands
 *
 * @param <T> Activity from which we are created
 */
public class ScanItemVoiceCommandReceiver<T extends AppCompatActivity & VoiceCommandActivity> extends VoiceCommandReceiver<T> {
    Phrase[] phrases = { Phrase.QUANTITY_OVERRIDE};

    /**
     * @param activity Activity from which we are created
     */
    public ScanItemVoiceCommandReceiver(T activity) {
        super(activity);

        insertPhrases(phrases);
    }
}