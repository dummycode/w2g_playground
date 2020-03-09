package edu.gatech.w2gplayground.Activities.Interfaces;

public interface VoiceCommandActivity {
    String LOG_TAG = "VOICE_COMMAND_ACTIVITY";
    String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";

    void RecognizerChangeCallback(final boolean isRecognizerActive);

    String getMethodName();
}
