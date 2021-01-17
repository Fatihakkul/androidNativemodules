package com.androidnativemodule;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import android.media.AudioRecord;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;

public class ImagePickerModule extends ReactContextBaseJavaModule {

    private  Promise stopRecordingPromise;
    private  boolean isRecording = false;
    private  String tmpfile;
    private  int bufferSize;
    private  AudioRecord recorder;
    private static final int IMAGE_PICKER_REQUEST = 13001;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    private Promise mPickerPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (requestCode == IMAGE_PICKER_REQUEST) {
                if (mPickerPromise != null) {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        mPickerPromise.reject(E_PICKER_CANCELLED, "Image picker was cancelled");
                    } else if (resultCode == Activity.RESULT_OK) {
                        Uri uri = intent.getData();

                        if (uri == null) {
                            mPickerPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");
                        } else {
                            mPickerPromise.resolve(uri.toString());
                        }
                    }

                    mPickerPromise = null;
                }
            }
        }
    };

    ImagePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);

        // Add the listener for `onActivityResult`
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "ImagePickerModule";
    }

    @ReactMethod
    public void pickImage(final Promise promise) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        // Store the promise to resolve/reject when picker returns data
        mPickerPromise = promise;

        try {
            final Intent galleryIntent = new Intent(Intent.ACTION_PICK);

            galleryIntent.setType("image/*");

            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Pick an image");

            currentActivity.startActivityForResult(chooserIntent, IMAGE_PICKER_REQUEST);
        } catch (Exception e) {
            mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
            mPickerPromise = null;
        }
    }
    @ReactMethod
    public  void startRecord(){
            isRecording = true;
            AudioRecord recorder = findAudioRecord();
            recorder.startRecording();

        Thread recorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int bytesRead;
                        int count = 0;
                        String base64Data;

                        byte[] buffer= new byte[bufferSize];
                        String filePath = getReactApplicationContext().getFilesDir().getAbsolutePath();
                        tmpfile = filePath + "/" +"temp.pcm";
                        FileOutputStream os = new FileOutputStream(tmpfile);
                        while (isRecording){
                            bytesRead = recorder.read(buffer, 0, buffer.length);

                            // skip first 2 buffers to eliminate "click sound"
                            if (bytesRead > 0 && ++count > 2) {
                                base64Data = Base64.encodeToString(buffer, Base64.NO_WRAP);

                                os.write(buffer, 0, bytesRead);
                            }
                        }
                        recorder.stop();
                        os.close();


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            recorThread.start();
    }
    @ReactMethod
    public void stop(Promise promise) {
        isRecording = false;
        stopRecordingPromise = promise;
    }
    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                      //  Log.d(C.TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                        //        + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                      //  Log.e(C.TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }
}
