package edu.gatech.w2gplayground.Voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;

import edu.gatech.w2gplayground.MainActivity;
import edu.gatech.w2gplayground.R;

import edu.gatech.w2gplayground.CustomToast;

/**
 * Class to encapsulate all voice commands
 */
public class VoiceCommandReceiver extends BroadcastReceiver {
    // Voice command substitutions. These substitutions are returned when phrases are recognized.
    // This is done by registering a phrase with a substitution. This eliminates localization issues
    // and is encouraged
    final String MATCH_TEST = "test";
    final String MATCH_SCAN = "scan";

    // Voice command custom intent names
    final String TOAST_EVENT = "other_toast";

    private MainActivity mainActivity;

    /**
     * Constructor which takes care of all speech recognizer registration
     *
     * @param activity MainActivity from which we are created
     */
    public VoiceCommandReceiver(MainActivity activity) {
        mainActivity = activity;
        mainActivity.registerReceiver(this, new IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND));
        Log.d(mainActivity.LOG_TAG, "Connecting to Vuzix Speech SDK");

        try {
            // Create a VuzixSpeechClient from the SDK
            VuzixSpeechClient sc = new VuzixSpeechClient(activity);

            // Delete every phrase in the dictionary!
            sc.deletePhrase("*");

            try {
                sc.insertWakeWordPhrase("hello vuzix");
                sc.insertWakeWordPhrase("voice on");
            } catch (NoSuchMethodError e) {
                Log.i(mainActivity.LOG_TAG, "Setting wake words is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            try {
                sc.insertVoiceOffPhrase("voice off");      // Add-back the default phrase for consistency
            } catch (NoSuchMethodError e) {
                Log.i(mainActivity.LOG_TAG, "Setting voice off is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            // Insert a custom intent.  Note: these are sent with sendBroadcastAsUser() from the service
            // If you are sending an event to another activity, be sure to test it from the adb shell
            // using: am broadcast -a "<your intent string>"
            // This example sends it to ourself, and we are sure we are active and registered for it
            Intent customToastIntent = new Intent(mainActivity.CUSTOM_SDK_INTENT);
            sc.defineIntent(TOAST_EVENT, customToastIntent );
            sc.insertIntentPhrase("canned toast", TOAST_EVENT);

            // Insert phrases for our broadcast handler
            //
            // ** NOTE **
            // The "s:" is required in the SDK version 1.2, but is not required in the latest JAR distribution
            // or SDK version 1.3.  But it is harmless when not required. It indicates that the recognizer is making a
            // substitution.  When the multi-word string is matched (in any language) the associated MATCH string
            // will be sent to the BroadcastReceiver
            sc.insertPhrase("test",  MATCH_TEST);
            sc.insertPhrase("scan", MATCH_SCAN);


            // See what we've done
            Log.i(mainActivity.LOG_TAG, sc.dump());

            // The recognizer may not yet be enabled in Settings. We can enable this directly
            VuzixSpeechClient.EnableRecognizer(mainActivity, true);
        } catch(NoClassDefFoundError e) {
            // We get this exception if the SDK stubs against which we compiled cannot be resolved
            // at runtime. This occurs if the code is not being run on a Vuzix device supporting the voice
            // SDK
            CustomToast.showTopToast(activity, mainActivity.getResources().getString(R.string.error));
            Log.e(mainActivity.LOG_TAG, activity.getResources().getString(R.string.error) );
            Log.e(mainActivity.LOG_TAG, e.getMessage());

            e.printStackTrace();

            activity.finish();
        } catch (Exception e) {
            Log.e(mainActivity.LOG_TAG, "Error setting custom vocabulary: " + e.getMessage());
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
        Log.e(mainActivity.LOG_TAG, mainActivity.getMethodName());
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
                    Log.e(mainActivity.LOG_TAG, mainActivity.getMethodName() + " \"" + phrase + "\"");

                    // Determine the specific phrase that was recognized and act accordingly
                    switch (phrase) {
                        case MATCH_TEST:
                            mainActivity.handleTestCommand();
                            break;
                        case MATCH_SCAN:
                            mainActivity.handleScanCommand();
                            break;
                        default:
                            Log.e(mainActivity.LOG_TAG, "Phrase not handled");
                    }
                } else if (extras.containsKey(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA)) {
                    // if we get a recognizer active bool extra, it means the recognizer was
                    // activated or stopped
                    boolean isRecognizerActive = extras.getBoolean(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA, false);
                    mainActivity.RecognizerChangeCallback(isRecognizerActive);
                } else {
                    Log.e(mainActivity.LOG_TAG, "Voice Intent not handled");
                }
            }
        }
        else {
            Log.e(mainActivity.LOG_TAG, "Other Intent not handled " + intent.getAction() );
        }
    }

    /**
     * Called to unregister for voice commands. An important cleanup step.
     */
    public void unregister() {
        try {
            mainActivity.unregisterReceiver(this);
            Log.i(mainActivity.LOG_TAG, "Custom vocab removed");
            mainActivity = null;
        } catch (Exception e) {
            Log.e(mainActivity.LOG_TAG, "Custom vocab died " + e.getMessage());
        }
    }

    /**
     * Handler called when "Listen" button is clicked. Activates the speech recognizer identically to
     * saying "Hello Vuzix"
     *
     * @param bOnOrOff boolean True to enable listening, false to cancel it
     */
    public void TriggerRecognizerToListen(boolean bOnOrOff) {
        try {
            VuzixSpeechClient.TriggerVoiceAudio(mainActivity, bOnOrOff);
        } catch (NoClassDefFoundError e) {
            // The voice SDK was added in version 1.2. The constructor will have failed if the
            // target device is not a Vuzix device that is compatible with SDK version 1.2.  But the
            // trigger command with the bool was added in SDK version 1.4.  It is possible the Vuzix
            // device does not yet have the TriggerVoiceAudio interface. If so, we get this exception.
            Toast.makeText(mainActivity, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

}