package edu.gatech.w2gplayground.Voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.gatech.w2gplayground.Activities.MainActivity;
import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Enums.Phrase;
import edu.gatech.w2gplayground.R;

import edu.gatech.w2gplayground.Utilities.CustomToast;

/**
 * Class to encapsulate all voice commands
 */
public abstract class VoiceCommandReceiver<T extends AppCompatActivity & VoiceCommandActivity> extends BroadcastReceiver {
    // Log tag for the voice command receiver
    final String LOG_TAG = "VOICE_COMMAND_RECEIVER";

    // Voice command custom intent names
    final String TOAST_EVENT = "other_toast";

    // Speech client instance variable
    VuzixSpeechClient sc;

    // Activity from which we are created
    T activity;

    // Valid phrases
    List<Phrase> phrases = new LinkedList<>();

    // Handler map for different commands
    HashMap<Phrase, Runnable> handlerMap = new HashMap<>();

    /**
     * Constructor which takes care of all speech recognizer registration
     *
     * @param activity MainActivity from which we are created
     */
    public VoiceCommandReceiver(T activity) {
        this.activity = activity;
        this.activity.registerReceiver(this, new IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND));
        Log.d(MainActivity.LOG_TAG, "Connecting to Vuzix Speech SDK");

        try {
            this.sc = new VuzixSpeechClient(activity);

            // Delete every phrase in the dictionary!
            sc.deletePhrase("*");

            try {
                sc.insertWakeWordPhrase("hello vuzix");
                sc.insertWakeWordPhrase("voice on");
            } catch (NoSuchMethodError e) {
                Log.i(MainActivity.LOG_TAG, "Setting wake words is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            try {
                sc.insertVoiceOffPhrase("voice off");
            } catch (NoSuchMethodError e) {
                Log.i(MainActivity.LOG_TAG, "Setting voice off is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            Log.i(MainActivity.LOG_TAG, sc.dump());

            // The recognizer may not yet be enabled in Settings. We can enable this directly
            VuzixSpeechClient.EnableRecognizer(this.activity, true);
        } catch(NoClassDefFoundError e) {
            // We get this exception if the SDK stubs against which we compiled cannot be resolved
            // at runtime. This occurs if the code is not being run on a Vuzix device supporting the voice
            // SDK
            CustomToast.showTopToast(activity, this.activity.getResources().getString(R.string.error));
            Log.e(MainActivity.LOG_TAG, activity.getResources().getString(R.string.error) );
            Log.e(MainActivity.LOG_TAG, e.getMessage());

            e.printStackTrace();

            activity.finish();
        } catch (Exception e) {
            Log.e(MainActivity.LOG_TAG, "Error setting custom vocabulary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * All custom phrases registered with insertPhrase() are handled here.
     *
     * Custom intents may also be directed here, but this example does not demonstrate this.
     *
     * Keycodes are never handled via this interface
     *
     * @param context Context in which the phrase is handled
     * @param intent Intent associated with the recognized phrase
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(LOG_TAG, this.activity.getMethodName());

        // All phrases registered with insertPhrase() match ACTION_VOICE_COMMAND as do
        // recognizer status updates
        if (intent.getAction().equals(VuzixSpeechClient.ACTION_VOICE_COMMAND)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // We will determine what type of message this is based upon the extras provided
                if (extras.containsKey(VuzixSpeechClient.PHRASE_STRING_EXTRA)) {
                    // If we get a phrase string extra, this was a recognized spoken phrase.
                    // The extra will contain the text that was recognized, unless a substitution
                    // was provided.  All phrases in this example have substitutions as it is
                    // considered best practice
                    String phrase = intent.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA);
                    Log.e(MainActivity.LOG_TAG, this.activity.getMethodName() + " \"" + phrase + "\"");

                    handleCommand(phrase);
                } else if (extras.containsKey(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA)) {
                    // if we get a recognizer active bool extra, it means the recognizer was
                    // activated or stopped
                    boolean isRecognizerActive = extras.getBoolean(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA, false);
                    this.activity.RecognizerChangeCallback(isRecognizerActive);
                } else {
                    Log.e(MainActivity.LOG_TAG, "Voice Intent not handled");
                }
            }
        } else {
            Log.e(MainActivity.LOG_TAG, "Other Intent not handled " + intent.getAction() );
        }
    }

    /**
     * Called to unregister for voice commands. An important cleanup step.
     */
    public void unregister() {
        try {
            activity.unregisterReceiver(this);
            Log.i(activity.LOG_TAG, "Custom vocab removed");
            activity = null;
        } catch (Exception e) {
            Log.e(activity.LOG_TAG, "Custom vocab died " + e.getMessage());
        }
    }

    /**
     * Activates the speech recognizer identically to saying "Hello Vuzix"
     *
     * @param isOn boolean True to enable listening, false to cancel it
     */
    public void triggerVoiceAudio(boolean isOn) {
        try {
            VuzixSpeechClient.TriggerVoiceAudio(this.activity, isOn);
        } catch (NoClassDefFoundError e) {
            Toast.makeText(this.activity, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to insert custom intent to the voice command receiver
     *
     * @param intent to be inserted
     */
    protected void insertCustomIntent(String intent) {
        try {
            // Insert a custom intent.  Note: these are sent with sendBroadcastAsUser() from the service
            // If you are sending an event to another activity, be sure to test it from the adb shell
            // using: am broadcast -a "<your intent string>"
            // This example sends it to ourself, and we are sure we are active and registered for it
            Intent customToastIntent = new Intent(this.activity.CUSTOM_SDK_INTENT);
            this.sc.defineIntent(TOAST_EVENT, customToastIntent);
            sc.insertIntentPhrase("canned toast", TOAST_EVENT);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error creating custom intent");
        }
    }

    /**
     * Method to add phrases to the library
     *
     * @param phrases Array of phrases
     */
    protected void insertPhrases(List<Phrase> phrases) {
        for (Phrase phrase: phrases) {
            this.sc.insertPhrase(phrase.getPhrase());
        }
    }

    /**
     * Method to delete phrases from the library
     *
     * @param phrases Array of phrases
     */
    protected void removePhrases(List<Phrase> phrases) {
        for (Phrase phrase: phrases) {
            this.sc.deletePhrase(phrase.getPhrase());
        }
    }

    /**
     * Method to clear the library
     */
    public void clearPhrases() {
        this.sc.deletePhrase("*");
    }

    protected abstract void handleCommand(String command);
}
